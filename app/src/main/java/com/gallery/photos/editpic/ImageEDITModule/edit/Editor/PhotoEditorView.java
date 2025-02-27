package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;
import java.util.ArrayList;
import java.util.List;
import org.wysaid.view.ImageGLSurfaceView;

/* loaded from: classes.dex */
public class PhotoEditorView extends StickerView {
    private List<Bitmap> bitmaplist;
    private BrushDrawingView brushDrawingView;
    private Bitmap currentBitmap;
    private FilterImageView filterImageView;
    public ImageGLSurfaceView imageGLSurfaceView;
    private int index;

    public PhotoEditorView(Context context) {
        super(context);
        this.bitmaplist = new ArrayList();
        this.index = -1;
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.bitmaplist = new ArrayList();
        this.index = -1;
        init(attributeSet);
    }

    public PhotoEditorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.bitmaplist = new ArrayList();
        this.index = -1;
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        FilterImageView filterImageView = new FilterImageView(getContext());
        this.filterImageView = filterImageView;
        filterImageView.setId(1);
        this.filterImageView.setAdjustViewBounds(true);
        this.filterImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(13, -1);
        BrushDrawingView brushDrawingView = new BrushDrawingView(getContext());
        this.brushDrawingView = brushDrawingView;
        brushDrawingView.setVisibility(GONE);
        this.brushDrawingView.setId(2);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams2.addRule(13, -1);
        layoutParams2.addRule(6, 1);
        layoutParams2.addRule(8, 1);
        ImageGLSurfaceView imageGLSurfaceView = new ImageGLSurfaceView(getContext(), attributeSet);
        this.imageGLSurfaceView = imageGLSurfaceView;
        imageGLSurfaceView.setId(3);
        this.imageGLSurfaceView.setVisibility(View.VISIBLE);
        this.imageGLSurfaceView.setAlpha(1.0f);
        this.imageGLSurfaceView.setDisplayMode(ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FIT);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams3.addRule(13, -1);
        layoutParams3.addRule(6, 1);
        layoutParams3.addRule(8, 1);
        addView(this.filterImageView, layoutParams);
        addView(this.imageGLSurfaceView, layoutParams3);
        addView(this.brushDrawingView, layoutParams2);
    }

    public void setImageSource(final Bitmap bitmap) {
        this.filterImageView.setImageBitmap(bitmap);
        if (this.imageGLSurfaceView.getImageHandler() != null) {
            this.imageGLSurfaceView.setImageBitmap(bitmap);
        } else {
            this.imageGLSurfaceView.setSurfaceCreatedCallback(new ImageGLSurfaceView.OnSurfaceCreatedCallback() { // from class: com.gallery.photos.editphotovideo.Editor.PhotoEditorView.1
                @Override // org.wysaid.view.ImageGLSurfaceView.OnSurfaceCreatedCallback
                public void surfaceCreated() {
                    PhotoEditorView.this.imageGLSurfaceView.setImageBitmap(bitmap);
                }
            });
        }
        this.currentBitmap = bitmap;
        this.bitmaplist.add(Bitmap.createBitmap(bitmap));
        this.index++;
    }

    public void setImageSourceUndoRedo(final Bitmap bitmap) {
        this.filterImageView.setImageBitmap(bitmap);
        if (this.imageGLSurfaceView.getImageHandler() != null) {
            this.imageGLSurfaceView.setImageBitmap(bitmap);
        } else {
            this.imageGLSurfaceView.setSurfaceCreatedCallback(new ImageGLSurfaceView.OnSurfaceCreatedCallback() { // from class: com.gallery.photos.editphotovideo.Editor.PhotoEditorView.2
                @Override // org.wysaid.view.ImageGLSurfaceView.OnSurfaceCreatedCallback
                public void surfaceCreated() {
                    PhotoEditorView.this.imageGLSurfaceView.setImageBitmap(bitmap);
                }
            });
        }
        this.currentBitmap = bitmap;
    }

    public void setImageSource(Bitmap bitmap, ImageGLSurfaceView.OnSurfaceCreatedCallback onSurfaceCreatedCallback) {
        this.filterImageView.setImageBitmap(bitmap);
        if (this.imageGLSurfaceView.getImageHandler() != null) {
            this.imageGLSurfaceView.setImageBitmap(bitmap);
        } else {
            this.imageGLSurfaceView.setSurfaceCreatedCallback(onSurfaceCreatedCallback);
        }
        this.currentBitmap = bitmap;
    }

    public boolean undo() {
        Log.d("TAG", "undo: " + this.index);
        int i = this.index;
        if (i <= 0) {
            return false;
        }
        List<Bitmap> list = this.bitmaplist;
        int i2 = i - 1;
        this.index = i2;
        setImageSourceUndoRedo(list.get(i2));
        return true;
    }

    public boolean redo() {
        Log.d("TAG", "redo: " + this.index);
        if (this.index + 1 >= this.bitmaplist.size()) {
            return false;
        }
        List<Bitmap> list = this.bitmaplist;
        int i = this.index + 1;
        this.index = i;
        setImageSourceUndoRedo(list.get(i));
        return true;
    }

    public Bitmap getCurrentBitmap() {
        return this.currentBitmap;
    }

    public BrushDrawingView getBrushDrawingView() {
        return this.brushDrawingView;
    }

    public ImageGLSurfaceView getGLSurfaceView() {
        return this.imageGLSurfaceView;
    }

    public void saveGLSurfaceViewAsBitmap(final OnSaveBitmap onSaveBitmap) {
        if (this.imageGLSurfaceView.getVisibility() == 0) {
            this.imageGLSurfaceView.getResultBitmap(new ImageGLSurfaceView.QueryResultBitmapCallback() { // from class: com.gallery.photos.editphotovideo.Editor.PhotoEditorView.3
                @Override // org.wysaid.view.ImageGLSurfaceView.QueryResultBitmapCallback
                public void get(Bitmap bitmap) {
                    onSaveBitmap.onBitmapReady(bitmap);
                }
            });
        }
    }

    public void setFilterEffect(String str) {
        this.imageGLSurfaceView.setFilterWithConfig(str);
    }

    public void setFilterIntensity(float f) {
        this.imageGLSurfaceView.setFilterIntensity(f);
    }
}
