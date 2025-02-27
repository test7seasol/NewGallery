package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.SvgShader;


/* loaded from: classes.dex */
public class ShapeImageView extends ShaderImageView {
    private SvgShader shader;

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ShapeImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        SvgShader svgShader = new SvgShader();
        this.shader = svgShader;
        return svgShader;
    }

    public void setStrokeMiter(int i) {
        SvgShader svgShader = this.shader;
        if (svgShader != null) {
            svgShader.setStrokeMiter(i);
            invalidate();
        }
    }

    public void setStrokeCap(int i) {
        SvgShader svgShader = this.shader;
        if (svgShader != null) {
            svgShader.setStrokeCap(i);
            invalidate();
        }
    }

    public void setStrokeJoin(int i) {
        SvgShader svgShader = this.shader;
        if (svgShader != null) {
            svgShader.setStrokeJoin(i);
            invalidate();
        }
    }

    public void setBorderType(int i) {
        SvgShader svgShader = this.shader;
        if (svgShader != null) {
            svgShader.setBorderType(i);
            invalidate();
        }
    }

    public void setShapeResId(int i) {
        SvgShader svgShader = this.shader;
        if (svgShader != null) {
            svgShader.setShapeResId(getContext(), i);
            invalidate();
        }
    }
}
