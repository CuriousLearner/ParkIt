package com.parkit.parkit_exit_scanner.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkit.parkit_exit_scanner.Constants;
import com.parkit.parkit_exit_scanner.R;
import com.parkit.parkit_exit_scanner.Utils;
import com.parkit.parkit_exit_scanner.ui.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vikram on 15/11/15.
 */
public class ConfigurationFragment extends Fragment {


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_configuration, container, false);
            ButterKnife.bind(this, view);

        }
        return view;
    }



    // OnClicks

    @OnClick(R.id.btn_clear_config)
    public void clearScannerConfiguration() {

        SharedPreferences.Editor scannerConfigEditor =
                this.getActivity().getSharedPreferences(Constants.SHARED_PREFS_KEY, 0).edit();
        scannerConfigEditor.clear().apply();

        Utils.showShortToast("Scanner Configuration Cleared ...",
                this.getActivity().getApplicationContext());

        // restart
        Intent restartIntent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(restartIntent);

    }
}
