package com.parkit.parkit_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.journeyapps.barcodescanner.Util;
import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.adapters.VehicleRecyclerAdapter;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.Vehicle;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vikram on 22/11/15.
 */
public class VehiclesFragment extends Fragment {


    private View view;
    private VehicleRecyclerAdapter vehicleRecyclerAdapter;
    private Customer currentCustomer;

    // View Bindings

    @Bind(R.id.recycler_view_vehicles)
    RecyclerView vehicleRecycler;

    @Bind(R.id.btn_add_vehicle)
    Button addVehicleBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            // form view
            view = inflater.inflate(R.layout.fragment_vehicles, container, false);
            ButterKnife.bind(this, view);

            // setup recycler
            Log.d(Constants.LOG_TAG, "Setting up the vehicle recycler recycler");
            // this allows optimization if each child is known to have the same dimensions
            vehicleRecycler.setHasFixedSize(true);


            // set layout manager for recycler view
            RecyclerView.LayoutManager linearLayoutManager =
                    new LinearLayoutManager(view.getContext());
            vehicleRecycler.setLayoutManager(linearLayoutManager);


            // set recycler adapter for recycler view
            vehicleRecyclerAdapter =
                    new VehicleRecyclerAdapter(new ArrayList<Vehicle>());

            vehicleRecycler.setAdapter(vehicleRecyclerAdapter);

            fetchVehicles();
        }

        return view;
    }


    private void fetchVehicles() {

        if(RestClient.parkItService == null) {
            // initalize service
            RestClient restClient = new RestClient();
        }

        SharedPreferences accountDetails = this.getActivity()
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");

        if(userHash.equals("")) {
            Log.d(Constants.LOG_TAG, "Hash not set");
            Utils.showShortToast("Hash error !!!", this.getActivity().getApplicationContext());
        } else {

            RestClient.parkItService.getCustomer(
                    Constants.PARKIT_AUTH_TOKEN,
                    userHash,
                    new Callback<Customer>() {
                        @Override
                        public void success(Customer customer, Response response) {
                            Context ctx = VehiclesFragment.this.getActivity()
                                    .getApplicationContext();
                            // 200
                            if(response.getStatus() == 200) {
                                Log.d(Constants.LOG_TAG, "200 OK, Customer data fetched");
                                Utils.showShortToast("Data fetched", ctx);

                                vehicleRecycler.setAdapter(
                                        new VehicleRecyclerAdapter(customer.vehicles));
                                vehicleRecycler.setVisibility(View.VISIBLE);
                                addVehicleBtn.setVisibility(View.VISIBLE);
                                currentCustomer = customer;


                            } else {
                                Log.d(Constants.LOG_TAG,
                                        "Unexpected success status code received : "
                                                + response.getStatus());
                                Utils.showLongToast(
                                        "Internal Application Error!!!," +
                                                "\nPlease contact ParkIt officials",
                                        ctx);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Context ctx = VehiclesFragment.this.getActivity()
                                    .getApplicationContext();

                            if(error.getResponse() == null) {
                                Log.d(Constants.LOG_TAG, "Null response, error kind : "
                                        +error.getKind());
                                Utils.showShortToast("Vehicles data could not be fetched !!!", ctx);
                            } else {
                                if(error.getResponse().getStatus() == 404) {
                                    // customer not found
                                    Log.d(Constants.LOG_TAG, "404 NOT FOUND");
                                    Utils.showShortToast(
                                            "Customer account not found on ParkIt servers", ctx);

                                } else {
                                    Log.d(Constants.LOG_TAG,
                                            "Unexpected error status code received : "
                                                    + error.getResponse().getStatus());
                                    Utils.showLongToast(
                                            "Internal Application Error!!!," +
                                                    "\nPlease contact ParkIt officials",
                                            ctx);
                                }
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchVehicles();
    }


    // OnClicks

    @OnClick(R.id.btn_add_vehicle)
    public void openAddNewVehicleForm() {
        Intent addNewVehicleIntent =
                new Intent(this.getActivity(), AddVehicleActivity.class);
        addNewVehicleIntent.putExtra(Constants.EXTRA_KEY_CUSTOMER, currentCustomer);
        startActivity(addNewVehicleIntent);
    }
}
