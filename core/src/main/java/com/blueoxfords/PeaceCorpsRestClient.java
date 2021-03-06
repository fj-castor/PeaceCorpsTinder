package com.blueoxfords;

import com.blueoxfords.models.Image;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by fjcastor on 15-02-14.
 */
public class PeaceCorpsRestClient {
    private static PeaceCorpsService REST_CLIENT;
    private static String ROOT = "http://www.peacecorps.gov/api/v1";

    static {
        setupRestClient();
    }

    private PeaceCorpsRestClient() {}

    public static PeaceCorpsService get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT).setConverter(new GsonConverter(gson))
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        REST_CLIENT = restAdapter.create(PeaceCorpsService.class);
    }
}
