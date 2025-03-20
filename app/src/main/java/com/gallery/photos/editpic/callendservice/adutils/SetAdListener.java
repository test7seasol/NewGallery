package com.gallery.photos.editpic.callendservice.adutils;

import com.google.android.gms.ads.LoadAdError;

public interface SetAdListener {
    void onAdFailedToLoad(LoadAdError loadAdError);

    void onAdImpression();

    void onAdLoad();
}
