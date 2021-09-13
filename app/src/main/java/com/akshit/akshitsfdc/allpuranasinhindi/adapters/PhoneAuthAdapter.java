package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneAuthAdapter extends PhoneAuthProvider.OnVerificationStateChangedCallbacks {
    @Override
    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

    }

    @Override
    public void onVerificationFailed(@NonNull FirebaseException e) {

    }
    @Override
    public void onCodeSent(@NonNull String verificationId,
                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
    }

    public PhoneAuthAdapter() {
        super();
    }

    @Override
    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
        super.onCodeAutoRetrievalTimeOut(s);
    }
}
