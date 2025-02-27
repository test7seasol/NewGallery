package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/* loaded from: classes.dex */
public class PathInfo {
    private final float height;
    private final Path path;
    private final float width;

    PathInfo(Path path2, float f, float f2) {
        this.path = path2;
        RectF rectF = new RectF();
        path2.computeBounds(rectF, true);
        if (f <= 0.0f && f2 <= 0.0f) {
            f = (float) Math.ceil((double) rectF.width());
            f2 = (float) Math.ceil((double) rectF.height());
            path2.offset(((float) Math.floor((double) rectF.left)) * -1.0f, ((float) Math.round(rectF.top)) * -1.0f);
        }
        this.width = f;
        this.height = f2;
    }
    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void transform(Matrix matrix, Path path) {
        this.path.transform(matrix, path);
    }
}
