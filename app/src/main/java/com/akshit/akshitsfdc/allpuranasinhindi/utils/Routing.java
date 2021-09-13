package com.akshit.akshitsfdc.allpuranasinhindi.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

public class Routing {

    private AppCompatActivity activity;
    private Bundle bundle;
    private FragmentManager fragmentManager;

    public Routing(AppCompatActivity activity){

        this.activity = activity;
        this.bundle = new Bundle();

        fragmentManager = activity.getSupportFragmentManager();
    }

    public <T> void navigate(Class<T> next, boolean shouldFinish){

        Intent i = new Intent(activity, next);
        i.putExtras(bundle);

        activity.startActivity(i);

        if(shouldFinish) {
            activity.finish();
        }
    }

    public <T> void navigateAndClear(Class<T> next){

        Intent i = new Intent(activity, next);
        i.putExtras(bundle);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(i);

        activity.finish();
    }

    public void clearParams(){
        this.bundle.clear();
    }

    public void appendParams(String key, Object object){

       if(object instanceof String){
           this.bundle.putString(key, object.toString().trim());
       }else if(object instanceof Boolean){
           this.bundle.putBoolean(key, (boolean) object);
       }else if(object instanceof Integer){
           this.bundle.putInt(key, (int) object);
       }else if(object instanceof Parcelable){
           this.bundle.putParcelable(key, (Parcelable) object);
       }else if(object instanceof Serializable){
           this.bundle.putSerializable(key, (Serializable) object);
       }else if(object instanceof Byte[]){
           this.bundle.putByteArray(key, (byte[]) object);
       }

    }
    public Object getParam(String key){
        Bundle getBundle = null;
        getBundle = activity.getIntent().getExtras();

        return getBundle.get(key);
    }

    public void openFragmentWithoutStack(Fragment fragment, String tag, int container){


        Fragment fr = activity.getSupportFragmentManager().findFragmentByTag(tag);

        fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(fr == null){
            try{
                fragmentManager.beginTransaction()
                        .replace(container, fragment,tag)
                        //.addToBackStack(tag)
                        .commit();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            boolean result = fragmentManager.popBackStackImmediate(tag, 1);
            Log.d("YYT4", "openFragment: removed "+result);
            fragmentManager.beginTransaction()
                    .replace(container, fragment,tag)
                    // .addToBackStack(tag)
                    .commit();
        }

    }

    public void openFragment(Fragment fragment, String tag, int container){

        Fragment fr = activity.getSupportFragmentManager().findFragmentByTag(tag);

        if(fr == null){
            try{
                fragmentManager.beginTransaction()
                        .replace(container, fragment,tag)
                        .addToBackStack(tag)
                        .commit();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            boolean result = fragmentManager.popBackStackImmediate(tag, 1);
            Log.d("YYT4", "openFragment: removed "+result);
            fragmentManager.beginTransaction()
                    .replace(container, fragment,tag)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    public void goBack(){
        fragmentManager.popBackStack();
    }

    public void openFragmentStateLoss(Fragment fragment, String tag, int container){

        Fragment fr = activity.getSupportFragmentManager().findFragmentByTag(tag);

        if(fr == null){
            try{
                fragmentManager.beginTransaction()
                        .replace(container, fragment,tag)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void openFragmentOver(Fragment fragment, String tag){

//        Fragment fr = activity.getSupportFragmentManager().findFragmentByTag(tag);
//
//        if(fr == null){
        try{
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(android.R.id.content, fragment,tag)
                    .addToBackStack(tag)
                    .commit();
            transaction.addToBackStack(null);
        }catch (Exception e){
            e.printStackTrace();
        }

//        }else {
//            Log.d("Checking", "openFragmentOver: fragment is not null");
//        }
    }

    public boolean hideFragment(String tag){
        boolean fragmentRemoved = false;
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);

        if(fragment != null){
            try{
                fragmentManager.beginTransaction().remove(fragment).commit();
                fragmentManager.popBackStackImmediate();
                fragmentRemoved = true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return fragmentRemoved;
    }


}
