package com.blueoxfords.peacecorpstinder.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

    AlertDialog dialog;

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

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.about_text)
                .setTitle("About");
        // 3. Get the AlertDialog from create()
        dialog = builder.create();

        TextView aboutButton = (TextView) rootView.findViewById(R.id.about);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        return rootView;
    }
}
