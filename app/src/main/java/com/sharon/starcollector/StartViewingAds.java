package com.sharon.starcollector;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class StartViewingAds extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StartViewingAds";
    Button reward1Button, reward2Button, reward3Button, reward4Button, reward5Button;
    TextView currentStars;
    FirebaseFirestore db;
    PrefManager prefManager;
    AppLovinIncentivizedInterstitial rewardAd1, rewardAd2, rewardAd3, rewardAd4, rewardAd5;
    AppLovinAdLoadListener listener1, listener2, listener3, listener4, listener5;
    AppLovinAdRewardListener rewardListener;
    AppLovinAdDisplayListener adDisplayListener1, adDisplayListener2, adDisplayListener3, adDisplayListener4, adDisplayListener5;
    private boolean bonusAdWatched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefManager = new PrefManager(this);
        db = FirebaseFirestore.getInstance();
        currentStars();

        currentStars = findViewById(R.id.currentstars);
        currentStars = findViewById(R.id.currentstars);
        initialiseAllButtons();

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build();
        mAdView.loadAd(adRequest);

        AppLovinSdk.initializeSdk(this);
        applovinInitialise();
    }

    private void applovinInitialise() {

        rewardListener = new AppLovinAdRewardListener() {
            @Override
            public void userRewardVerified(AppLovinAd ad, Map map) {
                Log.d(TAG, "userRewardVerified: " + map.get("stars"));
//                startAlertToCollectRewards(response.get);
            }

            @Override
            public void userOverQuota(AppLovinAd ad, Map<String, String> response) {
                Log.d(TAG, "userOverQuota: ");
                Toast.makeText(StartViewingAds.this, "Quota Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void userRewardRejected(AppLovinAd ad, Map<String, String> response) {

            }

            @Override
            public void validationRequestFailed(AppLovinAd ad, int errorCode) {

            }

            @Override
            public void userDeclinedToViewAd(AppLovinAd ad) {

            }
        };
        applovinDisplayListeneres();
        applovinAdsInitialise();
    }

    private void applovinDisplayListeneres() {
        adDisplayListener1 = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd ad) {
                Log.d(TAG, "adDisplayed: ");
            }

            @Override
            public void adHidden(AppLovinAd ad) {
                Log.d(TAG, "adHidden: ");
                reward1Button.setEnabled(false);
                startAlertToCollectRewards(2);
            }
        };

        adDisplayListener2 = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd ad) {
                Log.d(TAG, "adDisplayed: ");
            }

            @Override
            public void adHidden(AppLovinAd ad) {
                Log.d(TAG, "adHidden: ");
                reward2Button.setEnabled(false);
                startAlertToCollectRewards(2);
            }
        };

        adDisplayListener3 = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd ad) {
                Log.d(TAG, "adDisplayed: ");
            }

            @Override
            public void adHidden(AppLovinAd ad) {
                Log.d(TAG, "adHidden: ");
                reward3Button.setEnabled(false);
                startAlertToCollectRewards(2);
            }
        };

        adDisplayListener4 = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd ad) {
                Log.d(TAG, "adDisplayed: ");
            }

            @Override
            public void adHidden(AppLovinAd ad) {
                Log.d(TAG, "adHidden: ");
                reward4Button.setEnabled(false);
                startAlertToCollectRewards(2);
            }
        };

        adDisplayListener5 = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd ad) {
                Log.d(TAG, "adDisplayed: ");
            }

            @Override
            public void adHidden(AppLovinAd ad) {
                Log.d(TAG, "adHidden: ");
                reward5Button.setEnabled(false);
                startAlertToCollectRewards(2);
            }
        };
    }

    private void applovinAdsInitialise() {
        rewardAd1 = AppLovinIncentivizedInterstitial.create(this);
        listener1 = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                Log.d(TAG, "adReceived: ");
                reward1Button.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                Log.d(TAG, "failedToReceiveAd: 1");
            }
        };
        rewardAd1.preload(listener1);

        rewardAd2 = AppLovinIncentivizedInterstitial.create(this);
        listener2 = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                Log.d(TAG, "adReceived: ");
                reward2Button.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                Log.d(TAG, "failedToReceiveAd: 2");
            }
        };
        rewardAd2.preload(listener2);

        rewardAd3 = AppLovinIncentivizedInterstitial.create(this);
        listener3 = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                Log.d(TAG, "adReceived: ");
                reward3Button.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                Log.d(TAG, "failedToReceiveAd: 3");
            }
        };
        rewardAd3.preload(listener3);

        rewardAd4 = AppLovinIncentivizedInterstitial.create(this);
        listener4 = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                Log.d(TAG, "adReceived: ");
                reward4Button.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                Log.d(TAG, "failedToReceiveAd: 4");
            }
        };
        rewardAd4.preload(listener4);

        rewardAd5 = AppLovinIncentivizedInterstitial.create(this);
        listener5 = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                Log.d(TAG, "adReceived: ");
                reward5Button.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                Log.d(TAG, "failedToReceiveAd: 5");
            }
        };
        rewardAd5.preload(listener5);


    }

    private void initialiseAllButtons() {
        Log.d(TAG, "initialiseAllButtons: ");
        reward1Button = findViewById(R.id.rewardedad1);
        reward1Button.setOnClickListener(this);

        reward2Button = findViewById(R.id.rewardedad2);
        reward2Button.setOnClickListener(this);

        reward3Button = findViewById(R.id.rewardedad3);
        reward3Button.setOnClickListener(this);

        reward4Button = findViewById(R.id.rewardedad4);
        reward4Button.setOnClickListener(this);

        reward5Button = findViewById(R.id.rewardedad5);
        reward5Button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rewardedad1:
                if (rewardAd1.isAdReadyToDisplay()) {
                    rewardAd1.show(this, rewardListener, null, adDisplayListener1, null);
                }
                break;
            case R.id.rewardedad2:
                if (rewardAd2.isAdReadyToDisplay()) {
                    rewardAd2.show(this, rewardListener, null, adDisplayListener2, null);
                }
                break;
            case R.id.rewardedad3:
                if (rewardAd3.isAdReadyToDisplay()) {
                    rewardAd3.show(this, rewardListener, null, adDisplayListener3, null);
                }
                break;
            case R.id.rewardedad4:
                if (rewardAd4.isAdReadyToDisplay()) {
                    rewardAd4.show(this, rewardListener, null, adDisplayListener4, null);
                }
                break;
            case R.id.rewardedad5:
                if (rewardAd5.isAdReadyToDisplay()) {
                    rewardAd5.show(this, rewardListener, null, adDisplayListener5, null);
                }
                break;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        prefManager.setstoredTimeStamp(Timestamp.now().getSeconds());
    }

    private void startAlertToCollectRewards(final int amount) {
        AlertDialog.Builder alert = new AlertDialog.Builder(StartViewingAds.this, R.style.MyAlertDialogTheme);
        alert.setPositiveButton("Collect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateRewards(amount);
            }
        })
                .setView(R.layout.collect_rewards_alert)
                .setCancelable(false).create().show();
    }

    private void updateRewards(int reward) {
        DocumentReference userRef = db.collection("users")
                .document(prefManager.getUserName());
        userRef.update("stars", FieldValue.increment(reward), "ads_watched", FieldValue.increment(1)
                , "total_ads_watched", FieldValue.increment(1), "total_stars_lifetime", FieldValue.increment(reward));
        if (bonusAdWatched)
            userRef.update("bonus_ads_watched", FieldValue.increment(1));
        Toast.makeText(this, "2 reward stars added", Toast.LENGTH_SHORT).show();
        currentStars();
    }

    private void currentStars() {
        db.collection("users")
                .document(prefManager.getUserName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                currentStars.setText(Integer.parseInt(document.get("stars").toString()) + new String(new int[]{0x2B50}, 0, 1) + " stars collected");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}
