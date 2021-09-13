package com.akshit.akshitsfdc.allpuranasinhindi.activities;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.akshit.akshitsfdc.allpuranasinhindi.R;

import com.akshit.akshitsfdc.allpuranasinhindi.fragments.ForgetPasswordFragment;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends BaseActivity {

    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        uiUtils.setNoLimitForWindow();

        objectCreations();
        ObjectInitializations();
        setEvenListeners();
    }

    @Override
    protected void objectCreations() {

       emailField = findViewById(R.id.emailField);
       passwordField = findViewById(R.id.passwordField);
       loginButton = findViewById(R.id.loginButton);
       registerButton = findViewById(R.id.registerButton);
       progressBar = findViewById(R.id.progressBar);

    }

    @Override
    protected void ObjectInitializations() {
        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));
    }

    @Override
    protected void setEvenListeners() {

        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> register());

        findViewById(R.id.forgotPasswordTextView).setOnClickListener(v -> {
            routing.openFragmentOver(new ForgetPasswordFragment(this),
                    getString(R.string.forget_password_tag));
        });
    }

    private void disableLogin(String text){
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        loginButton.setText(text);
    }
    private void enableLogin(String text){
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        loginButton.setText(text);
    }

    private boolean validateForm(){

        boolean valid = true;
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            valid = false;
            emailField.setError(getString(R.string.field_required));
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Invalid Email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if(TextUtils.isEmpty(password)){
            valid = false;
            uiUtils.showShortErrorSnakeBar("Password is "+getString(R.string.field_required));
        }else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void login(){

        if(!validateForm()){
            return;
        }
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        disableLogin(getString(R.string.login_progress));
        fireAuthService.emailPwdSignIn(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        if(firebaseUser.isEmailVerified()){
                            routing.navigateAndClear(HomeActivity.class);
                        }else {
                            sendVerificationEmail(firebaseUser);
                        }
                    }else {
                        uiUtils.showShortErrorSnakeBar(getString(R.string.generic_error_msg));
                    }
                    enableLogin(getString(R.string.login_button));
                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongErrorSnakeBar(getString(R.string.login_error_msg));
                    enableLogin(getString(R.string.login_button));
                });

    }
    private void sendVerificationEmail(FirebaseUser user){
        if(user != null){

            fireAuthService.sendEmailVerificationToUser(user)
                    .addOnSuccessListener(aVoid -> {
                        decideRoute(user, true);
                    })
                    .addOnFailureListener(e -> {
                        decideRoute(user, false);
                    });
        }else {
            uiUtils.showShortErrorSnakeBar(getString(R.string.generic_error_msg));
        }
    }
    private void decideRoute(FirebaseUser user, boolean emailSent){
        if(user.isEmailVerified()){
            routing.navigateAndClear(HomeActivity.class);
        }else {
            routing.clearParams();
            routing.appendParams("isEmailSent", emailSent);
            routing.navigate(EmailVerificationActivity.class, false);

        }
    }
    private void register(){
        routing.navigate(RegisterUserActivity.class, false);
    }
}