package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Fragment.BackLogFragment;
import com.kdc.howlongyouplay.Fragment.FinishedListFragment;
import com.kdc.howlongyouplay.Fragment.PlayingListFragment;
import com.kdc.howlongyouplay.Fragment.RetiredListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách game của bạn");


        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_tab_layout);

        loadFragment(new PlayingListFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_backlog:
                        Fragment backlog = new BackLogFragment();
                        loadFragment(backlog);
                        return true;

                    case R.id.nav_finished:
                        Fragment finished = new FinishedListFragment();
                        loadFragment(finished);
                        return true;

                    case R.id.nav_playing:
                        Fragment playing = new PlayingListFragment();
                        loadFragment(playing);
                        return true;

                    case R.id.nav_retired:
                        Fragment retired = new RetiredListFragment();
                        loadFragment(retired);
                        return true;

                }
                return false;
            }
        });


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

            case R.id.edit_profile:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                finish();
                return true;
        }
        return false;
    }




    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        // nếu khoảng cách giữa 2 lần nhấn nút Back > 2 giây, thông báo hiện ra
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }

        else {
            backToast = Toast.makeText(MainActivity.this, "Ấn nút Back lần nữa để thoát", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    // load fragment
    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
