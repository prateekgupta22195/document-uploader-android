package com.pg.documentuploader.data.remote.simplifii;

import com.pg.documentuploader.app.network.HttpClient;
import com.pg.documentuploader.data.remote.DriveAPIService;
import com.pg.documentuploader.data.remote.simplifii.model.FilePostingResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.pg.documentuploader.BuildConfig;


public interface SimplifiiAPIService {

    //    TODO: we can store this token somewhere else for security purpose
    @Headers({"Accept: /", "Authorization: bearer " + BuildConfig.AUTH_KEY_SIMPLIFII, "Accept-Language: en-IN,en-GB;q=0.9,en;q=0.8", "Accept-Encoding: gzip, deflate, br", "Connection: keep-alive", "X-Requested-With: XMLHttpRequest"})
    @POST("https://be12.platform.simplifii.com/api/v1/s3/uploadimages")
    Call<FilePostingResponse> uploadFile(@Body RequestBody body);

    SimplifiiAPIService service = HttpClient.retrofit.create(SimplifiiAPIService.class);

}


