package com.pg.documentuploader.data.uploader;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount;
import static com.pg.documentuploader.app.Constants.ERROR;
import static com.pg.documentuploader.app.Constants.SHAREABLE_LINK;
import static com.pg.documentuploader.util.FileUtil.getMimeType;

import android.content.Context;
import android.content.Intent;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.pg.documentuploader.data.remote.DriveAPIService;
import com.pg.documentuploader.domain.AuthenticatedRemoteUploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class GoogleDriveUploader implements AuthenticatedRemoteUploader {
    private final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope(Scopes.DRIVE_FILE)).build();

    private static final GoogleDriveUploader INSTANCE;

    private GoogleDriveUploader() {
    }

    static {
        try {
            INSTANCE = new GoogleDriveUploader();
        } catch (Exception e) {
            // Exception handling code here
            throw new RuntimeException("Failed to create singleton instance");
        }
    }

    public static GoogleDriveUploader getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isUploadingPermissionGranted(Context context) {
        return getLastSignedInAccount(context) != null;
    }

    @Override
    public Intent getUploadingPermissionIntent(Context context) {
        return GoogleSignIn.getClient(context, gso).getSignInIntent();
    }

    @Override
    public ListenableWorker.Result uploadFile(String filePath, Context context) {
        try {
            String link = _uploadFile(filePath, context);
            String encodedLink = URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
            Data output = new Data.Builder().putString(SHAREABLE_LINK, encodedLink).build();
            return ListenableWorker.Result.success(output);
        } catch (GoogleAuthException | IllegalStateException | JSONException | IOException e) {
            Data output = new Data.Builder().putString(ERROR, e.getMessage()).build();
            return ListenableWorker.Result.failure(output);
        }
    }

    private String _uploadFile(String filePath, Context context) throws GoogleAuthException, IOException, JSONException, IllegalStateException {
        File file = new File(Objects.requireNonNull(filePath));
        MediaType type = MediaType.get(getMimeType(filePath));
        RequestBody requestBody = RequestBody.create(file, type);
        String token = "Bearer " + getGoogleAccessToken(context);
        Response<ResponseBody> response = DriveAPIService.service.uploadFile(token, requestBody, "webViewLink,id").execute();
        if (response.isSuccessful()) {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
            String fileID = jsonObject.getString("id");
            String permissionBody = "{\"role\":\"reader\", \"type\":\"anyone\"}";
            Response<ResponseBody> permissionResponse = DriveAPIService.service.setPermission(token, RequestBody.create(permissionBody, MediaType.parse("application/json")), fileID).execute();
            if (permissionResponse.isSuccessful()) return jsonObject.getString("webViewLink");
        }
        throw new IllegalStateException("Error in uploading File");
    }

    private String getGoogleAccessToken(Context context) throws GoogleAuthException, IOException {
        String scope = "oauth2:" + Scopes.DRIVE_FILE;
        GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(context);
        return GoogleAuthUtil.getToken(context, Objects.requireNonNull(Objects.requireNonNull(signedInAccount).getAccount()), scope);
    }

}
