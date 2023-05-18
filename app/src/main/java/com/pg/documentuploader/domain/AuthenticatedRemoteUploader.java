package com.pg.documentuploader.domain;

import android.content.Context;
import android.content.Intent;

import androidx.work.ListenableWorker;

public interface AuthenticatedRemoteUploader extends RemoteUploader{
    boolean isUploadingPermissionGranted(Context context);

    Intent getUploadingPermissionIntent(Context context);

}



