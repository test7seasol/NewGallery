package com.gallery.photos.editpic.ImageEDITModule.edit.crop;

import android.content.Context;
import android.graphics.Bitmap;

/* loaded from: classes.dex */
public interface DeeplabInterface {
    int getInputSize();

    boolean initialize(Context context);

    boolean isInitialized();

    Bitmap segment(Bitmap bitmap);
}
