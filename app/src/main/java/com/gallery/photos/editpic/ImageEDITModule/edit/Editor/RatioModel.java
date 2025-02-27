package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import com.steelkiwi.cropiwa.AspectRatio;

/* loaded from: classes.dex */
public class RatioModel extends AspectRatio {
    private String name;
    private int selectedIem;

    public RatioModel(int i, int i2, int i3, String str) {
        super(i, i2);
        this.selectedIem = i3;
        this.name = str;
    }

    public int getSelectedIem() {
        return this.selectedIem;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }
}
