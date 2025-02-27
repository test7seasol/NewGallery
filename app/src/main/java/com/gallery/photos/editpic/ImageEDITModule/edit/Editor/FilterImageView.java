package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/* loaded from: classes.dex */
public class FilterImageView extends AppCompatImageView {
    private OnImageChangedListener mOnImageChangedListener;

    interface OnImageChangedListener {
        void onBitmapLoaded(Bitmap bitmap);
    }

    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FilterImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // android.widget.ImageView
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // android.widget.ImageView
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // android.widget.ImageView
    public void setImageState(int[] iArr, boolean z) {
        super.setImageState(iArr, z);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // android.widget.ImageView
    public void setImageTintList(ColorStateList colorStateList) {
        super.setImageTintList(colorStateList);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // android.widget.ImageView
    public void setImageTintMode(PorterDuff.Mode mode) {
        super.setImageTintMode(mode);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageResource(int i) {
        super.setImageResource(i);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageLevel(int i) {
        super.setImageLevel(i);
        OnImageChangedListener onImageChangedListener = this.mOnImageChangedListener;
        if (onImageChangedListener != null) {
            onImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    public Bitmap getBitmap() {
        if (getDrawable() != null) {
            return ((BitmapDrawable) getDrawable()).getBitmap();
        }
        return null;
    }
}
