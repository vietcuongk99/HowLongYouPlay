package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.StatisticAdapter;
import com.kdc.howlongyouplay.GameRecord;
import com.kdc.howlongyouplay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YourStatisticActivity extends AppCompatActivity {

    private TextView user_name;
    private ExpandableListView expandableListView;
    private HashMap<String, List<GameRecord>> mData;
    private ArrayList<GameRecord> playingList, finishedList, retiredList, backlogList;
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
                            GameRecord gameRecord = snapshot1.getValue(GameRecord.class);

                            if (gameRecord.getStatus().contains("playing")) {
                                gameRecord.setRecord_id(snapshot1.getKey());
                                playingList.add(gameRecord);
                            }
                            if (gameRecord.getStatus().contains("finished")) {
                                gameRecord.setRecord_id(snapshot1.getKey());
                                finishedList.add(gameRecord);
                            }
                            if (gameRecord.getStatus().contains("retired")) {
                                gameRecord.setRecord_id(snapshot1.getKey());
                                retiredList.add(gameRecord);
                            }
                            if (gameRecord.getStatus().contains("backlog")) {
                                gameRecord.setRecord_id(snapshot1.getKey());
                                backlogList.add(gameRecord);
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
