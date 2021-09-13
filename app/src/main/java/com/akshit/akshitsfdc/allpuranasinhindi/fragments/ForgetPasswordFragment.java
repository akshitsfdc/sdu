package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.LoginEmailActivity;


public class ForgetPasswordFragment extends BaseFragment {

    private Button okButton, sendMailButton;
    private TextView message;
    private EditText fieldEmail;
    private LinearLayout msgLayout, editLayout;
    private View parent;
    private LoginEmailActivity loginEmailActivity;

    public ForgetPasswordFragment(AppCompatActivity activity){
        currentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        objectCreations(view);
        objectInitializations();
        setEvenListeners();

        return view;

    }

    @Override
    protected void objectCreations(View view) {
        message = view.findViewById(R.id.message);
        fieldEmail = view.findViewById(R.id.fieldEmail);
        okButton = view.findViewById(R.id.okButton);
        sendMailButton = view.findViewById(R.id.sendMailButton);
        editLayout = view.findViewById(R.id.editLayout);
        msgLayout = view.findViewById(R.id.msgLayout);
        parent = view.findViewById(R.id.parent);
    }

    @Override
    protected void objectInitializations() {
        loginEmailActivity = (LoginEmailActivity)currentActivity;
    }

    @Override
    protected void setEvenListeners() {

        sendMailButton.setOnClickListener(v->{
            sendResetMail();
        });
        okButton.setOnClickListener(v->{
            hideFragment();
        });
        parent.setOnClickListener(v->{
            hideFragment();
        });

    }

    private void sendResetMail(){

        if(!validateForm()){
            return;
        }

        String email = fieldEmail.getText().toString().trim();
        showLoading();
        loginEmailActivity.fireAuthService.sendResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    successMail(email);
                    afterUpdate();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    failureMail(email);
                    afterUpdate();
                });
    }
    private void successMail(String email){
        String successText = "We have sent you a password reset mail on your email address "+email+" please open your this email and follow the instructions";
        message.setText(successText);

    }
    private void failureMail(String email){
        String failText = "We could not send you password reset mail on your email address "+email+" this could be because this is not valid email address or this email address is not registered with us";
        message.setText(failText);
    }
    private void afterUpdate(){
        msgLayout.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);
    }
    private boolean validateForm(){
        boolean valid = true;

        String email = fieldEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError("Required.");
            valid = false;
        } else {
            fieldEmail.setError(null);
        }
        if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            valid = false;
            fieldEmail.setError("Enter a valid email.");
        }else {
            fieldEmail.setError(null);
        }

        return valid;

    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void showLoading(){
        sendMailButton.setEnabled(false);
        sendMailButton.setText("Please wait...");
    }
    private void hideLoading(){
        sendMailButton.setEnabled(true);
        sendMailButton.setText("Send Reset Email");
    }
    public void hideFragment(){

        try{
            loginEmailActivity.routing.goBack();

        }catch (Exception e){
            e.printStackTrace();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }
}