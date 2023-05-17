package com.pg.documentuploader.app.network;

import com.pg.documentuploader.data.remote.DriveAPIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private HttpClient(){}

    private final static String API_BASE_URL = "https://www.googleapis.com";
    public final static Retrofit retrofit;

    public final static DriveAPIService driveAPIService;

    //     Doing static initialisation of the client so that there should always be a single instance for retrofit
    static {
        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        driveAPIService = retrofit.create(DriveAPIService.class);
    }
}
