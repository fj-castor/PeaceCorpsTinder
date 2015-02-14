package com.blueoxfords;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by fjcastor on 15-02-14.
 */
public class ImageRestClient {
    private static ImageService REST_CLIENT;
    private static String ROOT = "https://www.flickr.com/services";

    static {
        setupRestClient();
    }

    private ImageRestClient() {}

    public static ImageService get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        REST_CLIENT = restAdapter.create(ImageService.class);
    }
}
