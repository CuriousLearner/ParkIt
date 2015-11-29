package com.parkit.parkit_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.CustomerLoginResponse;
import com.parkit.parkit_client.rest.models.parkit.LoginCredentials;
import com.parkit.parkit_client.rest.models.parkit.ParkItError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

            // refresh rest client
            RestClient restClient = new RestClient();

            // form login credentials
            LoginCredentials loginCredentials = new LoginCredentials(
                    emailIDEdit.getText().toString(),
                    passwordEdit.getText().toString()
            );

            Log.d(Constants.LOG_TAG, "Login credentials : \n"+loginCredentials);

            // context
            final Context ctx = this.getApplicationContext();


            // call park it api here

            RestClient.parkItService.login(
                    Constants.PARKIT_AUTH_TOKEN,
                    loginCredentials,
                    new Callback<CustomerLoginResponse>() {
                        @Override
                        public void success(
                                CustomerLoginResponse customerLoginResponse,
                                Response response) {
                            // 200 - successful login

                            if (response.getStatus() == 200) {
                                // successful login
                                Log.d(Constants.LOG_TAG, "200 OK");
                                Log.d(Constants.LOG_TAG, "Login response : " + customerLoginResponse);

                                // success
                                SharedPreferences.Editor prefEditor =
                                        getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0)
                                                .edit();

                                prefEditor.putString(
                                        Constants.CONFIG_KEY_HASH,
                                        customerLoginResponse.QR_CODE_DATA);
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_FIRST_NAME,
                                        customerLoginResponse.first_name);
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_LAST_NAME,
                                        customerLoginResponse.last_name);
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_EMAIL,
                                        customerLoginResponse.email
                                );
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_LICENSE_LINK,
                                        customerLoginResponse.driving_licence_link);
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_CONTACT_NO,
                                        customerLoginResponse.contact_no
                                );
                                prefEditor.putString(
                                        Constants.CONFIG_KEY_ADDRESS,
                                        customerLoginResponse.address
                                );
                                prefEditor.apply();

                                Utils.showShortToast("Successfully logged in !!!", ctx);

                                // open navigation drawer
                                Intent restartIntent =
                                        new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(restartIntent);

                            } else {
                                Utils.showLongToast(
                                        "Internal Application Error !!!\n" +
                                                "Please contact ParkIt officials",
                                        ctx
                                );
                                Log.d(Constants.LOG_TAG,
                                        "Unexpected failure response code received : "
                                                + response.getStatus());
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            // 400 - missing token
                            // 401 - invalid token OR
                            //       email / pass missing
                            // 404 - Invalid credentials, email/pass combo not valid


                            if (error.getResponse() == null) {
                                Log.d(Constants.LOG_TAG, "Response is null, error kind : "
                                        + error.getKind());

                                Utils.showShortToast("Login unsuccessfull !!!", ctx);

                                return;
                            }


                            ParkItError parkItError =
                                    (ParkItError) error.getBodyAs(ParkItError.class);
                            if (parkItError == null) {
                                Log.d(Constants.LOG_TAG, "ParkIt error object is null");
                                Utils.showLongToast(
                                        "Internal Application Error !!!\n" +
                                                "Please contact ParkIt officials",
                                        ctx
                                );
                                return;
                            }

                            switch (error.getResponse().getStatus()) {

                                case 400:
                                    // token missing
                                    Utils.showLongToast(
                                            "Internal Application Error !!!\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                    Log.d(Constants.LOG_TAG,
                                            "400 NOT FOUND\n" +
                                                    "Token is missing "
                                                    + error.getResponse().getStatus());

                                    break;

                                case 401:
                                    // invalid token or email/pass missing
                                    Log.d(Constants.LOG_TAG, parkItError.toString());
                                    Utils.showLongToast(
                                            "Internal Application Error !!!\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                    if (parkItError.Message.contains("Unauthorized access")) {
                                        Log.d(Constants.LOG_TAG, "Server response : Invalid token");
                                    } else if (
                                            parkItError.Message.contains("Invalid credentials")) {
                                        Log.d(
                                                Constants.LOG_TAG,
                                                "Server response : " +
                                                        "Email / Pass missing from login request"
                                        );
                                    } else {
                                        Log.d(Constants.LOG_TAG,
                                                "Unexpected error message received from servers");
                                    }
                                    break;

                                case 404:
                                    // invalid credentials
                                    Log.d(Constants.LOG_TAG, "404 NOT FOUND");
                                    Utils.showShortToast("Invalid credentials !!!", ctx);
                                    break;
                                default:
                                    Utils.showLongToast(
                                            "Internal Application Error !!!\n" +
                                                    "Please contact ParkIt officials",
                                            ctx
                                    );
                                    Log.d(Constants.LOG_TAG,
                                            "Unexpected failure response code received : "
                                                    + error.getResponse().getStatus());
                            }
                        }
                    }
            );


            // api return customer object

            // save customer details






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
