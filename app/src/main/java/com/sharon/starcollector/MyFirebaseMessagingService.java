package com.sharon.starcollector;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";
    PrefManager prefManager;

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = new PrefManager(this);
    }

    private void sendRegistrationToServer(String token) {
        //add db value for this token and update it whenever needed
        prefManager.setFCMRegistrationToken(token);

    }
}
