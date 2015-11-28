package com.parkit.parkit_client.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends ActionBarActivity {


    private static Pattern emailRegexPattern = Pattern.compile(Constants.EMAIL_REGEX_RFC_5322);

    // view bindings

    @Bind(R.id.edit_email)
    EditText emailIDEdit;

    @Bind(R.id.edit_password)
    EditText passwordEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @OnClick(R.id.btn_login)
    public void logIntoParkIt() {

        if(isInputValid()) {

            // call park it api here

            // api return customer object

            // save customer details

//        SharedPreferences.Editor prefEditor =
//                getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0)
//                        .edit();

//        Log.d(Constants.LOG_TAG, "Hash received : \n" + customer.QR_CODE_DATA);
//
//        prefEditor.putString(
//                Constants.CONFIG_KEY_HASH,
//                qrCodeResponse.QR_CODE_DATA);
//        prefEditor.putString(
//                Constants.CONFIG_KEY_FIRST_NAME,
//                customer.first_name);
//        prefEditor.putString(
//                Constants.CONFIG_KEY_LAST_NAME,
//                customer.last_name);
//        prefEditor.putString(
//                Constants.CONFIG_KEY_LICENSE_LINK,
//                customer.driving_licence_link);
//        prefEditor.putString(
//                Constants.CONFIG_KEY_RC_LINK,
//                customer.vehicles.get(0).vehicle_rc_link);
//        prefEditor.putString(
//                Constants.CONFIG_KEY_CONTACT_NO,
//                customer.contact_no
//        );
//        prefEditor.putString(
//                Constants.CONFIG_KEY_ADDRESS,
//                customer.address
//        );
//        prefEditor.apply();
//
//        showShortToast("Successfully registered !!!");


            final Context ctx = this.getApplicationContext();

            Utils.showShortToast("Successfully Logged In", ctx);




        }



    }

    private boolean isInputValid() {

        Context ctx = this.getApplicationContext();

        // empty check
        if(emailIDEdit.getText().equals("") || passwordEdit.getText().equals("")) {
            Utils.showShortToast("Please fill all fields !!!", ctx);
            return false;
        } else {
            Matcher emailMatcher = emailRegexPattern.matcher(emailIDEdit.getText().toString());
            if(!emailMatcher.matches()) {
                Utils.showShortToast("Invalid Email ID !!!", ctx);
                return false;
            } else {
                return true;
            }
        }
    }
}
