package com.gallery.photos.editpic.ImageEDITModule.edit.sticker;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public class DrawableSticker extends Sticker {
    private Drawable drawable;
    private Rect realBounds ;

    public DrawableSticker(Drawable drawable) {
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        this.drawable.setBounds(this.realBounds);
        this.drawable.draw(canvas);
        canvas.restore();
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getAlpha() {
        return this.drawable.getAlpha();
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public Drawable getDrawable() {
        return this.drawable;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getHeight() {
        return this.drawable.getIntrinsicHeight();
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getWidth() {
        return this.drawable.getIntrinsicWidth();
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public void release() {
        super.release();
        if (this.drawable != null) {
            this.drawable = null;
        }
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public DrawableSticker setAlpha(int i) {
        this.drawable.setAlpha(i);
        return this;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public DrawableSticker setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }
}
