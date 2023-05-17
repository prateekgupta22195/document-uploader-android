package com.pg.documentuploader.domain;

import android.content.Context;

import androidx.work.ListenableWorker;

public interface RemoteUploader {
    ListenableWorker.Result uploadFile(String filePath, Context context);

}
