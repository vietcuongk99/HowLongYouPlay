package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.LogAdapter;
import com.kdc.howlongyouplay.Model.GameLog;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Model.GameRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YourListActivity extends AppCompatActivity {
    private LogAdapter logAdapter;
    private List<GameLog> gameLogList;
    private HashMap<String, Object> records;

    private RecyclerView recyclerView;
    private RelativeLayout loading_state_view, empty_state_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_list);

        recyclerView = findViewById(R.id.your_list);
        loading_state_view = findViewById(R.id.loading);
        empty_state_view = findViewById(R.id.empty_list);
        recyclerView.setHasFixedSize(true);

        Resources resource = recyclerView.getResources();

        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(YourListActivity.this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(YourListActivity.this, 1));
        }

        gameLogList = new ArrayList<>();
        records = new HashMap<>();

        getLog();


    }

    private void getLog() {

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Logs").child(userid);
        loading_state_view.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameLogList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.hasChild("records")) {
                        final GameLog gameLog = snapshot.getValue(GameLog.class);
                        gameLog.setId_game(snapshot.getKey());



                        DatabaseReference databaseReference1 = databaseReference.child(gameLog.getId_game()).child("records");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                records.clear();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    GameRecord gameRecord = snapshot1.getValue(GameRecord.class);
                                    records.put(gameLog.getId_game(), gameRecord);
                                }

                                gameLog.setRecords(records);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        gameLogList.add(gameLog);

                    }

                }

                if (gameLogList.size() != 0) {
                    loading_state_view.setVisibility(View.GONE);
                    logAdapter = new LogAdapter(YourListActivity.this, gameLogList);
                    recyclerView.setAdapter(logAdapter);

                }
                else {
                    loading_state_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    empty_state_view.setVisibility(View.VISIBLE);

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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
