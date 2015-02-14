package com.blueoxfords;

import com.blueoxfords.models.VolunteerOpening;

import java.util.List;

import retrofit.http.GET;

/**
 * Created by fjcastor on 15-02-14.
 */
public interface PeaceCorpsService {

    public static class OpeningsWrapper {
        public List<VolunteerOpening> results;
    }

    @GET("/openings")
    OpeningsWrapper getVolunteerOpenings();

}
