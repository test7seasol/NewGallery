package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/* loaded from: classes.dex */
public class CustomViewFlipper extends ViewFlipper {
    public CustomViewFlipper(Context context) {
        super(context);
    }

    public CustomViewFlipper(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.ViewFlipper, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException unused) {
            stopFlipping();
        }
    }
}
