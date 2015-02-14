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
        public ImageResultsWrapper photos;
    }

    public static class ImageResultsWrapper {
        public List<Image> photo;
    }

    @GET("/rest?method=flickr.photos.search&api_key=82accfa8682763d29787a8f3c01079e0&per_page=1&extras=url_m&format=json&nojsoncallback=1&sort=relevance&safe_search=1&content_type=1")
    void getImagesFromKeyword(@Query("text") String q, Callback<ImageResponseWrapper> response);
}
