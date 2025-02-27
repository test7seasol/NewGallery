package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.CircleShader;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;


/* loaded from: classes.dex */
public class CircularImageView extends ShaderImageView {
    private CircleShader shader;

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CircularImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        CircleShader circleShader = new CircleShader();
        this.shader = circleShader;
        return circleShader;
    }

    public float getBorderRadius() {
        CircleShader circleShader = this.shader;
        if (circleShader != null) {
            return circleShader.getBorderRadius();
        }
        return 0.0f;
    }

    public void setBorderRadius(float f) {
        CircleShader circleShader = this.shader;
        if (circleShader != null) {
            circleShader.setBorderRadius(f);
            invalidate();
        }
    }
}
