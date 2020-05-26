package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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

import io.opencensus.resource.Resource;

public class SearchActivity extends AppCompatActivity {

    private List<GameLog> gameLogList;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private Resources resource;

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

        resource = recyclerView.getResources();

        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }
        gameLogList = new ArrayList<>();
        readList();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.search_view_btn);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.onActionViewExpanded();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (listAdapter != null) {
                    listAdapter.getFilter().filter(query);
                    // hiển thị list trong recyclerView
                    recyclerView.setAdapter(listAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SearchActivity.this, "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                    gameLog.setId_game(snapshot.getKey());
                    gameLogList.add(gameLog);

                }

                listAdapter = new ListAdapter(SearchActivity.this, gameLogList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
