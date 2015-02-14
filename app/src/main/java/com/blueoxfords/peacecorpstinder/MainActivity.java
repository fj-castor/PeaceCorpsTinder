package com.blueoxfords.peacecorpstinder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
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
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);

        final SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);


        RestClient.get().getVolunteerOpening(new Callback<PeaceCorpsService.OpeningsWrapper>() {
            @Override
            public void success(PeaceCorpsService.OpeningsWrapper openingsWrapper, Response response) {

                List<VolunteerOpening> volunteerOpeningList =  openingsWrapper.results.subList(0, 9);
                List<KeywordPairing> keywordPairingList = KeywordGenerator.generate(volunteerOpeningList);

                for (KeywordPairing opening : keywordPairingList) {

                    final KeywordPairing pairing = opening;

                    ImageRestClient.get().getImagesFromKeyword(opening.keyword, new Callback<ImageService.ImageResponseWrapper>() {
                        @Override
                        public void success(ImageService.ImageResponseWrapper imageResponseWrapper, Response response) {
                            List<Image> images = imageResponseWrapper.responseData.results;
                            String url = "http://i.imgur.com/DvpvklR.png";
                            if (images.size() > 0) {
                                Log.d("TESTING", images.get(0).url);
                                url = images.get(0).url;
                            }
                            adapter.add(new CardModel(pairing.volunteerOpening.title, pairing.keyword, url));
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
}
