package com.parkit.parkit_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.adapters.VehicleRecyclerAdapter;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.security.auth.callback.Callback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vikram on 20/11/15.
 */
public class UpdateInformationFragment extends Fragment {

    private View view;


    private Customer currentCustomer = null;
    private LayoutInflater inflater;
    private VehicleRecyclerAdapter vehicleRecyclerAdapter;


    // View Bindings
    @Bind(R.id.text_first_name)
    TextView firstNameText;

    @Bind(R.id.text_last_name)
    TextView lastNameText;

    @Bind(R.id.text_contact_number)
    TextView contactNumberText;

    @Bind(R.id.text_address)
    TextView addressText;

    @Bind(R.id.text_email_id)
    TextView emailIDText;

    @Bind(R.id.image_view_license)
    ImageView licenseImageView;


    @Bind(R.id.linear_layout_personal_details)
    LinearLayout personalDetailsLinearLayout;

    @Bind(R.id.btn_edit)
    Button editDetailsBtn;


//    @Bind(R.id.recycler_view_vehicles)
//    RecyclerView vehicleRecycler;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_update_information, container, false);
            ButterKnife.bind(this, view);
//            vehicleRecycler = (RecyclerView) view.findViewById(R.id.recycler_view_vehicles);
            this.inflater = inflater;


            /**
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


            // add default item animator
            vehicleRecycler.setItemAnimator(new DefaultItemAnimator());

            // handle scrolling for recycler view
            vehicleRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch(action) {
                        case MotionEvent.ACTION_MOVE:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

            });
             **/
            fetchDetails();
        }

        return view;
    }


    private void displayDetails(LayoutInflater layoutInflater) {

        if(currentCustomer == null) {
            Utils.showLongToast(
                    "Could not fetch account details, " +
                            "please check your network connection and try again",
                    this.getActivity().getApplicationContext());
            return;
        }

        firstNameText.setText(currentCustomer.first_name);
        lastNameText.setText(currentCustomer.last_name);
        contactNumberText.setText(currentCustomer.contact_no);
        emailIDText.setText(currentCustomer.email);
        addressText.setText(currentCustomer.address);


        // load license image
        Picasso.with(this.getActivity())
                .load(currentCustomer.driving_licence_link)
                .into(licenseImageView);

        personalDetailsLinearLayout.setVisibility(View.VISIBLE);
        editDetailsBtn.setVisibility(View.VISIBLE);

//        int adapterItemSize = 370;
//        int viewHeight = adapterItemSize * currentCustomer.vehicles.size();
//        vehicleRecycler.getLayoutParams().height = viewHeight;
//        vehicleRecycler.setAdapter(new VehicleRecyclerAdapter(currentCustomer.vehicles));

        // create a new recycler adapter and attach that
        // vehicleRecyclerAdapter.updateVehiclesData(currentCustomer.vehicles);
        //  has an implicit notifyDataSetChanged()

    }


    private void fetchDetails() {


        SharedPreferences accountDetails = this.getActivity().getSharedPreferences(
                Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");


        if(userHash.equals("")) {
            Log.d(Constants.LOG_TAG, "User hash is empty");
            Utils.showLongToast("Hash Error !!!", this.getActivity().getApplicationContext());
            return;
        }

        final Context ctx = this.getActivity().getApplicationContext();

        // call ParkIt API here
        RestClient.parkItService.getCustomer(
                Constants.PARKIT_AUTH_TOKEN,
                userHash,
                new retrofit.Callback<Customer>() {
                    @Override
                    public void success(Customer customer, Response response) {
                        // 200
                        if(response.getStatus() == 200) {
                            // successful resposnse
                            Utils.showShortToast("Data fetched", ctx);
                            Log.d(Constants.LOG_TAG, "200 OK");

                            currentCustomer = customer;

                            displayDetails(
                                    UpdateInformationFragment.this.inflater);




                        } else {

                            Utils.showShortToast("Account details could not be fetched", ctx);
                            Log.d(Constants.LOG_TAG, "Unexpected success response code : "
                                    +response.getStatus());

                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if(error.getResponse() == null) {
                            Log.d(Constants.LOG_TAG, "Response is null, error kind : "
                                    +error.getKind());
                            Utils.showShortToast("Account details could not be fetched", ctx);
                        } else {
                            if(error.getResponse().getStatus() == 404) {
                                Log.d(Constants.LOG_TAG, "404 NOT FOUND");
                                Utils.showLongToast(
                                        "Customer account not found on ParkIt servers", ctx);
                            } else {
                                Utils.showShortToast("Account details could not be fetched", ctx);
                                Log.d(Constants.LOG_TAG, "Unexpected error response code : "
                                        + error.getResponse().getStatus());
                            }
                        }
                    }
                }
        );


        // PROXY CODE
        /*
        ArrayList<Vehicle> proxyVehicles = new ArrayList<>();

        Vehicle proxyVehicle = new Vehicle("two_wheeler", "DL 09 1234",
                "http://icons.iconarchive.com/icons/iconshow/transport/256/Sportscar-car-icon.png");

        proxyVehicles.add(proxyVehicle);
        Customer proxyCustomer = new Customer(
                "Great",
                "Tester",
                "9992212345",
                "test villa",
                "http://icons.iconarchive.com/icons/iconshow/transport/256/Sportscar-car-icon.png",
                proxyVehicles
        );

        currentCustomer = proxyCustomer;
        */


    }

    // OnClicks

    @OnClick(R.id.btn_edit)
    public void openCustomerDetailsUpdateActivity() {
        Intent openCustomerDetailsUpdateActivity =
                new Intent(this.getActivity(), CustomerDetailsUpdateActivity.class);

        openCustomerDetailsUpdateActivity.putExtra(Constants.EXTRA_KEY_CUSTOMER, currentCustomer);

        startActivity(openCustomerDetailsUpdateActivity);


    }

}
