package com.gallery.photos.editpic.callendservice.utils;


public class AppUtils {
    public static boolean isEmptyString(String str) {
        return str == null || str.trim().equals("null") || str.trim().equals("") || str.trim().length() <= 0;
    }

    public static boolean isAppRunning = false;

    public static String addExtraZero(long j) {
        if (j >= 10) {
            return String.valueOf(j);
        }
        return "0" + String.valueOf(j);
    }
}
