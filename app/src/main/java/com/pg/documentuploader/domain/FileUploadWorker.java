package com.pg.documentuploader.domain;

import static com.pg.documentuploader.app.Constants.FILE_URI;
import static com.pg.documentuploader.util.FileUtil.getDocumentName;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pg.documentuploader.data.GoogleDriveUploader;
import com.pg.documentuploader.util.FileUtil;

public class FileUploadWorker extends Worker {

    RemoteUploader remoteUploader = GoogleDriveUploader.getInstance();

    public FileUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        Uri fileUri = Uri.parse(getInputData().getString(FILE_URI));
        String documentName = getDocumentName(fileUri, context);
        String filePath = FileUtil.copyFileToLocalDirectory(context, fileUri, documentName);
        return remoteUploader.uploadFile(filePath, context);
    }

}