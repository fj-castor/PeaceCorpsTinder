package com.blueoxfords.peacecorpstinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueoxfords.peacecorpstinder.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by soheil on 15-02-14.
 */
public class ChatFragment extends Fragment {

    public static ChatFragment newInstance(String match_id) {
        ChatFragment fragment = new ChatFragment();

        Bundle args = new Bundle();
        args.putString("match_id", match_id);
        fragment.setArguments(args);

        return fragment;
    }

    private ParseObject match;

    private ParseObject user;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        try {
            match = query.get(getArguments().getString("match_id"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseUser user1 = (ParseUser) match.get("user1");
        ParseUser user2 = (ParseUser) match.get("user2");

        user = user1.equals(ParseUser.getCurrentUser()) ? user2 : user1;

        try {
            user.fetchIfNeeded();
            Picasso.with(getActivity()).load(user.getString("pictureUrl")).into((ImageView) view.findViewById(R.id.profile));
            ((TextView) view.findViewById(R.id.textView)).setText("You matched with " + user.getString("firstName"));
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "error getting user", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return view;
    }
}
