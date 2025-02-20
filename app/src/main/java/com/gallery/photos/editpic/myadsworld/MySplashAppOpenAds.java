package com.gallery.photos.editpic.myadsworld;

import static com.gallery.photos.editpic.Extensions.ExtKt.isConnected;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

public class MySplashAppOpenAds {
    private static int counter = 0;
    static boolean loaded = false;

    public interface onNext {
        void onNext();
    }

    public static void SplashAppOpenShow(Activity SplashActivity, onNext onNext) {
        MyAddPrefs appPreferences = new MyAddPrefs(SplashActivity);
        Log.d("APPOPEN", "ID: " + appPreferences.getAdmAppOpenId());
        if (isConnected(SplashActivity) && !appPreferences.getAdmAppOpenId().isEmpty()) {

            new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                    counter++;
                    Log.e("APPOPEN", " - " + MyAppOpenManager.isAdAvailable());
                    if (MyAppOpenManager.isFailappOpen()) {
                        cancel();
                        onFinish();
                    } else if (MyAppOpenManager.isAdAvailable()) {
                        loaded = true;
                        cancel();
                        onFinish();
                    }
                    Log.e("APPOPEN", "- " + counter);
                }

                public void onFinish() {
                    if (!MyAppOpenManager.isShowingAd && MyAppOpenManager.isAdAvailable()) {
                        MyAppOpenManager.showAdIfAvailableAds(SplashActivity, new onInterCloseCallBack() {
                            @Override
                            public void onAdsClose() {
                                onNext.onNext();
                            }
                        });
                    } else if (!loaded) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onNext.onNext();
                            }
                        }, 500);
                    }
//                    else {
//                        goToNextStep(appPreferences, SplashActivity, StartActivity, ActivityFirstTime);
//                    }
                }
            }.start();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNext.onNext();
                }
            }, 500);
        }
    }
}
