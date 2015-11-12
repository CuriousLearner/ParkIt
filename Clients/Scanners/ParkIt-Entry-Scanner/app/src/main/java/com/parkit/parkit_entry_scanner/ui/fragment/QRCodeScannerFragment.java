package com.parkit.parkit_entry_scanner.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class QRCodeScannerFragment extends Fragment {

    public static final String LOG_TAG = "Message : ";



    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_qrcode_scanner, container, false);
            ButterKnife.bind(this, view);




        }


        return view;
    }


    // OnClicks
    @OnClick(R.id.btn_scan_qr_code)
    public void scanQRCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                QRCodeScannerFragment.this.getActivity());
        Log.d(LOG_TAG, "Initiating scan");
        intentIntegrator
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setCameraId(0)
                .setPrompt("Scan Customer's QR Code")
                .setBeepEnabled(true)
                .initiateScan();
    }


    public interface QRCodeScanListener {

        public void onQRCodeScan(IntentResult result);
    }

    @Override
    public void onAttach(Activity activity) throws ClassCastException{
        super.onAttach(activity);
        QRCodeScanListener qrCodeScanListener;
        try {
            qrCodeScanListener = (QRCodeScanListener) activity;
        } catch(ClassCastException cce) {
            throw new ClassCastException(activity.getPackageName()
                    + " must implement QRCodeScanListener interface");
        }

    }

    public static void onQRCodeScanned(IntentResult result) {
        // call ParkIt API here
        Log.d(Constants.LOG_TAG, "Registering on ParkIt with hash : " + result.getContents());


        

    }



}
