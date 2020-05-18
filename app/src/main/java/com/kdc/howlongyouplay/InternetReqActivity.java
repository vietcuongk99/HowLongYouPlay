package com.kdc.howlongyouplay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InternetReqActivity extends AppCompatActivity {

    private Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_req);

        start_btn = findViewById(R.id.start);


        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {

                    Intent intent = new Intent(InternetReqActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InternetReqActivity.this);
                    View view = LayoutInflater.from(InternetReqActivity.this).inflate(R.layout.dialog_alert_no_internet, null);
                    builder.setView(view);

                    AlertDialog dialog = builder.show();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(InternetReqActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
}
