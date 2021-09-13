package com.akshit.akshitsfdc.allpuranasinhindi.models;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class MotivationModel {

    private boolean videoMotivation;
    private String imageUrl;
    private String videoUrl;
    private String header;
    private String description;
    private String hindiDate;
    private long timeStamp;
    private SimpleExoPlayer player;

    public MotivationModel() {
    }

    public String getHindiDate() {
        return hindiDate;
    }

    public void setHindiDate(String hindiDate) {
        this.hindiDate = hindiDate;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isVideoMotivation() {
        return videoMotivation;
    }

    public void setVideoMotivation(boolean videoMotivation) {
        this.videoMotivation = videoMotivation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }
}
