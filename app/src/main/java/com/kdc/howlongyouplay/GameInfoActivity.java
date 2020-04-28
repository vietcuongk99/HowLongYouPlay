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

import java.util.ArrayList;
import java.util.HashMap;

// hiển thị nội dung khi click vào 1 log
public class GameInfoActivity extends AppCompatActivity {


    private TextView game_title;
    private TextView year;
    private Intent intent;
    private MaterialEditText time;
    private Button submit_btn;
    private FirebaseAuth mAuth;

    private String title, year_release, game_id, user_id, played_time;

    private ArrayList<GameLog> logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
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

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        // luân chuyển dữ liệu sang activity khác
        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        game_id = intent.getExtras().get("id").toString();


        // đặt nội dung cho TextView
        game_title.setText(title);
        year.setText(year_release);

        // kiểm tra id của log game (id của game đó)
        Log.d("ID: ", "ID: " + game_id);

        //lấy ra danh sách Log để so sánh
        logList = new ArrayList<>();
        readList();

        // xử lý sự kiện cho nút submit
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                played_time = time.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Logs").child(user_id).child(game_id);


                if (!checkSameLog(logList, game_id)) {
                    Toast.makeText(GameInfoActivity.this, "Bạn không thể thêm 2 bản ghi cho cùng 1 game", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("game_title", title);
                    hashMap.put("played_time", played_time);

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private boolean checkSameLog(ArrayList<GameLog> gameLogs, String game_id) {

        for(int i = 0; i<gameLogs.size(); i++) {
            if(gameLogs.get(i).getId_log().equals(game_id)) {
                return false;
            }
        }

        return true;

    }


    // lấy ra danh sách các gamelog có trong csdl
    private void readList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        // lấy ra danh sách GameLog và sử dụng listAdapter
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
