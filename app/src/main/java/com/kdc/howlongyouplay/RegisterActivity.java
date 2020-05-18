package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // các thành phần trong giao diện Register
    MaterialEditText username, email, password;
    Button btn_regiser;

    // Firebase
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // lấy ra id từng thành phần
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_regiser = findViewById(R.id.register_button);

        auth = FirebaseAuth.getInstance();


        // xử lý sự kiện khi click vào nút Register
        btn_regiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                // kiểm tra xem đã nhập vào các trường Username, Password, email phù hợp chưa, nếu chưa thì thông báo
                // gọi phương thức register()
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(RegisterActivity.this, "Cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Cần nhập password dài hơn 6 kí tự", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    // phương thức register() đăng ký tài khoản
    private void register(final String username, final String email, String password) {
        final ProgressDialog dialog = ProgressDialog
                .show(RegisterActivity.this,
                        "Loading Register",
                        "Bình tĩnh nhé, hệ thống đang xử lý...",
                        true);
        dialog.setCanceledOnTouchOutside(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            //đẩy một bản ghi mới vào nhánh User trong csdl Firebase
                            reference = FirebaseDatabase.getInstance().getReference("Users");

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("email", email);
                            hashMap.put("gender", "");
                            hashMap.put("information", "");
                            hashMap.put("role", "normal_user");
                            hashMap.put("avatar_url", "default");

                            reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        // chuyển sang màn hình MainActivity
                                        dialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        // hiển thị thông báo trên màn hình RegisterActivity
                                        dialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Không thể đăng ký với tài khoản này", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }

                        else {
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Email này đã được đăng ký, mời nhập lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
