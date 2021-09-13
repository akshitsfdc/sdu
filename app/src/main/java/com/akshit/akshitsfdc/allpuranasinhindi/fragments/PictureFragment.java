package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;

public class PictureFragment extends BaseFragment {

    private ImageView image;
    private String imageUrl;

    public PictureFragment(AppCompatActivity activity, String imageUrl){
        currentActivity = activity;
        this.imageUrl = imageUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        objectCreations(view);
        objectInitializations();
        setEvenListeners();

        return view;
    }

    @Override
    protected void objectCreations(View view) {
        image = view.findViewById(R.id.image);
    }

    @Override
    protected void objectInitializations() {

        BaseActivity baseActivity = (BaseActivity)currentActivity;
        if(baseActivity.utils.checkValidString(imageUrl)){
            baseActivity.showRemoteImage(imageUrl, image);
        }else {
            baseActivity.showImage(R.drawable.ic_profile_placeholder, image);
        }


    }

    @Override
    protected void setEvenListeners() {

    }

}