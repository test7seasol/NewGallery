package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;


import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.SvgShader;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;

/* loaded from: classes.dex */
public class StarImageView extends ShaderImageView {
    public StarImageView(Context context) {
        super(context);
    }

    public StarImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StarImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_star);
    }
}
