package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.LogAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.opencensus.resource.Resource;

public class YourListActivity extends AppCompatActivity {
    private LogAdapter logAdapter;
    private List<Log> logList;
    private HashMap<String, Object> records;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_list);


        recyclerView = findViewById(R.id.your_list);
        recyclerView.setHasFixedSize(true);

        Resources resource = recyclerView.getResources();

        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        }

        logList = new ArrayList<>();
        records = new HashMap<>();

        getLog();


    }

    private void getLog() {

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Logs").child(userid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                logList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.hasChild("records")) {
                        final Log log = snapshot.getValue(Log.class);
                        log.setId_game(snapshot.getKey());

                        android.util.Log.d("GAME ID: ", log.getId_game());
                        android.util.Log.d("IMG URL: ", log.getGame_title().toString());
                        android.util.Log.d("IMG URL: ", log.getImage_url().toString());
                        android.util.Log.d("IMG URL: ", log.getIcon_url().toString());

                        DatabaseReference databaseReference1 = databaseReference.child(log.getId_game()).child("records");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                records.clear();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    Record record = snapshot1.getValue(Record.class);
                                    records.put(log.getId_game(), record);
                                }

                                log.setRecords(records);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        logList.add(log);

                    }


                }

                logAdapter = new LogAdapter(getApplicationContext(), logList);
                recyclerView.setAdapter(logAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
