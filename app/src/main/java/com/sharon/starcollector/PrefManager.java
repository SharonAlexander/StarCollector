package com.sharon.starcollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.Timestamp;

class PrefManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    PrefManager(Context context) {
        this.preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    boolean isRegisteredUser(){
        return preferences.getBoolean("registered",false);
    }

    void setRegisteredUser(boolean b){
        editor.putBoolean("registered",b);
        editor.commit();
    }

    void setUserName(String userName){
        editor.putString("username",userName);
        editor.commit();
    }

    String getUserName(){
        return preferences.getString("username",null);
    }

    void setstoredTimeStamp(long timestamp){
        editor.putLong("storedtimestamp",timestamp);
        editor.commit();
    }

    long getStoredTimeStamp(){
        return preferences.getLong("storedtimestamp",0);
    }

    //app_defaults
    void setUpdateAppVersionCode(int versionCode){
        editor.putInt("update_app_version_code",versionCode);
        editor.commit();
    }

    int getUpdateAppVersionCode(){
        return preferences.getInt("update_app_version_code",0);
    }

    void setUpdateAppLink(String link){
        editor.putString("update_link",link);
        editor.commit();
    }

    String getUpdateAppLink(){
        return preferences.getString("update_link",null);
    }

    void setConversionRate(int rate){
        editor.putInt("conversion_rate",rate);
        editor.commit();
    }

    int getConversionRate(){
        return preferences.getInt("conversion_rate",4);
    }

    void setAdButtonInterval(long seconds){
        editor.putLong("ad_button_interval",seconds);
        editor.commit();
    }

    long getAdButtonInterval(){
        return preferences.getLong("ad_button_interval",20);
    }

    void setMoneySentWithin(int days){
        editor.putInt("money_sent_within",days);
        editor.commit();
    }

    int getMoneySentWithin(){
        return preferences.getInt("money_sent_within",2);
    }

    void setMinStarsToCashout(int minStarsToCashout){
        editor.putInt("min_stars_to_cashout",minStarsToCashout);
        editor.commit();
    }

    int getMinStarsToCashout(){
        return preferences.getInt("min_stars_to_cashout",2);
    }

    void setFCMRegistrationToken(String token){
        editor.putString("fcmtoken",token);
        editor.commit();
    }

    String getFCMRegistrationToken(){
        return preferences.getString("fcmtoken",null);
    }
}
