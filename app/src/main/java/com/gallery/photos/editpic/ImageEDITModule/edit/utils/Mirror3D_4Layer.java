package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class Mirror3D_4Layer extends RelativeLayout implements ScaleGestureDetector.OnScaleGestureListener {
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = 2.6f;
    private static final String TAG = "Mirror3D_4Layer";
    public float dx;
    public float dy;
    private float lastScaleFactor;
    private Mode mode;
    private float prevDx;
    private float prevDy;
    public float scale;
    private float startX;
    private float startY;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    public Mirror3D_4Layer(Context context) {
        super(context);
        this.mode = Mode.NONE;
        this.scale = 2.61f;
        this.lastScaleFactor = 2.61f;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.prevDx = 0.0f;
        this.prevDy = 0.0f;
        init(context);
    }

    public Mirror3D_4Layer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mode = Mode.NONE;
        this.scale = 2.61f;
        this.lastScaleFactor = 2.61f;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.prevDx = 0.0f;
        this.prevDy = 0.0f;
        init(context);
    }

    public Mirror3D_4Layer(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mode = Mode.NONE;
        this.scale = 2.61f;
        this.lastScaleFactor = 2.61f;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.prevDx = 0.0f;
        this.prevDy = 0.0f;
        init(context);
    }

    public Mirror3D_4Layer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mode = Mode.NONE;
        this.scale = 2.61f;
        this.lastScaleFactor = 2.61f;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.prevDx = 0.0f;
        this.prevDy = 0.0f;
        init(context);
    }

    public void init(Context context, final Mirror3D_4Layer mirror3D_4Layer) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(new OnTouchListener() { // from class: com.gallery.photos.editphotovideo.utils.Mirror3D_4Layer.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & 255;
                if (action == 0) {
                    Log.i(Mirror3D_4Layer.TAG, "DOWN");
                    if (Mirror3D_4Layer.this.scale > Mirror3D_4Layer.MIN_ZOOM) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                        Mirror3D_4Layer.this.startX = motionEvent.getX() - Mirror3D_4Layer.this.prevDx;
                    }
                } else if (action == 1) {
                    Log.i(Mirror3D_4Layer.TAG, "UP");
                    Mirror3D_4Layer.this.mode = Mode.NONE;
                    Mirror3D_4Layer mirror3D_4Layer2 = Mirror3D_4Layer.this;
                    mirror3D_4Layer2.prevDx = mirror3D_4Layer2.dx;
                } else if (action != 2) {
                    if (action == 5) {
                        Mirror3D_4Layer.this.mode = Mode.ZOOM;
                    } else if (action == 6) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                    }
                } else if (Mirror3D_4Layer.this.mode == Mode.DRAG) {
                    Mirror3D_4Layer.this.dx = motionEvent.getX() - Mirror3D_4Layer.this.startX;
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                if ((Mirror3D_4Layer.this.mode == Mode.DRAG && Mirror3D_4Layer.this.scale >= Mirror3D_4Layer.MIN_ZOOM) || Mirror3D_4Layer.this.mode == Mode.ZOOM) {
                    Mirror3D_4Layer.this.getParent().requestDisallowInterceptTouchEvent(true);
                    float width = ((Mirror3D_4Layer.this.child().getWidth() - (Mirror3D_4Layer.this.child().getWidth() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    float height = ((Mirror3D_4Layer.this.child().getHeight() - (Mirror3D_4Layer.this.child().getHeight() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    Mirror3D_4Layer mirror3D_4Layer3 = Mirror3D_4Layer.this;
                    mirror3D_4Layer3.dx = Math.min(Math.max(mirror3D_4Layer3.dx, -width), width);
                    Mirror3D_4Layer mirror3D_4Layer4 = Mirror3D_4Layer.this;
                    mirror3D_4Layer4.dy = Math.min(Math.max(mirror3D_4Layer4.dy, -height), height);
                    Log.i(Mirror3D_4Layer.TAG, "Width: " + Mirror3D_4Layer.this.child().getWidth() + ", scale " + Mirror3D_4Layer.this.scale + ", dx " + Mirror3D_4Layer.this.dx + ", max " + width);
                    Mirror3D_4Layer.this.applyScaleAndTranslation();
                    mirror3D_4Layer.applyScaleAndTranslation(-Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, -Mirror3D_4Layer.this.dx, -Mirror3D_4Layer.this.dy);
                }
                return true;
            }
        });
    }

    public void init(Context context, final Mirror3D_4Layer mirror3D_4Layer, boolean z) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(new OnTouchListener() { // from class: com.gallery.photos.editphotovideo.utils.Mirror3D_4Layer.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & 255;
                if (action == 0) {
                    Log.i(Mirror3D_4Layer.TAG, "DOWN");
                    if (Mirror3D_4Layer.this.scale > Mirror3D_4Layer.MIN_ZOOM) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                        Mirror3D_4Layer.this.startY = motionEvent.getY() - Mirror3D_4Layer.this.prevDy;
                    }
                } else if (action == 1) {
                    Log.i(Mirror3D_4Layer.TAG, "UP");
                    Mirror3D_4Layer.this.mode = Mode.NONE;
                    Mirror3D_4Layer mirror3D_4Layer2 = Mirror3D_4Layer.this;
                    mirror3D_4Layer2.prevDy = mirror3D_4Layer2.dy;
                } else if (action != 2) {
                    if (action == 5) {
                        Mirror3D_4Layer.this.mode = Mode.ZOOM;
                    } else if (action == 6) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                    }
                } else if (Mirror3D_4Layer.this.mode == Mode.DRAG) {
                    Mirror3D_4Layer.this.dy = motionEvent.getY() - Mirror3D_4Layer.this.startY;
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                if ((Mirror3D_4Layer.this.mode == Mode.DRAG && Mirror3D_4Layer.this.scale >= Mirror3D_4Layer.MIN_ZOOM) || Mirror3D_4Layer.this.mode == Mode.ZOOM) {
                    Mirror3D_4Layer.this.getParent().requestDisallowInterceptTouchEvent(true);
                    float width = ((Mirror3D_4Layer.this.child().getWidth() - (Mirror3D_4Layer.this.child().getWidth() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    float height = ((Mirror3D_4Layer.this.child().getHeight() - (Mirror3D_4Layer.this.child().getHeight() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    Mirror3D_4Layer mirror3D_4Layer3 = Mirror3D_4Layer.this;
                    mirror3D_4Layer3.dx = Math.min(Math.max(mirror3D_4Layer3.dx, -width), width);
                    Mirror3D_4Layer mirror3D_4Layer4 = Mirror3D_4Layer.this;
                    mirror3D_4Layer4.dy = Math.min(Math.max(mirror3D_4Layer4.dy, -height), height);
                    Log.i(Mirror3D_4Layer.TAG, "Width: " + Mirror3D_4Layer.this.child().getWidth() + ", scale " + Mirror3D_4Layer.this.scale + ", dx " + Mirror3D_4Layer.this.dx + ", max " + width);
                    Mirror3D_4Layer.this.applyScaleAndTranslationVertical();
                    mirror3D_4Layer.applyScaleAndTranslationVertical(Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.dx, -Mirror3D_4Layer.this.dy);
                }
                return true;
            }
        });
    }

    public void init(Context context, final Mirror3D_4Layer mirror3D_4Layer, final Mirror3D_4Layer mirror3D_4Layer2, final Mirror3D_4Layer mirror3D_4Layer3) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(new OnTouchListener() { // from class: com.gallery.photos.editphotovideo.utils.Mirror3D_4Layer.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & 255;
                if (action == 0) {
                    Log.i(Mirror3D_4Layer.TAG, "DOWN");
                    if (Mirror3D_4Layer.this.scale > Mirror3D_4Layer.MIN_ZOOM) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                        Mirror3D_4Layer.this.startX = motionEvent.getX() - Mirror3D_4Layer.this.prevDx;
                    }
                } else if (action == 1) {
                    Log.i(Mirror3D_4Layer.TAG, "UP");
                    Mirror3D_4Layer.this.mode = Mode.NONE;
                    Mirror3D_4Layer mirror3D_4Layer4 = Mirror3D_4Layer.this;
                    mirror3D_4Layer4.prevDx = mirror3D_4Layer4.dx;
                } else if (action != 2) {
                    if (action == 5) {
                        Mirror3D_4Layer.this.mode = Mode.ZOOM;
                    } else if (action == 6) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                    }
                } else if (Mirror3D_4Layer.this.mode == Mode.DRAG) {
                    Mirror3D_4Layer.this.dx = motionEvent.getX() - Mirror3D_4Layer.this.startX;
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                if ((Mirror3D_4Layer.this.mode == Mode.DRAG && Mirror3D_4Layer.this.scale >= Mirror3D_4Layer.MIN_ZOOM) || Mirror3D_4Layer.this.mode == Mode.ZOOM) {
                    Mirror3D_4Layer.this.getParent().requestDisallowInterceptTouchEvent(true);
                    float width = ((Mirror3D_4Layer.this.child().getWidth() - (Mirror3D_4Layer.this.child().getWidth() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    float height = ((Mirror3D_4Layer.this.child().getHeight() - (Mirror3D_4Layer.this.child().getHeight() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    Mirror3D_4Layer mirror3D_4Layer5 = Mirror3D_4Layer.this;
                    mirror3D_4Layer5.dx = Math.min(Math.max(mirror3D_4Layer5.dx, -width), width);
                    Mirror3D_4Layer mirror3D_4Layer6 = Mirror3D_4Layer.this;
                    mirror3D_4Layer6.dy = Math.min(Math.max(mirror3D_4Layer6.dy, -height), height);
                    Log.i(Mirror3D_4Layer.TAG, "Width: " + Mirror3D_4Layer.this.child().getWidth() + ", scale " + Mirror3D_4Layer.this.scale + ", dx " + Mirror3D_4Layer.this.dx + ", max " + width);
                    Mirror3D_4Layer.this.applyScaleAndTranslation();
                    mirror3D_4Layer.applyScaleAndTranslation(-Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, -Mirror3D_4Layer.this.dx, Mirror3D_4Layer.this.dy);
                    mirror3D_4Layer2.applyScaleAndTranslation(Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.dx, Mirror3D_4Layer.this.dy);
                    mirror3D_4Layer3.applyScaleAndTranslation(-Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, -Mirror3D_4Layer.this.dx, Mirror3D_4Layer.this.dy);
                }
                return true;
            }
        });
    }

    public void init(Context context, final Mirror3D_4Layer mirror3D_4Layer, final Mirror3D_4Layer mirror3D_4Layer2) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(new OnTouchListener() { // from class: com.gallery.photos.editphotovideo.utils.Mirror3D_4Layer.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & 255;
                if (action == 0) {
                    Log.i(Mirror3D_4Layer.TAG, "DOWN");
                    if (Mirror3D_4Layer.this.scale > Mirror3D_4Layer.MIN_ZOOM) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                        Mirror3D_4Layer.this.startX = motionEvent.getX() - Mirror3D_4Layer.this.prevDx;
                    }
                } else if (action == 1) {
                    Log.i(Mirror3D_4Layer.TAG, "UP");
                    Mirror3D_4Layer.this.mode = Mode.NONE;
                    Mirror3D_4Layer mirror3D_4Layer3 = Mirror3D_4Layer.this;
                    mirror3D_4Layer3.prevDx = mirror3D_4Layer3.dx;
                } else if (action != 2) {
                    if (action == 5) {
                        Mirror3D_4Layer.this.mode = Mode.ZOOM;
                    } else if (action == 6) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                    }
                } else if (Mirror3D_4Layer.this.mode == Mode.DRAG) {
                    Mirror3D_4Layer.this.dx = motionEvent.getX() - Mirror3D_4Layer.this.startX;
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                if ((Mirror3D_4Layer.this.mode == Mode.DRAG && Mirror3D_4Layer.this.scale >= Mirror3D_4Layer.MIN_ZOOM) || Mirror3D_4Layer.this.mode == Mode.ZOOM) {
                    Mirror3D_4Layer.this.getParent().requestDisallowInterceptTouchEvent(true);
                    float width = ((Mirror3D_4Layer.this.child().getWidth() - (Mirror3D_4Layer.this.child().getWidth() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    float height = ((Mirror3D_4Layer.this.child().getHeight() - (Mirror3D_4Layer.this.child().getHeight() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    Mirror3D_4Layer mirror3D_4Layer4 = Mirror3D_4Layer.this;
                    mirror3D_4Layer4.dx = Math.min(Math.max(mirror3D_4Layer4.dx, -width), width);
                    Mirror3D_4Layer mirror3D_4Layer5 = Mirror3D_4Layer.this;
                    mirror3D_4Layer5.dy = Math.min(Math.max(mirror3D_4Layer5.dy, -height), height);
                    Log.i(Mirror3D_4Layer.TAG, "Width: " + Mirror3D_4Layer.this.child().getWidth() + ", scale " + Mirror3D_4Layer.this.scale + ", dx " + Mirror3D_4Layer.this.dx + ", max " + width);
                    Mirror3D_4Layer.this.applyScaleAndTranslation();
                    mirror3D_4Layer.applyScaleAndTranslation(-Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, -Mirror3D_4Layer.this.dx, Mirror3D_4Layer.this.dy);
                    mirror3D_4Layer2.applyScaleAndTranslation(Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.scale, Mirror3D_4Layer.this.dx, Mirror3D_4Layer.this.dy);
                }
                return true;
            }
        });
    }

    public void init(Context context) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(new OnTouchListener() { // from class: com.gallery.photos.editphotovideo.utils.Mirror3D_4Layer.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & 255;
                if (action == 0) {
                    Log.i(Mirror3D_4Layer.TAG, "DOWN");
                    if (Mirror3D_4Layer.this.scale > Mirror3D_4Layer.MIN_ZOOM) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                        Mirror3D_4Layer.this.startX = motionEvent.getX() - Mirror3D_4Layer.this.prevDx;
                        Mirror3D_4Layer.this.startY = motionEvent.getY() - Mirror3D_4Layer.this.prevDy;
                    }
                } else if (action == 1) {
                    Log.i(Mirror3D_4Layer.TAG, "UP");
                    Mirror3D_4Layer.this.mode = Mode.NONE;
                    Mirror3D_4Layer mirror3D_4Layer = Mirror3D_4Layer.this;
                    mirror3D_4Layer.prevDx = mirror3D_4Layer.dx;
                    Mirror3D_4Layer mirror3D_4Layer2 = Mirror3D_4Layer.this;
                    mirror3D_4Layer2.prevDy = mirror3D_4Layer2.dy;
                } else if (action != 2) {
                    if (action == 5) {
                        Mirror3D_4Layer.this.mode = Mode.ZOOM;
                    } else if (action == 6) {
                        Mirror3D_4Layer.this.mode = Mode.DRAG;
                    }
                } else if (Mirror3D_4Layer.this.mode == Mode.DRAG) {
                    Mirror3D_4Layer.this.dx = motionEvent.getX() - Mirror3D_4Layer.this.startX;
                    Mirror3D_4Layer.this.dy = motionEvent.getY() - Mirror3D_4Layer.this.startY;
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                if ((Mirror3D_4Layer.this.mode == Mode.DRAG && Mirror3D_4Layer.this.scale >= Mirror3D_4Layer.MIN_ZOOM) || Mirror3D_4Layer.this.mode == Mode.ZOOM) {
                    Mirror3D_4Layer.this.getParent().requestDisallowInterceptTouchEvent(true);
                    float width = ((Mirror3D_4Layer.this.child().getWidth() - (Mirror3D_4Layer.this.child().getWidth() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    float height = ((Mirror3D_4Layer.this.child().getHeight() - (Mirror3D_4Layer.this.child().getHeight() / Mirror3D_4Layer.this.scale)) / 2.0f) * Mirror3D_4Layer.this.scale;
                    Mirror3D_4Layer mirror3D_4Layer3 = Mirror3D_4Layer.this;
                    mirror3D_4Layer3.dx = Math.min(Math.max(mirror3D_4Layer3.dx, -width), width);
                    Mirror3D_4Layer mirror3D_4Layer4 = Mirror3D_4Layer.this;
                    mirror3D_4Layer4.dy = Math.min(Math.max(mirror3D_4Layer4.dy, -height), height);
                    Log.i(Mirror3D_4Layer.TAG, "Width: " + Mirror3D_4Layer.this.child().getWidth() + ", scale " + Mirror3D_4Layer.this.scale + ", dx " + Mirror3D_4Layer.this.dx + ", max " + width);
                    Mirror3D_4Layer.this.applyScaleAndTranslation();
                }
                return true;
            }
        });
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        Log.i(TAG, "onScale" + scaleFactor);
        if (this.lastScaleFactor == 0.0f || Math.signum(scaleFactor) == Math.signum(this.lastScaleFactor)) {
            float f = this.scale * scaleFactor;
            this.scale = f;
            this.scale = Math.max(MIN_ZOOM, Math.min(f, MAX_ZOOM));
            this.lastScaleFactor = scaleFactor;
            return true;
        }
        this.lastScaleFactor = 0.0f;
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        Log.i(TAG, "onScaleEnd");
    }

    public void applyScaleAndTranslation() {
        child().setScaleX(this.scale);
        child().setScaleY(this.scale);
        child().setTranslationX(this.dx);
        child().setTranslationY(this.dy);
    }

    public void applyScaleAndTranslationVertical() {
        child().setScaleX(this.scale);
        child().setScaleY(this.scale);
        child().setTranslationX(this.dx);
        child().setTranslationY(this.dy);
    }

    public void applyScaleAndTranslation(float f, float f2, float f3, float f4) {
        child().setScaleX(f);
        child().setScaleY(f2);
        child().setTranslationX(f3);
        child().setTranslationY(f4);
    }

    public void applyScaleAndTranslationVertical(float f, float f2, float f3) {
        child().setScaleX(-f);
        child().setScaleY(f);
        child().setTranslationX(f2);
        child().setTranslationY(f3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View child() {
        return getChildAt(0);
    }
}
