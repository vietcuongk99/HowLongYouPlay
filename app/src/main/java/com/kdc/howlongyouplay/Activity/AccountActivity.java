package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nikartm.button.FitButton;
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
import com.kdc.howlongyouplay.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private ImageButton edit_infor_btn, edit_avatar_btn;
    private CircleImageView user_avatar;
    private ImageView user_page_img;
    private TextView user_infor;
    private TextView user_name;
    private TextView user_gender;
    private Uri avatar_url;
    private String user_id;
    private DatabaseReference databaseReference;
    private StorageReference firebaseStorage;
    private FirebaseAuth mAuth;

    private RelativeLayout choose_img;
    private ImageView new_avatar;
    private String newName, newInfor, genderPicked, username, information, gender, img_url;
    private TextView action_choose_img;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

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
        user_page_img = findViewById(R.id.user_page_img);
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
                    username = dataSnapshot.child("username").getValue().toString();
                    information = dataSnapshot.child("information").getValue().toString();
                    img_url = dataSnapshot.child("avatar_url").getValue().toString();
                    gender = dataSnapshot.child("gender").getValue().toString();

                    if(!img_url.equals("default")) {
                        user_name.setText(username);
                        Picasso.get().load(img_url).into(user_avatar);
                        Picasso.get().load(img_url).into(user_page_img);


                        if (information.equals("")) {
                            user_infor.setText(getApplicationContext().getResources().getString(R.string.zero));
                        } else {
                            user_infor.setText(information);
                        }

                        if (gender.equals("")) {
                            user_gender.setText(getApplicationContext().getResources().getString(R.string.not_define));
                        } else {
                            user_gender.setText(gender);
                        }

                    } else {
                        user_name.setText(username);

                        if (information.equals("")) {
                            user_infor.setText(getApplicationContext().getResources().getString(R.string.zero));
                        } else {
                            user_infor.setText(information);
                        }

                        if (gender.equals("")) {
                            user_gender.setText(getApplicationContext().getResources().getString(R.string.not_define));
                        } else {
                            user_gender.setText(gender);
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

                View header_view = layoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_edit_avatar_header, null);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);
//                Button choose_image = view.findViewById(R.id.choose_image);
                final ProgressBar progressBar = view.findViewById(R.id.progress_horizontal);
                new_avatar = view.findViewById(R.id.new_avatar);
                choose_img = view.findViewById(R.id.choose_img);
                action_choose_img = view.findViewById(R.id.action_choose_img);

                //hiển thị dialog
                builder.setCustomTitle(header_view);
                builder.setView(view);
                builder.setCancelable(false);
                 final AlertDialog dialog = builder.show();



                // xử lý sự kiện khi click vào khung ảnh
                choose_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkPermission()) {
                            action_choose_img.setVisibility(View.GONE);
                            chooseImage();
                        } else {
                            requestStoragePermission();
                        }

                    }
                });



                // xử lý sự kiện khi nhấn xác nhân/đóng
                accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkConnected()) {
                            uploadImage(progressBar);
                        } else {
                            Toast.makeText(AccountActivity.this, "Không cập nhật được ảnh đại diện, kiểm tra kết nối mạng", Toast.LENGTH_SHORT)
                                    .show();
                        }
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
                final View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_edit_information, null);

                View header_view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_edit_information_header, null);

                final MaterialEditText new_name = view.findViewById(R.id.new_username);
                final MaterialEditText new_infor = view.findViewById(R.id.new_infor);

                final RadioGroup genderGroup = view.findViewById(R.id.gender_group);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                //hiển thị dialog
                builder.setCustomTitle(header_view);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();

                //xử lý sự kiện khi nhấn nút chấp nhận
                accept_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //gán giá trị cho các String tương ứng
                        newName = new_name.getText().toString();
                        newInfor = new_infor.getText().toString();

                        int i = genderGroup.getCheckedRadioButtonId();
                        RadioButton checkedButton = view.findViewById(i);
                        genderPicked  = checkedButton.getText().toString();

                        if(TextUtils.isEmpty(newName) || TextUtils.isEmpty(newInfor)) {
                            Toast.makeText(v.getContext(), "Bạn cần điền tên và phần mô tả bản thân", Toast.LENGTH_SHORT).show();
                        } else {

                            //tạo hashMap cập nhật dữ liệu
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("username", newName);
                            hashMap.put("information", newInfor);
                            hashMap.put("gender", genderPicked);


                            databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Toast.makeText(AccountActivity.this, "Cập nhật thông tin cá nhân thành công", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });

                        }

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


        // xử lý sự kiện khi click vào avatar
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!img_url.equals("default")) {
                    Intent intent = new Intent(AccountActivity.this, ImageViewerActivity.class);
                    intent.putExtra("img_url", img_url);
                    startActivity(intent);
                } else {
                    Toast.makeText(AccountActivity.this, "Chưa có ảnh đại diện", Toast.LENGTH_SHORT).show();
                }

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


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AccountActivity.this, "Ứng dụng yêu cầu quyền truy cập bộ nhớ. Vui lòng bật quyền này cho ứng dụng trong Cài đặt.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private boolean isNetworkConnected() {
        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }

        return connected;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
