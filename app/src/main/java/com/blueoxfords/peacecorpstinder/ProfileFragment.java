package com.blueoxfords.peacecorpstinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by soheil on 15-02-14.
 */
public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userName = (TextView) rootView.findViewById(R.id.userName);
        userName.setText((String) ParseUser.getCurrentUser().get(Constants.ParseColumns.NICKNAME));

        Button signOutButton = (Button) rootView.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                LoginActivity.start(getActivity());
            }
        });
        return rootView;
    }
}
