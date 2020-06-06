package com.kdc.howlongyouplay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.kdc.howlongyouplay.R;
import com.naver.android.helloyako.imagecrop.view.ImageCropView;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageCropView full_image;
    private String img_url, game_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        full_image = findViewById(R.id.full_image_view);
        full_image.setGridOuterMode(ImageCropView.GRID_ON);
        full_image.setGridInnerMode(ImageCropView.GRID_OFF);

        Intent intent = getIntent();
        img_url = intent.getExtras().get("img_url").toString();
        // game_title = intent.getExtras().get("game_title").toString();

        Picasso.get().load(img_url).into(full_image);

//        if(game_title.equals("")) {
//            GameLog.d("avatar", "avatar");
//        } else {
//            Toast.makeText(ImageViewerActivity.this, "Game: " + game_title, Toast.LENGTH_SHORT).show();
//        }


    }
}
