package com.sharon.starcollector;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseAnalytics mFirebaseAnalytics;
    TextView tv_total_stars_value, tv_cashedout_stars_value;
    Button bt_startCollectingStars, bt_cashout, bt_cashoutHistory;
    Timestamp currentTimeStamp;
    String total_stars="0";
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        animateStar();
        MobileAds.initialize(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        prefManager = new PrefManager(this);

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome " + prefManager.getUserName());
        tv_cashedout_stars_value = findViewById(R.id.cashedout_stars_value);
        tv_total_stars_value = findViewById(R.id.total_stars_value);
        bt_startCollectingStars = findViewById(R.id.bt_startcollectingstars);
        bt_startCollectingStars.setOnClickListener(this);
        bt_cashout = findViewById(R.id.bt_cashout);
        bt_cashout.setOnClickListener(this);
        bt_cashoutHistory = findViewById(R.id.bt_cashout_history);
        bt_cashoutHistory.setOnClickListener(this);
        getAppDefaults();
        firebaseMessagingRegistrationToken();
        checkUpdate();
        retrieveFromFireStore(prefManager.getUserName());

    }

    private void animateStar() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);
        ImageView imgBell = findViewById(R.id.cointext);
        imgBell.setImageResource(R.drawable.ic_star);
        imgBell.setAnimation(shake);
    }

    private void firebaseMessagingRegistrationToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        prefManager.setFCMRegistrationToken(token);
                        db.collection("users").document(prefManager.getUserName()).update("fcm_registration_token", token);
                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkUpdate();
    }

    private void checkUpdate() {
        if (BuildConfig.VERSION_CODE < prefManager.getUpdateAppVersionCode()) {
            final String link = prefManager.getUpdateAppLink();
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogTheme);
            alert.setTitle("Update the app")
                    .setMessage("A new version is available ")
                    .setPositiveButton("update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent startDownlaod = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
                            startActivity(startDownlaod);
                            Toast.makeText(MainActivity.this, "Download the file and install it", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false).create().show();
        }
    }

    private void getAppDefaults() {
        db.collection("app_defaults").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        prefManager.setUpdateAppVersionCode(Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).get("update_app_version_code").toString()));
                        prefManager.setConversionRate(Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).get("coin_cash_conversion_rate").toString()));
                        prefManager.setAdButtonInterval(Long.parseLong(queryDocumentSnapshots.getDocuments().get(0).get("start_ad_button_interval").toString()));
                        prefManager.setUpdateAppLink(queryDocumentSnapshots.getDocuments().get(0).get("update_app_link").toString());
                        prefManager.setMoneySentWithin(Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).get("money_sent_within").toString()));
                        prefManager.setMinStarsToCashout(Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).get("min_stars_cashout").toString()));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveFromFireStore(prefManager.getUserName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.signout) {
            auth.signOut();
            prefManager.setRegisteredUser(false);
            prefManager.setstoredTimeStamp(0);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveFromFireStore(String mPhoneNumber) {
        db.collection("users")
                .document(mPhoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        bt_cashout.setEnabled(true);
                        bt_cashoutHistory.setEnabled(true);
                        bt_startCollectingStars.setEnabled(true);
                        try {
                            total_stars = documentSnapshot.get("stars").toString();
                            tv_total_stars_value.setText(total_stars + " stars earned so far");
                            tv_cashedout_stars_value.setText("Total money earned lifetime: Rs." + documentSnapshot.get("total_money_cashedout").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogTheme);
                            alert.setTitle("Unexpected Error")
                                    .setMessage("Message the developer. Go to Settings and send feedback")
                                    .setPositiveButton("OK", null);
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_startcollectingstars:

                currentTimeStamp = Timestamp.now();
                long diff = currentTimeStamp.getSeconds() - prefManager.getStoredTimeStamp();
                if (diff > prefManager.getAdButtonInterval() || prefManager.getStoredTimeStamp() == 0) {
                    Intent intent = new Intent(this, StartViewingAds.class);
                    startActivity(intent);
                } else {
                    long d = Math.abs(diff - prefManager.getAdButtonInterval());
                    Toast.makeText(this, "Collect stars again in " + d + " secs", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bt_cashout:
                if (Integer.parseInt(total_stars) >= prefManager.getMinStarsToCashout())
                    startActivity(new Intent(this, Payout.class));
                else
                    Toast.makeText(this, "Not enough stars, needs min "+prefManager.getMinStarsToCashout()+" stars", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_cashout_history:
                Intent intent = new Intent(this, CashoutHistory.class);
//                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
//                startActivity(intent, activityOptions.toBundle());
                startActivity(intent);
                break;
        }
    }
}
