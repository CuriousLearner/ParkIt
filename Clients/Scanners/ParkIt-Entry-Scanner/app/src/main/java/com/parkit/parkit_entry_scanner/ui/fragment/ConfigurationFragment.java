package com.parkit.parkit_entry_scanner.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parkit.parkit_entry_scanner.Constants;
import com.parkit.parkit_entry_scanner.R;
import com.parkit.parkit_entry_scanner.Utils;
import com.parkit.parkit_entry_scanner.ui.activity.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConfigurationFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            //@TODO: form configuration fragment view
            view = inflater.inflate(R.layout.fragment_configuration, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }


    @OnClick(R.id.btn_clear_config)
    public void clearScannerConfig() {
        // clear shared preferences
        SharedPreferences.Editor configEditor =
                this.getActivity().getSharedPreferences(
                        Constants.SHARED_PREFERENCES_KEY,
                        0
                ).edit();
        configEditor.clear().apply();
        Utils.showShortToast("Scanner Configuration Cleared ...",
                this.getActivity().getApplicationContext());

        // restart
        Intent restartIntent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(restartIntent);

    }
}
