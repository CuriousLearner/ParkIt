package com.parkit.parkit_client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parkit.parkit_client.ui.AccountFragment;
import com.parkit.parkit_client.ui.ParkItAPIHostConfigFragment;
import com.parkit.parkit_client.ui.ParkItWalletFragment;
import com.parkit.parkit_client.ui.QRCodeGenFragment;
import com.parkit.parkit_client.ui.UpdateInformationFragment;
import com.parkit.parkit_client.ui.VehiclesFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by vikram on 8/11/15.
 */
public class ParkItNavigationDrawer extends MaterialNavigationDrawer {


    static ParkItNavigationDrawer currentDrawer;

    @Override
    public void init(Bundle savedInstanceState) {

        // setup custom header
        View customHeaderView = LayoutInflater.from(this)
                .inflate(R.layout.custom_header_layout, null);
        this.setDrawerHeaderCustom(customHeaderView);


        MaterialSection accountSection  = newSection(
                "My Account",
                new IconDrawable(this, Iconify.IconValue.fa_cogs),
                new AccountFragment()
        );

        MaterialSection qrCodeGenSection  = newSection(
                "Park / Pay",
                new IconDrawable(this, Iconify.IconValue.fa_barcode),
                new QRCodeGenFragment()
        );

        MaterialSection eWalletSection = newSection(
                "ParkIt eWallet",
                new IconDrawable(this, Iconify.IconValue.fa_money),
                new ParkItWalletFragment()
        );


        MaterialSection updateInformationSection = newSection(
                "Update Info",
                new IconDrawable(this, Iconify.IconValue.fa_info),
                new UpdateInformationFragment()
        );

        MaterialSection myVehiclesSection = newSection(
                "My Vehicles",
                new IconDrawable(this, Iconify.IconValue.fa_car),
                new VehiclesFragment()
        );

        MaterialSection dynamicAPIHostConfigSection = newSection(
                "API Host Configuration",
                new IconDrawable(this, Iconify.IconValue.fa_cog),
                new ParkItAPIHostConfigFragment()
        );


        this.addSection(qrCodeGenSection);

        this.addSection(eWalletSection);

        this.addSection(updateInformationSection);

        this.addSection(myVehiclesSection);

        this.addSection(dynamicAPIHostConfigSection);

        this.addSection(accountSection);

        this.setDefaultSectionLoaded(0);
        disableLearningPattern();


        currentDrawer = this;
    }
}
