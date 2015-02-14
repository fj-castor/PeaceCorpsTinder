package com.blueoxfords.peacecorpstinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soheil on 15-02-14.
 */
public class TinderFragment extends Fragment {


    private CardContainer mCardContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tinder, container, false);

        mCardContainer = (CardContainer) rootView.findViewById(R.id.layoutview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());

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
    }
}
