package com.gallery.photos.editpic.callendservice.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class CDOUtiler {
    public static boolean isClickAppIcon = false;
    public static boolean isInitializedCalledOnce = false;

    public static void initializeAllAdsConfigs(Context context) {
        try {
            if (isInitializedCalledOnce) {
                return;
            }
            initializeMobileAdsSDK(context);
            isInitializedCalledOnce = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeMobileAdsSDK(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                String m = Application.getProcessName();
                if (!context.getPackageName().equals(m)) {
                    WebView.setDataDirectorySuffix(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public final void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(getTestDeviceIdList()).build());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static List<String> getTestDeviceIdList() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            arrayList.add("9ED2C9D18AD34D1EEF87A84F0C87A1C2");
            arrayList.add("C7B5E44F675716032C998B2413813FC8");
            arrayList.add("FF431CAA7544AABA508905A9852A47BA");
            arrayList.add("AB1BCA9C2B40832596FEF6E8FDACACDF");
            arrayList.add("DB221F148CC4CD672FC23DA8F8A78A4F");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void hideKeyboard(final Activity activity) {
        if (isOnMainThread()) {
            hideKeyboardSync(activity);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public final void run() {
                    CDOUtiler.hideKeyboardSync(activity);
                }
            });
        }
    }

    public static void hideKeyboardSync(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } else {
                inputMethodManager.hideSoftInputFromWindow(new View(activity).getWindowToken(), 0);
            }
            activity.getWindow().setSoftInputMode(3);
            activity.getCurrentFocus().clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideNavigationBar(Activity activity) {
        try {
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() | 2 | 4096);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
