package com.gallery.photos.editpic.ImageEDITModule.edit.listener;

import android.app.Activity;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.gallery.photos.editpic.ImageEDITModule.edit.activities.NeonActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.vector.DHANVINE_Vector2D;
import com.gallery.photos.editpic.ImageEDITModule.edit.vector.ScaleGestureDetector;

/* loaded from: classes.dex */
public class ScaleTouchListener implements View.OnTouchListener {
    Boolean forspiral;
    public boolean isRotateEnabled;
    public boolean isScaleEnabled;
    public boolean isTranslateEnabled;
    private int mActivePointerId;
    Activity mActivity;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;
    public float maximumScale;
    public float minimumScale;
    private Rect rect;

    private static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private DHANVINE_Vector2D mPrevSpanVector;

        @Override // com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.SimpleOnScaleGestureListener, com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(View view, ScaleGestureDetector scaleGestureDetector) {
        }

        private ScaleGestureListener() {
            this.mPrevSpanVector = new DHANVINE_Vector2D(0.0f, 0.0f);
        }

        @Override // com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.SimpleOnScaleGestureListener, com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(View view, ScaleGestureDetector scaleGestureDetector) {
            this.mPivotX = scaleGestureDetector.getFocusX();
            this.mPivotY = scaleGestureDetector.getFocusY();
            this.mPrevSpanVector.set(scaleGestureDetector.getCurrentSpanVector());
            return true;
        }

        @Override // com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.SimpleOnScaleGestureListener, com.gallery.photos.editphotovideo.vector.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(View view, ScaleGestureDetector scaleGestureDetector) {
            TransformInfo transformInfo = new TransformInfo();
            transformInfo.deltaScale = ScaleTouchListener.this.isScaleEnabled ? scaleGestureDetector.getScaleFactor() : 1.0f;
            transformInfo.deltaAngle = ScaleTouchListener.this.isRotateEnabled ? DHANVINE_Vector2D.getAngle(this.mPrevSpanVector, scaleGestureDetector.getCurrentSpanVector()) : 0.0f;
            transformInfo.deltaX = ScaleTouchListener.this.isTranslateEnabled ? scaleGestureDetector.getFocusX() - this.mPivotX : 0.0f;
            transformInfo.deltaY = ScaleTouchListener.this.isTranslateEnabled ? scaleGestureDetector.getFocusY() - this.mPivotY : 0.0f;
            transformInfo.pivotX = this.mPivotX;
            transformInfo.pivotY = this.mPivotY;
            transformInfo.minimumScale = ScaleTouchListener.this.minimumScale;
            transformInfo.maximumScale = ScaleTouchListener.this.maximumScale;
            ScaleTouchListener.this.move(view, transformInfo);
            return false;
        }
    }

    private class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;

        private TransformInfo() {
        }
    }

    public ScaleTouchListener(Activity activity, Boolean bool) {
        this.mActivity = activity;
        Boolean.valueOf(false);
        this.forspiral = bool;
        this.isRotateEnabled = false;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    }

    public void move(View view, TransformInfo transformInfo) {
        computeRenderOffset(view, transformInfo.pivotX, transformInfo.pivotY);
        adjustTranslation(view, transformInfo.deltaX, transformInfo.deltaY);
        float max = Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        view.setScaleX(max);
        view.setScaleY(max);
        view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
    }

    private static void adjustTranslation(View view, float f, float f2) {
        float[] fArr = {f, f2};
        view.getMatrix().mapVectors(fArr);
        view.setTranslationX(view.getTranslationX() + fArr[0]);
        view.setTranslationY(view.getTranslationY() + fArr[1]);
    }

    private static void computeRenderOffset(View view, float f, float f2) {
        if (view.getPivotX() == f && view.getPivotY() == f2) {
            return;
        }
        float[] fArr = {0.0f, 0.0f};
        view.getMatrix().mapPoints(fArr);
        view.setPivotX(f);
        view.setPivotY(f2);
        float[] fArr2 = {0.0f, 0.0f};
        view.getMatrix().mapPoints(fArr2);
        float f3 = fArr2[1] - fArr[1];
        view.setTranslationX(view.getTranslationX() - (fArr2[0] - fArr[0]));
        view.setTranslationY(view.getTranslationY() - f3);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.forspiral.booleanValue() && (this.mActivity instanceof NeonActivity)) {
            onTouch2(NeonActivity.imageViewFont, motionEvent);
        }
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (this.isTranslateEnabled) {
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            if (actionMasked == 6) {
                int i = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i) == this.mActivePointerId) {
                    int i2 = i == 0 ? 1 : 0;
                    this.mPrevX = motionEvent.getX(i2);
                    this.mPrevY = motionEvent.getY(i2);
                    this.mActivePointerId = motionEvent.getPointerId(i2);
                }
            } else if (actionMasked == 0) {
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            }
        }
        return true;
    }

    public boolean onTouch2(View view, MotionEvent motionEvent) {
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (this.isTranslateEnabled) {
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            if (actionMasked == 6) {
                int i = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i) == this.mActivePointerId) {
                    int i2 = i == 0 ? 1 : 0;
                    this.mPrevX = motionEvent.getX(i2);
                    this.mPrevY = motionEvent.getY(i2);
                    this.mActivePointerId = motionEvent.getPointerId(i2);
                }
            } else if (actionMasked == 0) {
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            }
        }
        return true;
    }
}
