package com.parkit.parkit_exit_scanner.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parkit.parkit_exit_scanner.Constants;
import com.parkit.parkit_exit_scanner.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vikram on 15/11/15.
 */
public class QRCodeScannerFragment extends Fragment {



    public static QRCodeScannerFragment currentFragment;

    private View view;


    public static QRCodeScannerFragment getCurrentFragmentInstance() { return currentFragment;  }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            //@TODO:Form Scanner Fragment View
            view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);
            ButterKnife.bind(this, view);


        }



        currentFragment = this;
        return view;
    }



    // OnClicks

    @OnClick(R.id.btn_scan_qr_code)
    public void scanQRCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                QRCodeScannerFragment.this.getActivity());
        Log.d(Constants.LOG_TAG, "Initiating scan");
        intentIntegrator
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setCameraId(0)
                .setPrompt("Scan Customer's QR Code")
                .setBeepEnabled(true)
                .initiateScan();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        QRCodeScanListener qrCodeScanListener;
        try {
            qrCodeScanListener = (QRCodeScanListener) activity;
        } catch(ClassCastException cce) {
            throw new ClassCastException(activity.getPackageName() +
                    "must implement QRCodeScanListener interface.");
        }
    }

    // interface for fragment - activity communication
    public interface QRCodeScanListener {

        public void onQRCodeScan(IntentResult result);

    }


    public void onQRCodeScanned(IntentResult result) {
        // call parkit exit API
        Log.d(Constants.LOG_TAG,
                "Scan Log : " +
                "\nFormat Name : " + result.getFormatName() +
                "\nContents : " + result.getContents() +
                "\nRaw Bytes : " + result.getRawBytes() +
                "\nError Correction Level : " + result.getErrorCorrectionLevel() +
                "\nOrientation : " + result.getOrientation() +
                "\nString Representation : " + result.toString()
        );
        Log.d(Constants.LOG_TAG, "Will call exit API with hash : "+result.getContents());

    }







}
