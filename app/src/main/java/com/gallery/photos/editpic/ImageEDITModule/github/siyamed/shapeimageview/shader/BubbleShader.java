package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.gallery.photos.editpic.R;


/* loaded from: classes.dex */
public class BubbleShader extends ShaderHelper {
    private static final int DEFAULT_HEIGHT_DP = 10;
    private int triangleHeightPx;
    private final Path path = new Path();
    private ArrowPosition arrowPosition = ArrowPosition.LEFT;

    public enum ArrowPosition {
        LEFT,
        RIGHT
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void init(Context context, AttributeSet attributeSet, int i) {
        super.init(context, attributeSet, i);
        this.borderWidth = 0;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ShaderImageView, i, 0);
            this.triangleHeightPx = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ShaderImageView_siTriangleHeight, 0);
            this.arrowPosition = ArrowPosition.values()[obtainStyledAttributes.getInt(R.styleable.ShaderImageView_siArrowPosition, ArrowPosition.LEFT.ordinal())];
            obtainStyledAttributes.recycle();
        }
        if (this.triangleHeightPx == 0) {
            this.triangleHeightPx = dpToPx(context.getResources().getDisplayMetrics(), 10);
        }
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        canvas.save();
        canvas.concat(this.matrix);
        canvas.drawPath(this.path, paint);
        canvas.restore();
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void calculate(int i, int i2, float f, float f2, float f3, float f4, float f5) {
        this.path.reset();
        float f6 = -f4;
        float f7 = -f5;
        float f8 = this.triangleHeightPx / f3;
        float f9 = i + (f4 * 2.0f);
        float f10 = i2 + (f5 * 2.0f);
        float f11 = (f10 / 2.0f) + f7;
        this.path.setFillType(Path.FillType.EVEN_ODD);
        int i3 = AnonymousClass1.$SwitchMap$com$github$siyamed$shapeimageview$shader$BubbleShader$ArrowPosition[this.arrowPosition.ordinal()];
        if (i3 == 1) {
            float f12 = f8 + f6;
            this.path.addRect(f12, f7, f9 + f12, f10 + f7, Path.Direction.CW);
            this.path.moveTo(f6, f11);
            this.path.lineTo(f12, f11 - f8);
            this.path.lineTo(f12, f8 + f11);
            this.path.lineTo(f6, f11);
            return;
        }
        if (i3 != 2) {
            return;
        }
        float f13 = f9 + f6;
        float f14 = f13 - f8;
        this.path.addRect(f6, f7, f14, f10 + f7, Path.Direction.CW);
        this.path.moveTo(f13, f11);
        this.path.lineTo(f14, f11 - f8);
        this.path.lineTo(f14, f8 + f11);
        this.path.lineTo(f13, f11);
    }

    /* renamed from: com.github.siyamed.shapeimageview.shader.BubbleShader$1, reason: invalid class name */
    static   class AnonymousClass1 {
        static final   int[] $SwitchMap$com$github$siyamed$shapeimageview$shader$BubbleShader$ArrowPosition;

        static {
            int[] iArr = new int[ArrowPosition.values().length];
            $SwitchMap$com$github$siyamed$shapeimageview$shader$BubbleShader$ArrowPosition = iArr;
            try {
                iArr[ArrowPosition.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$github$siyamed$shapeimageview$shader$BubbleShader$ArrowPosition[ArrowPosition.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void reset() {
        this.path.reset();
    }

    public int getTriangleHeightPx() {
        return this.triangleHeightPx;
    }

    public void setTriangleHeightPx(int i) {
        this.triangleHeightPx = i;
    }

    public ArrowPosition getArrowPosition() {
        return this.arrowPosition;
    }

    public void setArrowPosition(ArrowPosition arrowPosition) {
        this.arrowPosition = arrowPosition;
    }
}
