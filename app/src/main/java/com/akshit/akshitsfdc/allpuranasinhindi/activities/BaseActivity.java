package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.Loading;
import com.akshit.akshitsfdc.allpuranasinhindi.service.FireAuthService;
import com.akshit.akshitsfdc.allpuranasinhindi.service.FireStoreService;
import com.akshit.akshitsfdc.allpuranasinhindi.service.SQLService;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.LocalFileUtils;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.Routing;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.UIUtils;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public Routing routing;
    public UIUtils uiUtils;
    public LocalFileUtils localFileUtils;
    public Utils utils;
    public String TAG;
    public FireStoreService fireStoreService;
    public FireAuthService fireAuthService;
    private FragmentManager fragmentManager;
    public boolean internetConnected = true;
    public SQLService sqlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            TAG = this.getClass().getName();
        }catch (Exception e){
            e.printStackTrace();
        }


        routing = new Routing(this);
        uiUtils = new UIUtils(this);
        utils = new Utils();
        localFileUtils = new LocalFileUtils(this);
        sqlService  = new SQLService(this);

        fireStoreService = new FireStoreService();
        fireAuthService = new FireAuthService();
        fragmentManager = getSupportFragmentManager();
    }


    public void showLoading(){

        if(!internetConnected){
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");
        if(fragment == null){
            Loading loading = new Loading();
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, loading,"global_loader")
                    .commitAllowingStateLoss();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
    }

    public void hideLoading(){

        try{

            Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");

            if(fragment != null){
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }
    @Override
    public void onClick(View v) {

    }
    public View getSnakBarView(View view){
        return view.findViewById(R.id.view);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void showImage(int imageId, ImageView imageView){
        Glide.with(this).load(imageId).into(imageView);
    }
    public void showRemoteImage(String picUrl, ImageView imageView){
        try {
            Glide.with(this).load(picUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //                holder.progress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .error(R.drawable.ic_profile_placeholder).fallback(R.drawable.ic_profile_placeholder)
                    .into(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void showRemoteImage(String picUrl, ImageView imageView, int fallbackResourceId, View afterLoad){
        afterLoad.setVisibility(View.VISIBLE);
        Glide.with(this).load(picUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                holder.progress.setVisibility(View.GONE);
                        afterLoad.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        afterLoad.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(fallbackResourceId).fallback(fallbackResourceId)
                .into(imageView);
    }
    protected void objectCreations(){

    }
    protected void ObjectInitializations(){

    }
    protected void setEvenListeners(){

    }

    public void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public void showSystemUI() {

        try{
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(0);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_VISIBLE
                    | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }catch (Exception e){
            e.printStackTrace();
        }




    }
}