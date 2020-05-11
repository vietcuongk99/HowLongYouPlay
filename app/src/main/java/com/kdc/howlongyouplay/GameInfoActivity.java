package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.ListAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

// hiển thị nội dung khi click vào 1 log
public class GameInfoActivity extends AppCompatActivity {


    private TextView game_title;
    private TextView year;
    private Intent intent;
    private MaterialEditText time;
    private Button submit_btn;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private ImageButton add_btn, statistical_btn;

    private String title, year_release, user_id, id_game, img_url;

    private ArrayList<GameLog> logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm bản ghi mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


        game_title = findViewById(R.id.game_title);
        year = findViewById(R.id.year);
        intent = getIntent();
        imageView = (ImageView) findViewById(R.id.image_game);
        add_btn = findViewById(R.id.add_btn);
        statistical_btn = findViewById(R.id.statistical_btn);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        // lấy dữ liệu khi click vào một game bất kì trong danh sách tìm kiếm
        //LogAdapter
        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        id_game = intent.getExtras().get("game_id").toString();
        img_url = intent.getExtras().get("img_url").toString();


        //Hiển thị id của game đã chọn qua Log
        Log.d("ID Game", "ID game được chọn: " + id_game);


        // đặt nội dung cho TextView
        game_title.setText(title);
        year.setText(getApplicationContext().getResources().getString(R.string.year, year_release));

        //đặt hình ảnh cho ImageView
        Picasso.get().load(img_url).into(imageView);


        //xử lý sự kiện khi click vào ảnh game
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameInfoActivity.this, ImageViewerActivity.class);
                intent.putExtra("img_url", img_url);
                intent.putExtra("game_title", title);

                startActivity(intent);
                finish();
            }
        });


        //lấy ra danh sách Log để so sánh
        logList = new ArrayList<>();
        readLogList();

        // xử lý sự kiện cho nút add
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Logs").child(user_id);


                if (!checkSameLog(logList, title)) {
                    Toast.makeText(GameInfoActivity.this, "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy" + ", " + "HH:mm:ss a");
                    String time = timeFormat.format(calendar.getTime());
                    hashMap.put("game_title", title);
                    hashMap.put("img_url", img_url);
                    hashMap.put("total_record", "0");
                    hashMap.put("add_date", time);

                    DatabaseReference blank_record = databaseReference.push();

                    String key_record = blank_record.getKey();

                    blank_record.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(GameInfoActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(GameInfoActivity.this, "Bạn đã thêm thành công", Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                Toast.makeText(GameInfoActivity.this, "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                    });

                }
            }

        });




    }


    // kiểm tra xem game được chọn đã có trong log của người dùng hiện tại chưa
    // nếu chưa trả về true
    private boolean checkSameLog(ArrayList<GameLog> gameLogs, String title) {

        for(int i = 0; i<gameLogs.size(); i++) {
            if(gameLogs.get(i).getGame_title().equals(title)) {
                return false;
            }
        }

        return true;

    }


    // lấy ra danh sách game có trong danh sách của người dùng hiện tại
    private void readLogList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        // lấy ra danh sách GameLog và sử dụng LogAdapter
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                logList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GameLog gameLog = snapshot.getValue(GameLog.class);
                    gameLog.setId_log(snapshot.getKey());
                    logList.add(gameLog);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
