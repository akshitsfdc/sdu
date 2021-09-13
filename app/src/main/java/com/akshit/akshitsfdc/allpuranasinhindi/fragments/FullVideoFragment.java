package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.TodayMotivationActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class FullVideoFragment extends BaseFragment {

    private SimpleExoPlayer player;
    private PlayerView playerView, oldPlayerView;
    private ImageView fullMode;
    private BaseActivity baseActivity;
    private View containerView;


    public FullVideoFragment(AppCompatActivity activity, SimpleExoPlayer player,
                             View containerView, PlayerView oldPlayerView) {
        this.currentActivity = activity;
        this.containerView = containerView;
        this.oldPlayerView = oldPlayerView;
        this.player = player;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_video, container, false);
        objectCreations(view);
        objectInitializations();
        setEvenListeners();
        return view;
    }

    @Override
    protected void objectCreations(View view) {
        playerView = view.findViewById(R.id.playerView);
        baseActivity = (BaseActivity)currentActivity;
        fullMode = view.findViewById(R.id.fullMode);
    }

    @Override
    protected void objectInitializations() {
        playerView.setPlayer(player);

    }

    @Override
    protected void setEvenListeners() {
        fullMode.setOnClickListener(v ->{
            baseActivity.routing.goBack();
            try {
                baseActivity.showSystemUI();
            }catch (Exception e){
                e.printStackTrace();
            }
            containerView.setVisibility(View.GONE);
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.oldPlayerView.setPlayer(player);
            ((TodayMotivationActivity)currentActivity).fullScreenMode = false;
            ((TodayMotivationActivity)currentActivity).viewPager.setUserInputEnabled(true);
        });
    }
}