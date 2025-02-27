package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path;

import android.content.Context;


import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser.IoUtil;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser.PathInfo;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser.SvgToPath;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class SvgUtil {
    private static final Map<Integer, PathInfo> PATH_MAP = new ConcurrentHashMap();

    public static final PathInfo readSvg(Context context, int i) {
        Map<Integer, PathInfo> map = PATH_MAP;
        PathInfo pathInfo = map.get(Integer.valueOf(i));
        if (pathInfo != null) {
            return pathInfo;
        }
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(i);
            PathInfo sVGFromInputStream = SvgToPath.getSVGFromInputStream(inputStream);
            map.put(Integer.valueOf(i), sVGFromInputStream);
            return sVGFromInputStream;
        } finally {
            IoUtil.closeQuitely(inputStream);
        }
    }
}
