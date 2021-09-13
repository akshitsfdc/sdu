package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.viewpager2.widget.ViewPager2;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.MotivationPagerAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.MotivationModel;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TodayMotivationActivity extends BaseActivity {

    public ViewPager2 viewPager;
    private DocumentSnapshot lastVisible;
    private int docLimit = 5;
    private int pageCounter = 1;
    private MotivationPagerAdapter adapter;
    private boolean allowNewLoading = true;
    public int currentPagerPosition = 0;
    public boolean fullScreenMode = false;
    public View currentFullScreenContainer;
    public PlayerView oldPlayerView;
    private boolean fromNotification;
    private ProgressBar loadingProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_motivation);

        fromNotification = (boolean)routing.getParam("fromNotification");


        setReferences();
        initObjects();
        setEventListeners();

    }

    @Override
    public void onBackPressed() {
        if(fullScreenMode){
            adapter.exitFullScreenVideo();
            fullScreenMode = false;
        }else {
            if(fromNotification){
                routing.clearParams();
                routing.navigate(HomeActivity.class, true);
            }else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MotivationModel motivationModel = adapter.getList().get(currentPagerPosition);
        if( motivationModel != null){
            if(motivationModel.getPlayer() != null){
                motivationModel.getPlayer().pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void releasePlayer(){

        MotivationModel motivationModel = adapter.getList().get(currentPagerPosition);
        if( motivationModel != null){
            if(motivationModel.getPlayer() != null){
                motivationModel.getPlayer().pause();
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setReferences(){
        viewPager = findViewById(R.id.viewPager);
        adapter = new MotivationPagerAdapter(this, new ArrayList<>());
        loadingProgress = findViewById(R.id.loadingProgress);
    }
    private void initObjects(){
        viewPager.setAdapter(adapter
        );
        loadMotivations();

    }
    private void setEventListeners(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {


            @Override
            public void onPageSelected(int position) {

                super.onPageSelected(position);

                MotivationModel motivationModel = adapter.getList().get(currentPagerPosition);

                if( motivationModel != null){
                    if(motivationModel.getPlayer() != null){
                        motivationModel.getPlayer().pause();
                    }
                }

                if( ((position + 1) > pageCounter) && allowNewLoading && docLimit != -1){
                    pageCounter += 1;
                }

                if(pageCounter == docLimit && docLimit != -1){
                    loadMotivations();
                    pageCounter = 0;
                }
                currentPagerPosition = position;


            }
        });
    }

    private void loadMotivations(){


        Query query = getQuery();

        loadingProgress.setVisibility(View.VISIBLE);
        query.get()
                .addOnSuccessListener(documentSnapshots -> {

                    loadingProgress.setVisibility(View.GONE);
                    allowNewLoading = true;

                    if(documentSnapshots.size() <= 0){
                        docLimit = -1;
                        allowNewLoading = false;
                        return;
                    }
                    if(documentSnapshots.getDocuments().size() < docLimit){
                        docLimit = -1;
                        allowNewLoading = false;
                    }else {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1 );
                    }
                    List<MotivationModel> motivationModels = new ArrayList<>();

                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        if(document != null){
                            motivationModels.add(document.toObject(MotivationModel.class));
                        }
                    }
                    Log.d(TAG, "loadMotivations: "+motivationModels.size());
                    if(motivationModels.size() > 0){
                        adapter.extendList(motivationModels);
                    }




                }).addOnFailureListener(e -> {
                    Log.e(TAG, "loading motivation: ", e);
                    e.printStackTrace();
                    loadingProgress.setVisibility(View.GONE);
                });
    }

    private Query getQuery(){

        String bookCollection = "motivations";

        Query query;

        if(lastVisible != null){
            query = this.fireStoreService.getDB().collection(bookCollection)
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(docLimit);
        }else {
            query = this.fireStoreService.getDB().collection(bookCollection)
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .limit(docLimit);
        }



        return query;
    }

}