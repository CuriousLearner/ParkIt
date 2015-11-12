package com.parkit.parkit_entry_scanner;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parkit.parkit_entry_scanner.ui.fragment.ConfigurationFragment;
import com.parkit.parkit_entry_scanner.ui.fragment.QRCodeScannerFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class ParkItEntryScannerNavigationDrawer extends MaterialNavigationDrawer
        implements QRCodeScannerFragment.QRCodeScanListener {

    public static ParkItEntryScannerNavigationDrawer currentDrawer;

    public static IntentResult QRCodeScanResult;

    @Override
    public void init(Bundle bundle) {
        MaterialSection scannerSection, configSection;


        scannerSection = newSection(
                "Scanner",
                new IconDrawable(this, Iconify.IconValue.fa_barcode),
                new QRCodeScannerFragment()
        );


        configSection = newSection(
                "Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cog),
                new ConfigurationFragment()
        );


        this.addSection(scannerSection);
        this.addSection(configSection);

        this.setDefaultSectionLoaded(0);

        disableLearningPattern();

        currentDrawer = this;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constants.LOG_TAG, "In onActivity Result");
        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        onQRCodeScan(scanResult);


    }

    @Override
    public void onQRCodeScan(IntentResult scanResult) {

        if(scanResult != null) {
            // result is present

            String scanLog = "Scan Result : " + scanResult.toString();
            QRCodeScanResult = scanResult;



            // log result
            Log.d(Constants.LOG_TAG, scanLog);


            // Inform fragment
            QRCodeScannerFragment.onQRCodeScanned(scanResult);


        } else {
            Log.d(Constants.LOG_TAG, "Scan result is null");
        }


    }


}