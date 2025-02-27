package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;


import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.RoundedShader;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;


/* loaded from: classes.dex */
public class RoundedImageView extends ShaderImageView {
    private RoundedShader shader;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        RoundedShader roundedShader = new RoundedShader();
        this.shader = roundedShader;
        return roundedShader;
    }

    public final int getRadius() {
        RoundedShader roundedShader = this.shader;
        if (roundedShader != null) {
            return roundedShader.getRadius();
        }
        return 0;
    }

    public final void setRadius(int i) {
        RoundedShader roundedShader = this.shader;
        if (roundedShader != null) {
            roundedShader.setRadius(i);
            invalidate();
        }
    }
}
