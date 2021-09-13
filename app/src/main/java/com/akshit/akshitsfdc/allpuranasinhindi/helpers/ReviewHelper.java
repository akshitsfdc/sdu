package com.akshit.akshitsfdc.allpuranasinhindi.helpers;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class ReviewHelper {

    private AppCompatActivity activity;
    private ReviewManager manager;
    private Task<ReviewInfo> request;
    private ReviewInfo reviewInfo;
    private static final String TAG = "ReviewHelper";


    public ReviewHelper(AppCompatActivity activity) {

        this.activity = activity;
        this.manager = ReviewManagerFactory.create(activity);
        this.request = this.manager.requestReviewFlow();

        this.loadReviewInfo();

    }

    private void loadReviewInfo(){
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                this.reviewInfo = task.getResult();
            } else {
                // There was some problem, log or handle the error code.
                Log.d(TAG, "loadReviewInfo: >> error while loading review info");
            }
        });
    }



    public void askForReview(){
        
        if(this.reviewInfo == null){
            return;
        }
        
        Task<Void> flow = this.manager.launchReviewFlow(this.activity, this.reviewInfo);
        flow.addOnCompleteListener(task -> {
            Log.d(TAG, "askForReview: >> asked for review");
        });
    }

}
