package com.akshit.akshitsfdc.allpuranasinhindi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.activities.SplashActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserDataModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

public class Utils {

    public String getMyCountryCode(Activity activity){
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                TelephonyManager tm = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
                return tm.getNetworkCountryIso();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "US";
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public byte[] compressImageSmall(Uri imgUri, AppCompatActivity activity){


        try {
            Bitmap bitmap;
            File file = new File(imgUri.getPath());
            bitmap = new Compressor(activity)
                    .setMaxHeight(500) //Set height and width
                    .setMaxWidth(500)
                    .setQuality(1)// Set Quality
                    .compressToBitmap(file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            final byte[] bytes = baos.toByteArray();

            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }


    }

    public boolean checkValidString(String s){
        boolean valid = true;
        if(s == null){
            valid = false;
        }else if(s.length() == 0) {
            valid = false;
        }

        return valid;
    }

    public void updateUser(UserDataModel userDataModel){

        SplashActivity.USER_DATA.setPhotoUrl(userDataModel.getPhotoUrl());//
        SplashActivity.USER_DATA.setCreatedTime(userDataModel.getCreatedTime());//
        SplashActivity.USER_DATA.setEmail(userDataModel.getEmail());//
        SplashActivity.USER_DATA.setPhone(userDataModel.getPhone());//
        SplashActivity.USER_DATA.setName(userDataModel.getName());//
        SplashActivity.USER_DATA.setPurchasedBooks(userDataModel.getPurchasedBooks());//
        SplashActivity.USER_DATA.setuId(userDataModel.getuId());//
        SplashActivity.USER_DATA.setPrimeMember(userDataModel.isPrimeMember());//

    }
    public String getMaskedEmail(String email){
        return email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
    }
    public String getMaskedPhoneNumber(String phoneNumber){
        if(phoneNumber.length() < 10){
            return phoneNumber;
        }
        StringBuilder masked = new StringBuilder(phoneNumber);
        masked.setCharAt(3, 'x');
        masked.setCharAt(4, 'x');
        masked.setCharAt(5, 'x');
        masked.setCharAt(6, 'x');

        return masked.toString();
    }
    public String getLocalTimeString(long timestamp){
        Calendar cal = Calendar.getInstance();
        Date localTime = new Date(timestamp);
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        // you can get seconds by adding  "...:ss" to it


       return date.format(localTime);
    }

    public final long getAvailableMemory(){
        long free_memory = 0;
        try{
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            free_memory = (stat.getAvailableBlocksLong() * stat.getBlockSizeLong())/(1024 * 1024); //return value is in bytes
        }catch (Exception e){
            e.printStackTrace();
        }

        return free_memory;

    }
    public final long getTotalMemory(){
        long free_memory = 0;
        try{
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            free_memory = (stat.getAvailableBlocksLong() * stat.getBlockSizeLong())/(1024 * 1024); //return value is in bytes
        }catch (Exception e){
            e.printStackTrace();
        }

        return free_memory;

    }



}
