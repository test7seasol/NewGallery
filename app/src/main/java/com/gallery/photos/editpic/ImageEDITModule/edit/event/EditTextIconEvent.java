package com.gallery.photos.editpic.ImageEDITModule.edit.event;

import android.view.MotionEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;

/* loaded from: classes.dex */
public class EditTextIconEvent implements StickerIconEvent {
    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.editTextSticker();
    }
}
