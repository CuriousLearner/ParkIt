package com.parkit.parkit_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.ui.LoginActivity;
import com.parkit.parkit_client.ui.RegistrationActivity;
import com.parkit.parkit_client.ui.SplashScreenActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static boolean splashShown = false;

    @Bind(R.id.btn_register)
    Button registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
        ButterKnife.bind(this);
        // init rest client
        RestClient restClient = new RestClient();

        if(!splashShown) {
            Intent showSplashIntent = new Intent(this, SplashScreenActivity.class);
            Log.d(Constants.LOG_TAG, "Showing splash");
            this.startActivity(showSplashIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!splashShown) {
            splashShown = true;
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("parkit", 0);
        String hash = sharedPreferences.getString("hash", "");
        if(!hash.equals("")) {
            Toast.makeText(
                    this.getApplicationContext(),
                    "Welcome to ParkIt",
                    Toast.LENGTH_SHORT).show();
            Intent openNavDrawer = new Intent(this, ParkItNavigationDrawer.class);
            startActivity(openNavDrawer);
        }
        // init rest client
        RestClient restClient = new RestClient();
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



    @OnClick(R.id.btn_register)
    public void openRegistrationActivity() {
        Intent openRegistrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(openRegistrationActivity);
    }

    @OnClick(R.id.btn_login)
    public void openLoginActivity() {
        Intent openRegistrationActivity = new Intent(this, LoginActivity.class);
        startActivity(openRegistrationActivity);
    }


}
