package com.gallery.photos.editpic.ImageEDITModule.edit.resource;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import org.wysaid.common.SharedContext;
import org.wysaid.nativePort.CGEImageHandler;

/* loaded from: classes.dex */
public class OverlayFile {
    public static final OverlayCode[] OVERLAY_EFFECTS = {new OverlayCode(""), new OverlayCode("#unpack @krblend sr effect/overlay/blend_1.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_2.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_3.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_4.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_5.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_6.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_7.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_8.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_9.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_10.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_11.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_12.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_13.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_14.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_15.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_16.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_17.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_18.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_19.webp 100"), new OverlayCode("#unpack @krblend sr effect/overlay/blend_20.webp 100")};
    public static final OverlayCode[] MASK_EFFECTS = {new OverlayCode(""), new OverlayCode("#unpack @krblend hl effect/mask/mask_1.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_2.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_3.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_4.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_5.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_6.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_7.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_8.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_9.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_10.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_11.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_12.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_13.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_14.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_15.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_16.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_17.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_18.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_19.webp 100"), new OverlayCode("#unpack @krblend hl effect/mask/mask_20.webp 100")};
    public static final OverlayCode[] LIGHT_EFFECTS = {new OverlayCode(""), new OverlayCode("#unpack @krblend sr effect/light/light_1.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_2.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_3.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_4.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_5.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_6.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_7.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_8.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_9.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_10.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_11.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_12.webp 100"), new OverlayCode("#unpack @krblend sr effect/light/light_13.webp 100")};
    public static final OverlayCode[] DUST_EFFECTS = {new OverlayCode(""), new OverlayCode("#unpack @krblend sr effect/dust/dust_1.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_2.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_3.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_4.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_5.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_6.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_7.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_8.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_9.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_10.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_11.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_12.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_13.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_14.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_15.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_16.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_17.webp 100"), new OverlayCode("#unpack @krblend sr effect/dust/dust_18.webp 100")};
    public static final OverlayCode[] GRADIENT_EFFECTS = {new OverlayCode(""), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_1.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_2.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_3.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_4.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_5.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_6.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_7.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_8.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_9.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_10.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_11.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_12.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_13.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_14.webp 100"), new OverlayCode("#unpack @krblend sr effect/gradient/gradient_15.webp 100")};

    public static class OverlayCode {
        private String image;

        OverlayCode(String str) {
            this.image = str;
        }

        public String getImage() {
            return this.image;
        }

        public void setImage(String str) {
            this.image = str;
        }
    }

    public static List<Bitmap> getListBitmapOverlayEffect(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (OverlayCode overlayCode : OVERLAY_EFFECTS) {
            cGEImageHandler.setFilterWithConfig(overlayCode.getImage());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }

    public static List<Bitmap> getListBitmapGradientEffect(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (OverlayCode overlayCode : GRADIENT_EFFECTS) {
            cGEImageHandler.setFilterWithConfig(overlayCode.getImage());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }

    public static List<Bitmap> getListBitmapDustEffect(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (OverlayCode overlayCode : DUST_EFFECTS) {
            cGEImageHandler.setFilterWithConfig(overlayCode.getImage());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }

    public static List<Bitmap> getListBitmapMaskEffect(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (OverlayCode overlayCode : MASK_EFFECTS) {
            cGEImageHandler.setFilterWithConfig(overlayCode.getImage());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }

    public static List<Bitmap> getListBitmapLightEffect(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (OverlayCode overlayCode : LIGHT_EFFECTS) {
            cGEImageHandler.setFilterWithConfig(overlayCode.getImage());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }
}
