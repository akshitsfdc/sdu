package com.akshit.akshitsfdc.allpuranasinhindi.helpers;

import android.util.Log;

import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

public class UnityBannerListener implements BannerView.IListener {
    @Override
    public void onBannerLoaded(BannerView bannerAdView) {
        // Called when the banner is loaded.
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
//        Log.d("SupportTest", "Banner Error" + errorInfo.);
        // Note that the BannerErrorInfo object can indicate a no fill (see API documentation).
    }

    @Override
    public void onBannerClick(BannerView bannerAdView) {
        // Called when a banner is clicked.
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerAdView) {
        // Called when the banner links out of the application.
    }
}
