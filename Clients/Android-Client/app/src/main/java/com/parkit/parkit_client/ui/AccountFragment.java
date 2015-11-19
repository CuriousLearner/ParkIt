package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.ParkItNavigationDrawer;
import com.parkit.parkit_client.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountFragment extends Fragment {


    Button dummyBtn;
    TextView welcomeView;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_account, container, false);
            Button dummyBtn = (Button) view.findViewById(R.id.btn_dummy);
            welcomeView = (TextView) view.findViewById(R.id.welcome_view);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("parkit", 0);
            String name = sharedPreferences.getString("first_name", "");
            String hash = sharedPreferences.getString("hash", "");
            String licenseLink = sharedPreferences.getString("license_link", "");
            String rcLink = sharedPreferences.getString("rc_link", "");
            if(!name.equals("")) {
                welcomeView.setText("Welcome, " + name);
                if(!(   licenseLink.equals("") &&
                        hash.equals("") &&
                        rcLink.equals(""))) {
                    welcomeView.setText(welcomeView.getText().toString() +
                            "\nLicense link : "+licenseLink +
                            "\nHash : " + hash + "\nRCLink : " + rcLink

                    );
                }
            }
            dummyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("parkit", 0).edit();
                    editor.clear();
                    editor.apply();


                    Toast.makeText(
                            AccountFragment.this.getActivity(),
                            "Logged out !!!",
                            Toast.LENGTH_SHORT).show();


                    Intent restart = new Intent(AccountFragment.this.getActivity(), MainActivity.class);
                    startActivity(restart);

                }
            });
        }
        return view;
    }

}


