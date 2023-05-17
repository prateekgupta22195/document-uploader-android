package com.pg.documentuploader.util;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;

public class FileUtil {

    public static String copyFileToLocalDirectory(Context context, Uri uri, String fileName) {
        String destinationDirectory = context.getFilesDir().getAbsolutePath();


        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);

        if (documentFile != null && documentFile.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                File directory = new File(destinationDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File outputFile = new File(directory, fileName);
                inputStream = context.getContentResolver().openInputStream(uri);
                outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return outputFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public static String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getDocumentName(Uri documentUri, Context context) {
        String documentName = null;
        String[] projection = {OpenableColumns.DISPLAY_NAME};
        try (android.database.Cursor cursor = context.getContentResolver().query(documentUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    documentName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentName;
    }

}
