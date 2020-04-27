package com.kdc.howlongyouplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
// hiển thị nội dung khi click vào 1 log
public class GameInfoActivity extends AppCompatActivity {


    private TextView game_title;
    private TextView year;
    private Intent intent;

    private String title, year_release, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);


        game_title = findViewById(R.id.game_title);
        year = findViewById(R.id.year);
        intent = getIntent();

        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        id = intent.getExtras().get("id").toString();

        game_title.setText(title);
        year.setText(year_release);

        Log.d("ID: ", "ID: " + id);



    }
}
