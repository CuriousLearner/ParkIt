package com.parkit.parkit_exit_scanner.ui.activity;

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

import com.parkit.parkit_exit_scanner.Constants;
import com.parkit.parkit_exit_scanner.R;
import com.parkit.parkit_exit_scanner.Utils;

import org.apache.http.cookie.CookieIdentityComparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannerConfigurationActivity extends ActionBarActivity {

    @Bind(R.id.edit_parking_lot_id)
    EditText parkingLotIdEdit;

    @Bind(R.id.spinner_vehicle_type)
    Spinner vehicleTypeSpinner;

    private String[] vehicleTypes;
    private String vehicleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_configuration);
        ButterKnife.bind(this);
        setupVehicleTypeSpinner();
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

    private void setupVehicleTypeSpinner() {

        vehicleTypes = getResources().getStringArray(R.array.vehicle_types);

        ArrayAdapter vehicleTypesAdapter = new ArrayAdapter<String>(
                this.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                vehicleTypes
        );

        vehicleTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        vehicleTypeSpinner.setAdapter(vehicleTypesAdapter);
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


    // OnClicks

    @OnClick(R.id.btn_save_config)
    public void saveScannerConfiguration() {

        Log.d(Constants.LOG_TAG, "PARKING LOT ID : " + parkingLotIdEdit.getText().toString());
        // validate inputs
        if(parkingLotIdEdit.getText().toString().equals("")) {
            Utils.showShortToast("Invalid Parking Lot ID ...", this.getApplicationContext());
            return;
        }

        String willSaveLog = "Parking Lot ID : "
                + parkingLotIdEdit.getText().toString()
                + "\nVehicle Type : " + vehicleType;

        Log.d(Constants.LOG_TAG, "Will save configuration : \n" + willSaveLog);


        SharedPreferences.Editor scannerConfigEditor =
                getSharedPreferences(Constants.SHARED_PREFS_KEY, 0).edit();

        scannerConfigEditor.putString(Constants.CONFIG_KEY_PARKING_LOT_ID,
                parkingLotIdEdit.getText().toString());

        scannerConfigEditor.putString(Constants.CONFIG_KEY_VEHICLE_TYPE,
                vehicleType);

        scannerConfigEditor.apply();

        Utils.showShortToast("Scanner Configured ...", this.getApplicationContext());

        // restart
        Intent restartIntent = new Intent(this, MainActivity.class);
        startActivity(restartIntent);
    }
}
