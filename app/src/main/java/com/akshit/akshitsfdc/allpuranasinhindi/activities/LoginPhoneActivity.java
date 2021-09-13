package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.PhoneAuthAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.OtpInputFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class LoginPhoneActivity extends BaseActivity {

    private CountryCodePicker ccp;
    private TextInputLayout phoneNumberLayout;
    private EditText phoneNumberField;
    private Button getOtpButton;
    private PhoneAuthProvider.ForceResendingToken phoneAuthToken;
    private String phoneAuthVerificationId;
    private ProgressBar progressBar;
    private TextView enterOtpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        uiUtils.setNoLimitForWindow();

        initObjects();
        setInitValues();
        setListeners();
    }

    private void initObjects(){
        ccp = findViewById(R.id.ccp);
        phoneNumberLayout = findViewById(R.id.phoneNumberLayout);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        getOtpButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        enterOtpTextView = findViewById(R.id.enterOtpTextView);

    }

    private void setInitValues(){

        try {
            ccp.setDefaultCountryUsingNameCode(utils.getMyCountryCode(this));
        }catch (Exception e){
            e.printStackTrace();
            ccp.setDefaultCountryUsingNameCode(getString(R.string.default_country_code));
        }
        phoneNumberLayout.setPrefixText(ccp.getSelectedCountryCodeWithPlus());
        ccp.resetToDefaultCountry();

        ccp.registerPhoneNumberTextView(phoneNumberField);
        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

    }
    private void setListeners(){
        ccp.setOnCountryChangeListener(selectedCountry -> phoneNumberLayout.setPrefixText(ccp.getSelectedCountryCodeWithPlus()));
        getOtpButton.setOnClickListener(v -> sendOtp());
        enterOtpTextView.setOnClickListener(v -> routing.openFragmentOver(new OtpInputFragment(LoginPhoneActivity.this), getString(R.string.otp_frg_tag)));
    }
    private boolean validateForm(){
        boolean valid = true;
        String phoneNumber = phoneNumberField.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)){
            valid = false;
            phoneNumberField.setError(getString(R.string.field_required));
        }else {
            phoneNumberField.setError(null);
        }

        if(!TextUtils.isEmpty(phoneNumber) && !ccp.isValid()){
            valid = false;
            phoneNumberField.setError(getString(R.string.field_invalid));
        }else {
            phoneNumberField.setError(null);
        }

        return valid;
    }
    private void sendOtp(){

        if(!validateForm()){
            return;
        }


        disableGetOtp(getString(R.string.sending_opt));

        fireAuthService.startPhoneAuthentication(getPhoneNumber(), this, new PhoneAuthAdapter(){
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                fireAuthService.signInWithPhoneAuthCredential(phoneAuthCredential)
                        .addOnSuccessListener(authResult -> {
                            navigateToHome();
                            
                        })
                        .addOnFailureListener(e -> {
                            uiUtils.showLongErrorSnakeBar(getString(R.string.login_error));
                        });

                enableGetOtp(getString(R.string.send_opt));
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                uiUtils.showLongErrorSnakeBar(getString(R.string.generic_error_msg));
                enableGetOtp(getString(R.string.send_opt));
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                enterOtpTextView.setVisibility(View.VISIBLE);

                phoneAuthToken = token;
                phoneAuthVerificationId = verificationId;

                disableGetOtp(getString(R.string.auto_retrieving_opt));


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {

                routing.openFragmentOver(new OtpInputFragment(LoginPhoneActivity.this), getString(R.string.otp_frg_tag));
                enableGetOtp(getString(R.string.send_opt));

            }
        });


    }

    private void disableGetOtp(String text){
        progressBar.setVisibility(View.VISIBLE);
        getOtpButton.setEnabled(false);
        getOtpButton.setText(text);
    }
    private void enableGetOtp(String text){
        enterOtpTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        getOtpButton.setEnabled(true);
        getOtpButton.setText(text);
    }

    public void navigateToHome(){
        routing.navigateAndClear(HomeActivity.class);
    }
    public PhoneAuthProvider.ForceResendingToken getPhoneAuthToken() {
        return phoneAuthToken;
    }
    public void setPhoneAuthToken(PhoneAuthProvider.ForceResendingToken token) {
         phoneAuthToken = token;
    }
    public String getPhoneNumber(){
       return ccp.getSelectedCountryCodeWithPlus()+phoneNumberField.getText().toString().trim();
    }

    public String getVerificationId() {
        return phoneAuthVerificationId;
    }
    public void setVerificationId(String verificationId) {
        phoneAuthVerificationId = verificationId;
    }
}