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

import java.util.ArrayList;
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

    private String title, year_release, user_id, played_time, id_game, img_url;

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
        time = (MaterialEditText) findViewById(R.id.played_time);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        imageView = (ImageView) findViewById(R.id.image_game);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        // lấy dữ liệu qua intent khi click vào một game bất kì trong danh sách tìm kiếm
        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        id_game = intent.getExtras().get("game_id").toString();
        img_url = intent.getExtras().get("img_url").toString();



        //Hiển thị id của game đã chọn qua Log
        Log.d("ID Game", "ID game được chọn: " + id_game);


        // đặt nội dung cho TextView
        game_title.setText(title);
        year.setText(year_release);

        //đặt hình ảnh cho ImageView
        Picasso.get().load(img_url).into(imageView);


        //lấy ra danh sách Log để so sánh
        logList = new ArrayList<>();
        readList();

        // xử lý sự kiện cho nút submit
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                played_time = time.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Logs").child(user_id);


                if (!checkSameLog(logList, title)) {
                    Toast.makeText(GameInfoActivity.this, "Bạn không thể thêm 2 bản ghi cho cùng 1 game", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("game_title", title);
                    hashMap.put("played_time", played_time);
                    hashMap.put("img_url", img_url);

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
                                Toast.makeText(GameInfoActivity.this, "Bạn đã thêm Log thành công", Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                Toast.makeText(GameInfoActivity.this, "Bạn đã thêm Log cho game này rồi, vui lòng xem lại danh sách", Toast.LENGTH_SHORT)
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


    // lấy ra danh sách các log của người dùng hiện tại có trong csdl
    private void readList() {
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
