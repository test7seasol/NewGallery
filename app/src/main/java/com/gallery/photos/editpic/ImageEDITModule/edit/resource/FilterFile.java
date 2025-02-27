package com.gallery.photos.editpic.ImageEDITModule.edit.resource;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.wysaid.common.SharedContext;
import org.wysaid.nativePort.CGEImageHandler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FilterFile {
    public static final FiltersCode[] FILTERS = {new FiltersCode("", "none", Color.parseColor("#000000"),
            true), new FiltersCode("@adjust lut filter/bright_1.webp", "BG-1",
            Color.parseColor("#F3C892"), false),
            new FiltersCode("@adjust lut filter/bright_2.webp",
                    "BG-2", Color.parseColor("#F3C892"),
                    false), new FiltersCode("@adjust lut filter/bright_3.webp", "BG-3",
            Color.parseColor("#F3C892"), false), new FiltersCode("@adjust lut filter/bright_4.webp",
            "BG-4", Color.parseColor("#F3C892"), true), new FiltersCode("@adjust lut filter/color_1.webp", "CL-1", Color.parseColor("#DF8931"), false), new FiltersCode("@adjust lut filter/color_2.webp", "CL-2", Color.parseColor("#DF8931"), false), new FiltersCode("@adjust lut filter/chill_1.webp", "CL-3", Color.parseColor("#DF8931"), false), new FiltersCode("@adjust lut filter/code_1.webp", "CL-4", Color.parseColor("#DF8931"), true), new FiltersCode("@adjust lut filter/cube_1.webp", "CB-1", Color.parseColor("#FFD8CC"), false), new FiltersCode("@adjust lut filter/cube_2.webp", "CB-2", Color.parseColor("#FFD8CC"), false), new FiltersCode("@adjust lut filter/cube_3.webp", "CB-3", Color.parseColor("#FFD8CC"), false), new FiltersCode("@adjust lut filter/cube_4.webp", "CB-4", Color.parseColor("#FFD8CC"), false), new FiltersCode("@adjust lut filter/cube_5.webp", "CB-5", Color.parseColor("#FFD8CC"), false), new FiltersCode("@adjust lut filter/cube_6.webp", "CB-6", Color.parseColor("#FFD8CC"), true), new FiltersCode("@adjust lut filter/vintage_1.webp", "VT-1", Color.parseColor("#4FBDBA"), false), new FiltersCode("@adjust lut filter/vintage_2.webp", "VT-2", Color.parseColor("#4FBDBA"), false), new FiltersCode("@adjust lut filter/vintage_3.webp", "VT-3", Color.parseColor("#4FBDBA"), true), new FiltersCode("@adjust lut filter/tone_1.webp", "TN-1", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_2.webp", "TN-2", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_3.webp", "TN-3", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_4.webp", "TN-4", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_5.webp", "TN-5", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_6.webp", "TN-6", Color.parseColor("#F999B7"), false), new FiltersCode("@adjust lut filter/tone_7.webp", "TN-7", Color.parseColor("#F999B7"), true), new FiltersCode("@adjust lut filter/euro_1.webp", "ER-1", Color.parseColor("#EED6C4"), false), new FiltersCode("@adjust lut filter/euro_2.webp", "ER-3", Color.parseColor("#EED6C4"), false), new FiltersCode("@adjust lut filter/euro_3.webp", "ER-3", Color.parseColor("#EED6C4"), true), new FiltersCode("@adjust lut filter/film_1.webp", "FL-1", Color.parseColor("#E79E4F"), false), new FiltersCode("@adjust lut filter/film_2.webp", "FL-2", Color.parseColor("#E79E4F"), false), new FiltersCode("@adjust lut filter/film_3.webp", "FL-3", Color.parseColor("#E79E4F"), false), new FiltersCode("@adjust lut filter/film_4.webp", "FL-4", Color.parseColor("#E79E4F"), false), new FiltersCode("@adjust lut filter/fade_1.webp", "FL-7", Color.parseColor("#E79E4F"), true), new FiltersCode("@adjust lut filter/fuji_1.webp", "FJ-1", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_2.webp", "FJ-2", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_3.webp", "FJ-3", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_4.webp", "FJ-4", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_5.webp", "FJ-5", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_6.webp", "FJ-6", Color.parseColor("#9088D4"), false), new FiltersCode("@adjust lut filter/fuji_7.webp", "FJ-7", Color.parseColor("#9088D4"), true), new FiltersCode("@adjust lut filter/bw_1.webp", "BW-1", Color.parseColor("#C5C5C5"), false), new FiltersCode("@adjust lut filter/bw_2.webp", "BW-2", Color.parseColor("#C5C5C5"), false), new FiltersCode("@adjust lut filter/bw_3.webp", "BW-3", Color.parseColor("#C5C5C5"), false), new FiltersCode("@adjust lut filter/bw_4.webp", "BW-4", Color.parseColor("#C5C5C5"), true), new FiltersCode("@adjust lut filter/kodak_1.webp", "KD-1", Color.parseColor("#6A65D8"), false), new FiltersCode("@adjust lut filter/kodak_2.webp", "KD-2", Color.parseColor("#6A65D8"), false), new FiltersCode("@adjust lut filter/kodak_3.webp", "KD-3", Color.parseColor("#6A65D8"), false), new FiltersCode("@adjust lut filter/kodak_4.webp", "KD-4", Color.parseColor("#6A65D8"), false), new FiltersCode("@adjust lut filter/kodak_5.webp", "KD-5", Color.parseColor("#6A65D8"), true), new FiltersCode("@adjust lut filter/legacy_1.webp", "LG-1", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_2.webp", "LG-2", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_3.webp", "LG-3", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_3.webp", "LG-4", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_5.webp", "LG-5", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_6.webp", "LG-6", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_7.webp", "LG-7", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_8.webp", "LG-8", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_9.webp", "LG-9", Color.parseColor("#F67280"), false), new FiltersCode("@adjust lut filter/legacy_10.webp", "LG-10", Color.parseColor("#F67280"), true), new FiltersCode("@adjust lut filter/lomo_1.webp", "LM-1", Color.parseColor("#D4A5A5"), false), new FiltersCode("@adjust lut filter/lomo_2.webp", "LM-2", Color.parseColor("#D4A5A5"), false), new FiltersCode("@adjust lut filter/land_1.webp", "LM-6", Color.parseColor("#D4A5A5"), false), new FiltersCode("@adjust lut filter/light_1.webp", "LM-7", Color.parseColor("#D4A5A5"), true), new FiltersCode("@adjust lut filter/smooth_1.webp", "ST-1", Color.parseColor("#A6D0E4"), false), new FiltersCode("@adjust lut filter/smooth_2.webp", "ST-2", Color.parseColor("#A6D0E4"), false), new FiltersCode("@adjust lut filter/smooth_3.webp", "ST-3", Color.parseColor("#A6D0E4"), false), new FiltersCode("@adjust lut filter/smooth_4.webp", "ST-4", Color.parseColor("#A6D0E4"), false), new FiltersCode("@adjust lut filter/smooth_5.webp", "ST-5", Color.parseColor("#A6D0E4"), false), new FiltersCode("@adjust lut filter/smooth_6.webp", "ST-6", Color.parseColor("#A6D0E4"), true), new FiltersCode("@adjust lut filter/mood_1.webp", "MD-1", Color.parseColor("#40A798"), false), new FiltersCode("@adjust lut filter/mood_2.webp", "MD-2", Color.parseColor("#40A798"), false), new FiltersCode("@adjust lut filter/mood_3.webp", "MD-3", Color.parseColor("#40A798"), false), new FiltersCode("@adjust lut filter/mood_4.webp", "MD-4", Color.parseColor("#40A798"), true), new FiltersCode("@adjust lut filter/movie_1.webp", "MV-1", Color.parseColor("#FF6D24"), false), new FiltersCode("@adjust lut filter/movie_2.webp", "MV-2", Color.parseColor("#FF6D24"), false), new FiltersCode("@adjust lut filter/movie_3.webp", "MV-3", Color.parseColor("#FF6D24"), true), new FiltersCode("@adjust lut filter/normal_1.webp", "NR-1", Color.parseColor("#E2434B"), false), new FiltersCode("@adjust lut filter/normal_2.webp", "NR-2", Color.parseColor("#E2434B"), false), new FiltersCode("@adjust lut filter/normal_3.webp", "NR-3", Color.parseColor("#E2434B"), false), new FiltersCode("@adjust lut filter/normal_4.webp", "NR-4", Color.parseColor("#E2434B"), false), new FiltersCode("@adjust lut filter/normal_5.webp", "NR-5", Color.parseColor("#E2434B"), false), new FiltersCode("@adjust lut filter/normal_6.webp", "NR-6", Color.parseColor("#E2434B"), true), new FiltersCode("@adjust lut filter/cold_1.webp", "CD-1", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_2.webp", "CD-2", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_3.webp", "CD-3", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_4.webp", "CD-4", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_5.webp", "CD-5", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_6.webp", "CD-6", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_7.webp", "CD-7", Color.parseColor("#DBCC8F"), false), new FiltersCode("@adjust lut filter/cold_8.webp", "CD-8", Color.parseColor("#DBCC8F"), true), new FiltersCode("@adjust lut filter/palette_1.webp", "PT-1", Color.parseColor("#F090D9"), false), new FiltersCode("@adjust lut filter/palette_2.webp", "PT-2", Color.parseColor("#F090D9"), false), new FiltersCode("@adjust lut filter/palette_3.webp", "PT-3", Color.parseColor("#F090D9"), false), new FiltersCode("@adjust lut filter/palette_4.webp", "PT-4", Color.parseColor("#F090D9"), false), new FiltersCode("@adjust lut filter/palette_5.webp", "PT-5", Color.parseColor("#F090D9"), true), new FiltersCode("@adjust lut filter/pro_1.webp", "PRO-1", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_2.webp", "PRO-2", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_3.webp", "PRO-3", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_4.webp", "PRO-4", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_5.webp", "PRO-5", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_6.webp", "PRO-6", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_7.webp", "PRO-7", Color.parseColor("#96D1C7"), false), new FiltersCode("@adjust lut filter/pro_8.webp", "PRO-8", Color.parseColor("#96D1C7"), true), new FiltersCode("@adjust lut filter/retro_1.webp", "RT-1", Color.parseColor("#010A43"), false), new FiltersCode("@adjust lut filter/retro_2.webp", "RT-2", Color.parseColor("#010A43"), false), new FiltersCode("@adjust lut filter/retro_3.webp", "RT-3", Color.parseColor("#010A43"), false), new FiltersCode("@adjust lut filter/retro_4.webp", "RT-4", Color.parseColor("#010A43"), false), new FiltersCode("@adjust lut filter/retro_5.webp", "RT-5", Color.parseColor("#010A43"), false)};

    public static class FiltersCode {
        private String code;
        private int color;
        private boolean lastItem;
        private String name;

        FiltersCode(String str, String str2, int i, boolean z) {
            this.code = str;
            this.name = str2;
            this.color = i;
            this.lastItem = z;
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(String str) {
            this.code = str;
        }

        public boolean isLastItem() {
            return this.lastItem;
        }

        public void setLastItem(boolean z) {
            this.lastItem = z;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public int getColor() {
            return this.color;
        }

        public void setColor(int i) {
            this.color = i;
        }
    }

    public static List<Bitmap> getListBitmapFilter(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (FiltersCode filtersCode : FILTERS) {
            cGEImageHandler.setFilterWithConfig(filtersCode.getCode());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }

    public static Bitmap getBlurImageFromBitmap(Bitmap bitmap, float f) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig(MessageFormat.format("@blur lerp {0}", (f / 10.0f) + ""));
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap cloneBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }
}
