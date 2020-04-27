package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);


        game_title = findViewById(R.id.game_title);
        year = findViewById(R.id.year);
        intent = getIntent();

        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        game_id = intent.getExtras().get("id").toString();

        game_title.setText(title);
        year.setText(year_release);

        Log.d("ID: ", "ID: " + game_id);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        time = (MaterialEditText) findViewById(R.id.played_time);

        submit_btn = (Button) findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                played_time = time.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id).child(game_id);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("game_title", title);
                hashMap.put("played_time", played_time);

                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            Intent intent = new Intent(GameInfoActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(GameInfoActivity.this, "Bạn đã thêm Log thành công", Toast.LENGTH_SHORT)
                                    .show();

                        }

                        else {
                            Toast.makeText(GameInfoActivity.this, "Bạn đã thêm Log cho game này rồi, vui lòng xem lại danh sách", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });

            }
        });




    }
}
