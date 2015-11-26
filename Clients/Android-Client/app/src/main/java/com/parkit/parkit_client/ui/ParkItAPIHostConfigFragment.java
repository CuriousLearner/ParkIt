package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.services.ParkItService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ParkItAPIHostConfigFragment extends Fragment {

    private View view;

    // view bindings

    @Bind(R.id.edit_api_host_address)
    EditText parkItAPIHostAddressEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            // form view
            view = inflater.inflate(R.layout.fragment_park_it_apihost_config, container, false);
            ButterKnife.bind(this, view);

        }
        return view;
    }

    // OnClicks

    @OnClick(R.id.btn_refresh_parkit_api_service)
    public void refreshParkItAPIService() {
        // validate
        if(parkItAPIHostAddressEdit.getText().toString().equals("")) {
            Utils.showShortToast(
                    "Invalid host address",
                    this.getActivity().getApplicationContext()
            );
        } else {
            RestClient.refreshParkItService(parkItAPIHostAddressEdit.getText().toString());
            Utils.showShortToast(
                    "API Host Configured !!!",
                    this.getActivity().getApplicationContext()
            );
            // restart
            Intent restartIntent = new Intent(this.getActivity(), MainActivity.class);
            startActivity(restartIntent);
        }
    }

}
