package com.parkit.parkit_client;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vikram on 20/11/15.
 */
public class Utils {


    public static void showShortToast(String message, Context context) {

        Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        ).show();

    }


    public static void showLongToast(String message, Context context) {

        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show();

    }
}
