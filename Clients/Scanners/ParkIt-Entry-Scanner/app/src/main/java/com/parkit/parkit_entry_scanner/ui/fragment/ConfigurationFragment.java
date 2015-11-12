package com.parkit.parkit_entry_scanner.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkit.parkit_entry_scanner.R;


public class ConfigurationFragment extends Fragment {

    private View view;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            //@TODO: form configuration fragment view
            view = inflater.inflate(R.layout.fragment_configuration, container, false);

        }


        return view;
    }
}
