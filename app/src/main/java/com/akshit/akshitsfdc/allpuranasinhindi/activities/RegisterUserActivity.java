package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class RegisterUserActivity extends BaseActivity {

    private EditText emailField, passwordField, nameField;
    private Button registerButton;
    private ProgressBar progressBar;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        uiUtils.setNoLimitForWindow();

        objectCreations();
        ObjectInitializations();
        setEvenListeners();

    }

    @Override
    protected void objectCreations() {
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        nameField = findViewById(R.id.nameField);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void ObjectInitializations() {
        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));
    }

    @Override
    protected void setEvenListeners() {

        registerButton.setOnClickListener(v -> {
            registerUser();
        });
    }

    private boolean validateForm(){

        boolean valid = true;

        String name = nameField.getText().toString().trim();

        if (name.isEmpty()) {
            nameField.setError("Required");
            valid = false;
        }  else {
            nameField.setError(null);
        }

        String emailInput = emailField.getText().toString().trim();

        if (emailInput.isEmpty()) {
            emailField.setError("Required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailField.setError("Invalid Email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String passwordInput = passwordField.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            uiUtils.showShortErrorSnakeBar("Password is "+getString(R.string.field_required));
            valid = false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            uiUtils.showLongErrorSnakeBar(getString(R.string.password_error));
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }


    private void disableLogin(String text){
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
        registerButton.setText(text);
    }
    private void enableLogin(String text){
        progressBar.setVisibility(View.GONE);
        registerButton.setEnabled(true);
        registerButton.setText(text);
    }

    private void registerUser(){

        if(!validateForm()){
            return;
        }

        String emailInput = emailField.getText().toString().trim();
        String passwordInput = passwordField.getText().toString().trim();

        disableLogin(getString(R.string.register_progress));
        fireAuthService.createAccount(emailInput, passwordInput)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    routeUser(firebaseUser);
                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongErrorSnakeBar(getString(R.string.register_error_msg));
                    enableLogin(getString(R.string.register_now));
                });
    }

    private void routeUser(FirebaseUser user){
        String name = nameField.getText().toString().trim();
        if(user != null){
            fireAuthService.updateDisplayName(user, name);

            fireAuthService.sendEmailVerificationToUser(user)
                    .addOnSuccessListener(aVoid -> {
                        decideRoute(user, true);
                    })
                    .addOnFailureListener(e -> {
                        decideRoute(user, false);
                    });
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
        enableLogin(getString(R.string.register_now));
    }

}