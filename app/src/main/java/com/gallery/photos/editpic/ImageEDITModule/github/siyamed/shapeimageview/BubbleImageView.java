package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.BubbleShader;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader.ShaderHelper;


/* loaded from: classes.dex */
public class BubbleImageView extends ShaderImageView {
    private BubbleShader shader;

    public BubbleImageView(Context context) {
        super(context);
    }

    public BubbleImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BubbleImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.github.siyamed.shapeimageview.ShaderImageView
    public ShaderHelper createImageViewHelper() {
        BubbleShader bubbleShader = new BubbleShader();
        this.shader = bubbleShader;
        return bubbleShader;
    }

    public int getTriangleHeightPx() {
        BubbleShader bubbleShader = this.shader;
        if (bubbleShader != null) {
            return bubbleShader.getTriangleHeightPx();
        }
        return 0;
    }

    public void setTriangleHeightPx(int i) {
        BubbleShader bubbleShader = this.shader;
        if (bubbleShader != null) {
            bubbleShader.setTriangleHeightPx(i);
            invalidate();
        }
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        BubbleShader bubbleShader = this.shader;
        if (bubbleShader != null) {
            return bubbleShader.getArrowPosition();
        }
        return BubbleShader.ArrowPosition.LEFT;
    }

    public void setArrowPosition(BubbleShader.ArrowPosition arrowPosition) {
        BubbleShader bubbleShader = this.shader;
        if (bubbleShader != null) {
            bubbleShader.setArrowPosition(arrowPosition);
            invalidate();
        }
    }
}
