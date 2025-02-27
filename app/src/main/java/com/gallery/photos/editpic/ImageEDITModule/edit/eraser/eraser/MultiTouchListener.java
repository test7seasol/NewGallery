package com.gallery.photos.editpic.ImageEDITModule.edit.eraser.eraser;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes.dex */
public class MultiTouchListener implements View.OnTouchListener {
    Bitmap bitmap;
    private float mPrevX;
    private float mPrevY;
    boolean bt = false;
    private boolean disContinueHandleTransparecy = true;
    GestureDetector gd = null;
    private boolean handleTransparecy = false;
    public boolean isRotateEnabled = true;
    public boolean isRotationEnabled = false;
    public boolean isScaleEnabled = true;
    public boolean isTranslateEnabled = true;
    private TouchCallbackListener listener = null;
    private int mActivePointerId = -1;
    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    public float maximumScale = 8.0f;
    public float minimumScale = 0.5f;

    public interface TouchCallbackListener {
        void onTouchCallback(View view);

        void onTouchUpCallback(View view);
    }

    private static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        @Override // com.gallery.photos.editphotovideo.eraser.eraser.ScaleGestureDetector.SimpleOnScaleGestureListener, com.gallery.photos.editphotovideo.eraser.eraser.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(View view, ScaleGestureDetector scaleGestureDetector) {
            this.mPivotX = scaleGestureDetector.getFocusX();
            this.mPivotY = scaleGestureDetector.getFocusY();
            this.mPrevSpanVector.set(scaleGestureDetector.getCurrentSpanVector());
            return true;
        }

        @Override // com.gallery.photos.editphotovideo.eraser.eraser.ScaleGestureDetector.SimpleOnScaleGestureListener, com.gallery.photos.editphotovideo.eraser.eraser.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(View view, ScaleGestureDetector scaleGestureDetector) {
            TransformInfo transformInfo = new TransformInfo();
            transformInfo.deltaScale = MultiTouchListener.this.isScaleEnabled ? scaleGestureDetector.getScaleFactor() : 1.0f;
            transformInfo.deltaAngle = MultiTouchListener.this.isRotateEnabled ? Vector2D.getAngle(this.mPrevSpanVector, scaleGestureDetector.getCurrentSpanVector()) : 0.0f;
            transformInfo.deltaX = MultiTouchListener.this.isTranslateEnabled ? scaleGestureDetector.getFocusX() - this.mPivotX : 0.0f;
            transformInfo.deltaY = MultiTouchListener.this.isTranslateEnabled ? scaleGestureDetector.getFocusY() - this.mPivotY : 0.0f;
            transformInfo.pivotX = this.mPivotX;
            transformInfo.pivotY = this.mPivotY;
            transformInfo.minimumScale = MultiTouchListener.this.minimumScale;
            transformInfo.maximumScale = MultiTouchListener.this.maximumScale;
            MultiTouchListener.this.move(view, transformInfo);
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

    public MultiTouchListener setHandleTransparecy(boolean z) {
        this.handleTransparecy = z;
        return this;
    }

    public MultiTouchListener setGestureListener(GestureDetector gestureDetector) {
        this.gd = gestureDetector;
        return this;
    }

    public MultiTouchListener setOnTouchCallbackListener(TouchCallbackListener touchCallbackListener) {
        this.listener = touchCallbackListener;
        return this;
    }

    public MultiTouchListener enableRotation(boolean z) {
        this.isRotationEnabled = z;
        return this;
    }

    public MultiTouchListener setMinScale(float f) {
        this.minimumScale = f;
        return this;
    }

    public void move(View view, TransformInfo transformInfo) {
        computeRenderOffset(view, transformInfo.pivotX, transformInfo.pivotY);
        adjustTranslation(view, transformInfo.deltaX, transformInfo.deltaY);
        float max = Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        view.setScaleX(max);
        view.setScaleY(max);
        if (this.isRotationEnabled) {
            view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
        }
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

   /* public boolean handleTransparency(View view, MotionEvent motionEvent) {
        try {
            if (motionEvent.getAction() == 2) {
                Log.i("MOVE_TESTs", "ACTION_MOVE");
                if (this.bt) {
                    return true;
                }
            }
            if (motionEvent.getAction() == 1) {
                Log.i("MOVE_TESTs", "ACTION_UP");
                if (this.bt) {
                    this.bt = false;
                    Bitmap bitmap = this.bitmap;
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    return true;
                }
            }
            view.getLocationOnScreen(new int[2]);
            int rawX = (int) (motionEvent.getRawX() - r3[0]);
            int rawY = (int) (motionEvent.getRawY() - r3[1]);
            float rotation = view.getRotation();
            Matrix matrix = new Matrix();
            matrix.postRotate(-rotation);
            float[] fArr = {rawX, rawY};
            matrix.mapPoints(fArr);
            int i = (int) fArr[0];
            int i2 = (int) fArr[1];
            if (motionEvent.getAction() == 0) {
                Log.i("MOVE_TESTs", "View Width/height " + view.getWidth() + " / " + view.getHeight());
                this.bt = false;
                view.setDrawingCacheEnabled(true);
                this.bitmap = Bitmap.createBitmap(view.getDrawingCache());
                i = (int) (i * (r0.getWidth() / (this.bitmap.getWidth() * view.getScaleX())));
                i2 = (int) (i2 * (this.bitmap.getHeight() / (this.bitmap.getHeight() * view.getScaleX())));
                view.setDrawingCacheEnabled(false);
            }
            if (i >= 0 && i2 >= 0 && i <= this.bitmap.getWidth() && i2 <= this.bitmap.getHeight()) {
                boolean z = this.bitmap.getPixel(i, i2) == 0;
                if (motionEvent.getAction() != 0) {
                    return z;
                }
                this.bt = z;
                return z;
            }
        } catch (Exception unused) {
        }
        return false;
    }*/

    public boolean handleTransparency(View view, MotionEvent motionEvent) {
        try {
            boolean z = true;
            if (motionEvent.getAction() == 2) {
                Log.i("MOVE_TESTs", "ACTION_MOVE");
                if (this.bt) {
                    return true;
                }
            }
            if (motionEvent.getAction() == 1) {
                Log.i("MOVE_TESTs", "ACTION_UP");
                if (this.bt) {
                    this.bt = false;
                    Bitmap bitmap2 = this.bitmap;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    return true;
                }
            }
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            int rawY = (int) (motionEvent.getRawY() - ((float) iArr[1]));
            float rotation = view.getRotation();
            Matrix matrix = new Matrix();
            matrix.postRotate(-rotation);
            float[] fArr = {(float) ((int) (motionEvent.getRawX() - ((float) iArr[0]))), (float) rawY};
            matrix.mapPoints(fArr);
            int i = (int) fArr[0];
            int i2 = (int) fArr[1];
            if (motionEvent.getAction() == 0) {
                Log.i("MOVE_TESTs", "View Width/height " + view.getWidth() + " / " + view.getHeight());
                this.bt = false;
                view.setDrawingCacheEnabled(true);
                Bitmap createBitmap = Bitmap.createBitmap(view.getDrawingCache());
                this.bitmap = createBitmap;
                i = (int) (((float) i) * (((float) createBitmap.getWidth()) / (((float) this.bitmap.getWidth()) * view.getScaleX())));
                i2 = (int) (((float) i2) * (((float) this.bitmap.getHeight()) / (((float) this.bitmap.getHeight()) * view.getScaleX())));
                view.setDrawingCacheEnabled(false);
            }
            if (i >= 0 && i2 >= 0 && i <= this.bitmap.getWidth() && i2 <= this.bitmap.getHeight()) {
                if (this.bitmap.getPixel(i, i2) != 0) {
                    z = false;
                }
                if (motionEvent.getAction() != 0) {
                    return z;
                }
                this.bt = z;
                return z;
            }
        } catch (Exception unused) {
        }
        return false;
    }


    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (this.handleTransparecy && this.disContinueHandleTransparecy) {
            if (handleTransparency(view, motionEvent)) {
                return false;
            }
            this.disContinueHandleTransparecy = false;
        }
        GestureDetector gestureDetector = this.gd;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(motionEvent);
        }
        if (!this.isTranslateEnabled) {
            return true;
        }
        int action = motionEvent.getAction();
        int actionMasked = motionEvent.getActionMasked() & action;
        if (actionMasked == 0) {
            TouchCallbackListener touchCallbackListener = this.listener;
            if (touchCallbackListener != null) {
                touchCallbackListener.onTouchCallback(view);
            }
            this.mPrevX = motionEvent.getX();
            this.mPrevY = motionEvent.getY();
            this.mActivePointerId = motionEvent.getPointerId(0);
            return true;
        }
        if (actionMasked == 1) {
            this.mActivePointerId = -1;
            this.disContinueHandleTransparecy = true;
            TouchCallbackListener touchCallbackListener2 = this.listener;
            if (touchCallbackListener2 == null) {
                return true;
            }
            touchCallbackListener2.onTouchUpCallback(view);
            return true;
        }
        if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
            if (findPointerIndex == -1) {
                return true;
            }
            float x = motionEvent.getX(findPointerIndex);
            float y = motionEvent.getY(findPointerIndex);
            if (this.mScaleGestureDetector.isInProgress()) {
                return true;
            }
            adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
            return true;
        }
        if (actionMasked == 3) {
            this.mActivePointerId = -1;
            return true;
        }
        if (actionMasked != 6) {
            return true;
        }
        int i = (65280 & action) >> 8;
        if (motionEvent.getPointerId(i) != this.mActivePointerId) {
            return true;
        }
        int i2 = i == 0 ? 1 : 0;
        this.mPrevX = motionEvent.getX(i2);
        this.mPrevY = motionEvent.getY(i2);
        this.mActivePointerId = motionEvent.getPointerId(i2);
        return true;
    }
}
