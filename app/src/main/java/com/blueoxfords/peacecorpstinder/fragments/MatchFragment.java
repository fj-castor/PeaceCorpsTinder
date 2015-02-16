package com.blueoxfords.peacecorpstinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blueoxfords.peacecorpstinder.R;
import com.blueoxfords.peacecorpstinder.activities.MatchActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Soheil Koushan on 2014-08-27.
 */
public class MatchFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private FeedAdapter adapter;
    private Queue<DoneInflatingListener> doneInflatingListeners = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new FeedAdapter(getActivity());
        setListAdapter(adapter);

        doneInflatingListeners.add(new DoneInflatingListener() {
            @Override
            public void done() {
                swipeLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    private interface DoneInflatingListener {
        void done();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(
                R.color.red,
                R.color.tab_grey,
                R.color.red,
                R.color.tab_grey);

        while (doneInflatingListeners.size() != 0) {
            doneInflatingListeners.element().done();
            doneInflatingListeners.remove();
        }

        return rootView;
    }

    static class ViewHolder {
        TextView broadcastName;
        TextView broadcastDistance;
        ImageView broadcastImage;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MatchActivity.start(getActivity(), adapter.getItem(position).getObjectId());
    }

    private class FeedAdapter extends BaseAdapter implements ListAdapter {

        private final Context context;

        List<ParseObject> matches = new ArrayList<>();

        FeedAdapter(Context c) {
            this.context = c;
        }

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public ParseObject getItem(int i) {
            return matches.get(i);
        }

        @Override
        public long getItemId(int i) {
            return matches.get(i).getObjectId().hashCode();
        }

        public void refresh() {
            ParseQuery<ParseObject> lotsOfWins = ParseQuery.getQuery("Match");
            lotsOfWins.whereEqualTo("user1", ParseUser.getCurrentUser());

            ParseQuery<ParseObject> fewWins = ParseQuery.getQuery("Match");
            fewWins.whereEqualTo("user2", ParseUser.getCurrentUser());

            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(lotsOfWins);
            queries.add(fewWins);

            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> results, ParseException e) {
                    if (e == null) {
                        matches.clear();

                        matches.addAll(results);

                        notifyDataSetChanged();
                        swipeLayout.setRefreshing(false);
                    }
                }
            });
//            mainActivity.getLocation(new LocationCallback() {
//                @Override
//                public void done(final ParseGeoPoint location, ParseException e) {
//                    HashMap<String, Object> params = new HashMap<String, Object>();
//                    params.put("location", location);
//                    ParseCloud.callFunctionInBackground("getNearbyBroadcasts", params, new FunctionCallback<Object>() {
//                        @Override
//                        public void done(Object o, ParseException e) {
//                            if (e == null) {
//                                matches.clear();
//                                distances.clear();
//
//                                ArrayList<ParseObject> fetchedBroadcasts = (ArrayList<ParseObject>) o;
//                                for (ParseObject broadcast : fetchedBroadcasts) {
//                                    matches.add(broadcast);
//                                    ParseGeoPoint p = broadcast.getParseGeoPoint("location");
//                                    if (p != null && location != null) {
//                                        distances.put(broadcast, location.distanceInKilometersTo(p));
//                                    }
//                                }
//
//                                notifyDataSetChanged();
//                                swipeLayout.setRefreshing(false);
//                            }
//                        }
//                    });
//
//                }
//            });

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_match, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.broadcastName = (TextView) convertView.findViewById(R.id.broadcast_name);
                viewHolder.broadcastImage = (ImageView) convertView.findViewById(R.id.broadcast_image);
                viewHolder.broadcastDistance = (TextView) convertView.findViewById(R.id.broadcast_distance);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.broadcastName.setText(getItem(position).getString("title"));
            String url = getItem(position).getString("pictureUrl");
            Picasso.with(getActivity()).load(url).into(viewHolder.broadcastImage);

            String country = getItem(position).getString("country");
            String countryCapped = Character.toUpperCase(country.charAt(0)) + country.substring(1);
            viewHolder.broadcastDistance.setText(countryCapped);
//            Double distance = distances.get(getItem(position));
//            if (distance != null) {
//                if (distance < 0.05) {
//                    viewHolder.broadcastDistance.setText("At current location");
//                } else {
//                    viewHolder.broadcastDistance.setText(String.format("%.2f km away", distance));
//                }
//            } else {
//                viewHolder.broadcastDistance.setText("");
//            }
//
//            List<ParseFile> images = getItem(position).getList("images");
//            if (images != null && images.size() > 0) {
//                ParseFile firstImage = images.get(0);
//                if (firstImage != null) {
//                    firstImage.getDataInBackground(new GetDataCallback() {
//                        public void done(byte[] data, ParseException e) {
//                            if (e == null) {
//                                Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                viewHolder.broadcastImage.setImageBitmap(bMap);
//                            }
//                        }
//                    });
//                }
//            }

            return convertView;
        }
    }

    @Override
    public void onRefresh() {
        adapter.refresh();
    }
}
