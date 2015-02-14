package com.blueoxfords.peacecorpstinder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueoxfords.RestClient;
import com.blueoxfords.models.VolunteerOpening;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soheil on 15-02-14.
 */
public class OpeningDetailsFragment extends Fragment {

    public static OpeningDetailsFragment newInstance(String req_id, String match_id) {
        OpeningDetailsFragment fragment = new OpeningDetailsFragment();

        Bundle args = new Bundle();
        args.putString("req_id", req_id);
        args.putString("match_id", match_id);
        fragment.setArguments(args);

        return fragment;
    }

    private VolunteerOpening opening;

    private ParseObject match;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        try {
            match = query.get(getArguments().getString("match_id"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_opening_details, container, false);

        Picasso.with(getActivity()).load(match.getString("pictureUrl")).into((ImageView) view.findViewById(R.id.imageView));

        RestClient.get().getVolunteerOpening(getArguments().getString("req_id"), new Callback<VolunteerOpening>() {
            @Override
            public void success(VolunteerOpening volunteerOpening, Response response) {
                opening = volunteerOpening;
                ((TextView) view.findViewById(R.id.title)).setText(opening.title);
                ((TextView) view.findViewById(R.id.description)).setText(opening.project_description);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
