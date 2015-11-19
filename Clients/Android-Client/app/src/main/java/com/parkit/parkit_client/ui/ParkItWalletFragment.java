package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Balance;
import com.parkit.parkit_client.rest.models.parkit.ParkItError;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ParkItWalletFragment extends Fragment {

    private View view;



    @Bind(R.id.text_credits_balance)
    TextView walletBalanceText;

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


}
