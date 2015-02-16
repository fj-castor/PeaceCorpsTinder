package com.blueoxfords.peacecorpstinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.blueoxfords.ImageRestClient;
import com.blueoxfords.ImageService;
import com.blueoxfords.KeywordGenerator;
import com.blueoxfords.PeaceCorpsRestClient;
import com.blueoxfords.PeaceCorpsService;
import com.blueoxfords.models.Image;
import com.blueoxfords.models.KeywordPairing;
import com.blueoxfords.models.VolunteerOpening;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soheil on 15-02-14.
 */
public class TinderFragment extends Fragment {


    private CardContainer mCardContainer;
    private static Context context;
    private static Activity activity;
    private static int cardCount = 0;
    private static int startIndex = 0;
    private static int endIndex = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tinder, container, false);

        mCardContainer = (CardContainer) rootView.findViewById(R.id.layoutview);

        context = this.getActivity();
        activity = this.getActivity();

        loadCards();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void match(View view, VolunteerOpening opening) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TinderFragment.context);
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        ((LinearLayout) iv.getParent()).removeView(iv);


        builder.setTitle("You've been matched with\n " + opening.title + "!!\n");
        // Add the buttons
        builder.setPositiveButton("View matches!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Keep swiping!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setView(iv);
        builder.create().show();
    }

    public void loadCards() {
        final SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(activity);

        final HashMap<String, VolunteerOpening>idMap = new HashMap<String, VolunteerOpening>();
        final HashMap<String, Integer>scoreMap = new HashMap<String, Integer>();
        PeaceCorpsRestClient.get().getVolunteerOpening(new Callback<PeaceCorpsService.OpeningsWrapper>() {
            @Override
            public void success(PeaceCorpsService.OpeningsWrapper openingsWrapper, Response response) {

                List<VolunteerOpening> volunteerOpeningList = openingsWrapper.results.subList(startIndex, endIndex);
                List<KeywordPairing> keywordPairingList = KeywordGenerator.generate(volunteerOpeningList);

                int index = 0;
                for (KeywordPairing opening : keywordPairingList) {

                    scoreMap.put(opening.volunteerOpening.req_id, 0);
                    idMap.put(opening.volunteerOpening.req_id, opening.volunteerOpening);

                    final int indice = index;
                    final KeywordPairing pairing = opening;
                    Log.d("Pairing", pairing.volunteerOpening.title);

                    String keyword = opening.keyword;
                    Log.wtf("testingg", keyword);

                    ImageRestClient.get().getImagesFromKeyword(keyword, new Callback<ImageService.ImageResponseWrapper>() {
                        @Override
                        public void success(ImageService.ImageResponseWrapper imageResponseWrapper, Response response) {
                            List<Image> images = imageResponseWrapper.photos.photo;
                            String url = "http://i.imgur.com/DvpvklR.png";
                            if (images.size() > 0) {
                                Log.d("TESTING", images.get(0).url_m);
                                url = images.get(0).url_m;
                            }
                            final CardModel card = new CardModel(pairing.volunteerOpening.title, pairing.keyword, url, indice, pairing.volunteerOpening);
                            Log.d("PairName", pairing.volunteerOpening.title + " " + pairing.volunteerOpening.country);


                            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {

                                @Override
                                public void onDislike(View topCard) {
                                    cardCount ++;
                                    String id =  (String) ((TextView)topCard.findViewById(R.id.req_id)).getText();
                                    String url =  (String) ((TextView)topCard.findViewById(R.id.url)).getText();
                                    int curScore = scoreMap.get(id) + 1;
                                    scoreMap.put(id, curScore);
                                    Log.d("CARD SWIPED", card.opening.title + " " + card.opening.country);
                                    for (String name: scoreMap.keySet()){

                                        String key = name;
                                        String value = scoreMap.get(name).toString();
                                        Log.d("SCOREMAP",key + " " + value);


                                    }
                                    if (curScore == 3) {
                                        Log.d("OHSHIT", "YOU GOT MATCHED UP WITH "+idMap.get(id).title + " " + idMap.get(id).country);
                                        match(topCard, idMap.get(id), url);
                                    }
                                    if (cardCount >= 10) {
                                        startIndex += 5;
                                        endIndex += 5;
                                        loadCards();
                                        cardCount = 0;
                                    }
                                }

                                @Override
                                public void onLike(View topCard) {
                                      cardCount ++;
                                    if (cardCount >= 10) {
                                        startIndex += 5;
                                        endIndex += 5;
                                        loadCards();
                                        cardCount = 0;
                                    }
//                                    int curScore = scoreMap.get(card.opening);
//                                    scoreMap.put(card.opening, curScore - 1);
                                }
                            });

                            adapter.add(card);

                            mCardContainer.setAdapter(adapter);
                        }



                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public static void match(View view, final VolunteerOpening opening, String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TinderFragment.context);
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        ((LinearLayout) iv.getParent()).removeView(iv);

        ParseQuery<ParseObject> openingQ = ParseQuery.getQuery("Match");
        openingQ.whereEqualTo("opening", opening.req_id);
        openingQ.whereEqualTo("user2", null);

        final String urls = url;

        openingQ.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {

                    //Create new doc
                    ParseObject newMatch = new ParseObject("Match");
                    newMatch.put("opening", opening.req_id);
                    newMatch.put("title", opening.title);
                    newMatch.put("user1", ParseUser.getCurrentUser());
                    newMatch.put("pictureUrl", urls);
                    newMatch.put("country", opening.country);

                    newMatch.saveInBackground();

                    Log.d("score", "The getFirst request failed.");
                } else {
                    // doc exists add to user2

                    object.put("user2", ParseUser.getCurrentUser());
                    object.saveInBackground();

                    Log.d("score", "Retrieved the object.");
                }
            }
        });


        builder.setTitle("You've been matched with \n " + opening.title + "\n");
        // Add the buttons
        builder.setPositiveButton("View matches!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                
            }
        });
        builder.setNegativeButton("Keep swiping!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setView(iv);
        builder.create().show();
    }
}
