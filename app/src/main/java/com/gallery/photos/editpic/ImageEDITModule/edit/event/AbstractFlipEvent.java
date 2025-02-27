package com.gallery.photos.editpic.ImageEDITModule.edit.event;

import android.view.MotionEvent;

import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;
/* loaded from: classes.dex */
public abstract class AbstractFlipEvent implements StickerIconEvent {
    protected abstract int getFlipDirection();

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.flipCurrentSticker(getFlipDirection());
    }
}
