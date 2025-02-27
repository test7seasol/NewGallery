package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.SvgUtil;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser.PathInfo;
import com.gallery.photos.editpic.R;


public class SvgShader extends ShaderHelper {
    public static final int BORDER_TYPE_DEFAULT = 0;
    public static final int BORDER_TYPE_FILL = 1;
    public static final int STROKE_CAP_BUTT = 0;
    public static final int STROKE_CAP_DEFAULT = -1;
    public static final int STROKE_CAP_ROUND = 1;
    public static final int STROKE_CAP_SQUARE = 2;
    public static final int STROKE_JOIN_BEVEL = 0;
    public static final int STROKE_JOIN_DEFAULT = -1;
    public static final int STROKE_JOIN_MITER = 1;
    public static final int STROKE_JOIN_ROUND = 2;
    private final Path borderPath;
    private int borderType;
    private final Path path;
    private final float[] pathDimensions;
    private final Matrix pathMatrix;
    private int resId;
    private PathInfo shapePath;
    private int strokeCap;
    private int strokeJoin;
    private int strokeMiter;

    public SvgShader() {
        this.path = new Path();
        this.borderPath = new Path();
        this.pathMatrix = new Matrix();
        this.pathDimensions = new float[2];
        this.resId = -1;
        this.borderType = 0;
        this.strokeCap = -1;
        this.strokeJoin = -1;
        this.strokeMiter = 0;
    }

    public SvgShader(int i) {
        this.path = new Path();
        this.borderPath = new Path();
        this.pathMatrix = new Matrix();
        this.pathDimensions = new float[2];
        this.borderType = 0;
        this.strokeCap = -1;
        this.strokeJoin = -1;
        this.strokeMiter = 0;
        this.resId = i;
    }

    public SvgShader(int i, int i2) {
        this.path = new Path();
        this.borderPath = new Path();
        this.pathMatrix = new Matrix();
        this.pathDimensions = new float[2];
        this.strokeCap = -1;
        this.strokeJoin = -1;
        this.strokeMiter = 0;
        this.resId = i;
        this.borderType = i2;
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void init(Context context, AttributeSet attributeSet, int i) {
        super.init(context, attributeSet, i);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ShaderImageView, i, 0);
            this.resId = obtainStyledAttributes.getResourceId(R.styleable.ShaderImageView_siShape, this.resId);
            this.borderType = obtainStyledAttributes.getInt(R.styleable.ShaderImageView_siBorderType, this.borderType);
            this.strokeCap = obtainStyledAttributes.getInt(R.styleable.ShaderImageView_siStrokeCap, this.strokeCap);
            this.strokeJoin = obtainStyledAttributes.getInt(R.styleable.ShaderImageView_siStrokeJoin, this.strokeJoin);
            this.strokeMiter = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ShaderImageView_siStrokeMiter, this.strokeMiter);
            obtainStyledAttributes.recycle();
        }
        setShapeResId(context, this.resId);
        setBorderType(this.borderType);
        setStrokeCap(this.strokeCap);
        setStrokeJoin(this.strokeJoin);
        setStrokeMiter(this.strokeMiter);
    }

    public void setShapeResId(Context context, int i) {
        if (i != -1) {
            this.shapePath = SvgUtil.readSvg(context, i);
            return;
        }
        throw new RuntimeException("No resource is defined as shape");
    }

    public void setStrokeMiter(int i) {
        this.strokeMiter = i;
        if (i > 0) {
            this.borderPaint.setStrokeMiter(i);
        }
    }

    public void setStrokeCap(int i) {
        this.strokeCap = i;
        if (i == 0) {
            this.borderPaint.setStrokeCap(Paint.Cap.BUTT);
        } else if (i == 1) {
            this.borderPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            if (i != 2) {
                return;
            }
            this.borderPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }

    public void setStrokeJoin(int i) {
        this.strokeJoin = i;
        if (i == 0) {
            this.borderPaint.setStrokeJoin(Paint.Join.BEVEL);
        } else if (i == 1) {
            this.borderPaint.setStrokeJoin(Paint.Join.MITER);
        } else {
            if (i != 2) {
                return;
            }
            this.borderPaint.setStrokeJoin(Paint.Join.ROUND);
        }
    }

    public void setBorderType(int i) {
        this.borderType = i;
        if (i == 1) {
            this.borderPaint.setStyle(Paint.Style.FILL);
        } else {
            this.borderPaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        canvas.save();
        canvas.drawPath(this.borderPath, paint2);
        canvas.concat(this.matrix);
        canvas.drawPath(this.path, paint);
        canvas.restore();
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void calculate(int i, int i2, float f, float f2, float f3, float f4, float f5) {
        float f6;
        float f7;
        float f8;
        this.path.reset();
        this.borderPath.reset();
        this.pathDimensions[0] = this.shapePath.getWidth();
        this.pathDimensions[1] = this.shapePath.getHeight();
        this.pathMatrix.reset();
        float[] fArr = this.pathDimensions;
        float min = Math.min(f / fArr[0], f2 / fArr[1]);
        float round = Math.round((f - (this.pathDimensions[0] * min)) * 0.5f);
        float round2 = Math.round((f2 - (this.pathDimensions[1] * min)) * 0.5f);
        this.pathMatrix.setScale(min, min);
        this.pathMatrix.postTranslate(round, round2);
        this.shapePath.transform(this.pathMatrix, this.path);
        this.path.offset(this.borderWidth, this.borderWidth);
        if (this.borderWidth > 0) {
            this.pathMatrix.reset();
            if (this.borderType == 0) {
                f6 = this.viewWidth - this.borderWidth;
                f7 = this.viewHeight - this.borderWidth;
                f8 = this.borderWidth / 2.0f;
            } else {
                f6 = this.viewWidth;
                f7 = this.viewHeight;
                f8 = 0.0f;
            }
            float[] fArr2 = this.pathDimensions;
            float min2 = Math.min(f6 / fArr2[0], f7 / fArr2[1]);
            float round3 = Math.round(((f6 - (this.pathDimensions[0] * min2)) * 0.5f) + f8);
            float round4 = Math.round(((f7 - (this.pathDimensions[1] * min2)) * 0.5f) + f8);
            this.pathMatrix.setScale(min2, min2);
            this.pathMatrix.postTranslate(round3, round4);
            this.shapePath.transform(this.pathMatrix, this.borderPath);
        }
        this.pathMatrix.reset();
        this.matrix.invert(this.pathMatrix);
        this.path.transform(this.pathMatrix);
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void reset() {
        this.path.reset();
        this.borderPath.reset();
    }
}
