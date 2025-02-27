package com.gallery.photos.editpic.ImageEDITModule.edit.event;

import android.view.MotionEvent;

import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;

/* loaded from: classes.dex */
public interface StickerIconEvent {
    void onActionDown(StickerView stickerView, MotionEvent motionEvent);

    void onActionMove(StickerView stickerView, MotionEvent motionEvent);

    void onActionUp(StickerView stickerView, MotionEvent motionEvent);
}
