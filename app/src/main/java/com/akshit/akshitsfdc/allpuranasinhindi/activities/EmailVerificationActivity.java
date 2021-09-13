package com.akshit.akshitsfdc.allpuranasinhindi.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends BaseActivity {

    private TextView descText;
    private Button registerAgainButton, verifiedButton, resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_verification);

        uiUtils.setNoLimitForWindow();

        objectCreations();
        ObjectInitializations();
        setEvenListeners();
    }

    @Override
    protected void objectCreations() {
        descText = findViewById(R.id.descText);
        registerAgainButton = findViewById(R.id.registerAgainButton);
        verifiedButton = findViewById(R.id.verifiedButton);
        resendButton = findViewById(R.id.resendButton);


    }

    @Override
    protected void ObjectInitializations() {

        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

         boolean emailSent = (boolean)routing.getParam("isEmailSent");

        FirebaseUser user = fireAuthService.getCurrentUser();

        if(!emailSent){
            verifiedButton.setVisibility(View.GONE);
            String desStr = "We could not send you verification email to "+user.getEmail()+" at this moment, request you to pleas try again later. If email entered by you is incorrect kindly Reset Registration.";
            descText.setText(desStr);
            uiUtils.showLongErrorSnakeBar("Could not send you verification email, please retry.");
        }else {

            String desStr = "We have sent you an email to "+user.getEmail()+" with account verification link, kindly check your inbox and click on verification link";
            descText.setText(desStr);
        }


    }

    @Override
    protected void setEvenListeners() {
        verifiedButton.setOnClickListener(v -> {
            afterUserVerified();
        });
        resendButton.setOnClickListener(v -> {
            sendVerificationEmail();
        });
        registerAgainButton.setOnClickListener(v -> {
            fireAuthService.logoutUser();
            routing.navigateAndClear(LoginActivity.class);
        });
    }

    private void afterUserVerified(){

        fireAuthService.reloadCurrentUser()
                .addOnSuccessListener(aVoid -> {

                    FirebaseUser user = fireAuthService.getCurrentUser();

                    if(user.isEmailVerified()){
                        routing.navigateAndClear(HomeActivity.class);
                    }else {
                        uiUtils.showLongErrorSnakeBar(getString(R.string.verify_email_first));
                    }

                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongErrorSnakeBar(getString(R.string.verification_email_not_sent));
                });
    }

    private void sendVerificationEmail(){

        FirebaseUser user = fireAuthService.getCurrentUser();

        showLoading();


        fireAuthService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Verification email sent to "+user.getEmail());
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Failed to send you verification email at this moment, please try again later.");
                });
    }

}