package com.kdc.howlongyouplay;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private ArrayList<GameLog> LogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("How Long To Beat");

        recyclerView = findViewById(R.id.list_item);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        LogList = new ArrayList<>();

        getLogList();



    }

    // hiển thị menu trên toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // xử lý cho từng option trong menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;

            case R.id.search_button:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                finish();
                return true;
        }
        return false;
    }




    // lấy ra danh sách gamelog để hiển thị
    private void getLogList() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LogList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GameLog gameLog = snapshot.getValue(GameLog.class);
                    gameLog.setId_log(snapshot.getKey());
                    LogList.add(gameLog);

                }


                logAdapter = new LogAdapter(MainActivity.this,  LogList);
                recyclerView.setAdapter(logAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
