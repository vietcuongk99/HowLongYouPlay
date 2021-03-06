package com.kdc.howlongyouplay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kdc.howlongyouplay.R;

public class StartActivity extends AppCompatActivity {

    Button login, register;
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        background = findViewById(R.id.background);

        background.setScrollbarFadingEnabled(true);
        background.setVerticalScrollBarEnabled(true);



        // xử lý sự kiện khi click nút Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chuyển sang màn LoginActivity
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // chuyển sang màn RegisterActivity
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

}