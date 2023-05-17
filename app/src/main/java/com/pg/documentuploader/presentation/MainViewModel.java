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

    private final RemoteUploader remoteUploader = GoogleDriveUploader.getInstance();

    public Intent getPermissionIntent(Context context) {
        return remoteUploader.getUploadingPermissionIntent(context);
    }

    public boolean isUploadingPermissionGranted(Context context) {
        return remoteUploader.isUploadingPermissionGranted(context);
    }


    public UUID handleSelectedDocument(Uri documentUri, Context context) {
        // Handle the selected document URI
        Data inputData = new Data.Builder().putString(FILE_URI, documentUri.toString()).build();
        UUID workID = UUID.randomUUID();
        OneTimeWorkRequest fileUploadRequest = new OneTimeWorkRequest.Builder(FileUploadWorker.class).setInputData(inputData).setId(workID).build();
        // Enqueue the file upload request
        WorkManager.getInstance(context).enqueue(fileUploadRequest);
        return workID;
    }



    public Intent shareableLinkIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        return intent;
    }
}