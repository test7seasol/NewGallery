package org.wysaid.common;

import static android.opengl.EGL14.EGL_OPENGL_ES2_BIT;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class SharedContext {
    public static final int EGL_RECORDABLE_ANDROID = 12610;
    public static final String LOG_TAG = "libCGE_java";
    private static int mBitsA = 8;
    private static int mBitsB = 8;
    private static int mBitsG = 8;
    private static int mBitsR = 8;
    private EGLConfig mConfig;
    private EGLContext mContext;
    private EGLDisplay mDisplay;
    private EGL10 mEgl;
    private GL10 mGl;
    private EGLSurface mSurface;

    public static void setContextColorBits(int i, int i2, int i3, int i4) {
        mBitsR = i;
        mBitsG = i2;
        mBitsB = i3;
        mBitsA = i4;
    }

    public static SharedContext create() {
        return create(EGL10.EGL_NO_CONTEXT, 64, 64, 1, null);
    }

    public static SharedContext create(int i, int i2) {
        return create(EGL10.EGL_NO_CONTEXT, i, i2, 1, null);
    }

    public static SharedContext create(EGLContext eGLContext, int i, int i2) {
        return create(eGLContext, i, i2, 1, null);
    }

    public static SharedContext create(EGLContext eGLContext, int i, int i2, int i3, Object obj) {
        SharedContext sharedContext = new SharedContext();
        if (sharedContext.initEGL(eGLContext, i, i2, i3, obj)) {
            return sharedContext;
        }
        sharedContext.release();
        return null;
    }

    public EGLContext getContext() {
        return this.mContext;
    }

    public EGLDisplay getDisplay() {
        return this.mDisplay;
    }

    public EGLSurface getSurface() {
        return this.mSurface;
    }

    public EGL10 getEGL() {
        return this.mEgl;
    }

    public GL10 getGL() {
        return this.mGl;
    }

    SharedContext() {
    }

    public void release() {
        Log.i("libCGE_java", "#### CGESharedGLContext Destroying context... ####");
        if (this.mDisplay != EGL10.EGL_NO_DISPLAY) {
            this.mEgl.eglMakeCurrent(this.mDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            this.mEgl.eglDestroyContext(this.mDisplay, this.mContext);
            this.mEgl.eglDestroySurface(this.mDisplay, this.mSurface);
            this.mEgl.eglTerminate(this.mDisplay);
        }
        this.mDisplay = EGL10.EGL_NO_DISPLAY;
        this.mSurface = EGL10.EGL_NO_SURFACE;
        this.mContext = EGL10.EGL_NO_CONTEXT;
    }

    public void makeCurrent() {
        EGL10 egl10 = this.mEgl;
        EGLDisplay eGLDisplay = this.mDisplay;
        EGLSurface eGLSurface = this.mSurface;
        if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mContext)) {
            return;
        }
        Log.e("libCGE_java", "eglMakeCurrent failed:" + this.mEgl.eglGetError());
    }

    public boolean swapBuffers() {
        return this.mEgl.eglSwapBuffers(this.mDisplay, this.mSurface);
    }

    private boolean initEGL(EGLContext sharedContext, int width, int height, int surfaceType, Object surface) {
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098; // Used to specify OpenGL ES version
        final int[] contextAttribs = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};

        // EGL Attributes for surface
        final int[] surfaceAttribs = {EGL10.EGL_WIDTH, width, EGL10.EGL_HEIGHT, height, EGL10.EGL_NONE};

        // EGL configuration attributes
        final int[] configAttribs = {
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, // Request OpenGL ES 2.0
                EGL10.EGL_RED_SIZE, 8,  // 8-bit red channel
                EGL10.EGL_GREEN_SIZE, 8,  // 8-bit green channel
                EGL10.EGL_BLUE_SIZE, 8,  // 8-bit blue channel
                EGL10.EGL_ALPHA_SIZE, 8,  // 8-bit alpha channel
                EGL10.EGL_DEPTH_SIZE, 16, // 16-bit depth buffer
                EGL10.EGL_STENCIL_SIZE, 8, // 8-bit stencil buffer
                EGL10.EGL_NONE
        };

        // Get the EGL instance
        mEgl = (EGL10) EGLContext.getEGL();
        mDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mDisplay == EGL10.EGL_NO_DISPLAY) {
            Log.e(LOG_TAG, "Failed to get EGL display");
            return false;
        }

        // Initialize EGL
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mDisplay, version)) {
            Log.e(LOG_TAG, "Failed to initialize EGL");
            return false;
        }

        // Choose an EGL configuration
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!mEgl.eglChooseConfig(mDisplay, configAttribs, configs, 1, numConfigs)) {
            Log.e(LOG_TAG, "Failed to choose EGL config");
            return false;
        }
        mConfig = configs[0];

        // Create EGL context
        mContext = mEgl.eglCreateContext(mDisplay, mConfig, sharedContext, contextAttribs);
        if (mContext == EGL10.EGL_NO_CONTEXT) {
            Log.e(LOG_TAG, "Failed to create EGL context");
            return false;
        }

        // Create EGL surface based on the surfaceType
        if (surfaceType == 1) { // Pbuffer surface
            mSurface = mEgl.eglCreatePbufferSurface(mDisplay, mConfig, surfaceAttribs);
        } else if (surfaceType == 2) { // Pixmap surface
            mSurface = mEgl.eglCreatePixmapSurface(mDisplay, mConfig, surface, surfaceAttribs);
        } else if (surfaceType == 4) { // Window surface
            mSurface = mEgl.eglCreateWindowSurface(mDisplay, mConfig, surface, surfaceAttribs);
        } else {
            Log.e(LOG_TAG, "Invalid surface type");
            return false;
        }

        if (mSurface == EGL10.EGL_NO_SURFACE) {
            Log.e(LOG_TAG, "Failed to create EGL surface");
            return false;
        }

        // Make the EGL context current
        if (!mEgl.eglMakeCurrent(mDisplay, mSurface, mSurface, mContext)) {
            Log.e(LOG_TAG, "Failed to make EGL context current");
            return false;
        }

        // Verify EGL version
        int[] actualContextVersion = new int[1];
        mEgl.eglQueryContext(mDisplay, mContext, EGL_CONTEXT_CLIENT_VERSION, actualContextVersion);
        Log.i(LOG_TAG, "EGL context created with version " + actualContextVersion[0]);

        // Get the GL10 instance
        mGl = (GL10) mContext.getGL();
        return true;
    }

}
