package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.ParkItNavigationDrawer;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Customer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountFragment extends Fragment {




    private View view;

    // view bindings

    @Bind(R.id.text_first_name)
    TextView firstNameText;

    @Bind(R.id.text_last_name)
    TextView lastNameText;

    @Bind(R.id.text_contact_number)
    TextView contactNumberText;

    @Bind(R.id.text_address)
    TextView addressText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_account, container, false);
            ButterKnife.bind(this, view);

            updateDetailsView();


            fetchAndUpdateAccountDetails();

        }
        return view;
    }

    private void updateDetailsView() {
        SharedPreferences accountDetails = getActivity()
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);
        String firstName = accountDetails.getString(Constants.CONFIG_KEY_FIRST_NAME, "");
        String lastName = accountDetails.getString(Constants.CONFIG_KEY_LAST_NAME, "");
        String contactNumber = accountDetails.getString(Constants.CONFIG_KEY_CONTACT_NO, "");
        String address = accountDetails.getString(Constants.CONFIG_KEY_ADDRESS, "");

        if(!firstName.equals("")) {
            // customer details are stored
            firstNameText.setText(firstName);
            lastNameText.setText(lastName);
            contactNumberText.setText(contactNumber);
            addressText.setText(address);
        } else {
            Log.d(Constants.LOG_TAG, "Hash values not set");
        }
    }

    private void fetchAndUpdateAccountDetails() {

        SharedPreferences accountDetails = this.getActivity()
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");

        if(userHash.equals("")) {
            Log.d(Constants.LOG_TAG, "Hash is not set");
            return;
        }

        // hash set
        final SharedPreferences.Editor prefEditor = this.getActivity()
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0).edit();
        final Context ctx = this.getActivity().getApplicationContext();

        RestClient.parkItService.getCustomer(
                Constants.PARKIT_AUTH_TOKEN,
                userHash,
                new Callback<Customer>() {
                    @Override
                    public void success(Customer customer, Response response) {
                        if(response.getStatus() == 200) {
                            // customer fetched update view and local storage

                            prefEditor.putString(
                                    Constants.CONFIG_KEY_FIRST_NAME,
                                    customer.first_name);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_LAST_NAME,
                                    customer.last_name);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_LICENSE_LINK,
                                    customer.driving_licence_link);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_CONTACT_NO,
                                    customer.contact_no
                            );
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_ADDRESS,
                                    customer.address
                            );
                            prefEditor.apply();

                            Utils.showShortToast("Account details updated", ctx);
                            Log.d(Constants.LOG_TAG, "Local storage updated successfully");
                            updateDetailsView();

                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // could not fetch latest details
                        Log.d(Constants.LOG_TAG, "in failure callback, error kind : "
                                +error.getKind());
                    }
                }

        );


    }


    // OnClicks

    @OnClick(R.id.btn_logout)
    public void logout() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("parkit", 0).edit();
        editor.clear();
        editor.apply();


        Toast.makeText(
                AccountFragment.this.getActivity(),
                "Logged out !!!",
                Toast.LENGTH_SHORT).show();


        Intent restart = new Intent(AccountFragment.this.getActivity(), MainActivity.class);
        startActivity(restart);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDetailsView();
        fetchAndUpdateAccountDetails();
    }
}


