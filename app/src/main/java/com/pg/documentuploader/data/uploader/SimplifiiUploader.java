package com.pg.documentuploader.data.uploader;

import static com.pg.documentuploader.app.Constants.ERROR;
import static com.pg.documentuploader.app.Constants.SHAREABLE_LINK;
import static com.pg.documentuploader.util.FileUtil.getMimeType;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.pg.documentuploader.data.remote.simplifii.SimplifiiAPIService;
import com.pg.documentuploader.data.remote.simplifii.model.FilePostingResponse;
import com.pg.documentuploader.domain.RemoteUploader;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class SimplifiiUploader implements RemoteUploader {

    private static final SimplifiiUploader INSTANCE;

    private SimplifiiUploader() {
    }

    static {
        try {
            INSTANCE = new SimplifiiUploader();
        } catch (Exception e) {
            // Exception handling code here
            throw new RuntimeException("Failed to create singleton instance");
        }
    }

    public static SimplifiiUploader getInstance() {
        return INSTANCE;
    }


    @Override
    public ListenableWorker.Result uploadFile(String filePath, Context context) {
        try {
            String link = _uploadFile(filePath);
            String encodedLink = URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
            Data output = new Data.Builder().putString(SHAREABLE_LINK, encodedLink).build();
            return ListenableWorker.Result.success(output);
        } catch (IOException | RuntimeException e) {
            Data output = new Data.Builder().putString(ERROR, e.getMessage()).build();
            return ListenableWorker.Result.failure(output);
        }
    }

    private String _uploadFile(String filePath) throws IOException, RuntimeException {
        File file = new File(filePath);
        MediaType type = MediaType.get(getMimeType(filePath));
        RequestBody fileBody = RequestBody.create(file, type);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("files[0]", file.getName(), fileBody)
                .addFormDataPart("sub_dir1", "android_assignment")
                .addFormDataPart("sub_dir2", "prateekg")
                .build();
        Response<FilePostingResponse> response = SimplifiiAPIService.service.uploadFile(requestBody).execute();
        if (response.isSuccessful())
            return Objects.requireNonNull(response.body()).getResponse().getData().get(0).getUrl();
        throw new IllegalStateException("Error in uploading File");
    }


}
