package com.gallery.photos.editpic.callendservice.overlayscreen;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.text.Normalizer;
import java.util.Locale;

import kotlin.text.Regex;
import kotlin.text.StringsKt;

public class OverlayUtil {

    public static final String XIAOMI = "xiaomi";

    public static final boolean isBackgroundStartActivityPermissionGranted(Context context) {
        try {
            Object systemService = context.getSystemService(Context.APP_OPS_SERVICE);
            Object invoke = AppOpsManager.class.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class).invoke((AppOpsManager) systemService, Integer.valueOf((int) 10021), Integer.valueOf(Process.myUid()), context.getPackageName());
            return ((Integer) invoke).intValue() == 0;
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            return true;
        }
    }

    public static final String getTagName(Context t) {
        String simpleName = t.getClass().getSimpleName();
        return simpleName;
    }

    public static final boolean isManufacturerXiaomi() {
        String MANUFACTURER = Build.MANUFACTURER;
        Locale ROOT = Locale.ROOT;
        String lowerCase = MANUFACTURER.toLowerCase(ROOT);
        return compareAreEquals(XIAOMI, lowerCase);
    }

    public static final boolean compareAreEquals(String str, String value) {
        return StringsKt.compareTo(normalize(str), normalize(value), true) == 0;
    }

    public static final String normalize(String str) {
        String normalize = Normalizer.normalize(str, Normalizer.Form.NFD);
        String lowerCase = new Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalize, "").toLowerCase(Locale.ROOT);
        return lowerCase;
    }

}
