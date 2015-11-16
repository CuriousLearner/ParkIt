package com.parkit.parkit_exit_scanner;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vikram on 15/11/15.
 */
public class Utils {

    public static void showShortToast(String message, Context ctx) {
        Toast.makeText(
                ctx,
                message,
                Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context ctx) {
        Toast.makeText(
                ctx,
                message,
                Toast.LENGTH_LONG).show();
    }

}
