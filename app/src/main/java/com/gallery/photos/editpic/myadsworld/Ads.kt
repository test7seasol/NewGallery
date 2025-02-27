package com.gallery.photos.editpic.myadsworld

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View.GONE
import android.view.WindowManager
import android.widget.FrameLayout
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

fun loadBanner(
    shimmerView: ShimmerFrameLayout,
    adView: AdView? = null,
    frameBanner: FrameLayout,
    context: Context,
    adSize: AdSize
) {
    val shimmer = Shimmer.AlphaHighlightBuilder() // The builder for a ShimmerDrawable
        .setDuration(1800) // how long the shimmering animation takes to do one full sweep
        .setBaseAlpha(0.9f) // the alpha of the underlying children
        .setHighlightAlpha(0.8f) // the shimmer alpha amount
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

//        navigator.showDefaultSmsDialog(this)
    shimmerView.setShimmer(shimmer)
    adView?.adUnitId = (MyAddPrefs(context).admInlineBannerId)
    frameBanner.addView(adView)
    adView?.setAdSize(adSize)
    val adRequest = AdRequest.Builder().build()
    adView?.loadAd(adRequest)
    adView?.adListener = object : AdListener() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            shimmerView.visibility = GONE
            Log.d("TAG", "onAdFailedToLoad: " + loadAdError.message)
        }

        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d("TAG", "onAdFailedToLoad1: " + "loaded")
            shimmerView.visibility = GONE
        }
    }
}

fun Context.getAdSize(windowManager: WindowManager): AdSize {
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    val widthPixels = outMetrics.widthPixels.toFloat()
    val density = outMetrics.density
    val adWidth = (widthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this@getAdSize, adWidth)
}
