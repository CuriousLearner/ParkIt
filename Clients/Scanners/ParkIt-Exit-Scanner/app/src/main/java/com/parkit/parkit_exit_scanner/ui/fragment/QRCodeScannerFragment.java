package com.parkit.parkit_exit_scanner.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parkit.parkit_exit_scanner.Constants;
import com.parkit.parkit_exit_scanner.R;
import com.parkit.parkit_exit_scanner.Utils;
import com.parkit.parkit_exit_scanner.rest.RestClient;
import com.parkit.parkit_exit_scanner.rest.models.Cost;
import com.parkit.parkit_exit_scanner.rest.models.ExitRequest;
import com.parkit.parkit_exit_scanner.rest.models.ParkItError;

import java.security.acl.LastOwnerException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vikram on 15/11/15.
 */
public class QRCodeScannerFragment extends Fragment {



    public static QRCodeScannerFragment currentFragment;

    private View view;


    public static QRCodeScannerFragment getCurrentFragmentInstance() { return currentFragment;  }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            //@TODO:Form Scanner Fragment View
            view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);
            ButterKnife.bind(this, view);


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



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        QRCodeScanListener qrCodeScanListener;
        try {
            qrCodeScanListener = (QRCodeScanListener) activity;
        } catch(ClassCastException cce) {
            throw new ClassCastException(activity.getPackageName() +
                    "must implement QRCodeScanListener interface.");
        }
    }

    // interface for fragment - activity communication
    public interface QRCodeScanListener {

        public void onQRCodeScan(IntentResult result);

    }


    public void onQRCodeScanned(final IntentResult result) {
        Log.d(Constants.LOG_TAG,
                "Scan Log : " +
                "\nFormat Name : " + result.getFormatName() +
                "\nContents : " + result.getContents() +
                "\nRaw Bytes : " + result.getRawBytes() +
                "\nError Correction Level : " + result.getErrorCorrectionLevel() +
                "\nOrientation : " + result.getOrientation() +
                "\nString Representation : " + result.toString()
        );
        Log.d(Constants.LOG_TAG, "Will call exit API with hash : "+result.getContents());

        SharedPreferences sharedPreferences =
                this.getActivity().getSharedPreferences(Constants.SHARED_PREFS_KEY, 0);


        // FORM EXIT REQUEST
        ExitRequest exitRequest = new ExitRequest(
                result.getContents(),
                sharedPreferences.getString(Constants.CONFIG_KEY_PARKING_LOT_ID, ""),
                sharedPreferences.getString(Constants.CONFIG_KEY_VEHICLE_TYPE, "")
        );

        Log.d(Constants.LOG_TAG, "Exit Request Object : \n" + exitRequest );

        // reload api service
        RestClient restClient = new RestClient();

        // call api here
        RestClient.parkItService.requestExit(
                Constants.PARKIT_API_AUTH_TOKEN,
                exitRequest,
                new Callback<Cost>() {
                    @Override
                    public void success(Cost cost, Response response) {
                        // 200 - successful entry
                        Context ctx = QRCodeScannerFragment.this
                                .getActivity().getApplicationContext();


                        Log.d(Constants.LOG_TAG, "in success callback");
                        if(response.getStatus() == 200) {
                            Log.d(Constants.LOG_TAG, "200 response");
                            Log.d(Constants.LOG_TAG, "Cost response : "+cost.toString());
                            Utils.showShortToast("Successful Exit", ctx);
                            Utils.showLongToast(
                                    "Total parking cost : Rs. " + cost.getCost() +
                                            "\nThis amount has been deducted from your ParkIt eWallet",
                                    ctx);
                            Utils.showShortToast("Thank You for using ParkIt :)", ctx);
                        } else {
                            Log.d(Constants.LOG_TAG, "Unexpected success response code : "
                                    +response.getStatus());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // 400 - token, pid, QR_CODE_DATA or vehicle type not present
                        // 401 - invalid token
                        // 404 - customer not found OR
                        //       active transaction not found
                        // 409 - entrance lot and exit lot do not match
                        Context ctx = QRCodeScannerFragment
                                .this.getActivity().getApplicationContext();

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

                        Utils.showShortToast("Unsuccessful exit !!!", ctx);


                        // log error
                        Log.d(Constants.LOG_TAG,
                                "Retrofit error : " +
                                        "\nResponse : " + error.getResponse() +
                                        "\nURL : " + error.getUrl() +
                                        "\nMessage : " + error.getMessage() +
                                        "\nString Representation : " + error.toString() +
                                        "\nLocalized Message : " + error.getLocalizedMessage() +
                                        "\nBody : " + error.getBody() +
                                        "\nKind : " + error.getKind() +
                                        "\nSuccess Type : " + error.getSuccessType()
                        );

                        ParkItError parkItError = null;

                        try {
                            parkItError =
                                    (ParkItError) error.getBodyAs(ParkItError.class);
                        } catch(ClassCastException cce) {
                            Log.d(Constants.LOG_TAG,
                                    "Class cast exception occurred while casting to ParkItError");
                        }

                        // Log ParkIt Error
                        Log.d(Constants.LOG_TAG,
                                "ParkItError Object : " +
                                        ((parkItError == null) ? "null" : parkItError.toString()));

                        if(error.getResponse() == null) {
                            Log.d(Constants.LOG_TAG, "Null response");
                            return;
                        }


                        switch (error.getResponse().getStatus()) {

                            case 400:
                            case 401:
                                Utils.showShortToast("Internal Application Error," +
                                        "\nPlease Contact ParkIt Officials", ctx);
                                break;
                            case 402:
                                // balance is low
                                Utils.showLongToast(
                                        "Your eWallet balance is low, please recharge",
                                        ctx);
                                break;
                            case 404:
                                if(parkItError == null) {
                                    Log.d(Constants.LOG_TAG, "ParkIt Error object is null");
                                    return;
                                }
                                // customer or active transaction not found
                                if(parkItError.getMessage().contains("Customer")) {
                                    // customer object not found
                                    Utils.showLongToast(
                                            "Customer account not found on ParkIt Servers, " +
                                            "please register on ParkIt to park with ease.", ctx);
                                    Log.d(Constants.LOG_TAG, "Customer not found 404");
                                } else {
                                    Utils.showShortToast(
                                            "No active parking transaction.", ctx);
                                    Log.d(Constants.LOG_TAG, "Active transaction not found 404");
                                }
                                break;
                            case 409:
                                // user entered from a different parking lot
                                Utils.showShortToast(
                                        "Illegal activity detected !!!", ctx);
                                break;
                            default:
                                Utils.showShortToast("Internal Application Error," +
                                        "\nPlease Contact ParkIt Officials", ctx);
                                Log.d(Constants.LOG_TAG, "Unexpected failure status code : "
                                        +error.getResponse().getStatus());
                                break;
                        }
                    }
                }
        );




    }







}
