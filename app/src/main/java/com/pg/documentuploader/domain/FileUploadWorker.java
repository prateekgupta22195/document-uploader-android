package com.pg.documentuploader.domain;

import static com.pg.documentuploader.app.Constants.FILE_URI;
import static com.pg.documentuploader.util.FileUtil.getDocumentName;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pg.documentuploader.data.uploader.SimplifiiUploader;
import com.pg.documentuploader.util.FileUtil;

/**
 * Used to upload file on cloud, accepts document uri as {@link ListenableWorker#getInputData()}
 */
public class FileUploadWorker extends Worker {

    RemoteUploader remoteUploader = SimplifiiUploader.getInstance();

    public FileUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {

        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        /* Retrieving document uri */
        Uri fileUri = Uri.parse(getInputData().getString(FILE_URI));
        String documentName = getDocumentName(fileUri, context);
        /* copying file to app directory to get file path */
        String filePath = FileUtil.copyFileToLocalDirectory(context, fileUri, documentName);
        /* returning result from file uploader */
        return remoteUploader.uploadFile(filePath, context);
    }

}