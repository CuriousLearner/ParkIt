package com.parkit.parkit_entry_scanner.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.zxing.integration.android.IntentResult;
import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.ParkItEntryScannerNavigationDrawer;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {


    private static boolean splashShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
        ButterKnife.bind(this);


        if(!splashShown) {
            splashShown = true;
            Intent showSplashScreenIntent = new Intent(this, SplashScreenActivity.class);
            startActivity(showSplashScreenIntent);
            Log.d(Constants.LOG_TAG, "Showing splash");
            return;
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
    @OnClick(R.id.btn_check_login)
    public void configureScanner() {

        // open configuration login activity
        Utils.showShortToast("Please enter the pass key to configure the scanner ...", this.getApplicationContext());
        Intent configureScannerIntent = new Intent(this, ConfigurationLogin.class);
        startActivity(configureScannerIntent);


    }


    @Override
    public void onResume() {
        super.onResume();
        // check if configured
        SharedPreferences parkItEntryScannerConfig = this.getSharedPreferences(
                Constants.SHARED_PREFERENCES_KEY, 0);

        String parkingLotIdConfig =
                parkItEntryScannerConfig.getString(Constants.CONFIG_KEY_PARKING_LOT_ID, "");

        if(!parkingLotIdConfig.equals("")) {
            // configured scanner

            // open navDrawer
            Intent openNavDrawerIntent = new Intent(this, ParkItEntryScannerNavigationDrawer.class);
            startActivity(openNavDrawerIntent);

            Utils.showShortToast("Welcome !!!", this.getApplicationContext());
        }
    }
}
