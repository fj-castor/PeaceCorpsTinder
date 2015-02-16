package com.blueoxfords.peacecorpstinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueoxfords.peacecorpstinder.Constants;
import com.blueoxfords.peacecorpstinder.R;
import com.blueoxfords.peacecorpstinder.activities.LoginActivity;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by soheil on 15-02-14.
 */
public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        ParseUser currentUser = ParseUser.getCurrentUser();
        TextView userName = (TextView) rootView.findViewById(R.id.userName);
        userName.setText((String) currentUser.get(Constants.ParseColumns.NICKNAME));
        Picasso.with(getActivity()).load(currentUser.getString(Constants.ParseColumns.PICTURE_URL)).into((ImageView) rootView.findViewById(R.id.profile));
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
