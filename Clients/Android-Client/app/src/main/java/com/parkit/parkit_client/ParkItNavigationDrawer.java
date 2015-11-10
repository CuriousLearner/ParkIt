package com.parkit.parkit_client;

import android.os.Bundle;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parkit.parkit_client.ui.AccountFragment;
import com.parkit.parkit_client.ui.QRCodeGenFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by vikram on 8/11/15.
 */
public class ParkItNavigationDrawer extends MaterialNavigationDrawer {


    static ParkItNavigationDrawer currentDrawer;

    @Override
    public void init(Bundle savedInstanceState) {
        MaterialSection accountSection  = newSection(
                "Account",
                new IconDrawable(this, Iconify.IconValue.fa_money),
                new AccountFragment()
        );

        MaterialSection qrCodeGenSection  = newSection(
                "Park / Pay",
                new IconDrawable(this, Iconify.IconValue.fa_barcode),
                new QRCodeGenFragment()
        );

        this.addSection(accountSection);

        this.addSection(qrCodeGenSection);



        this.setDefaultSectionLoaded(0);
        disableLearningPattern();


        currentDrawer = this;
    }
}
