package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import com.kdc.howlongyouplay.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btn_login;

    FirebaseAuth auth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login_button);


        // xử lý sự kiện khi nhấn nút Login
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();



                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    // hiển thị thông báo
                    Toast.makeText(LoginActivity.this, "Bạn cần điền đầy đủ email và password", Toast.LENGTH_SHORT).show();
                }
                else {

                    dialog = ProgressDialog.show(LoginActivity.this,
                            "Loading Login",
                            "Bình tĩnh nhé, hệ thống đang xử lý...",
                            true);

                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        //chuyển màn sang MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        // hiển thị thông báo
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this,
                                                "Đăng nhập lỗi, mời nhập lại email/mật khẩu hoặc kiểm tra kết nối mạng",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(dialog!=null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        dialog = null;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }



}
