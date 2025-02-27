package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

/* loaded from: classes.dex */
public class SaveSettings {
    private boolean isClearViewsEnabled;
    private boolean isTransparencyEnabled;

    public boolean isTransparencyEnabled() {
        return this.isTransparencyEnabled;
    }

    public boolean isClearViewsEnabled() {
        return this.isClearViewsEnabled;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    public static class Builder {
        public boolean isClearViewsEnabled = true;
        public boolean isTransparencyEnabled = true;

        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
