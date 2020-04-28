package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<GameLog> gameLogList;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.search_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gameLogList = new ArrayList<>();
        readList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.search_view_btn);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.onActionViewExpanded();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                listAdapter.getFilter().filter(query);
                // hiển thị list trong recyclerView
                recyclerView.setAdapter(listAdapter);
                recyclerView.setVisibility(View.VISIBLE);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText == null || newText.length() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else {
                    listAdapter.getFilter().filter(newText);
                    // hiển thị list trong recyclerView
                    recyclerView.setAdapter(listAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }



    // lấy ra danh sách các gamelog có trong csdl
    private void readList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("List");

        // lấy ra danh sách GameLog và sử dụng listAdapter
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameLogList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GameLog gameLog = snapshot.getValue(GameLog.class);
                    gameLog.setId_log(snapshot.getKey());
                    gameLogList.add(gameLog);

                }

                listAdapter = new ListAdapter(SearchActivity.this, gameLogList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
