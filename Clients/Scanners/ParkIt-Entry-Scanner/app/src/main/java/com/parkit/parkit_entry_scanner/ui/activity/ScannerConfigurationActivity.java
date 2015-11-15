package com.parkit.parkit_entry_scanner.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannerConfigurationActivity extends ActionBarActivity {

    private String[] vehicleTypes;
    private String vehicleType;

    @Bind(R.id.edit_parking_lot_id)
    EditText parkingLotIdEdit;

    @Bind(R.id.spinner_vehicle_type)
    Spinner vehicleTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_configuration);
        ButterKnife.bind(this);
        setUpVehicleTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanner_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btn_save_config)
    void saveConfiguration() {

        // form validation
        if(parkingLotIdEdit.getText().toString().equals("")) {
            Utils.showShortToast("Invalid Parking Lot ID", this.getApplicationContext());
            return;
        } else if(vehicleType == null) {
            Utils.showShortToast("Please select a vehicle type", this.getApplicationContext());
            return;
        }

        String willSaveLog = "Parking Lot ID : "+parkingLotIdEdit.getText().toString() +
                "\nVehicle Type : "+vehicleType;

        Log.d(Constants.LOG_TAG, "Will save following configuration : \n"+willSaveLog);


        // valid form fields
        SharedPreferences scannerConfig = getSharedPreferences(
                Constants.SHARED_PREFERENCES_KEY, 0);

        SharedPreferences.Editor scannerConfigEditor = scannerConfig.edit();

        // store parking lot id
        scannerConfigEditor.putString(
                Constants.CONFIG_KEY_PARKING_LOT_ID,
                parkingLotIdEdit.getText().toString());

        // store vehicle type
        scannerConfigEditor.putString(
                Constants.CONFIG_KEY_VEHICLE_TYPE,
                vehicleType);

        scannerConfigEditor.apply();

        Utils.showShortToast("Scanner Configured !!!", this.getApplicationContext());

        // restart app
        Intent restartIntent = new Intent(this, MainActivity.class);
        startActivity(restartIntent);
    }


    private void setUpVehicleTypeSpinner() {
        vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<String>(
                this.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                vehicleTypes
        );
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



}
