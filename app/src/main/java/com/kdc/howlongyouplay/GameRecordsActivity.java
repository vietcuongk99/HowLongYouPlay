package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.Adapter.LogAdapter;
import com.kdc.howlongyouplay.Adapter.RecordAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GameRecordsActivity extends AppCompatActivity {

    private Intent intent;
    private ImageButton add_record_btn, statistic_btn;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<Record> recordList;
    private RecordAdapter recordAdapter;

    private String game_title, log_id, user_id, img_url, records,
            hour_value, minute_value, second_value, note_detail, status_picked, date_created, date_modifield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_records);

        intent = getIntent();
        game_title = intent.getExtras().get("game_title").toString();
        log_id = intent.getExtras().get("id").toString();
        img_url = intent.getExtras().get("img_url").toString();
        records = intent.getExtras().get("records").toString();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Logs").child(user_id).child(log_id);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(game_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameRecordsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        imageView = findViewById(R.id.image_game);
        add_record_btn = findViewById(R.id.add_record_btn);
        statistic_btn = findViewById(R.id.statistical_btn);
        recyclerView = findViewById(R.id.list_record);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GameRecordsActivity.this));


        Picasso.get().load(img_url).into(imageView);
        Log.d("ID: ", "ID: " + log_id);

        recordList = new ArrayList<>();
        getRecordList();


        add_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(GameRecordsActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(GameRecordsActivity.this);
                final View view = layoutInflater.from(GameRecordsActivity.this).inflate(R.layout.dialog_add_record, null);

                ImageButton accept_btn = view.findViewById(R.id.accept_btn);
                ImageButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);

                final RadioGroup statusGroup = view.findViewById(R.id.group_status);
                RadioButton finished_picked = view.findViewById(R.id.finished);
                RadioButton playing_picked = view.findViewById(R.id.playing);

                //kiểm tra thay đổi khi lựa chọn trạng thái trong statusGroup
                finished_picked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        onChangeStatusPicked(buttonView, isChecked);
                    }
                });
                playing_picked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        onChangeStatusPicked(buttonView, isChecked);
                    }
                });


                //hiển thị dialog
                builder.setTitle("Thêm bản ghi mới");
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();

                accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int new_records = Integer.parseInt(records) + 1;
                        String new_record = String.valueOf(new_records);
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy" + ", " + "HH:mm:ss a");
                        String time = timeFormat.format(calendar.getTime());
                        hour_value = hour.getText().toString();
                        minute_value = minute.getText().toString();
                        second_value = second.getText().toString();
                        note_detail = note.getText().toString();
                        date_created = time;


                        int i = statusGroup.getCheckedRadioButtonId();
                        RadioButton checkedButton = view.findViewById(i);
                        status_picked = checkedButton.getText().toString();


                        final HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("hour", hour_value);
                        hashMap.put("minute", minute_value);
                        hashMap.put("second", second_value);
                        hashMap.put("status", status_picked);
                        hashMap.put("note", note_detail);
                        hashMap.put("date_created", date_created);

                        DatabaseReference blank_record = databaseReference.child("records").push();

                        blank_record.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(GameRecordsActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            }
                        });



                        final HashMap<String, Object> hashMap_2 = new HashMap<>();
                        hashMap_2.put("total_record", new_record);
                        databaseReference.updateChildren(hashMap_2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(GameRecordsActivity.this, "Cập nhật số bản ghi thành công", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });


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

    }

    //nhận biết thay đổi khi chọn button trong RadioGroup
    private void onChangeStatusPicked(CompoundButton compoundButton, boolean isChecked) {
        RadioButton radio = (RadioButton) compoundButton;
        Log.d("Trạng thái: ", radio.getText().toString() + isChecked);
    }

    // lấy ra danh sách bản ghi của game tương ứng
    private void getRecordList() {

        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Logs").child(user_id).child(log_id).child("records");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recordList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Record record = snapshot.getValue(Record.class);
                    record.setRecord_id(snapshot.getKey());
                    recordList.add(record);

                }


                recordAdapter = new RecordAdapter(GameRecordsActivity.this,  recordList);
                recyclerView.setAdapter(recordAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}