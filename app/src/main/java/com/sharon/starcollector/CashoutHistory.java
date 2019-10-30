package com.sharon.starcollector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CashoutHistory extends AppCompatActivity {

    FirebaseFirestore db;
    PrefManager prefManager;
    LinearLayout linearLayout;
    TextView total_stars_value,total_money_value,total_ads_value,total_bonusads_value;
    private static final String TAG = "CashoutHistory";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashout_history_layout);

        db = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(this);

        total_stars_value = findViewById(R.id.history_total_stars_value);
        total_money_value = findViewById(R.id.history_total_money_value);
        total_ads_value = findViewById(R.id.history_total_ads_normal_value);
        total_bonusads_value = findViewById(R.id.history_total_ads_bonus_value);
        retrieveHistoryValues();

        linearLayout = findViewById(R.id.historylinearlayout);
        addViews();
    }

    private void retrieveHistoryValues() {
        db.collection("users").document(prefManager.getUserName())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        total_stars_value.setText(documentSnapshot.get("total_stars_lifetime").toString());
                        total_money_value.setText(documentSnapshot.get("total_money_cashedout").toString());
                        total_ads_value.setText(documentSnapshot.get("total_ads_watched").toString());
                        total_bonusads_value.setText(documentSnapshot.get("bonus_ads_watched").toString());
                    }
                });
    }

    private void addViews() {
        CollectionReference historys = db.collection("payouts").document(prefManager.getUserName()).collection("history");
        historys.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                Collections.reverse(documentSnapshotList);
                for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                    addPayoutRow(documentSnapshot);
                }
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
        linearLayout.addView(layout);
    }
}
