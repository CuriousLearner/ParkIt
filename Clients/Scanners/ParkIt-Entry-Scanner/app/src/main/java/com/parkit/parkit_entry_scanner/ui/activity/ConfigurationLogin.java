package com.parkit.parkit_entry_scanner.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigurationLogin extends ActionBarActivity {


    @Bind(R.id.edit_config_pass_key)
    EditText configPassKeyEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_login);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration_login, menu);
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


    @OnClick(R.id.btn_config_login)
    void verifyLoginKey() {
        if(!configPassKeyEdit.getText().toString().equals(Constants.CONFIG_PASS_KEY))
            Utils.showShortToast("Invalid Configuration Pass Key !!!",
                    this.getApplicationContext());
        else {
            Utils.showShortToast("Valid Pass Key\nPlease configure the scanner ...",
                    this.getApplicationContext());
            // open configuration activity
            Intent configureScannerIntent =
                    new Intent(this, ScannerConfigurationActivity.class);
            startActivity(configureScannerIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // clear pass key field
        configPassKeyEdit.getText().clear();
    }
}
