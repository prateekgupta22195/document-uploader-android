package com.pg.documentuploader.data.remote;

import com.pg.documentuploader.app.network.HttpClient;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriveAPIService {
    @POST("/upload/drive/v3/files")
    Call<ResponseBody> uploadFile(@Header("Authorization") String auth, @Body RequestBody body, @Query("fields") String fields);


    @POST("/drive/v3/files/{fileId}/permissions")
    Call<ResponseBody> setPermission(@Header("Authorization") String auth, @Body RequestBody body, @Path("fileId") String fileId);

    DriveAPIService service = HttpClient.retrofit.create(DriveAPIService.class);
}
