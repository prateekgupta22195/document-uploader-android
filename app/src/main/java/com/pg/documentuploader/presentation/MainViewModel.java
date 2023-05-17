package com.pg.documentuploader.presentation;

import static com.pg.documentuploader.app.Constants.FILE_URI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.pg.documentuploader.domain.FileUploadWorker;
import com.pg.documentuploader.data.GoogleDriveUploader;
import com.pg.documentuploader.domain.RemoteUploader;

import java.util.UUID;

public class MainViewModel extends ViewModel {

    /**
     * instantiate the file uploader, here provided uploader is {@link com.pg.documentuploader.data.GoogleDriveUploader}
     */
    private final RemoteUploader remoteUploader = GoogleDriveUploader.getInstance();

    /**
     * used to get permission intent for uploading files
     * @param context: required to build intent
     * @return intent which will be used to start a Activity
     */
    public Intent getPermissionIntent(Context context) {
        return remoteUploader.getUploadingPermissionIntent(context);
    }

    /**
     * Tell whether file uploading permission is granted or not.
     *
     * @param context : required by the file-uploader SDK
     * @return true: if file uploading permission is granted else false
     */
    public boolean isUploadingPermissionGranted(Context context) {
        return remoteUploader.isUploadingPermissionGranted(context);
    }


    /**
     * This method is used to start a worker which can upload the file.
     *
     * @param documentUri: uri will be sent to the worker, which will upload the file
     * @param context: it is required to get instance of work manager here
     * @return UUID of the worker so that we can observe result using it.
     */
    public UUID handleSelectedDocument(Uri documentUri, Context context) {
        // Handle the selected document URI
        Data inputData = new Data.Builder().putString(FILE_URI, documentUri.toString()).build();
        UUID workID = UUID.randomUUID();
        OneTimeWorkRequest fileUploadRequest = new OneTimeWorkRequest.Builder(FileUploadWorker.class).setInputData(inputData).setId(workID).build();
        // Enqueue the file upload request
        WorkManager.getInstance(context).enqueue(fileUploadRequest);
        return workID;
    }


    /**
     * Used to get shareable intent correspondence to the shareable link
     * @param  url: takes shareable link
     * @return Intent which will show shareable options when called with startActivity()
     */
    public Intent shareableLinkIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        return intent;
    }
}