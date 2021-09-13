package com.akshit.akshitsfdc.allpuranasinhindi.service;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.PhoneAuthAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.cloud.datastore.core.number.IndexNumberDecoder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class FireAuthService {

    private FirebaseAuth firebaseAuth;
    public final int RC_SIGN_IN = 9001;

    public FireAuthService() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

    }


    public String getUserId(){

        return firebaseAuth.getCurrentUser().getUid();
    }

    public FirebaseUser getCurrentUser(){

        return firebaseAuth.getCurrentUser();

    }
    public boolean isUserLoggedIn(){
        return getCurrentUser() != null;
    }

    public Task<Void> reloadCurrentUser(){
        return getCurrentUser().reload();
    }
    public Task<AuthResult> emailPwdSignIn(String email, String password){

        return firebaseAuth.signInWithEmailAndPassword(email, password);

    }

    public Task<Void> sendEmailVerification(FirebaseUser user) {

        return user.sendEmailVerification();

    }
    public Task<Void> sendResetEmail(String emailAddress){
         return firebaseAuth.sendPasswordResetEmail(emailAddress);
    }
    public Task<Void> sendEmailVerification() {

        FirebaseUser user = getCurrentUser();
        if(user != null){
            return user.sendEmailVerification();
        }
        return null;
    }
    public Task<Void> sendEmailVerificationToUser(FirebaseUser user) {

        if(user != null){
            return user.sendEmailVerification();
        }
        return null;
    }

    public Task<AuthResult> createAccount(String email, String password){
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }
    public void openGoogleSignIn(Activity activity){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        activity.startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    public Task<AuthResult> firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        return firebaseAuth.signInWithCredential(credential);

    }

    public void startPhoneAuthentication(String phoneNumber, Activity activity, PhoneAuthAdapter phoneAuthAdapter){
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(phoneAuthAdapter)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public PhoneAuthCredential verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
         return PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
    }

    // [START resend_verification]
    public void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token, Activity activity, PhoneAuthAdapter phoneAuthAdapter) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(phoneAuthAdapter)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    public Task<AuthResult> signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential);
    }
    // [END sign_in_with_phone]
  public void logoutUser(){
    this.firebaseAuth.signOut();
  }

  public Task<Void> updateDisplayName(FirebaseUser user, String name){
      UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
              .setDisplayName(name)
              .build();

      return user.updateProfile(profileUpdates);

  }
    public Task<Void> updatePhotoUrl(FirebaseUser user, String url){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();

        return user.updateProfile(profileUpdates);

    }

    public Task<String> getDeviceToken(){
        return FirebaseMessaging.getInstance().getToken();
    }
}
