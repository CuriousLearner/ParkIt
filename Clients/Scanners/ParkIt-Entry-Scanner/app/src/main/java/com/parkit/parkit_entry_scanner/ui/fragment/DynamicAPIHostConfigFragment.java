package com.parkit.parkit_entry_scanner.ui.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;
import com.parkit.parkit_entry_scanner.rest.RestClient;
import com.parkit.parkit_entry_scanner.ui.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DynamicAPIHostConfigFragment extends Fragment {


    private View view;

    // view bindings
    @Bind(R.id.edit_api_host_address)
    EditText parkItAPIHostAddressEdit;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null) {

            view = inflater.inflate(R.layout.fragment_dynamic_apihost_config, container, false);
            ButterKnife.bind(this, view);


        }
        return view;
    }


    // OnClicks

    @OnClick(R.id.btn_refresh_parkit_api_service)
    public void refreshAPIHostConfig() {

        if(parkItAPIHostAddressEdit.getText().toString().equals("")) {
            Utils.showShortToast(
                    "Invalid host address !!!",
                    this.getActivity().getApplicationContext()
            );
            return;
        }

        String baseURL = "http://"+parkItAPIHostAddressEdit.getText().toString()+":5000";

        Constants.PARKIT_API_HOST_ADDRESS = baseURL;

        // refresh API
        RestClient restClient = new RestClient();

        Utils.showShortToast(
                "API Host configuration refreshed !!!",
                this.getActivity().getApplicationContext()
        );

        // restart
        Intent restartIntent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(restartIntent);



    }

}
