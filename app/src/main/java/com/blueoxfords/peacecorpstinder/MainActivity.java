package com.blueoxfords.peacecorpstinder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.blueoxfords.ImageRestClient;
import com.blueoxfords.ImageService;
import com.blueoxfords.KeywordGenerator;
import com.blueoxfords.PeaceCorpsService;
import com.blueoxfords.RestClient;
import com.blueoxfords.models.Image;
import com.blueoxfords.models.KeywordPairing;
import com.blueoxfords.models.VolunteerOpening;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity {
    private CardContainer mCardContainer;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);

        final SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

        final HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
        final HashMap<String, VolunteerOpening> idMap = new HashMap<String, VolunteerOpening>();

        context = this;

        RestClient.get().getVolunteerOpening(new Callback<PeaceCorpsService.OpeningsWrapper>() {
            @Override
            public void success(PeaceCorpsService.OpeningsWrapper openingsWrapper, Response response) {

                List<VolunteerOpening> volunteerOpeningList = openingsWrapper.results.subList(0, 5);
                List<KeywordPairing> keywordPairingList = KeywordGenerator.generate(volunteerOpeningList);

                int index = 0;
                for (KeywordPairing opening : keywordPairingList) {

                    scoreMap.put(opening.volunteerOpening.req_id, 0);
                    idMap.put(opening.volunteerOpening.req_id, opening.volunteerOpening);

                    final int indice = index;
                    final KeywordPairing pairing = opening;
                    Log.d("Pairing", pairing.volunteerOpening.title);

                    ImageRestClient.get().getImagesFromKeyword(opening.keyword, new Callback<ImageService.ImageResponseWrapper>() {
                        @Override
                        public void success(ImageService.ImageResponseWrapper imageResponseWrapper, Response response) {
                            List<Image> images = imageResponseWrapper.responseData.results;
                            String url = "http://i.imgur.com/DvpvklR.png"; // Default image
                            if (images.size() > 0) {
                                Log.d("URLTESTING", images.get(0).url);
                                url = images.get(0).url;
                            }
                            final CardModel card = new CardModel(pairing.volunteerOpening.title, pairing.keyword, url, indice, pairing.volunteerOpening);
                            Log.d("PairName", pairing.volunteerOpening.title + " " + pairing.volunteerOpening.country);


                            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {

                                @Override
                                public void onDislike(View topCard) {
                                    String id =  (String) ((TextView)topCard.findViewById(R.id.req_id)).getText();
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
                                        match(topCard, idMap.get(id));
                                    }
                                }

                                @Override
                                public void onLike(View topCard) {
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

                    index++;

                }


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

//        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.pizza_slice)));
//
//        CardModel cardModel = new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.pastrami));
//        cardModel.setOnClickListener(new CardModel.OnClickListener() {
//            @Override
//            public void OnClickListener() {
//                Log.i("Swipeable Cards","I am pressing the card");
//            }
//        });
//
//        cardModel.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
//            @Override
//            public void onLike() {
//                Log.i("Swipeable Cards", "I like the card");
//            }
//
//            @Override
//            public void onDislike() {
//                Log.i("Swipeable Cards","I dislike the card");
//            }
//        });
//
//        adapter.add(cardModel);



    }
    public static void match(View view, VolunteerOpening opening) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        ((LinearLayout)iv.getParent()).removeView(iv);


        builder.setTitle("You've been matched with \n "+ opening.title+"\n");
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
}
