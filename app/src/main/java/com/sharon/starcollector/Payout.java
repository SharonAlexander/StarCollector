package com.sharon.starcollector;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Payout extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Payout";
    Button payme;
    TextView payout_stars;
    int ads_watched = 0;
    int total_stars = 0;
    FirebaseFirestore db;
    PrefManager prefManager;
    private int total_payouts = 0;
    LinearLayout scrollLinearLayout;
    ListenerRegistration querylistener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_layout);

        db = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(this);

        scrollLinearLayout = findViewById(R.id.scroll_linear_layout);
        payme = findViewById(R.id.bt_payme);
        payme.setOnClickListener(this);
        payout_stars = findViewById(R.id.payout_stars);
        retrieveProfileInfo();
        retievePayoutHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        querylistener.remove();
    }

    private void retievePayoutHistory() {
        Query query = db.collection("payouts").document(prefManager.getUserName()).collection("history").whereEqualTo("request_status",false);
        querylistener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    Toast.makeText(Payout.this, "Unable to fetch you bt_cashout history", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                Collections.reverse(documentSnapshotList);
                scrollLinearLayout.removeAllViews();
                for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                    addPayoutRow(documentSnapshot);
                }
            }
        });
    }

    private void retrieveProfileInfo() {
        db.collection("users")
                .document(prefManager.getUserName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                total_stars = Integer.parseInt(document.get("stars").toString());
                                payout_stars.setText(String.valueOf(total_stars));
                                ads_watched = Integer.parseInt(document.get("ads_watched").toString());
                                total_payouts = Integer.parseInt(document.get("total_payouts").toString());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_payme:
                if (total_stars!=0) {
                    final double money = (double)total_stars / prefManager.getConversionRate();

                    AlertDialog.Builder alert = new AlertDialog.Builder(Payout.this,R.style.MyAlertDialogTheme);
                    alert.setTitle("Are you sure to use stars?")
                            .setMessage("Stars to use: "+total_stars+"\n"
                                    +"Money you'll get: "+money)
                            .setPositiveButton("USE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    makePayRequest();
                                }
                            })
                            .setNegativeButton("NO",null)
                            .create()
                            .show();
                }else{
                    Toast.makeText(this, "Not enough stars for payout", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void makePayRequest() {
        final double money = (double)total_stars / prefManager.getConversionRate();
        Log.d(TAG, "makePayRequest: money: "+money);
        final Map<String, Object> pay_request = new HashMap<>();
        pay_request.put("stars_used", total_stars);
        pay_request.put("ads_watched", ads_watched);
        pay_request.put("money_converted", money);
        pay_request.put("request_status", false);
        pay_request.put("request_time", Timestamp.now());

        db.collection("payouts")
                .document(prefManager.getUserName())
                .collection("history")
                .document(String.valueOf(total_payouts + 1))
                .set(pay_request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateProfile(money);
                        Toast.makeText(Payout.this, "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPayoutRow(DocumentSnapshot pay_request) {
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = layoutInflater.inflate(R.layout.history_rows,null,false);

        TextView starsused = view.findViewById(R.id.stars_used);
        TextView moneyconverted = view.findViewById(R.id.money_converted);
        TextView requesttime = view.findViewById(R.id.request_time);
        TextView requeststatus = view.findViewById(R.id.request_status);

        starsused.setText("Stars used: "+pay_request.get("stars_used").toString());
        moneyconverted.setText("Money converted: "+pay_request.get("money_converted").toString());
        Timestamp timestamp = (Timestamp) pay_request.get("request_time");
        Date date = timestamp.toDate();
        String datetime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
        requesttime.setText("Requested on: "+datetime);
        requeststatus.setText("Status: "+(Boolean.parseBoolean(pay_request.get("request_status").toString())?"Paid Successfully":"Waiting"));

        layout.addView(view);
        scrollLinearLayout.addView(layout);
    }

    private void updateProfile(double money) {
        DocumentReference userRef = db.collection("users")
                .document(prefManager.getUserName());
        userRef.update("stars", 0, "ads_watched", 0, "total_payouts", FieldValue.increment(1),"total_money_cashedout",FieldValue.increment(money));
        resetStars();
    }

    private void resetStars() {
        db.collection("users")
                .document(prefManager.getUserName())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        payout_stars.setText(documentSnapshot.get("stars").toString());
                        total_stars = 0;
                    }
                });
    }
}
