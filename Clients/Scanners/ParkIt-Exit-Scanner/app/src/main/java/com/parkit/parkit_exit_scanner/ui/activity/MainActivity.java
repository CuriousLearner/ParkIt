package com.parkit.parkit_exit_scanner.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parkit.parkit_exit_scanner.Constants;
import com.parkit.parkit_exit_scanner.ParkItExitScannerNavigationDrawer;
import com.parkit.parkit_exit_scanner.R;
import com.parkit.parkit_exit_scanner.Utils;
import com.parkit.parkit_exit_scanner.rest.RestClient;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // initialize rest client
        RestClient restClient = new RestClient();

        SharedPreferences scannerConfiguration =
                getSharedPreferences(Constants.SHARED_PREFS_KEY, 0);

        String parkingLotID = scannerConfiguration
                .getString(Constants.CONFIG_KEY_PARKING_LOT_ID, "");

        if(!parkingLotID.equals("")) {
            // scanner is configured

            // open navigation drawer

            Intent openNavigationDrawer = new Intent(this, ParkItExitScannerNavigationDrawer.class);
            startActivity(openNavigationDrawer);

            Utils.showShortToast("Welcome", this.getApplicationContext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    // OnClicks
    @OnClick(R.id.btn_configure_scanner)
    public void configureScanner() {
        // open configuration login
        Intent configurationLoginIntent = new Intent(this, ConfigurationLoginActivity.class);
        startActivity(configurationLoginIntent);

        Utils.showShortToast(
                "Please enter the configuration pass key, to configure the scanner",
                this.getApplicationContext());

    }


}
