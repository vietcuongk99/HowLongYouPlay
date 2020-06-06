package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Fragment.BackLogFragment;
import com.kdc.howlongyouplay.Fragment.FinishedListFragment;
import com.kdc.howlongyouplay.Fragment.PlayingListFragment;
import com.kdc.howlongyouplay.Fragment.RetiredListFragment;
import com.kdc.howlongyouplay.R;


public class MainActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_name = dataSnapshot.child("username").getValue().toString();
                getSupportActionBar().setTitle(user_name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_playing:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_finished:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_retired:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_backlog:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_playing).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_finished).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_retired).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_backlog).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

            case R.id.your_statistic:
                startActivity(new Intent(MainActivity.this, YourStatisticActivity.class));
                finish();
                return true;

            case R.id.your_list:
                startActivity(new Intent(MainActivity.this, YourListActivity.class));
                finish();
                return true;
        }
        return false;
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PlayingListFragment();
                case 1:
                    return new FinishedListFragment();
                case 2:
                    return new RetiredListFragment();
                case 3:
                    return new BackLogFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }


    }


    @Override
    public void onBackPressed() {

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

    private boolean isNetworkConnected() {
        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }

        return connected;
    }

}
