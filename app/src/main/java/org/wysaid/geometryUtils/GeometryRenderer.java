package org.wysaid.geometryUtils;

import android.opengl.GLES20;

import org.wysaid.common.ProgramObject;

/* loaded from: classes4.dex */
public class GeometryRenderer {
    protected static final String CANVAS_SIZE = "canvasSize";
    protected static final String COLOR_NAME = "color";
    protected static final String POSITION_NAME = "vPosition";
    private static final String fshDrawOrigin = "precision mediump float;\nuniform vec4 color;\nvoid main()\n{\n   gl_FragColor = color;\n}";
    protected static final String vshDrawDefault = "attribute vec2 vPosition;\nuniform vec2 canvasSize;\nvoid main()\n{\n   gl_Position = vec4((vPosition / canvasSize) * 2.0 - 1.0, 0.0, 1.0);\n}";
    protected float mCanvasHeight;
    protected float mCanvasWidth;
    protected ProgramObject mProgram;
    protected int mVertexBuffer;

    GeometryRenderer() {
    }

    protected boolean init() {
        ProgramObject programObject = new ProgramObject();
        this.mProgram = programObject;
        programObject.bindAttribLocation(POSITION_NAME, 0);
        if (!this.mProgram.init(vshDrawDefault, fshDrawOrigin)) {
            release();
            return false;
        }
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setCanvasSize(1.0f, 1.0f);
        return true;
    }

    public void release() {
        ProgramObject programObject = this.mProgram;
        if (programObject != null) {
            programObject.release();
            this.mProgram = null;
        }
        int i = this.mVertexBuffer;
        if (i != 0) {
            GLES20.glDeleteBuffers(1, new int[]{i}, 0);
            this.mVertexBuffer = 0;
        }
    }

    public static GeometryRenderer create() {
        GeometryRenderer geometryRenderer = new GeometryRenderer();
        if (geometryRenderer.init()) {
            return geometryRenderer;
        }
        geometryRenderer.release();
        return null;
    }

    public void setColor(float f, float f2, float f3, float f4) {
        this.mProgram.bind();
        this.mProgram.sendUniformf("color", f, f2, f3, f4);
    }

    public int getVertexBuffer() {
        return this.mVertexBuffer;
    }

    public void setVertexBuffer(int i) {
        this.mVertexBuffer = i;
    }

    public void setCanvasSize(float f, float f2) {
        this.mCanvasWidth = f;
        this.mCanvasHeight = f2;
        this.mProgram.bind();
        this.mProgram.sendUniformf(CANVAS_SIZE, f, f2);
    }

    public ProgramObject getProgram() {
        return this.mProgram;
    }

    public void bindBufferAttrib() {
        GLES20.glBindBuffer(34962, this.mVertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 0, 0);
    }

    public void render(int i, int i2, int i3) {
        bindBufferAttrib();
        this.mProgram.bind();
        GLES20.glDrawArrays(i, i2, i3);
    }
}
