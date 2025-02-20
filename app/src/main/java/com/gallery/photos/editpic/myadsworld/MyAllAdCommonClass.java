package com.gallery.photos.editpic.myadsworld;

import static com.gallery.photos.editpic.Extensions.ExtKt.isConnected;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.myadsworld.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;

public class MyAllAdCommonClass {

    //testing
//    public static final String JSON_URL = "https://7seasol-application.s3.amazonaws.com/admin_prod/comtesting.json";

    // Live
//    public static final String JSON_URL = "https://7seasol-application.s3.amazonaws.com/admin_prod/pbz-tnyyrel-cubgbf-rqvgcvp.json";

    public static final String JSON_URL = "7+dH3xwW79YajlGM74j5OKZ6Z6wBx7zUfM+f4xFUnE18qcgC4roZdtTR4a8tVb+s52yE3zQht/JgSmwNVl+cgQeGt7tMruwTcgKxaaFktYMCvGSPO76ptHbdt+TESjiM";

    public static InterstitialAd mInterstitialAd;
    public static MyListener myListener;
    public static NativeAd loadednative;
    public static int count_inters = 0;
    public static boolean isnativeload = true;
    public static int amfb_native_cnt = 0;
    public static AdView adViewBanner;

    public interface MyListener {
        void callback();
    }

