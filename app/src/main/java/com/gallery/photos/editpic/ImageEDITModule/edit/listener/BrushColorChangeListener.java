package com.gallery.photos.editpic.ImageEDITModule.edit.listener;

import com.gallery.photos.editpic.ImageEDITModule.edit.draw.BrushDrawingView;

/* loaded from: classes.dex */
public interface BrushColorChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);
}
