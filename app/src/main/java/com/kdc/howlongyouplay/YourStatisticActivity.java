package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.RecordAdapter;
import com.kdc.howlongyouplay.Adapter.StatisticAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class YourStatisticActivity extends AppCompatActivity {

    private TextView user_name;
    private ExpandableListView expandableListView;
    private HashMap<String, List<Record>> mData;
    private ArrayList<Record> playingList, finishedList, retiredList, backlogList;
    private StatisticAdapter statisticAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_statistic);

        user_name = findViewById(R.id.user_name);
        expandableListView = findViewById(R.id.group_status);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_name.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        playingList = new ArrayList<>();
        finishedList = new ArrayList<>();
        retiredList = new ArrayList<>();
        backlogList = new ArrayList<>();
        getList();

        final List<String> listHeader = new ArrayList<>();
        mData = new HashMap<>();

        listHeader.add("Playing ");
        listHeader.add("Finished ");
        listHeader.add("Retired ");
        listHeader.add("Backlog ");

        mData.put(listHeader.get(0), playingList);
        mData.put(listHeader.get(1), finishedList);
        mData.put(listHeader.get(2), retiredList);
        mData.put(listHeader.get(3), backlogList);


        statisticAdapter = new StatisticAdapter(this, listHeader, mData);
        expandableListView.setAdapter(statisticAdapter);

    }


    // lấy ra danh sách gamelog để hiển thị
    private void getList() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = firebaseUser.getUid();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Logs").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                backlogList.clear();
                finishedList.clear();
                playingList.clear();
                retiredList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if(snapshot.hasChild("records")) {
                        for (DataSnapshot snapshot1 : snapshot.child("records").getChildren()) {
                            Record record = snapshot1.getValue(Record.class);

                            if (record.getStatus().contains("playing")) {
                                record.setRecord_id(snapshot1.getKey());
                                playingList.add(record);
                            }
                            if (record.getStatus().contains("finished")) {
                                record.setRecord_id(snapshot1.getKey());
                                finishedList.add(record);
                            }
                            if (record.getStatus().contains("retired")) {
                                record.setRecord_id(snapshot1.getKey());
                                retiredList.add(record);
                            }
                            if (record.getStatus().contains("backlog")) {
                                record.setRecord_id(snapshot1.getKey());
                                backlogList.add(record);
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(YourStatisticActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