    public static void SmallNativeBannerLoad(Context context, final TemplateView template, ShimmerFrameLayout shimmerFrameLayout, String nativeId) {

        AdLoader adLoader = new AdLoader.Builder(context, nativeId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.e("NativeFirstnativeload", "Firston NativeAdLoaded: ");
                template.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                template.setNativeAd(nativeAd, new MyAddPrefs(context).getButtonColor());
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("NativeFirstnativeload", "onAdFailedToLoad: " + loadAdError.getMessage());
                isnativeload = false;
                shimmerFrameLayout.setVisibility(View.GONE);
//                load_Fb_Native_banner(context, template, rl, fblayout, txtadsloading, quizfram, shimmerFrameLayout);
            }
        }).build();


        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public static void showAdmobBanner(Activity activity, FrameLayout layout_adBanner, ShimmerFrameLayout shimmerContainerBanner, boolean isBig, String admBannerId) {
        Log.d("BANNERAD", "showAdmobBanner");
        if (isConnected(activity)) {
            try {
                loadBanner(activity, layout_adBanner, admBannerId, shimmerContainerBanner);
            } catch (Exception e) {
                shimmerContainerBanner.setVisibility(View.INVISIBLE);
                layout_adBanner.setVisibility(View.INVISIBLE);
            }
        } else {
            shimmerContainerBanner.setVisibility(View.INVISIBLE);
            layout_adBanner.setVisibility(View.INVISIBLE);
        }
    }

    public static void loadBanner(Activity activity, boolean isBig, ShimmerFrameLayout shimmerContainerBanner, LinearLayoutCompat layout_adBanner, String admBannerId) {

        adViewBanner = new AdView(activity);
        adViewBanner.setAdUnitId(admBannerId);

        if (isBig) {
            AdSize adSize = AdSize.MEDIUM_RECTANGLE;
            adViewBanner.setAdSize(adSize);
        } else {
            AdSize adSize = AdSize.BANNER;
            adViewBanner.setAdSize(adSize);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        adViewBanner.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                shimmerContainerBanner.setVisibility(View.GONE);
                layout_adBanner.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                shimmerContainerBanner.setVisibility(View.GONE);
            }
        });
    }

    public static void loadBanner(Activity activity, FrameLayout frameLayout, String admBannerId, ShimmerFrameLayout shimmerContainerBanner) {
        AdView adView = new AdView(activity);

        // Get display metrics
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // Get screen density
        float density = outMetrics.density;

        // Get the width of the frame layout (if available), or use screen width
        float adWidthPixels = frameLayout.getWidth();
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
        }

        // Calculate ad width in dp
        int adWidth = (int) (adWidthPixels / density);

        // Get adaptive banner ad size
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);

        // Add adView to frameLayout
        frameLayout.addView(adView);

        // Set the ad unit ID (replace "BannerId" with the actual ID key or string resource)
        adView.setAdUnitId(admBannerId); // Example: replace "R.string.BannerId" with your actual banner ID or resource

        // Set the ad size
        adView.setAdSize(adSize);

        // Create and load the ad request
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("BANNERAD", "Banner onAdFailedToLoad: " + loadAdError.getMessage());
                shimmerContainerBanner.setVisibility(View.INVISIBLE);
                frameLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdLoaded() {
                Log.d("BANNERAD", "Banner onAdLoad Banner");
                super.onAdLoaded();
                shimmerContainerBanner.setVisibility(View.INVISIBLE);

            }
        });
    }

    public static void load_Admob_Interstial(final Context context) {
        if (mInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, new MyAddPrefs((context)).getAdmInterId(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    Log.e("Admobintertitial", "Admob Inter FirstonAdLoaded: ");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("Admobintertitial", "Admob Inter onAdFailedToLoad: " + loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
        }
    }

    public static Dialog showdialog;

    public static void dialogProgress(Activity context) {
        try {
            if (showdialog == null) {
                showdialog = new Dialog(context, R.style.exitAddialog_style);
                showdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                showdialog.setContentView(R.layout.showads_dialog);
                showdialog.show();
                showdialog.setCancelable(false);
            }
        } catch (Exception e) {
        }
    }

    public static boolean adInterFirst = true;

    public static boolean isInterOpen = false;

    public static void AdShowdialogFirstActivityQue(final Activity context, MyListener myListenerData) {
        myListener = myListenerData;

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    MyAllAdCommonClass.load_Admob_Interstial(context);
                    myListener.callback();
                    isInterOpen = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    myListener.callback();
                    isInterOpen = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    isInterOpen = true;

//                    Bundle bundle = new Bundle();
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Show_admob_inter");
//                    FirebaseAnalytics.getInstance(context).logEvent("admob_inter", bundle);
                }
            });
        } else {
            myListenerData.callback();
            isInterOpen = false;
        }

      /*  if (count_inters % new MyAddPrefs((context).getAdmShowclick() == 0) {
            adInterFirst = false;
            myListener = myListenerData;
            AdShowdialogCustomActivityQue(context, myListener);
        } else {
            myListenerData.callback();
            isInterOpen = false;
        }*/
        count_inters++;
    }

    public static void AdShowdialogCustomActivityQue(final Activity context, MyListener myListenerData) {
        myListener = myListenerData;

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    myListener.callback();
                    isInterOpen = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    myListener.callback();
                    isInterOpen = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    isInterOpen = true;

//                    Bundle bundle = new Bundle();
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Show_admob_inter");
//                    FirebaseAnalytics.getInstance(context).logEvent("admob_inter", bundle);
                }
            });
        } else {
            dialogProgress(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, new MyAddPrefs((context)).getSecAdmInterId(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Log.e("Admobintertitial", "Admob Inter FirstonAdLoaded2: ");
                    interstitialAd.show((Activity) context);
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            myListener.callback();
                            isInterOpen = false;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                            showdialog.dismiss();
                            myListener.callback();
                            isInterOpen = false;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isInterOpen = true;
//                            Bundle bundle = new Bundle();
//                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Show_admob_inter2");
//                            FirebaseAnalytics.getInstance(context).logEvent("admob_inter", bundle);
                            showdialog.dismiss();
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    showdialog.dismiss();
                    myListener.callback();
                    isInterOpen = false;
                }
            });
        }
    }

    public static void showNativeAdsId(Context context, TemplateView template, ShimmerFrameLayout shimmer_view_container, String admNativeId) {

        Log.d("FATZ", "Native Ad ID: " + admNativeId);

        AdLoader adLoader = new AdLoader.Builder(context, admNativeId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.e("Firstnativeload", "Firston NativeAdLoaded: ");
                template.setVisibility(View.VISIBLE);
                shimmer_view_container.setVisibility(View.GONE);
                template.setNativeAd(nativeAd, new MyAddPrefs((context)).getButtonColor());
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                template.setVisibility(View.GONE);
                isnativeload = false;
                shimmer_view_container.setVisibility(View.GONE);

//                Bundle bundle = new Bundle();
//                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Fail_admob_native");
//                FirebaseAnalytics.getInstance(context).logEvent("admob_native", bundle);

                Log.e("Firstnativeload", "onAdFailedToLoad111: " + loadAdError.getMessage());
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public static void startNativeLoad(Context context) {
        if (isnativeload) {
            isnativeload = false;
            AdLoader adLoader = new AdLoader.Builder(context, new MyAddPrefs(context).getAdmNativeId()).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    Log.e("Firstnativeload", "Firston NativeAdLoaded: ");
                    loadednative = nativeAd;
                }
            }).withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isnativeload = true;
                    loadednative = null;
                    Log.e("Firstnativeload", "onAdFailedToLoad: " + loadAdError.getMessage());
                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }
}
