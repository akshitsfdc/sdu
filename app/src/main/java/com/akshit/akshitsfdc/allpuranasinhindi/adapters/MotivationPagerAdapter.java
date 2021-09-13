package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.TodayMotivationActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.FullVideoFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.MotivationModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class MotivationPagerAdapter extends RecyclerView.Adapter<MotivationPagerAdapter.ViewHolder>{

    private AppCompatActivity activity;
    private BaseActivity baseActivity;
    private List<MotivationModel> motivationModels;

    public MotivationPagerAdapter(AppCompatActivity activity, List<MotivationModel> motivationModels){
        this.activity = activity;
        this.motivationModels = motivationModels;
        baseActivity = (BaseActivity)activity;
    }

    public void extendList(List<MotivationModel> motivationModels){
        this.motivationModels.addAll(motivationModels);
        notifyDataSetChanged();
    }
    public  List<MotivationModel> getList(){
        return motivationModels;
    }
    @NonNull
    @Override
    public MotivationPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.motivation_container_layout, parent, false);
        return new MotivationPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotivationPagerAdapter.ViewHolder holder, int position) {
        MotivationModel motivationModel = motivationModels.get(position);

        if(!motivationModel.isVideoMotivation()){
            holder.videoView.setVisibility(View.GONE);
            holder.coverImage.setVisibility(View.VISIBLE);
            baseActivity.showRemoteImage(motivationModel.getImageUrl(), holder.coverImage);
        }else {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.coverImage.setVisibility(View.GONE);

            initVideoPlayer(holder.playerView, motivationModel);
            holder.fullMode.setOnClickListener(v ->{
                startFullScreenVideo(motivationModel, holder.fullVideoContainer, holder.playerView);
            });
        }

        holder.header.setText(motivationModel.getHeader());
        holder.description.setText(motivationModel.getDescription());
        holder.date.setText(motivationModel.getHindiDate());


    }

    @Override
    public int getItemCount() {
        return motivationModels.size();

    }

    private void startFullScreenVideo(MotivationModel motivationModel, View container, PlayerView playerView){
        playerView.setPlayer(null);
        try {
            baseActivity.hideSystemUI();
        }catch (Exception e){
            e.printStackTrace();
        }
        container.setVisibility(View.VISIBLE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        baseActivity.routing.openFragment(new FullVideoFragment(activity, motivationModel.getPlayer(), container, playerView),
                activity.getString(R.string.full_video_tag), container.getId());
        TodayMotivationActivity todayMotivationActivity = ((TodayMotivationActivity)activity);
        todayMotivationActivity.fullScreenMode = true;
        todayMotivationActivity.currentFullScreenContainer = container;
        todayMotivationActivity.oldPlayerView = playerView;
        todayMotivationActivity.viewPager.setUserInputEnabled(false);
    }
    public void exitFullScreenVideo(){

        baseActivity.routing.goBack();
        try {
            baseActivity.showSystemUI();
        }catch (Exception e){
            e.printStackTrace();
        }

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        TodayMotivationActivity todayMotivationActivity = ((TodayMotivationActivity)activity);
        if(todayMotivationActivity.currentFullScreenContainer != null){
            todayMotivationActivity.currentFullScreenContainer.setVisibility(View.GONE);
        }
        if( todayMotivationActivity.oldPlayerView != null){
            MotivationModel motivationModel = motivationModels.get(todayMotivationActivity.currentPagerPosition);
            todayMotivationActivity.oldPlayerView.setPlayer(motivationModel.getPlayer());
        }
        todayMotivationActivity.viewPager.setUserInputEnabled(true);
    }
    private void initVideoPlayer(PlayerView playerView, MotivationModel motivationModel){

        SimpleExoPlayer player = new SimpleExoPlayer.Builder(activity).build();
        motivationModel.setPlayer(player);

        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(motivationModel.getVideoUrl());
        player.setMediaItem(mediaItem);

        player.prepare();

//        player.play();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PlayerView playerView;

        private ImageView fullMode, coverImage;
        private View fullVideoContainer, videoView, mainContentLayout;
        private TextView description, header, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            fullMode = itemView.findViewById(R.id.fullMode);
            fullVideoContainer = itemView.findViewById(R.id.fullVideoContainer);
            description = itemView.findViewById(R.id.description);
            header = itemView.findViewById(R.id.header);
            videoView = itemView.findViewById(R.id.videoView);
            coverImage = itemView.findViewById(R.id.coverImage);
            date = itemView.findViewById(R.id.date);
            mainContentLayout = itemView.findViewById(R.id.mainContentLayout);
        }
    }



}
