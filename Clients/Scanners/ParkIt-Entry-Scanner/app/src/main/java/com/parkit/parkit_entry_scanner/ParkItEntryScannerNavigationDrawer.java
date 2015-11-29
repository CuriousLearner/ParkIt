package com.parkit.parkit_entry_scanner;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parkit.parkit_entry_scanner.ui.fragment.ConfigurationFragment;
import com.parkit.parkit_entry_scanner.ui.fragment.DynamicAPIHostConfigFragment;
import com.parkit.parkit_entry_scanner.ui.fragment.QRCodeScannerFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class ParkItEntryScannerNavigationDrawer extends MaterialNavigationDrawer
        implements QRCodeScannerFragment.QRCodeScanListener {

    public static ParkItEntryScannerNavigationDrawer currentDrawer;

    public static IntentResult QRCodeScanResult;

    @Override
    public void init(Bundle bundle) {

        // setup header
        View customHeaderView = LayoutInflater.from(this)
                .inflate(R.layout.custom_header_layout, null);
        this.setDrawerHeaderCustom(customHeaderView);

        MaterialSection scannerSection, configSection, dynamicAPIHostConfigSection;


        scannerSection = newSection(
                "Entry Scanner",
                new IconDrawable(this, Iconify.IconValue.fa_barcode),
                new QRCodeScannerFragment()
        );


        configSection = newSection(
                "Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cog),
                new ConfigurationFragment()
        );

        dynamicAPIHostConfigSection = newSection(
                "API Host Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cogs),
                new DynamicAPIHostConfigFragment()
        );


        this.addSection(scannerSection);
        this.addSection(configSection);
        this.addSection(dynamicAPIHostConfigSection);

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
            QRCodeScannerFragment.getCurrentFragmentInstance().onQRCodeScanned(scanResult);


        } else {
            Log.d(Constants.LOG_TAG, "Scan result is null");
        }


    }


}