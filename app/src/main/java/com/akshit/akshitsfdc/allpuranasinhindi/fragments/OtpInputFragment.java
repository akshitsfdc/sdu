package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.LoginPhoneActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.PhoneAuthAdapter;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;

public class OtpInputFragment extends BaseFragment {

    private static final long START_TIME_IN_MILLIS = 60000;

    private Button loginButton;
    private TextView resendTextView, optTimerTextView;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private LoginPhoneActivity loginPhoneActivity;
    private ProgressBar progressBar;
    private EditText otpField;

    public OtpInputFragment(AppCompatActivity activity){
        this.currentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_input, container, false);
        objectCreations(view);
        objectInitializations();
        setEvenListeners();
        return view;

    }



    @Override
    protected void objectCreations(View view) {
        loginButton = view.findViewById(R.id.loginButton);
        resendTextView = view.findViewById(R.id.resendTextView);
        optTimerTextView = view.findViewById(R.id.optTimerTextView);
        progressBar = view.findViewById(R.id.progressBar);
        otpField = view.findViewById(R.id.otpField);
    }

    @Override
    protected void objectInitializations() {

        startTimer();
        loginPhoneActivity = ((LoginPhoneActivity)currentActivity);

    }

    @Override
    protected void setEvenListeners() {

        loginButton.setOnClickListener(v -> {
            if(!validate()){
                return;
            }
            String code = otpField.getText().toString().trim();
            signInWithCode(code);
        });
        resendTextView.setOnClickListener(v -> {
            resendOTP();
        });

    }

    private boolean validate(){

        boolean valid = true;

        String code = otpField.getText().toString().trim();

        if(TextUtils.isEmpty(code)){
            valid = false;
            otpField.setError("Required");
        }

        return valid;
    }
    private void disableLoginButton(String text){
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        loginButton.setText(text);
    }
    private void enableLoginButton(String text){
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        loginButton.setText(text);
    }

    private void signInWithCode(String code){

        if(loginPhoneActivity.getVerificationId() == null || code == null){
            return;
        }

       PhoneAuthCredential phoneAuthCredential = loginPhoneActivity.fireAuthService.
                verifyPhoneNumberWithCode(loginPhoneActivity.getVerificationId().trim(),
                        code.trim());

       if(phoneAuthCredential != null){

           disableLoginButton(getString(R.string.verifying_code));

           loginPhoneActivity.fireAuthService.signInWithPhoneAuthCredential(phoneAuthCredential)
                   .addOnSuccessListener(authResult -> {
                       enableLoginButton(currentActivity.getString(R.string.login_button));
                       loginPhoneActivity.navigateToHome();

                   })
                   .addOnFailureListener(e -> {
                       enableLoginButton(currentActivity.getString(R.string.login_button));
                       loginPhoneActivity.uiUtils.showLongErrorSnakeBar(getString(R.string.wrong_otp));
                   });

       }


    }

    private void resendOTP(){

        disableResend();
        loginPhoneActivity.fireAuthService.resendVerificationCode(loginPhoneActivity.getPhoneNumber(),
                loginPhoneActivity.getPhoneAuthToken(), loginPhoneActivity, new PhoneAuthAdapter(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        loginPhoneActivity.fireAuthService.signInWithPhoneAuthCredential(phoneAuthCredential)
                                .addOnSuccessListener(authResult -> {
                                    loginPhoneActivity.navigateToHome();
                                })
                                .addOnFailureListener(e -> {
                                    loginPhoneActivity.uiUtils.showLongErrorSnakeBar(getString(R.string.login_error));
                                });

                        startTimer();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loginPhoneActivity.uiUtils.showLongErrorSnakeBar(getString(R.string.resend_otp_error));
                        enableResend();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        loginPhoneActivity.uiUtils.showLongSuccessSnakeBar(getString(R.string.otp_success));
                        loginPhoneActivity.setPhoneAuthToken(token);
                        loginPhoneActivity.setVerificationId(verificationId);
                        startTimer();
                    }
                });
    }

    private void enableResend(){
        try {
            resendTextView.setTextColor(currentActivity.getResources().getColor(R.color.primary));
            resendTextView.setEnabled(true);

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void disableResend(){
        try {
            resendTextView.setTextColor(getResources().getColor(R.color.grey));
            resendTextView.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void startTimer() {
        disableResend();
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                mCountDownTimer.cancel();
                enableResend();
            }
        }.start();

    }

    private void updateCountDownText() {

        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        String finalString = "You can resend OTP in "+timeLeftFormatted;
        optTimerTextView.setText(finalString);
    }
}