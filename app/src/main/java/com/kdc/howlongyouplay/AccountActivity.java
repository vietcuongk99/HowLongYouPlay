package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private ImageButton edit_infor_btn, edit_avatar_btn;
    private CircleImageView user_avatar;
    private TextView user_infor;
    private TextView user_name;
    private TextView user_gender;
    private ArrayList<User> users;

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

        // gán dữ liệu cho các textview tương ứng
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String user_id = mAuth.getCurrentUser().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String information = dataSnapshot.child("information").getValue().toString();
                    String avatar_url = dataSnapshot.child("avatar_url").getValue().toString();

                    if(!avatar_url.equals("default")) {
                        user_name.setText(username);
                        Picasso.get().load(avatar_url).into(user_avatar);

                        if (gender.equals("")) {
                            user_gender.setText(getApplicationContext().getResources().getString(R.string.not_define));
                        } else {
                            user_gender.setText(gender);
                        }

                        if (information.equals("")) {
                            user_infor.setText(getApplicationContext().getResources().getString(R.string.zero));
                        } else {
                            user_infor.setText(information);
                        }
                    }
                    else {
                        user_name.setText(username);

                        if (gender.equals("")) {
                            user_gender.setText(getApplicationContext().getResources().getString(R.string.not_define));
                        } else {
                            user_gender.setText(gender);
                        }

                        if (information.equals("")) {
                            user_infor.setText(getApplicationContext().getResources().getString(R.string.zero));
                        } else {
                            user_infor.setText(information);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //xử lý sự kiện cho nút sửa avatar
        edit_avatar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);

                builder.setTitle("Thay đổi ảnh đại diện");
                builder.setView(R.layout.dialog_edit_avatar);
                builder.setCancelable(true);
                final AlertDialog dialog = builder.show();





            }
        });


        //xử lý sự kiện cho nút sửa thông tin cá nhân
        edit_infor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);

                builder.setTitle("Thay đổi thông tin cá nhân");
                builder.setView(R.layout.dialog_edit_information);
                builder.setCancelable(true);
                final AlertDialog dialog = builder.show();

                MaterialEditText new_name = findViewById(R.id.new_username);
                MaterialEditText new_infor = findViewById(R.id.new_infor);

                String newName = new_name.getText().toString();
                String newInfor = new_infor.getText().toString();

                DatabaseReference mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                mDataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }




    // phương thức tự viết
    // hơi cùi bắp :v
    private void addUserInformations(String user_id, ArrayList<User> users) {

        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getId().equals(user_id)) {
                user_name.setText(users.get(i).getUsername());
                user_gender.setText(users.get(i).getGender());
                user_infor.setText(users.get(i).getInformation());
            }
        }

    }
    private void addUserAvatar(String user_id, ArrayList<User> users) {

        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getId().equals(user_id)) {
                if (!users.get(i).getAvatar_url().equals("default")) {
                    Picasso.get().load(users.get(i).getAvatar_url()).into(user_avatar);
                }
            }
        }

    }



}
