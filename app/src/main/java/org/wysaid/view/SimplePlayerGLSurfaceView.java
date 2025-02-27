package org.wysaid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import org.wysaid.common.Common;
import org.wysaid.texUtils.TextureRenderer;
import org.wysaid.texUtils.TextureRendererDrawOrigin;
import org.wysaid.texUtils.TextureRendererMask;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class SimplePlayerGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    static final   boolean $assertionsDisabled = false;
    public static final String LOG_TAG = "libCGE_java";
    private TextureRenderer mDrawer;
    private float mDrawerFlipScaleX;
    private float mDrawerFlipScaleY;
    private boolean mFitFullView;
    private long mFramesCount2;
    private boolean mIsUsingMask;
    private long mLastTimestamp2;
    private float mMaskAspectRatio;
    private OnCreateCallback mOnCreateCallback;
    PlayCompletionCallback mPlayCompletionCallback;
    private MediaPlayer mPlayer;
    PlayerInitializeCallback mPlayerInitCallback;
    PlayPreparedCallback mPreparedCallback;
    private TextureRenderer.Viewport mRenderViewport;
    private SurfaceTexture mSurfaceTexture;
    private long mTimeCount2;
    private float[] mTransformMatrix;
    private int mVideoHeight;
    private int mVideoTextureID;
    private Uri mVideoUri;
    private int mVideoWidth;
    private int mViewHeight;
    private int mViewWidth;

    public interface OnCreateCallback {
        void createOK();
    }

    public interface PlayCompletionCallback {
        void playComplete(MediaPlayer mediaPlayer);

        boolean playFailed(MediaPlayer mediaPlayer, int i, int i2);
    }

    public interface PlayPreparedCallback {
        void playPrepared(MediaPlayer mediaPlayer);
    }

    public interface PlayerInitializeCallback {
        void initPlayer(MediaPlayer mediaPlayer);
    }

    public interface SetMaskBitmapCallback {
        void setMaskOK(TextureRendererMask textureRendererMask);

        void unsetMaskOK(TextureRenderer textureRenderer);
    }

    public interface TakeShotCallback {
        void takeShotOK(Bitmap bitmap);
    }

    public void setTextureRenderer(TextureRenderer textureRenderer) {
        TextureRenderer textureRenderer2 = this.mDrawer;
        if (textureRenderer2 == null) {
            Log.e("libCGE_java", "Invalid Drawer!");
        } else if (textureRenderer2 != textureRenderer) {
            textureRenderer2.release();
            this.mDrawer = textureRenderer;
            calcViewport();
        }
    }

    public boolean isUsingMask() {
        return this.mIsUsingMask;
    }

    public int getViewWidth() {
        return this.mViewWidth;
    }

    public int getViewheight() {
        return this.mViewHeight;
    }

    public void setFitFullView(boolean z) {
        this.mFitFullView = z;
        if (this.mDrawer != null) {
            calcViewport();
        }
    }

    public void setPlayerInitializeCallback(PlayerInitializeCallback playerInitializeCallback) {
        this.mPlayerInitCallback = playerInitializeCallback;
    }

    public synchronized void setVideoUri(Uri uri, PlayPreparedCallback playPreparedCallback, PlayCompletionCallback playCompletionCallback) {
        this.mVideoUri = uri;
        this.mPreparedCallback = playPreparedCallback;
        this.mPlayCompletionCallback = playCompletionCallback;
        if (this.mDrawer != null) {
            queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.i("libCGE_java", "setVideoUri...");
                    if (SimplePlayerGLSurfaceView.this.mSurfaceTexture == null || SimplePlayerGLSurfaceView.this.mVideoTextureID == 0) {
                        SimplePlayerGLSurfaceView.this.mVideoTextureID = Common.genSurfaceTextureID();
                        SimplePlayerGLSurfaceView.this.mSurfaceTexture = new SurfaceTexture(SimplePlayerGLSurfaceView.this.mVideoTextureID);
                        SimplePlayerGLSurfaceView.this.mSurfaceTexture.setOnFrameAvailableListener(SimplePlayerGLSurfaceView.this);
                    }
                    SimplePlayerGLSurfaceView.this._useUri();
                }
            });
        }
    }

    public void setMaskBitmap(Bitmap bitmap, boolean z) {
        setMaskBitmap(bitmap, z, null);
    }

    public synchronized void setMaskBitmap(final Bitmap bitmap, final boolean z, final SetMaskBitmapCallback setMaskBitmapCallback) {
        if (this.mDrawer == null) {
            Log.e("libCGE_java", "setMaskBitmap after release!");
        } else {
            queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.2
                @Override // java.lang.Runnable
                public void run() {
                    if (bitmap == null) {
                        Log.i("libCGE_java", "Cancel Mask Bitmap!");
                        SimplePlayerGLSurfaceView.this.setMaskTexture(0, 1.0f);
                        SetMaskBitmapCallback setMaskBitmapCallback2 = setMaskBitmapCallback;
                        if (setMaskBitmapCallback2 != null) {
                            setMaskBitmapCallback2.unsetMaskOK(SimplePlayerGLSurfaceView.this.mDrawer);
                            return;
                        }
                        return;
                    }
                    Log.i("libCGE_java", "Use Mask Bitmap!");
                    int[] iArr = {0};
                    GLES20.glGenTextures(1, iArr, 0);
                    GLES20.glBindTexture(3553, iArr[0]);
                    GLUtils.texImage2D(3553, 0, bitmap, 0);
                    GLES20.glTexParameteri(3553, 10241, 9728);
                    GLES20.glTexParameteri(3553, 10240, 9728);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    SimplePlayerGLSurfaceView.this.setMaskTexture(iArr[0], bitmap.getWidth() / bitmap.getHeight());
                    if (setMaskBitmapCallback != null && (SimplePlayerGLSurfaceView.this.mDrawer instanceof TextureRendererMask)) {
                        setMaskBitmapCallback.setMaskOK((TextureRendererMask) SimplePlayerGLSurfaceView.this.mDrawer);
                    }
                    if (z) {
                        bitmap.recycle();
                    }
                }
            });
        }
    }

    public synchronized void setMaskTexture(int i, float f) {
        Log.i("libCGE_java", "setMaskTexture... ");
        if (i == 0) {
            TextureRenderer textureRenderer = this.mDrawer;
            if (textureRenderer instanceof TextureRendererMask) {
                textureRenderer.release();
                this.mDrawer = TextureRendererDrawOrigin.create(true);
            }
            this.mIsUsingMask = false;
        } else {
            TextureRenderer textureRenderer2 = this.mDrawer;
            if (!(textureRenderer2 instanceof TextureRendererMask)) {
                textureRenderer2.release();
                TextureRendererMask create = TextureRendererMask.create(true);
                create.setMaskTexture(i);
                this.mDrawer = create;
            }
            this.mIsUsingMask = true;
        }
        this.mMaskAspectRatio = f;
        calcViewport();
    }

    public synchronized MediaPlayer getPlayer() {
        if (this.mPlayer == null) {
            Log.e("libCGE_java", "Player is not initialized!");
        }
        return this.mPlayer;
    }

    public void setOnCreateCallback(final OnCreateCallback onCreateCallback) {
        if (this.mDrawer == null) {
            this.mOnCreateCallback = onCreateCallback;
        } else {
            queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.3
                @Override // java.lang.Runnable
                public void run() {
                    onCreateCallback.createOK();
                }
            });
        }
    }

    public SimplePlayerGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRenderViewport = new TextureRenderer.Viewport();
        this.mTransformMatrix = new float[16];
        this.mIsUsingMask = false;
        this.mMaskAspectRatio = 1.0f;
        this.mDrawerFlipScaleX = 1.0f;
        this.mDrawerFlipScaleY = 1.0f;
        this.mViewWidth = 1000;
        this.mViewHeight = 1000;
        this.mVideoWidth = 1000;
        this.mVideoHeight = 1000;
        this.mFitFullView = false;
        this.mTimeCount2 = 0L;
        this.mFramesCount2 = 0L;
        this.mLastTimestamp2 = 0L;
        Log.i("libCGE_java", "MyGLSurfaceView Construct...");
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 8, 0);
        getHolder().setFormat(1);
        setRenderer(this);
        setRenderMode(0);
        setZOrderOnTop(true);
        Log.i("libCGE_java", "MyGLSurfaceView Construct OK...");
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Log.i("libCGE_java", "video player onSurfaceCreated...");
        GLES20.glDisable(2929);
        GLES20.glDisable(2960);
        TextureRendererDrawOrigin create = TextureRendererDrawOrigin.create(true);
        this.mDrawer = create;
        if (create == null) {
            Log.e("libCGE_java", "Create Drawer Failed!");
            return;
        }
        OnCreateCallback onCreateCallback = this.mOnCreateCallback;
        if (onCreateCallback != null) {
            onCreateCallback.createOK();
        }
        if (this.mVideoUri != null) {
            if (this.mSurfaceTexture == null || this.mVideoTextureID == 0) {
                this.mVideoTextureID = Common.genSurfaceTextureID();
                SurfaceTexture surfaceTexture = new SurfaceTexture(this.mVideoTextureID);
                this.mSurfaceTexture = surfaceTexture;
                surfaceTexture.setOnFrameAvailableListener(this);
                _useUri();
            }
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.mViewWidth = i;
        this.mViewHeight = i2;
        calcViewport();
    }

    public void release() {
        Log.i("libCGE_java", "Video player view release...");
        if (this.mPlayer != null) {
            queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.4
                @Override // java.lang.Runnable
                public void run() {
                    Log.i("libCGE_java", "Video player view release run...");
                    if (SimplePlayerGLSurfaceView.this.mPlayer != null) {
                        SimplePlayerGLSurfaceView.this.mPlayer.setSurface(null);
                        if (SimplePlayerGLSurfaceView.this.mPlayer.isPlaying()) {
                            SimplePlayerGLSurfaceView.this.mPlayer.stop();
                        }
                        SimplePlayerGLSurfaceView.this.mPlayer.release();
                        SimplePlayerGLSurfaceView.this.mPlayer = null;
                    }
                    if (SimplePlayerGLSurfaceView.this.mDrawer != null) {
                        SimplePlayerGLSurfaceView.this.mDrawer.release();
                        SimplePlayerGLSurfaceView.this.mDrawer = null;
                    }
                    if (SimplePlayerGLSurfaceView.this.mSurfaceTexture != null) {
                        SimplePlayerGLSurfaceView.this.mSurfaceTexture.release();
                        SimplePlayerGLSurfaceView.this.mSurfaceTexture = null;
                    }
                    if (SimplePlayerGLSurfaceView.this.mVideoTextureID != 0) {
                        GLES20.glDeleteTextures(1, new int[]{SimplePlayerGLSurfaceView.this.mVideoTextureID}, 0);
                        SimplePlayerGLSurfaceView.this.mVideoTextureID = 0;
                    }
                    SimplePlayerGLSurfaceView.this.mIsUsingMask = false;
                    SimplePlayerGLSurfaceView.this.mPreparedCallback = null;
                    SimplePlayerGLSurfaceView.this.mPlayCompletionCallback = null;
                    Log.i("libCGE_java", "Video player view release OK");
                }
            });
        }
    }

    @Override // android.opengl.GLSurfaceView
    public void onPause() {
        Log.i("libCGE_java", "surfaceview onPause ...");
        super.onPause();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture == null) {
            return;
        }
        surfaceTexture.updateTexImage();
        if (this.mPlayer.isPlaying()) {
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClear(16384);
            GLES20.glViewport(0, 0, this.mViewWidth, this.mViewHeight);
            this.mSurfaceTexture.getTransformMatrix(this.mTransformMatrix);
            this.mDrawer.setTransform(this.mTransformMatrix);
            this.mDrawer.renderTexture(this.mVideoTextureID, this.mRenderViewport);
        }
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
        if (this.mLastTimestamp2 == 0) {
            this.mLastTimestamp2 = System.currentTimeMillis();
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mFramesCount2 + 1;
        this.mFramesCount2 = j;
        long j2 = this.mTimeCount2 + (currentTimeMillis - this.mLastTimestamp2);
        this.mTimeCount2 = j2;
        this.mLastTimestamp2 = currentTimeMillis;
        if (j2 >= 1000.0d) {
            Log.i("libCGE_java", String.format("播放帧率: %d", Long.valueOf(j)));
            this.mTimeCount2 = (long) (this.mTimeCount2 - 1000.0d);
            this.mFramesCount2 = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calcViewport() {
        float f;
        if (this.mIsUsingMask) {
            flushMaskAspectRatio();
            f = this.mMaskAspectRatio;
        } else {
            this.mDrawer.setFlipscale(this.mDrawerFlipScaleX, this.mDrawerFlipScaleY);
            f = this.mVideoWidth / this.mVideoHeight;
        }
        int i = this.mViewWidth;
        int i2 = this.mViewHeight;
        float f2 = f / (i / i2);
        if (!this.mFitFullView ? f2 > 1.0d : f2 <= 1.0d) {
            i = (int) (i2 * f);
        } else {
            i2 = (int) (i / f);
        }
        this.mRenderViewport.width = i;
        this.mRenderViewport.height = i2;
        TextureRenderer.Viewport viewport = this.mRenderViewport;
        viewport.x = (this.mViewWidth - viewport.width) / 2;
        TextureRenderer.Viewport viewport2 = this.mRenderViewport;
        viewport2.y = (this.mViewHeight - viewport2.height) / 2;
        Log.i("libCGE_java", String.format("View port: %d, %d, %d, %d", Integer.valueOf(this.mRenderViewport.x), Integer.valueOf(this.mRenderViewport.y), Integer.valueOf(this.mRenderViewport.width), Integer.valueOf(this.mRenderViewport.height)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _useUri() {
        MediaPlayer mediaPlayer = this.mPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mPlayer.reset();
        } else {
            this.mPlayer = new MediaPlayer();
        }
        try {
            this.mPlayer.setDataSource(getContext(), this.mVideoUri);
            this.mPlayer.setSurface(new Surface(this.mSurfaceTexture));
            PlayerInitializeCallback playerInitializeCallback = this.mPlayerInitCallback;
            if (playerInitializeCallback != null) {
                playerInitializeCallback.initPlayer(this.mPlayer);
            }
            this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.6
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer2) {
                    if (SimplePlayerGLSurfaceView.this.mPlayCompletionCallback != null) {
                        SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playComplete(SimplePlayerGLSurfaceView.this.mPlayer);
                    }
                    Log.i("libCGE_java", "Video Play Over");
                }
            });
            this.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.7
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mediaPlayer2) {
                    SimplePlayerGLSurfaceView.this.mVideoWidth = mediaPlayer2.getVideoWidth();
                    SimplePlayerGLSurfaceView.this.mVideoHeight = mediaPlayer2.getVideoHeight();
                    SimplePlayerGLSurfaceView.this.queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.7.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SimplePlayerGLSurfaceView.this.calcViewport();
                        }
                    });
                    if (SimplePlayerGLSurfaceView.this.mPreparedCallback != null) {
                        SimplePlayerGLSurfaceView.this.mPreparedCallback.playPrepared(SimplePlayerGLSurfaceView.this.mPlayer);
                    } else {
                        mediaPlayer2.start();
                    }
                    Log.i("libCGE_java", String.format("Video resolution 1: %d x %d", Integer.valueOf(SimplePlayerGLSurfaceView.this.mVideoWidth), Integer.valueOf(SimplePlayerGLSurfaceView.this.mVideoHeight)));
                }
            });
            this.mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.8
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                    if (SimplePlayerGLSurfaceView.this.mPlayCompletionCallback != null) {
                        return SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playFailed(mediaPlayer2, i, i2);
                    }
                    return false;
                }
            });
            try {
                this.mPlayer.prepareAsync();
            } catch (Exception e) {
                Log.i("libCGE_java", String.format("Error handled: %s, play failure handler would be called!", e.toString()));
                if (this.mPlayCompletionCallback != null) {
                    post(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.9
                        @Override // java.lang.Runnable
                        public void run() {
                            if (SimplePlayerGLSurfaceView.this.mPlayCompletionCallback == null || SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playFailed(SimplePlayerGLSurfaceView.this.mPlayer, 1, -1010)) {
                                return;
                            }
                            SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playComplete(SimplePlayerGLSurfaceView.this.mPlayer);
                        }
                    });
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            Log.e("libCGE_java", "useUri failed");
            if (this.mPlayCompletionCallback != null) {
                post(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.5
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SimplePlayerGLSurfaceView.this.mPlayCompletionCallback == null || SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playFailed(SimplePlayerGLSurfaceView.this.mPlayer, 1, -1010)) {
                            return;
                        }
                        SimplePlayerGLSurfaceView.this.mPlayCompletionCallback.playComplete(SimplePlayerGLSurfaceView.this.mPlayer);
                    }
                });
            }
        }
    }

    private void flushMaskAspectRatio() {
        float f = (this.mVideoWidth / this.mVideoHeight) / this.mMaskAspectRatio;
        if (f > 1.0f) {
            this.mDrawer.setFlipscale(this.mDrawerFlipScaleX / f, this.mDrawerFlipScaleY);
        } else {
            this.mDrawer.setFlipscale(this.mDrawerFlipScaleX, f * this.mDrawerFlipScaleY);
        }
    }

    public synchronized void takeShot(final TakeShotCallback takeShotCallback) {
        if (this.mDrawer == null) {
            Log.e("libCGE_java", "Drawer not initialized!");
            takeShotCallback.takeShotOK(null);
        } else {
            queueEvent(new Runnable() { // from class: org.wysaid.view.SimplePlayerGLSurfaceView.10
                @Override // java.lang.Runnable
                public void run() {
                    IntBuffer allocate = IntBuffer.allocate(SimplePlayerGLSurfaceView.this.mRenderViewport.width * SimplePlayerGLSurfaceView.this.mRenderViewport.height);
                    GLES20.glReadPixels(SimplePlayerGLSurfaceView.this.mRenderViewport.x, SimplePlayerGLSurfaceView.this.mRenderViewport.y, SimplePlayerGLSurfaceView.this.mRenderViewport.width, SimplePlayerGLSurfaceView.this.mRenderViewport.height, 6408, 5121, allocate);
                    Bitmap createBitmap = Bitmap.createBitmap(SimplePlayerGLSurfaceView.this.mRenderViewport.width, SimplePlayerGLSurfaceView.this.mRenderViewport.height, Bitmap.Config.ARGB_8888);
                    createBitmap.copyPixelsFromBuffer(allocate);
                    Bitmap createBitmap2 = Bitmap.createBitmap(SimplePlayerGLSurfaceView.this.mRenderViewport.width, SimplePlayerGLSurfaceView.this.mRenderViewport.height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap2);
                    Matrix matrix = new Matrix();
                    matrix.setTranslate(0.0f, (-SimplePlayerGLSurfaceView.this.mRenderViewport.height) / 2.0f);
                    matrix.postScale(1.0f, -1.0f);
                    matrix.postTranslate(0.0f, SimplePlayerGLSurfaceView.this.mRenderViewport.height / 2.0f);
                    canvas.drawBitmap(createBitmap, matrix, null);
                    createBitmap.recycle();
                    takeShotCallback.takeShotOK(createBitmap2);
                }
            });
        }
    }
}
