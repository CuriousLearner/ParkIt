package com.parkit.parkit_entry_scanner.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;
import com.parkit.parkit_entry_scanner.rest.RestClient;
import com.parkit.parkit_entry_scanner.rest.models.Customer;
import com.parkit.parkit_entry_scanner.rest.models.EntryRequest;
import com.parkit.parkit_entry_scanner.rest.models.ParkItError;

import java.util.Locale;
import java.util.logging.SocketHandler;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class QRCodeScannerFragment extends Fragment {


    public static QRCodeScannerFragment currentFragment;

    private View view;



    public static QRCodeScannerFragment getCurrentFragmentInstance() { return currentFragment; }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_qrcode_scanner, container, false);
            ButterKnife.bind(this, view);
            RestClient restClient = new RestClient();



        }

        currentFragment = this;
        return view;
    }


    // OnClicks
    @OnClick(R.id.btn_scan_qr_code)
    public void scanQRCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                QRCodeScannerFragment.this.getActivity());
        Log.d(Constants.LOG_TAG, "Initiating scan");
        intentIntegrator
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setCameraId(0)
                .setPrompt("Scan Customer's QR Code")
                .setBeepEnabled(true)
                .initiateScan();
    }


    public interface QRCodeScanListener {

        public void onQRCodeScan(IntentResult result);
    }

    @Override
    public void onAttach(Activity activity) throws ClassCastException{
        super.onAttach(activity);
        QRCodeScanListener qrCodeScanListener;
        try {
            qrCodeScanListener = (QRCodeScanListener) activity;
        } catch(ClassCastException cce) {
            throw new ClassCastException(activity.getPackageName()
                    + " must implement QRCodeScanListener interface");
        }

    }

    public void onQRCodeScanned(IntentResult result) {
        // call ParkIt API here
        Log.d(Constants.LOG_TAG, "Registering on ParkIt with hash : " + result.getContents());

        SharedPreferences scannerConfig = this.getActivity().getSharedPreferences(
                Constants.SHARED_PREFERENCES_KEY,
                0
        );
        // form request object for park it API call
        final EntryRequest entryRequest = new EntryRequest(
                result.getContents(),
                Integer.parseInt(scannerConfig.getString(Constants.CONFIG_KEY_PARKING_LOT_ID, "")),
                scannerConfig.getString(Constants.CONFIG_KEY_VEHICLE_TYPE, "")
        );

        final Context ctx = this.getActivity().getApplicationContext();

        Log.d(Constants.LOG_TAG, "Entry Request Object : \n" + entryRequest);

        RestClient.parkItService.requestEntry(
                Constants.PARKIT_AUTH_TOKEN,
                entryRequest,
                new Callback<Customer>() {
                    @Override
                    public void success(Customer customer, Response response) {
                       // 200 - successful entry

                        Log.d(Constants.LOG_TAG, "in onSuccess");
                        logResponse(response);

                        Log.d(Constants.LOG_TAG, "Successful login by : "+customer.first_name);

                        // successful entry
                        Utils.showShortToast(
                                "Successful Entry!!!\n Welcome :  "
                                        + customer.first_name, ctx);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // 400 - token invalid   OR
                        //       pid not present OR
                        //       QR_CODE_DATA not present OR
                        //       vehicle_type not present
                        //
                        // 401 - invalid token
                        // 402 - balance is low
                        // 404 - customer not found
                        // 409 - already an active transaction for user OR
                        //       parking full for given vehicle_type

                        if(error.getResponse() == null) {
                            Log.d(Constants.LOG_TAG, "Response is null, error kind : " +
                                    error.getKind());
                            Utils.showShortToast("Unsuccessfull entry !!!", ctx);
                            return;
                        }

                        Context ctx =
                                QRCodeScannerFragment.this.getActivity().getApplicationContext();

                        // unsuccessful entry
                        Utils.showShortToast("Unsuccessful entry !!!", ctx);
                        Log.d(Constants.LOG_TAG, "Unsuccessfull entry");
                        // log error
                        Log.d(Constants.LOG_TAG,
                                "Retrofit error : " +
                                "\nResponse : "+ error.getResponse() +
                                "\nURL : " + error.getUrl() +
                                "\nMessage : " + error.getMessage() +
                                "\nString Representation : " + error.toString() +
                                "\nLocalized Message : " + error.getLocalizedMessage() +
                                "\nBody : " + error.getBody() +
                                "\nKind : " + error.getKind() +
                                        "\nSuccess Type : " + error.getSuccessType()
                        );

                        ParkItError  parkItError = (ParkItError) error.getBodyAs(ParkItError.class);
                        // log park-it error
                        Log.d(Constants.LOG_TAG,
                                "ParkIt API Error : "
                                        +((parkItError == null)? "null" : parkItError.toString()));

                        switch(error.getResponse().getStatus()) {

                            case 400:
                            case 401:
                                // internal application error
                                Utils.showShortToast(
                                        "Internal Application Error," +
                                                "\n Please contact ParkIt officials", ctx);

                                break;
                            case 402:
                                // balance is low
                                Utils.showLongToast(
                                        "Your eWallet balance is low, please recharge",
                                        ctx);
                                break;
                            case 404:
                                // customer not found
                                Utils.showShortToast(
                                        "Customer account not found on ParkIt Servers", ctx);
                            case 409:
                                // parking full OR already active transaction
                                if (parkItError.getMessage().contains("Parking Full")) {
                                    SharedPreferences scannerConfig =
                                            QRCodeScannerFragment.this.getActivity()
                                                    .getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);

                                    String vehicleType =
                                            scannerConfig.getString(
                                                    Constants.CONFIG_KEY_VEHICLE_TYPE, "");
                                    Utils.showShortToast(
                                            "Parking full for " + vehicleType +
                                                    "s, please visit another parking lot", ctx);
                                } else {
                                    // already active transaction
                                    Utils.showLongToast(
                                            "Your vehicle is already parked",
                                            ctx
                                    );
                                }
                                break;
                            default:
                                // unexpected status
                                Utils.showShortToast("Internal Server Error !!!", ctx);
                                Log.d(Constants.LOG_TAG, "Unexpected response !!!");
                                break;
                        }
                    }
                }
        );

    }


    private void logResponse(Response response) {
        Log.d(Constants.LOG_TAG, "Response : " +
            "\nStatus : " + response.getStatus() +
            "\nBody : " + response.getBody() +
            "\nURL : " + response.getUrl() +
            "\nReason : " + response.getReason() +
            "\nHeaders : " + response.getHeaders() +
            "\nString Representation : " + response.toString()
        );


    }



}
