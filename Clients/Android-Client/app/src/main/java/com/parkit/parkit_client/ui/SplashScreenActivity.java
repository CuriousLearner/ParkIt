package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;


public class SplashScreenActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        Handler hideSplashHandler = new Handler();
        hideSplashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // delayed hide
                Intent hideSplashIntent =
                        new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(hideSplashIntent);
                SplashScreenActivity.this.finish();
                Log.d(Constants.LOG_TAG, "hiding splash after delay");
            }
        }, Constants.SPLASH_DELAY_SECONDS * 1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
}
