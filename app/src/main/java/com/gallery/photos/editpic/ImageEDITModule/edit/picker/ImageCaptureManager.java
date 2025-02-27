package com.gallery.photos.editpic.ImageEDITModule.edit.picker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class ImageCaptureManager {
    private Context mContext;
    private String mCurrentPhotoPath;

    public ImageCaptureManager(Context context) {
        this.mContext = context;
    }

    public File createImageFile() throws IOException {
        String str = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".jpg";
        File externalFilesDir = this.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir.exists() || externalFilesDir.mkdir()) {
            File file = new File(externalFilesDir, str);
            this.mCurrentPhotoPath = file.getAbsolutePath();
            return file;
        }
        Log.e("TAG", "Throwing Errors....");
        throw new IOException();
    }

    public Intent dispatchTakePictureIntent() throws IOException {
        Uri fromFile;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(this.mContext.getPackageManager()) != null) {
            File createImageFile = createImageFile();
            if (Build.VERSION.SDK_INT >= 24) {
                fromFile = FileProvider.getUriForFile(this.mContext.getApplicationContext(), this.mContext.getApplicationInfo().packageName + ".provider", createImageFile);
            } else {
                fromFile = Uri.fromFile(createImageFile);
            }
            if (fromFile != null) {
                intent.putExtra("output", fromFile);
            }
        }
        return intent;
    }

    public void galleryAddPic() {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        if (TextUtils.isEmpty(this.mCurrentPhotoPath)) {
            return;
        }
        intent.setData(Uri.fromFile(new File(this.mCurrentPhotoPath)));
        this.mContext.sendBroadcast(intent);
    }

    public String getCurrentPhotoPath() {
        return this.mCurrentPhotoPath;
    }
}
