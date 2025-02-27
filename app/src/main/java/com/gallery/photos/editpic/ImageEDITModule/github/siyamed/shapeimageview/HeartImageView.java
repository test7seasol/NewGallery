package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;


import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.SvgShader;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class HeartImageView extends ShaderImageView {
    public HeartImageView(Context context) {
        super(context);
    }

    public HeartImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public HeartImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_heart, 1);
    }
}
