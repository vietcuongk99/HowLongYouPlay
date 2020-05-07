package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private ImageButton edit_infor_btn, edit_avatar_btn;
    private CircleImageView user_avatar;
    private TextView user_infor;
    private TextView user_name;
    private TextView user_gender;
    private Uri avatar_url;
    private String user_id;
    private DatabaseReference databaseReference;
    private StorageReference firebaseStorage;
    private FirebaseAuth mAuth;
    private ImageView new_avatar;
    private String newName, newInfor;

    private static final int PICK_IMAGE_REQUEST = 1;

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

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        // gán dữ liệu cho các textview tương ứng
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String information = dataSnapshot.child("information").getValue().toString();
                    String avatar_url = dataSnapshot.child("avatar_url").getValue().toString();

                    if(!avatar_url.equals("default")) {
                        user_name.setText(username);
                        Picasso.get().load(avatar_url).into(user_avatar);


                        if (information.equals("")) {
                            user_infor.setText(getApplicationContext().getResources().getString(R.string.zero));
                        } else {
                            user_infor.setText(information);
                        }
                    }
                    else {
                        user_name.setText(username);


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

                LayoutInflater layoutInflater = LayoutInflater.from(AccountActivity.this);
                View view = layoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_edit_avatar, null);

                ImageButton add_btn = view.findViewById(R.id.add_btn);
                ImageButton close_btn = view.findViewById(R.id.close_btn);
                Button choose_image = view.findViewById(R.id.choose_image);
                final ProgressBar progressBar = view.findViewById(R.id.progress_horizontal);
                new_avatar = view.findViewById(R.id.new_avatar);

                //hiển thị dialog
                builder.setTitle("Thay đổi ảnh đại diện");
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();


                choose_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadImage(progressBar);
                    }
                });

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });


        //xử lý sự kiện cho nút sửa thông tin cá nhân
        edit_infor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_edit_information, null);

                final MaterialEditText new_name = view.findViewById(R.id.new_username);
                final MaterialEditText new_infor = view.findViewById(R.id.new_infor);

                ImageButton accept_btn = view.findViewById(R.id.accept_btn);
                ImageButton close_btn = view.findViewById(R.id.close_btn);

                //hiển thị dialog
                builder.setTitle("Thay đổi thông tin cá nhân");
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();



                //xử lý sự kiện khi nhấn nút chấp nhận
                accept_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        newName = new_name.getText().toString();
                        newInfor = new_infor.getText().toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", newName);
                        hashMap.put("information", newInfor);


                        databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(AccountActivity.this, "Cập nhật thông tin cá nhân thành công", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                });

                //xử lý sự kiện khi nhấn nút đóng
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



            }
        });


    }








    //chọn ảnh từ bộ nhớ máy
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            avatar_url = data.getData();

            Picasso.get().load(avatar_url).into(new_avatar);

        }

    }



    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //thêm ảnh vào storage
    private void uploadImage(final ProgressBar progressBar) {

        DatabaseReference mDataRef = databaseReference.child("avatar_url");
        if(avatar_url != null) {

            // đẩy ảnh vào storage của Firebase
            final StorageReference ImageRef = firebaseStorage.child("Users").child(user_id + "/" + "avatar"
                    + "." + getFileExtension(avatar_url));

            ImageRef.putFile(avatar_url)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);

                            // lấy ra đường dẫn dowload ảnh từ firebase storage và thêm bản ghi mới vào csdl
                            ImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String img_url = uri.toString();

                                    // tạo bản ghi rỗng
                                    final HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("avatar_url", img_url);
                                    databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);

                        }
                    });

        }
        else {
            Toast.makeText(getApplicationContext(), "Bạn phải chọn ảnh trước khi cập nhật", Toast.LENGTH_SHORT).show();
        }
    }



}
