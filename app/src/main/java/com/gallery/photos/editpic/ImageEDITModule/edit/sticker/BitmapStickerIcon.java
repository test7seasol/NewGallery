package com.gallery.photos.editpic.ImageEDITModule.edit.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.gallery.photos.editpic.ImageEDITModule.edit.event.StickerIconEvent;

/* loaded from: classes.dex */
public class BitmapStickerIcon extends DrawableSticker implements StickerIconEvent {
    public static final String ALIGN_HORIZONTALLY = "ALIGN_HORIZONTALLY";
    public static final String EDIT = "EDIT";
    public static final String FLIP = "FLIP";
    public static final String REMOVE = "REMOVE";
    public static final String ROTATE = "ROTATE";
    public static final String ZOOM = "ZOOM";
    private StickerIconEvent iconEvent;
    private float iconExtraRadius;
    private float iconRadius;
    private int position;
    private String tag;
    private float x;
    private float y;

    public BitmapStickerIcon(Drawable drawable, int i, String str) {
        super(drawable);

        Log.e("TAGee", "BitmapStickerIcon: "+drawable );
        this.iconExtraRadius = 10.0f;
        this.iconRadius = 30.0f;
        this.position = i;
        this.tag = str;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(this.x, this.y, this.iconRadius, paint);
        draw(canvas);
    }

    public float getIconRadius() {
        return this.iconRadius;
    }

    public int getPosition() {
        return this.position;
    }

    public String getTag() {
        return this.tag;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionDown(stickerView, motionEvent);
        }
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionMove(stickerView, motionEvent);
        }
    }

    @Override // com.gallery.photos.editphotovideo.event.StickerIconEvent
    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionUp(stickerView, motionEvent);
        }
    }

    public void setIconEvent(StickerIconEvent stickerIconEvent) {
        this.iconEvent = stickerIconEvent;
    }

    public void setTag(String str) {
        this.tag = str;
    }

    public void setX(float f) {
        this.x = f;
    }

    public void setY(float f) {
        this.y = f;
    }
}
