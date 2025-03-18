package com.gallery.photos.editpic.myadsworld;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import static com.gallery.photos.editpic.Extensions.ExtKt.isConnected;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.gallery.photos.editpic.Activity.MyApplicationClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class MyAppOpenManager implements LifecycleObserver {

    public static AppOpenAd appOpenAd = null;
    @SuppressLint("StaticFieldLeak")
    public static Activity currentActivity;
    public static boolean isShowingAd = false;
    public static boolean failappOpen = false;
    public static AppOpenAd.AppOpenAdLoadCallback loadCallback;
    public static String Strcheckad = "StrClosed";
    public static MyApplicationClass myApplication;

    public MyAppOpenManager(MyApplicationClass myApplication) {
        MyAppOpenManager.myApplication = myApplication;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        Log.d("FFL", "OnstartCallback");
        showAdIfAvailable();
    }

    public static void fetchAd() {
        MyAddPrefs appPreference = new MyAddPrefs(myApplication);
        if (isAdAvailable() || !isConnected(myApplication) || appPreference.getAdmAppOpenId().isEmpty()) {
            return;
        }
        failappOpen = false;
        Log.e("@@AppOpenManager2", " - " + appPreference.getAdmAppOpenId());
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                Log.e("@@AppOpenManager", "Loaded");
                appOpenAd = ad;
//                NativeAdsEvent.NativeAdsLoads(myApplication);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e("@@AppOpenManager", "Error: " + loadAdError.getMessage());
                appOpenAd = null;
                failappOpen = true;
//                NativeAdsEvent.NativeAdsLoads(myApplication);
            }
        };
        AppOpenAd.load(myApplication, appPreference.getAdmAppOpenId(), new AdRequest.Builder().build(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    public static void fetchAds(Activity activity) {
        MyAddPrefs appPreference = new MyAddPrefs(activity);
        if (isAdAvailable() || !isConnected(activity) || appPreference.getAdmAppOpenId().isEmpty()) {
            return;
        }
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                Log.e("@@AppOpenManager", "Loaded");
                appOpenAd = ad;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e("@@AppOpenManager", "Error: " + loadAdError.toString());
                failappOpen = true;
            }
        };
        AppOpenAd.load(activity, appPreference.getAdmAppOpenId(), new AdRequest.Builder().build(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    public static void showAdComposeAds(Activity activity, onInterCloseCallBack onInterCloseCallBack) {
        if (Strcheckad.equalsIgnoreCase("StrClosed") && !isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.e("notiiiiAppOpenManager", "AdDismissedFullScreenContent");
                    appOpenAd = null;
                    isShowingAd = false;
                    Strcheckad = "StrClosed";
                    onInterCloseCallBack.onAdsClose();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e("notiiiiAppOpenManager", "AdFailedToShowFullScreenContent" + adError.toString());
                    Strcheckad = "StrClosed";
                    onInterCloseCallBack.onAdsClose();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.e("notiiiiAppOpenManager", "AdShowedFullScreenContent");
                    Strcheckad = "StrOpen";
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);
        } else {
            onInterCloseCallBack.onAdsClose();
        }
    }

    public static void showAdIfAvailable() {
        if (Strcheckad.equalsIgnoreCase("StrClosed") && !isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.e("@@AppOpenManager111", "AdDismissedFullScreenContent");
                    appOpenAd = null;
                    isShowingAd = false;
                    fetchAd();
                    Strcheckad = "StrClosed";
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e("@@AppOpenManager111", "AdFailedToShowFullScreenContent" + adError.toString());
                    Strcheckad = "StrClosed";
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.e("@@AppOpenManager111", "AdShowedFullScreenContent");
                    Strcheckad = "StrOpen";
                    isShowingAd = true;
                }
            };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            Log.e("@@AppOpenManager111", " - fetchAd going");
            fetchAd();
        }
    }

    public static void showAdIfAvailableAds(Activity activity, onInterCloseCallBack onInterCloseCallBack) {
        if (Strcheckad.equalsIgnoreCase("StrClosed") && !isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.e("@@AppOpenManager", "AdDismissedFullScreenContent");
                    appOpenAd = null;
                    isShowingAd = false;
                    fetchAds(activity);
                    Strcheckad = "StrClosed";
                    onInterCloseCallBack.onAdsClose();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e("@@AppOpenManager", "AdFailedToShowFullScreenContent" + adError.toString());
                    Strcheckad = "StrClosed";
                    onInterCloseCallBack.onAdsClose();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.e("@@AppOpenManager", "AdShowedFullScreenContent");
                    Strcheckad = "StrOpen";
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);
        } else {
            onInterCloseCallBack.onAdsClose();
            fetchAds(activity);
        }
    }

    public static boolean isAdAvailable() {
        return appOpenAd != null;
    }

    public static boolean isFailappOpen() {
        return failappOpen;
    }

    public static void clearInstance() {
        currentActivity = null;
        isShowingAd = false;
        appOpenAd = null;
    }
}
