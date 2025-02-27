package com.gallery.photos.editpic.ImageEDITModule.edit.event;

import android.view.MotionEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;

/* loaded from: classes.dex */
public class ZoomIconEvent implements StickerIconEvent {
    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.zoomAndRotateCurrentSticker(motionEvent);
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        if (stickerView.getOnStickerOperationListener() != null) {
            stickerView.getOnStickerOperationListener().onStickerZoomFinished(stickerView.getCurrentSticker());
        }
    }
}
