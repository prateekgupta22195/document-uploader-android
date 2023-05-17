package com.pg.documentuploader.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;


import com.pg.documentuploader.R;


public class DialogUtil {
    public static AlertDialog fileUploadedDialog(String fileLink, Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("File Successfully uploaded!").setMessage(fileLink)
                .setPositiveButton("Share Link", onClickListener).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder.create();
    }


    public static AlertDialog showProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_progress, null);
        builder.setView(dialogView);
        AlertDialog progressDialog = builder.create();
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}
