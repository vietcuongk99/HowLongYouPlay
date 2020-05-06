package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditLogActivity extends AppCompatActivity {

    private TextView game_title;
    private TextView time;
    private Intent intent;
    private MaterialEditText time_update;
    private Button update_btn;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String title, old_time, log_id, user_id, new_time, img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sửa bản ghi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditLogActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


        game_title = findViewById(R.id.game_title);
        time = findViewById(R.id.time);
        imageView = findViewById(R.id.image_game);
        intent = getIntent();

        title = intent.getExtras().get("game_title").toString();
        old_time = intent.getExtras().get("play_time").toString();
        log_id = intent.getExtras().get("id").toString();
        img_url = intent.getExtras().get("img_url").toString();


        game_title.setText(title);

        if(old_time.equals("")) {
            time.setText(getApplicationContext().getResources().getString(R.string.played_time,
                    getApplicationContext().getResources().getString(R.string.zero)));
        }
        else {
            time.setText(getApplicationContext().getResources().getString(R.string.played_time, old_time));
        }

        Picasso.get().load(img_url).into(imageView);

        Log.d("ID: ", "ID: " + log_id);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        time_update = (MaterialEditText) findViewById(R.id.new_played_time);

        update_btn = (Button) findViewById(R.id.update_btn);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_time = time_update.getText().toString();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id).child(log_id);

//                if(new_time.equals("")) {
//                    Toast.makeText(EditLogActivity.this, "Bạn phải nhập gì đó chứ", Toast.LENGTH_SHORT).show();
//                }
//                else { }

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("played_time", new_time);

                    databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Intent intent = new Intent(EditLogActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                                Toast.makeText(EditLogActivity.this, "Sửa thành công", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            else {
                                Toast.makeText(EditLogActivity.this, "Lỗi", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                    });

                }
        });




    }
}
