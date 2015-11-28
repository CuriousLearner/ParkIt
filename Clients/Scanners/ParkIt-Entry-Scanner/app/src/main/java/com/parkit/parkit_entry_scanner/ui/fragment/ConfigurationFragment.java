package com.parkit.parkit_entry_scanner.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;
import com.parkit.parkit_entry_scanner.ui.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConfigurationFragment extends Fragment {

    private View view;


    @Bind(R.id.text_parking_lot_id)
    TextView parkingLotIDText;

    @Bind(R.id.text_vehicle_type)
    TextView vehicleTypeText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            //@TODO: form configuration fragment view
            view = inflater.inflate(R.layout.fragment_configuration, container, false);
            ButterKnife.bind(this, view);
            showConfig();
        }
        return view;
    }



    private void showConfig() {

        SharedPreferences scannerConfig =
                this.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);

        String parkingLotID = scannerConfig
                .getString(Constants.CONFIG_KEY_PARKING_LOT_ID, "not configured");
        String vehicleType = scannerConfig
                .getString(Constants.CONFIG_KEY_VEHICLE_TYPE, "not configured");


        if(!vehicleType.equals("not configured")) {
            String vehicleTypeViewRep = vehicleType;

            String selectedVehicleTypeModel = vehicleType;



            // initialize view and model values

            String[] vehicleTypesView = getResources().getStringArray(R.array.vehicle_type_views);
            String[] vehicleTypesModel = getResources().getStringArray(R.array.vehicle_type_models);

            String  twoWheelerModel = vehicleTypesModel[0],
                    fourWheelerModel = vehicleTypesModel[1],
                    heavyVehicleModel = vehicleTypesModel[2],
                    twoWheelerView = vehicleTypesView[0],
                    fourWheelerView = vehicleTypesView[1],
                    heavyVehicleView = vehicleTypesView[2];

            if(selectedVehicleTypeModel.equals(twoWheelerModel)) {
                vehicleTypeViewRep = twoWheelerView;
            } else if(selectedVehicleTypeModel.equals(fourWheelerModel)) {
                vehicleTypeViewRep = fourWheelerView;
            } else if(selectedVehicleTypeModel.equals(heavyVehicleModel)) {
                vehicleTypeViewRep = heavyVehicleView;
            }

            vehicleType = vehicleTypeViewRep;
        }


        parkingLotIDText.setText(
                "Parking Lot ID : " + parkingLotID
        );

        vehicleTypeText.setText(
                "Vehicle Type : " + vehicleType
        );
    }

    @OnClick(R.id.btn_clear_config)
    public void clearScannerConfig() {
        // clear shared preferences
        SharedPreferences.Editor configEditor =
                this.getActivity().getSharedPreferences(
                        Constants.SHARED_PREFERENCES_KEY,
                        0
                ).edit();
        configEditor.clear().apply();
        Utils.showShortToast("Scanner Configuration Cleared ...",
                this.getActivity().getApplicationContext());

        // restart
        Intent restartIntent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(restartIntent);

    }
}
