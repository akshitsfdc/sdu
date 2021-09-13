package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.content.Intent;
import android.icu.util.IndianCalendar;
import android.os.Bundle;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uiUtils.setNoLimitForWindow();

        setEventListeners();

        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));
    }

    private void setEventListeners(){
        findViewById(R.id.googleSignInButton).setOnClickListener(v ->{
            fireAuthService.openGoogleSignIn(this);
        });
        findViewById(R.id.loginPhoneButton).setOnClickListener(v -> {
            routing.navigate(LoginPhoneActivity.class, false);
        });
        findViewById(R.id.emailPasswordButton).setOnClickListener(v -> {
            routing.navigate(LoginEmailActivity.class, false);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == fireAuthService.RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                loginWithGoogle(task.getResult(ApiException.class));
            } catch (ApiException e) {
                uiUtils.showShortSnakeBar(getString(R.string.generic_error_msg));
                updateUI(null);
            }
        }
    }

    private void loginWithGoogle(GoogleSignInAccount acct){

        showLoading();

        fireAuthService.firebaseAuthWithGoogle(acct)

                .addOnSuccessListener(authResult -> {
                    updateUI(authResult.getUser());
                    hideLoading();
                })
                .addOnFailureListener(e -> {
                    updateUI(null);
                    hideLoading();
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            routing.navigateAndClear(HomeActivity.class);
        } else {
            uiUtils.showShortErrorSnakeBar(getString(R.string.generic_error_msg));
        }
    }

}