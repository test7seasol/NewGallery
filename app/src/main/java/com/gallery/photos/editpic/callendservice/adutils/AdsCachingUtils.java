package com.gallery.photos.editpic.callendservice.adutils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;


public class AdsCachingUtils {
    public static SetAdListener adListnerFullScreenBanner = null;
    public static AdRequest cdoBannerAdRequest = null;
    public static AdRequest cdoScreenAdRequest = null;
    public static boolean isBannerCDOAdImpression = false;
    public static boolean isBannerCDOAdLoadFailed = false;
    public static boolean isBannerCDOAdLoadProcessing = false;
    public static boolean isBannerCDOAdShow = false;
    public static AdView mBannerCDOAd;

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void loadAndShowLargeBannerAdsWithRequest(final Activity activity, String str, final RelativeLayout relativeLayout,
                                                            final View viewForSpaceFull,
                                                            final FrameLayout adContainerFullBanner) {
        try {
            Log.e("AdsLoading", "======AdsLoading====> request SECOND_BANNER ");
            if (isNetworkAvailable(activity)) {
                final AdView adView = new AdView(activity);
                adView.setAdUnitId(str);
                if (adContainerFullBanner != null) {
                    adContainerFullBanner.removeAllViews();
                    adContainerFullBanner.addView(adView);
                }

                AdSize adSize = AdSize.getPortraitInlineAdaptiveBannerAdSize(activity, -1);

//                AdSize adSize = AdSize.MEDIUM_RECTANGLE;
                if (viewForSpaceFull != null) {
                    int height2 = adSize.getHeight();
                    viewForSpaceFull.getLayoutParams().height = (int) ((height2 * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
                }

                adView.setAdSize(adSize);
                AdRequest adRequest = cdoScreenAdRequest;
                if (adRequest == null) {
                    adRequest = new AdRequest.Builder().build();
                }
                final AdRequest adRequest2 = adRequest;
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e("AdsLoading", "======AdsLoading====> request SECOND_BANNER onAdLoaded");
                        checkNullAndSetVisibility(relativeLayout, 0);
                        checkNullAndSetVisibility(adContainerFullBanner, 0);
                        if (adView.getParent() != null) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }
                        adContainerFullBanner.removeAllViews();
                        adContainerFullBanner.addView(adView);

                        viewForSpaceFull.setVisibility(View.GONE);
                        adContainerFullBanner.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        AdsCachingUtils.cdoScreenAdRequest = adRequest2;

                        Log.e("AdsLoading", "======AdsLoading====> request SECOND_BANNER onAdFailedToLoad");

                        checkNullAndSetVisibility(relativeLayout, 8);
                        checkNullAndSetVisibility(adContainerFullBanner, 8);

                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        AdsCachingUtils.cdoScreenAdRequest = null;
                    }
                });
                adView.loadAd(adRequest2);
                return;
            }
            checkNullAndSetVisibility(relativeLayout, 8);
            checkNullAndSetVisibility(adContainerFullBanner, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkNullAndSetVisibility(View view, int i) {
        if (view != null) {
            try {
                view.setVisibility(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isCDOBannerAvailable() {
        return mBannerCDOAd != null;
    }

    public static void preLoadBannerCdoAds(final Context context, String str) {
        try {
            Log.e("FullScreenBannerAds", "preLoadBannerCdoAds: call " + mBannerCDOAd + " " + isNetworkAvailable(context) + " " + isBannerCDOAdLoadProcessing + " " + isBannerCDOAdLoadFailed);
            if (mBannerCDOAd != null || !isNetworkAvailable(context) || isBannerCDOAdLoadProcessing || isBannerCDOAdLoadFailed) {
                return;
            }
//            if (MobileAds.getInitializationStatus() == null) {
//                MobileAds.initialize(context);
//            }
            isBannerCDOAdLoadProcessing = true;
            AdRequest adRequest = cdoBannerAdRequest;
            if (adRequest == null) {
                adRequest = new AdRequest.Builder().build();
            }
            AdSize portraitInlineAdaptiveBannerAdSize = AdSize.getPortraitInlineAdaptiveBannerAdSize(context, -1);
            final AdView adView = new AdView(context);
            Log.e("FullScreenBannerAds", "======AdsLoading====> request FULL_BANNER " + str);
            adView.setAdUnitId(str);
            adView.setAdSize(portraitInlineAdaptiveBannerAdSize);
            adView.loadAd(adRequest);
            AdRequest finalAdRequest = adRequest;
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("FullScreenBannerAds", "preLoadBannerCdoAds: onAdImpression ");
                    AdsCachingUtils.isBannerCDOAdLoadProcessing = false;
                    AdsCachingUtils.isBannerCDOAdLoadFailed = false;
                    AdsCachingUtils.isBannerCDOAdImpression = true;
                    AdsCachingUtils.cdoBannerAdRequest = null;
                    AdsCachingUtils.mBannerCDOAd = null;
                    SetAdListener setAdListener = AdsCachingUtils.adListnerFullScreenBanner;
                    if (setAdListener != null) {
                        setAdListener.onAdImpression();
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("FullScreenBannerAds", "preLoadBannerCdoAds: onAdLoaded ");
                    AdsCachingUtils.mBannerCDOAd = adView;
                    AdsCachingUtils.isBannerCDOAdLoadProcessing = false;
                    AdsCachingUtils.isBannerCDOAdImpression = false;
                    AdsCachingUtils.isBannerCDOAdLoadFailed = false;
                    SetAdListener setAdListener = AdsCachingUtils.adListnerFullScreenBanner;
                    if (setAdListener != null) {
                        setAdListener.onAdLoad();
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    try {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e("FullScreenBannerAds", "preLoadBannerCdoAds: onAdFailedToLoad " + loadAdError.toString());
                        AdsCachingUtils.isBannerCDOAdLoadProcessing = false;
                        AdsCachingUtils.isBannerCDOAdLoadFailed = true;
                        AdsCachingUtils.cdoBannerAdRequest = finalAdRequest;
                        SetAdListener setAdListener = AdsCachingUtils.adListnerFullScreenBanner;
                        if (setAdListener != null) {
                            setAdListener.onAdFailedToLoad(loadAdError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAdListenerFullScreenBanner(SetAdListener setAdListener) {
        adListnerFullScreenBanner = setAdListener;
    }
}
