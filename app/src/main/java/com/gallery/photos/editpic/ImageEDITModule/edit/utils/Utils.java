package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.provider.Settings;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public class Utils {
    public static String md5_Hash(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            messageDigest = null;
        }
        messageDigest.update(str.getBytes(), 0, str.length());
        return new BigInteger(1, messageDigest.digest()).toString(16);
    }

    public static String getDeviceID(Context context) {
        return md5_Hash(Settings.Secure.getString(context.getContentResolver(), "android_id")).toUpperCase();
    }
}
