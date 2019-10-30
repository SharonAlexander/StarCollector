package com.sharon.starcollector;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    PrefManager prefManager;
    private EditText phoneNumberField,codefield;
    private TextView resendotp;
    private ProgressBar progressBar;
    private Button enter,loginButton;
    private String mVerificationId;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        loginButton = findViewById(R.id.loginbutton);
        resendotp = findViewById(R.id.resendsms);
        enter = findViewById(R.id.verifybutton);
        progressBar = findViewById(R.id.progressBar);
        phoneNumberField = findViewById(R.id.phoneNumber);
        codefield = findViewById(R.id.verification_code);
        loginButton.setOnClickListener(this);
        enter.setOnClickListener(this);
        resendotp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    if(!isOnline()){
                        Toast.makeText(LoginActivity.this, "Connect to internet first", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(LoginActivity.this, "Quota exceeded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                resendotp.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(prefManager.isRegisteredUser()){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginbutton:
                if (phoneNumberField.getText().length() != 10) {
                    phoneNumberField.setError("Enter valid phone number");
                }
                else{
                    if (isOnline()) {
                        codefield.setVisibility(View.VISIBLE);
                        enter.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        resendotp.setVisibility(View.VISIBLE);
                        login(phoneNumberField.getText().toString());
                    } else {
                        Toast.makeText(this, "Connect to internet and try again", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.verifybutton:
                verifyWithCode();
                break;
            case R.id.resendsms:
                resendOtp(phoneNumberField.getText().toString());
                break;
        }
    }

    private void resendOtp(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                mResendToken);
    }

    private void verifyWithCode() {
        verificationCode = codefield.getText().toString();
        if (verificationCode.length()!=0) {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(this, "Restart the app and try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter code", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void login(final String phoneNumber) {
        mAuth.getFirebaseAuthSettings().setAutoRetrievedSmsCodeForPhoneNumber("+919998887771","234567");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            long creationTimestamp = user.getMetadata().getCreationTimestamp();
                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                            if (creationTimestamp==lastSignInTimestamp||Math.abs(creationTimestamp-lastSignInTimestamp)==1) {
                                startFireStore(user.getPhoneNumber());
                            } else {
                                returningUser(user.getPhoneNumber());
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
                            }else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                Toast.makeText(LoginActivity.this, "Your account is disabled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void returningUser(String phoneNumber) {
        prefManager.setRegisteredUser(true);
        prefManager.setUserName(phoneNumber);
        startMain();
    }

    private void startMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startFireStore(final String mPhoneNumber) {
        //user's profile data
        Map<String, Object> user = new HashMap<>();
        user.put("username", mPhoneNumber);
        user.put("name",null);
        user.put("stars",0);
        user.put("ads_watched",0);
        user.put("total_ads_watched",0);
        user.put("bonus_ads_watched",0);
        user.put("total_payouts",0);
        user.put("total_stars_lifetime",0);
        user.put("total_money_cashedout",0);
        user.put("fcm_registration_token",null);
        user.put("paypal_id",null);
        user.put("paypal_verified",false);
        user.put("extra_verification_id",null);
        user.put("extra_verified",false);
        user.put("invitation_code",null);
        user.put("invitation_link",null);
        user.put("no_of_invites",0);

        db.collection("users")
                .document(mPhoneNumber)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        prefManager.setRegisteredUser(true);
                        prefManager.setUserName(mPhoneNumber);
                        startMain();
                    }
                });

        db.collection("payouts").document(mPhoneNumber)
                .set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                    }
                });
    }
}
