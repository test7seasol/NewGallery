package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

/* loaded from: classes.dex */
public interface OnPhotoEditorListener {
    void onAddViewListener(ViewType viewType, int i);

    void onRemoveViewListener(int i);

    void onRemoveViewListener(ViewType viewType, int i);

    void onStartViewChangeListener(ViewType viewType);

    void onStopViewChangeListener(ViewType viewType);
}
