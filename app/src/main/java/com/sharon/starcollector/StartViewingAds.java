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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartViewingAds extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StartViewingAds";
    Button reward1Button, reward2Button,reward3Button,reward4Button,reward5Button;
    TextView currentStars;
    FirebaseFirestore db;
    PrefManager prefManager;
    RewardedAdLoadCallback adLoadCallback1, adLoadCallback2,adLoadCallback3,adLoadCallback4,adLoadCallback5;
    private boolean bonusAdWatched = false;
    private RewardedAd rewardedAd1, rewardedAd2,rewardedAd3,rewardedAd4,rewardedAd5;

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
        initialiseAllAdButtons();

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build();
        mAdView.loadAd(adRequest);

        initialiseAllAds();
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

    private void initialiseAllAds() {
        //reward 1
        rewardedAd1 = new RewardedAd(this,
                "ca-app-pub-1740451756664908/6481039877");

        adLoadCallback1 = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                reward1Button.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: 1");
            }
        };
        rewardedAd1.loadAd(new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build(), adLoadCallback1);

        //reward 2
        rewardedAd2 = new RewardedAd(this,
                "ca-app-pub-1740451756664908/3870709473");

        adLoadCallback2 = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                reward2Button.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: 2");
            }
        };
        rewardedAd2.loadAd(new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build(), adLoadCallback2);

        //reward 3
        rewardedAd3 = new RewardedAd(this,
                "ca-app-pub-1740451756664908/1134828944");

        adLoadCallback3 = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                reward3Button.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: 3");
            }
        };
        rewardedAd3.loadAd(new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build(), adLoadCallback3);

        //reward 4
        rewardedAd4 = new RewardedAd(this,
                "ca-app-pub-1740451756664908/3681440541");

        adLoadCallback4 = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                reward4Button.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: 4");
            }
        };
        rewardedAd4.loadAd(new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build(), adLoadCallback4);

        //reward 5
        rewardedAd5 = new RewardedAd(this,
                "ca-app-pub-1740451756664908/9261070284");

        adLoadCallback5 = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                reward5Button.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: 5");
            }
        };
        rewardedAd5.loadAd(new AdRequest.Builder().addTestDevice("28C860176FFCDA81CE79CBEE1E3F6D38").build(), adLoadCallback5);


    }

    private void initialiseAllAdButtons() {
        //reward 1
        reward1Button = findViewById(R.id.rewardedad1);
        reward1Button.setOnClickListener(this);
        reward1Button.setEnabled(false);

        //reward 2
        reward2Button = findViewById(R.id.rewardedad2);
        reward2Button.setOnClickListener(this);
        reward2Button.setEnabled(false);

        //reward 2
        reward3Button = findViewById(R.id.rewardedad3);
        reward3Button.setOnClickListener(this);
        reward3Button.setEnabled(false);

        //reward 2
        reward4Button = findViewById(R.id.rewardedad4);
        reward4Button.setOnClickListener(this);
        reward4Button.setEnabled(false);

        //reward 2
        reward5Button = findViewById(R.id.rewardedad5);
        reward5Button.setOnClickListener(this);
        reward5Button.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rewardedad1:
                startReward1();
                break;
            case R.id.rewardedad2:
                startReward2();
                break;
            case R.id.rewardedad3:
                startReward3();
                break;
            case R.id.rewardedad4:
                startReward4();
                break;
            case R.id.rewardedad5:
                startReward5();
                break;
        }
    }

    private void startReward5() {
        if (rewardedAd5.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    bonusAdWatched = true;
                    startAlertToCollectRewards(rewardItem.getAmount());
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    reward5Button.setEnabled(false);
                }

            };
            rewardedAd5.show(this, rewardedAdCallback);
        } else {
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startReward4() {
        if (rewardedAd4.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    startAlertToCollectRewards(rewardItem.getAmount());
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    reward4Button.setEnabled(false);
                }

            };
            rewardedAd4.show(this, rewardedAdCallback);
        } else {
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startReward3() {
        if (rewardedAd3.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    startAlertToCollectRewards(rewardItem.getAmount());
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    reward3Button.setEnabled(false);
                }

            };
            rewardedAd3.show(this, rewardedAdCallback);
        } else {
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startReward2() {
        if (rewardedAd2.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    startAlertToCollectRewards(rewardItem.getAmount());
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    reward2Button.setEnabled(false);
                }

            };
            rewardedAd2.show(this, rewardedAdCallback);
        } else {
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startReward1() {
        if (rewardedAd1.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    startAlertToCollectRewards(rewardItem.getAmount());
                }

                @Override
                public void onRewardedAdClosed() {
                    reward1Button.setEnabled(false);
                }

            };
            rewardedAd1.show(this, rewardedAdCallback);
        } else {
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAlertToCollectRewards(final int amount) {
        AlertDialog.Builder alert = new AlertDialog.Builder(StartViewingAds.this,R.style.MyAlertDialogTheme);
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
                , "total_ads_watched", FieldValue.increment(1),"total_stars_lifetime",FieldValue.increment(reward));
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
                                currentStars.setText(Integer.parseInt(document.get("stars").toString())+ new String(new int[] { 0x2B50 }, 0, 1)+" stars collected");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}
