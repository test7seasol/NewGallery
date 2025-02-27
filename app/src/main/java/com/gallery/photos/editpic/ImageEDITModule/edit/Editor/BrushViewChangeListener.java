package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

/* loaded from: classes.dex */
interface BrushViewChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);
}
