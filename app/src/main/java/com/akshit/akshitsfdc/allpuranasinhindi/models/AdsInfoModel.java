package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class AdsInfoModel {

    private String googleHomeBannerId;
    private String googleDownloadingGapId;
    private String googleBookAccessRewardId;

    private String unityHomeBannerId;
    private String unityDownloadingGapId;
    private String unityBookAccessRewardId;

    private boolean showUnityAds;
    private boolean showGoogleAds;

    public AdsInfoModel() {

    }


    public String getGoogleHomeBannerId() {
        return googleHomeBannerId;
    }

    public void setGoogleHomeBannerId(String googleHomeBannerId) {
        this.googleHomeBannerId = googleHomeBannerId;
    }

    public String getGoogleDownloadingGapId() {
        return googleDownloadingGapId;
    }

    public void setGoogleDownloadingGapId(String googleDownloadingGapId) {
        this.googleDownloadingGapId = googleDownloadingGapId;
    }

    public String getGoogleBookAccessRewardId() {
        return googleBookAccessRewardId;
    }

    public void setGoogleBookAccessRewardId(String googleBookAccessRewardId) {
        this.googleBookAccessRewardId = googleBookAccessRewardId;
    }

    public String getUnityHomeBannerId() {
        return unityHomeBannerId;
    }

    public void setUnityHomeBannerId(String unityHomeBannerId) {
        this.unityHomeBannerId = unityHomeBannerId;
    }

    public String getUnityDownloadingGapId() {
        return unityDownloadingGapId;
    }

    public void setUnityDownloadingGapId(String unityDownloadingGapId) {
        this.unityDownloadingGapId = unityDownloadingGapId;
    }

    public String getUnityBookAccessRewardId() {
        return unityBookAccessRewardId;
    }

    public void setUnityBookAccessRewardId(String unityBookAccessRewardId) {
        this.unityBookAccessRewardId = unityBookAccessRewardId;
    }

    public boolean isShowUnityAds() {
        return showUnityAds;
    }

    public void setShowUnityAds(boolean showUnityAds) {
        this.showUnityAds = showUnityAds;
    }

    public boolean isShowGoogleAds() {
        return showGoogleAds;
    }

    public void setShowGoogleAds(boolean showGoogleAds) {
        this.showGoogleAds = showGoogleAds;
    }
}
