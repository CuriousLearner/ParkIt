package com.parkit.parkit_client;

/**
 * Created by vikram on 19/11/15.
 */
public class Constants {

    public static final String PARKIT_AUTH_TOKEN =
            "WyIxIiwiY2UwZWY0MDFjYTA3MmJlODcyODkzYjYxOGQzZjk4YzUiXQ.B5e5Sg.qcsDcaMgiRqx21YTC0OwwnihINM";

    public static final String CONFIG_KEY_HASH = "hash";
    public static final String CONFIG_KEY_FIRST_NAME = "first_name";
    public static final String CONFIG_KEY_LAST_NAME = "last_name";
    public static final String CONFIG_KEY_LICENSE_LINK = "license_link";
    public static final String CONFIG_KEY_CONTACT_NO = "contact_no";
    public static final String CONFIG_KEY_ADDRESS = "address";
    public static final String CONFIG_KEY_RC_LINK = "rc_link";
    public static final String CONFIG_KEY_BALANCE = "balance";
    public static final String CONFIG_KEY_EMAIL = "email";
    public static final String KEY_SHARED_PREFERENCES = "parkit";
    public static final String LOG_TAG = "Message : ";
    public static final String EXTRA_KEY_CUSTOMER = "currentCustomer";
    public static final int SPLASH_DELAY_SECONDS = 2;
    public static final String EMAIL_REGEX_RFC_5322 =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static final String LICENSE_PLATE_NUMBER_REGEX = "^[A-Z]{1,3}-[A-Z]{1,2}-[0-9]{1,4}$";


    // variable
    public static String PARKIT_HOST_ADDRESS = "http://192.168.1.2:5000";



}
