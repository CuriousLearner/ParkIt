package com.parkit.parkit_exit_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parkit.parkit_exit_scanner.ui.fragment.ConfigurationFragment;
import com.parkit.parkit_exit_scanner.ui.fragment.DynamicAPIHostConfigFragment;
import com.parkit.parkit_exit_scanner.ui.fragment.QRCodeScannerFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by vikram on 15/11/15.
 */
public class ParkItExitScannerNavigationDrawer extends MaterialNavigationDrawer implements
        QRCodeScannerFragment.QRCodeScanListener {




    @Override
    public void init(Bundle bundle) {

        // setup custom header
        View customerHeaderView = LayoutInflater.from(this)
                .inflate(R.layout.custom_header_layout, null);
        this.setDrawerHeaderCustom(customerHeaderView);

        MaterialSection scannerSection = newSection(
                "Exit Scanner",
                new IconDrawable(this, Iconify.IconValue.fa_barcode),
                new QRCodeScannerFragment()
        );

        MaterialSection configurationSection = newSection(
                "Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cog),
                new ConfigurationFragment()
        );


        MaterialSection dynamicAPIHostConfigSection = newSection(
                "API Host Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cogs),
                new DynamicAPIHostConfigFragment()
        );


        this.addSection(scannerSection);                // section number : 0

        this.addSection(configurationSection);          // section number : 1

        this.addSection(dynamicAPIHostConfigSection);   // section number : 2


        this.setDefaultSectionLoaded(0);

        disableLearningPattern();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constants.LOG_TAG, "in navigation drawer's onActivityResult");
        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        onQRCodeScan(scanResult);
    }

    @Override
    public void onQRCodeScan(IntentResult result) {
        // called by onActivityResult
        if(result != null) {
            Log.d(Constants.LOG_TAG, "Scan Result : "+result.toString());

            QRCodeScannerFragment.getCurrentFragmentInstance().onQRCodeScanned(result);

        } else {
            Log.d(Constants.LOG_TAG, "Scan result is null");
        }
    }
}
