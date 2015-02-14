package com.blueoxfords;

import com.blueoxfords.models.Image;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by fjcastor on 15-02-14.
 */
public interface ImageService {
    public static class ImageResponseWrapper {
        public ImageResultsWrapper responseData;
    }

    public static class ImageResultsWrapper {
        public List<Image> results;
    }

    @GET("/images?v=1.0")
    void getImagesFromKeyword(@Query("q") String q, Callback<ImageResponseWrapper> response);
}
