package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Balance;
import com.parkit.parkit_client.rest.models.parkit.ParkItError;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ParkItWalletFragment extends Fragment {

    private View view;

    // view bindings

    @Bind(R.id.text_credits_balance)
    TextView walletBalanceText;

    @Bind(R.id.edit_coupon_code)
    EditText couponCodeEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        if(view == null)  {
            // form wallet view
            view = inflater.inflate(R.layout.fragment_park_it_wallet, container, false);
            ButterKnife.bind(this, view);

            // if wallet balance is set display that
            SharedPreferences userAccountDetails =
                    this.getActivity().getSharedPreferences(
                            Constants.KEY_SHARED_PREFERENCES, 0);
            String walletBalance = userAccountDetails.getString(Constants.CONFIG_KEY_BALANCE, "");
            if(!walletBalance.equals(""))
                walletBalanceText.setText("Rs. "+walletBalance);
            fetchWalletBalance();


        }



        return view;



    }


    private void fetchWalletBalance() {
        if(RestClient.parkItService == null) {
            RestClient restClient = new RestClient();
        } else {


            final SharedPreferences sharedPreferences =
                    this.getActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);


            String userHash = sharedPreferences.getString(Constants.CONFIG_KEY_HASH, "");

            Log.d(Constants.LOG_TAG, "Fetching wallet balance");
            RestClient.parkItService.getWalletBalance(
                    Constants.PARKIT_AUTH_TOKEN,
                    userHash,
                    new Callback<Balance>() {
                        @Override
                        public void success(Balance balance, Response response) {
                            // 200
                            Log.d(Constants.LOG_TAG, "in OnSuccess");
                            if(response.getStatus() == 200) {
                                Log.d(Constants.LOG_TAG, "200 OK");
                                Log.d(Constants.LOG_TAG, "Wallet Balance : " + balance.getBalance());
                                walletBalanceText.setText("Rs. " + balance.getBalance());
                                SharedPreferences.Editor userAccountDetailsEditor =
                                        ParkItWalletFragment.this.getActivity()
                                                .getSharedPreferences(
                                                        Constants.KEY_SHARED_PREFERENCES, 0).edit();
                                userAccountDetailsEditor.putString(
                                        Constants.CONFIG_KEY_BALANCE, balance.toString());
                                userAccountDetailsEditor.apply();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            // 404
                            Log.d(Constants.LOG_TAG, "in failure");
                            if(error.getResponse() == null) {
                                Log.d(Constants.LOG_TAG, "null response");
                                return;
                            }
                            if(error.getResponse().getStatus() == 404) {
                                // customer not found
                                Log.d(Constants.LOG_TAG, "404 NOT FOUND");
                            }
                        }
                    }

            );
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        fetchWalletBalance();
    }


    // OnClicks

    @OnClick(R.id.btn_apply_coupon)
    public void applyCoupon() {

        // validate
        if(couponCodeEdit.getText().toString().equals("")) {
            Utils.showShortToast(
                    "Invalid coupon code !!!",
                    this.getActivity().getApplicationContext()
            );
            return;
        }

        // context
        final Context ctx = this.getActivity().getApplicationContext();


        SharedPreferences accountDetails = this.getActivity()
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");

        if(userHash.equals("")) {
            Utils.showShortToast("Hash error !!!", ctx);
            Log.d(Constants.LOG_TAG, "Hash has not been set");
            return;
        }


        Log.d(Constants.LOG_TAG, "Will post : " +
                "\nAuth token : " + Constants.PARKIT_AUTH_TOKEN +
                "\nUser hash : " + userHash +
                "\nCoupon code : " + couponCodeEdit.getText().toString()
        );

        // recharge
        RestClient.parkItService.rechargeWalletWithCoupon(
                Constants.PARKIT_AUTH_TOKEN,
                userHash,
                couponCodeEdit.getText().toString(),
                new Callback<ParkItError>() {
                    @Override
                    public void success(ParkItError parkItError, Response response) {
                        // 200
                        if(response.getStatus() == 200) {
                            // success full recharge
                            Log.d(Constants.LOG_TAG, "200 OK");
                            Utils.showShortToast("Recharge successful", ctx);
                            fetchWalletBalance();
                        } else {
                            Log.d(Constants.LOG_TAG,
                                    "Unexpected success response code received : "
                                            +response.getStatus());
                            Utils.showLongToast(
                                    "Internal Application Error,\n" +
                                            "Please contact ParkIt officials",
                                    ctx
                            );
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // 400 - token not present OR
                        //       coupon not valid
                        // 401 - invalid token
                        // 404 - customer not found OR
                        //       coupon not found
                        if(error.getResponse() == null) {
                            Utils.showShortToast("Recharge Unsuccessfull", ctx);
                            Log.d(Constants.LOG_TAG, "Response is null, error kind : "
                                    + error.getKind());
                            return;
                        }
                        ParkItError message = (ParkItError) error.getBodyAs(ParkItError.class);
                        if(message == null) {
                            Log.d(Constants.LOG_TAG, "ParkIt error object is null");
                            Utils.showLongToast(
                                    "Internal Application Error,\n" +
                                            "Please contact ParkIt officials",
                                    ctx
                            );
                            return;
                        }

                        switch (error.getResponse().getStatus()) {

                            case 400:
                                // token not present or coupon not valid
                                Log.d(Constants.LOG_TAG, "400 BAD REQUEST");
                                if(message.toString().contains("credentials")) {
                                    // token not present
                                    Log.d(Constants.LOG_TAG,
                                            "Server response : Auth token not present in request");
                                    Utils.showLongToast(
                                            "Internal Application Error,\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                } else if(message.toString().contains("Coupon not valid")) {
                                    // coupon not valid anymore
                                    Log.d(Constants.LOG_TAG,
                                            "Server response : Coupon not valid");
                                    Utils.showLongToast(
                                            "Invalid coupon code !!!",
                                            ctx
                                    );
                                } else {
                                    // unexpected message
                                    Log.d(Constants.LOG_TAG,
                                            "Unexpected 400 error message : " + message.toString());
                                    Utils.showLongToast(
                                            "Internal Application Error,\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                }
                                break;
                            case 401:
                                Utils.showLongToast(
                                        "Internal Application Error,\n" +
                                                "Please contact ParkIt officials",
                                        ctx
                                );
                                Log.d(Constants.LOG_TAG, "Invalid auth token");
                                break;
                            case 404:
                                // customer or coupon not found
                                Log.d(Constants.LOG_TAG, "404 NOT FOUND");
                                Log.d(Constants.LOG_TAG,
                                        "Server response : "+message.toString());

                                if(message.toString().contains("Customer not found")) {
                                    // customer not found
                                    Log.d(Constants.LOG_TAG, "Customer record not found on server");
                                    Utils.showShortToast(
                                            "Customer account not found on ParkIt servers", ctx);
                                } else if(message.toString().contains("Coupon not found")) {
                                    // coupon not found
                                    Log.d(Constants.LOG_TAG, "Coupon not found");
                                    Utils.showShortToast(
                                            "Invalid coupon code !!!", ctx);
                                } else {
                                    // unexpected message
                                    Log.d(Constants.LOG_TAG,
                                            "Unexpected 404 error message : " + message.toString());
                                    Utils.showLongToast(
                                            "Internal Application Error,\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                }
                                break;
                            default:
                                Log.d(Constants.LOG_TAG,
                                        "Unexpected failure response code received : "+
                                                error.getResponse().getStatus()
                                );
                                Utils.showLongToast(
                                        "Internal Application Error,\n" +
                                                "Please contact ParkIt officials",
                                        ctx
                                );
                                break;

                        }
                    }
                }

        );



    }
}
