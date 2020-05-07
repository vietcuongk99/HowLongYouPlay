package com.kdc.howlongyouplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private ImageButton edit_infor_btn, edit_avatar_btn;
    private CircleImageView user_avatar;
    private TextView user_infor;
    private TextView user_name;
    private TextView user_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tài khoản của tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


        user_name = findViewById(R.id.user_name);
        user_infor = findViewById(R.id.user_infor);
        user_gender = findViewById(R.id.user_gender);
        user_avatar = findViewById(R.id.user_avatar);
        edit_infor_btn = findViewById(R.id.edit_infor_btn);
        edit_avatar_btn = findViewById(R.id.edit_avatar_btn);

    }
}
