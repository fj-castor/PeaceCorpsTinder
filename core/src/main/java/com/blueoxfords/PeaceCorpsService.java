package com.blueoxfords;

import com.blueoxfords.models.VolunteerOpening;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by fjcastor on 15-02-14.
 */
public interface PeaceCorpsService {

    public static class OpeningsWrapper {
        public List<VolunteerOpening> results;
    }

    @GET("/openings")
    OpeningsWrapper getVolunteerOpenings();

    @GET("/openings")
    void getVolunteerOpening(Callback<OpeningsWrapper> response);

    @GET("/openings/{req_id}")
    VolunteerOpening getVolunteerOpening(@Path("req_id") String req_id);

    @GET("/openings/{req_id}")
    void getVolunteerOpening(@Path("req_id") String req_id, Callback<VolunteerOpening> callback);

}
