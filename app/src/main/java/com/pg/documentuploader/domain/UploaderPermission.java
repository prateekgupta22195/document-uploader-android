package com.pg.documentuploader.domain;

import android.content.Context;
import android.content.Intent;

public interface UploaderPermission {
    boolean isUploadingPermissionGranted(Context context);

    Intent getUploadingPermissionIntent(Context context);

}
