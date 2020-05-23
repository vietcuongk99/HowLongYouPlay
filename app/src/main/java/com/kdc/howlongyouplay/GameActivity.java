package com.kdc.howlongyouplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nikartm.button.FitButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.skydoves.transformationlayout.TransformationLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.opencensus.resource.Resource;

// hiển thị nội dung khi click vào 1 log
public class GameActivity extends AppCompatActivity {

    private TextView title_value, game_title, year, genre, developer, pulisher, play_on;

    private Intent intent;

    private ImageView imageView;
    private FirebaseAuth mAuth;

    private String title, year_release, user_id, id_game, img_url, icon_url,
            genre_name, developer_name, publisher_name, play_on_device,
            hour_value, minute_value, second_value, note_detail, date_created, finished_date;

    private int hour_format, minute_format, second_format;

    private FitButton add_playing, add_finished, add_retired, add_backlog, add_record_btn, btn_2;

    private TransformationLayout button_1, button_2;
    private ConstraintLayout group_action, group_divider, view1, view2, view3, view4;
    private RelativeLayout top_content;


    private ArrayList<Record> finishedList, playingList, retiredList, backlogList;
    private DatabaseReference databaseReference;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_2);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        title_value = findViewById(R.id.title);
        game_title = findViewById(R.id.game_title);
        year = findViewById(R.id.year);
        developer = findViewById(R.id.developer);
        pulisher = findViewById(R.id.pulisher);
        play_on = findViewById(R.id.play_on);
        genre = findViewById(R.id.genre);
        imageView = (ImageView) findViewById(R.id.image_game);

        group_action = findViewById(R.id.group_action);
        group_divider = findViewById(R.id.group_divider);
        top_content = findViewById(R.id.top_content);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);

        button_1 = findViewById(R.id.button_transform_1);
        add_record_btn = findViewById(R.id.add_record_btn);
        FitButton cancel_button = findViewById(R.id.cancel_action);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        final Resources resource = appBarLayout.getResources();



        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            int width = size.x;
            top_content.getLayoutParams().height = height/3;

            ConstraintSet constraintSet = new ConstraintSet();

            constraintSet.clone(group_divider);

            group_divider.getLayoutParams().width = width;
            group_divider.getLayoutParams().height = width/6;

            //view 1
            constraintSet.connect(R.id.view1,ConstraintSet.START,R.id.group_divider,ConstraintSet.START,0);
            constraintSet.connect(R.id.view1,ConstraintSet.TOP,R.id.group_divider,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.view1,ConstraintSet.BOTTOM,R.id.group_divider,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.view1,ConstraintSet.END,R.id.view2,ConstraintSet.START,0);
            constraintSet.setDimensionRatio(R.id.view1, "1");


            //view 2
            constraintSet.connect(R.id.view2,ConstraintSet.START,R.id.view1,ConstraintSet.END,0);
            constraintSet.connect(R.id.view2,ConstraintSet.TOP,R.id.group_divider,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.view2,ConstraintSet.BOTTOM,R.id.group_divider,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.view2,ConstraintSet.END,R.id.view3,ConstraintSet.START,0);

            constraintSet.setDimensionRatio(R.id.view2, "1");

            //view 3

            constraintSet.connect(R.id.view3,ConstraintSet.START,R.id.view2,ConstraintSet.END,0);
            constraintSet.connect(R.id.view3,ConstraintSet.TOP,R.id.group_divider,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.view3,ConstraintSet.BOTTOM,R.id.group_divider,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.view3,ConstraintSet.END,R.id.view4,ConstraintSet.START,0);

            constraintSet.setDimensionRatio(R.id.view3, "1");

            //view 4
            //view4.requestLayout();
            constraintSet.connect(R.id.view4,ConstraintSet.START,R.id.view3,ConstraintSet.END,0);
            constraintSet.connect(R.id.view4,ConstraintSet.TOP,R.id.group_divider,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.view4,ConstraintSet.BOTTOM,R.id.group_divider,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.view4,ConstraintSet.END,R.id.group_divider,ConstraintSet.END,0);

            constraintSet.setDimensionRatio(R.id.view4, "1");

            constraintSet.applyTo(group_divider);

        }

        add_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_1.startTransform();
            }
        });


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_1.finishTransform();
            }
        });




        add_playing = findViewById(R.id.add_playing);
        add_finished = findViewById(R.id.add_finished);
        add_backlog = findViewById(R.id.add_backlog);
        add_retired = findViewById(R.id.add_retired);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        // lấy dữ liệu khi click vào một game bất kì trong danh sách tìm kiếm
        //ListAdapter
        intent = getIntent();
        title = intent.getExtras().get("game_title").toString();
        year_release = intent.getExtras().get("year").toString();
        genre_name = intent.getExtras().get("genre").toString();
        play_on_device = intent.getExtras().get("play_on").toString();
        developer_name = intent.getExtras().get("developer").toString();
        publisher_name = intent.getExtras().get("pulisher").toString();
        id_game = intent.getExtras().get("game_id").toString();
        img_url = intent.getExtras().get("img_url").toString();
        icon_url = intent.getExtras().get("icon_url").toString();


        //Hiển thị id của game đã chọn qua Log
        Log.d("ID Game", "ID game được chọn: " + id_game);


        // đặt nội dung cho TextView
        game_title.setText(title);
        title_value.setText(title);
        year.setText(getApplicationContext().getResources().getString(R.string.year, year_release));
        genre.setText(getApplicationContext().getResources().getString(R.string.genre, genre_name));
        developer.setText(getApplicationContext().getResources().getString(R.string.developer, developer_name));
        pulisher.setText(getApplicationContext().getResources().getString(R.string.pulisher, publisher_name));
        play_on.setText(getApplicationContext().getResources().getString(R.string.play_on, play_on_device));

        //đặt hình ảnh cho ImageView
        Picasso.get().load(img_url).into(imageView);


        //xử lý sự kiện khi click vào ảnh game
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, ImageViewerActivity.class);
                intent.putExtra("img_url", img_url);
                intent.putExtra("game_title", title);

                startActivity(intent);
            }
        });


        //lấy ra danh sách Log để so sánh
        finishedList = new ArrayList<>();
        playingList = new ArrayList<>();
        retiredList = new ArrayList<>();
        backlogList = new ArrayList<>();
        readLogList();





        // xử lý sự kiện cho nút add
        add_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkSameLog(playingList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {


                    // tạo builder và các phần tử liên quan
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                    LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);

                    FitButton accept_btn = view.findViewById(R.id.accept_btn);
                    FitButton close_btn = view.findViewById(R.id.close_btn);

                    final MaterialEditText hour = view.findViewById(R.id.hour);
                    final MaterialEditText minute = view.findViewById(R.id.minute);
                    final MaterialEditText second = view.findViewById(R.id.second);
                    final MaterialEditText note = view.findViewById(R.id.note);


                    //hiển thị dialog
                    builder.setTitle("Thêm bản ghi mới");
                    builder.setView(view);
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            toast = Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT);
                            toast.show();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy" + ", " + "HH:mm:ss a");
                            String time = timeFormat.format(calendar.getTime());
                            note_detail = note.getText().toString();
                            date_created = time;
                            if (hour.getText().toString().equals("") || Integer.parseInt(hour.getText().toString()) == 0) {
                                hour_value = "00";
                            } else {
                                hour_format = Integer.parseInt(hour.getText().toString());
                            }
                            if (minute.getText().toString().equals("") || Integer.parseInt(minute.getText().toString()) == 0) {
                                minute_value = "00";
                            } else {
                                minute_format = Integer.parseInt(minute.getText().toString());
                            }
                            if (second.getText().toString().equals("") || Integer.parseInt(second.getText().toString()) == 0) {
                                second_value = "00";
                            } else {
                                second_format = Integer.parseInt(second.getText().toString());
                            }

                            TimeCorrect timeCorrect = new TimeCorrect(hour_format, minute_format, second_format);
                            timeCorrect.correctTimeInput();

                            hour_value = String.format("%02d", timeCorrect.getHour());
                            minute_value = String.format("%02d", timeCorrect.getMinute());
                            second_value = String.format("%02d", timeCorrect.getSecond());


                            final HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("game_title", title);
                            hashMap.put("icon_url", icon_url);
                            hashMap.put("image_url", img_url);
                            hashMap.put("hour", hour_value);
                            hashMap.put("minute", minute_value);
                            hashMap.put("second", second_value);
                            hashMap.put("note", note_detail);
                            hashMap.put("date_created", date_created);
                            hashMap.put("date_modified", "");
                            hashMap.put("status", "playing");
                            hashMap.put("finished_date", "");

                            DatabaseReference blank_record = databaseReference.child("playing").push();

                            blank_record.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    toast.cancel();
                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
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
            }

        });



        add_backlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkSameLog(backlogList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    // tạo builder và các phần tử liên quan
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                    LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);

                    FitButton accept_btn = view.findViewById(R.id.accept_btn);
                    FitButton close_btn = view.findViewById(R.id.close_btn);

                    final MaterialEditText note = view.findViewById(R.id.note);
                    LinearLayout play_time = view.findViewById(R.id.play_time);
                    TextView title_1 = view.findViewById(R.id.title_1);
                    title_1.setVisibility(View.GONE);
                    play_time.setVisibility(View.GONE);

                    //hiển thị dialog
                    builder.setTitle("Thêm bản ghi mới");
                    builder.setView(view);
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            toast = Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT);
                            toast.show();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy" + ", " + "HH:mm:ss a");
                            String time = timeFormat.format(calendar.getTime());
                            note_detail = note.getText().toString();
                            date_created = time;


                            final HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("game_title", title);
                            hashMap.put("icon_url", icon_url);
                            hashMap.put("image_url", img_url);
                            hashMap.put("note", note_detail);
                            hashMap.put("date_created", date_created);
                            hashMap.put("date_modified", "");
                            hashMap.put("status", "backlog");

                            DatabaseReference blank_record = databaseReference.child("backlog").push();

                            blank_record.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    toast.cancel();
                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
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
            }

        });



        add_finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSameLog(finishedList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {


                    // tạo builder và các phần tử liên quan
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                    LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);

                    FitButton accept_btn = view.findViewById(R.id.accept_btn);
                    FitButton close_btn = view.findViewById(R.id.close_btn);

                    final MaterialEditText hour = view.findViewById(R.id.hour);
                    final MaterialEditText minute = view.findViewById(R.id.minute);
                    final MaterialEditText second = view.findViewById(R.id.second);
                    final MaterialEditText note = view.findViewById(R.id.note);
                    final MaterialEditText date_completed = view.findViewById(R.id.finished_date);

                    date_completed.setVisibility(View.VISIBLE);


                    //hiển thị dialog
                    builder.setTitle("Thêm bản ghi mới");
                    builder.setView(view);
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            toast = Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT);
                            toast.show();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy" + ", " + "HH:mm:ss a");
                            String time = timeFormat.format(calendar.getTime());
                            note_detail = note.getText().toString();
                            date_created = time;
                            finished_date = date_completed.getText().toString();
                            if (hour.getText().toString().equals("") || Integer.parseInt(hour.getText().toString()) == 0) {
                                hour_value = "00";
                            } else {
                                hour_format = Integer.parseInt(hour.getText().toString());
                            }
                            if (minute.getText().toString().equals("") || Integer.parseInt(minute.getText().toString()) == 0) {
                                minute_value = "00";
                            } else {
                                minute_format = Integer.parseInt(minute.getText().toString());
                            }
                            if (second.getText().toString().equals("") || Integer.parseInt(second.getText().toString()) == 0) {
                                second_value = "00";
                            } else {
                                second_format = Integer.parseInt(second.getText().toString());
                            }

                            TimeCorrect timeCorrect = new TimeCorrect(hour_format, minute_format, second_format);
                            timeCorrect.correctTimeInput();

                            hour_value = String.format("%02d", timeCorrect.getHour());
                            minute_value = String.format("%02d", timeCorrect.getMinute());
                            second_value = String.format("%02d", timeCorrect.getSecond());


                            final HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("game_title", title);
                            hashMap.put("icon_url", icon_url);
                            hashMap.put("image_url", img_url);
                            hashMap.put("hour", hour_value);
                            hashMap.put("minute", minute_value);
                            hashMap.put("second", second_value);
                            hashMap.put("note", note_detail);
                            hashMap.put("date_created", date_created);
                            hashMap.put("date_modified", "");
                            hashMap.put("status", "finished");
                            hashMap.put("finished_date", finished_date);

                            DatabaseReference blank_record = databaseReference.child("finished").push();

                            blank_record.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    toast.cancel();
                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
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
            }

        });



        add_retired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSameLog(retiredList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {


                    // tạo builder và các phần tử liên quan
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                    LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);

                    FitButton accept_btn = view.findViewById(R.id.accept_btn);
                    FitButton close_btn = view.findViewById(R.id.close_btn);

                    final MaterialEditText hour = view.findViewById(R.id.hour);
                    final MaterialEditText minute = view.findViewById(R.id.minute);
                    final MaterialEditText second = view.findViewById(R.id.second);
                    final MaterialEditText note = view.findViewById(R.id.note);


                    //hiển thị dialog
                    builder.setTitle("Thêm bản ghi mới");
                    builder.setView(view);
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            toast = Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT);
                            toast.show();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy" + ", " + "HH:mm:ss a");
                            String time = timeFormat.format(calendar.getTime());
                            note_detail = note.getText().toString();
                            date_created = time;


                            if (hour.getText().toString().equals("") || Integer.parseInt(hour.getText().toString()) == 0) {
                                hour_value = "00";
                            } else {
                                hour_format = Integer.parseInt(hour.getText().toString());
                            }
                            if (minute.getText().toString().equals("") || Integer.parseInt(minute.getText().toString()) == 0) {
                                minute_value = "00";
                            } else {
                                minute_format = Integer.parseInt(minute.getText().toString());
                            }
                            if (second.getText().toString().equals("") || Integer.parseInt(second.getText().toString()) == 0) {
                                second_value = "00";
                            } else {
                                second_format = Integer.parseInt(second.getText().toString());
                            }

                            TimeCorrect timeCorrect = new TimeCorrect(hour_format, minute_format, second_format);
                            timeCorrect.correctTimeInput();

                            hour_value = String.format("%02d", timeCorrect.getHour());
                            minute_value = String.format("%02d", timeCorrect.getMinute());
                            second_value = String.format("%02d", timeCorrect.getSecond());


                            final HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("game_title", title);
                            hashMap.put("icon_url", icon_url);
                            hashMap.put("image_url", img_url);
                            hashMap.put("hour", hour_value);
                            hashMap.put("minute", minute_value);
                            hashMap.put("second", second_value);
                            hashMap.put("note", note_detail);
                            hashMap.put("date_created", date_created);
                            hashMap.put("date_modified", "");
                            hashMap.put("status", "retired");
                            hashMap.put("finished_date", "");

                            DatabaseReference blank_record = databaseReference.child("retired").push();

                            blank_record.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    toast.cancel();
                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
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
            }

        });


    }


    // kiểm tra xem game được chọn đã có trong log của người dùng hiện tại chưa
    // nếu chưa trả về true
    private boolean checkSameLog(ArrayList<Record> records, String game_title) {

        for(int i = 0; i<records.size(); i++) {
            if(records.get(i).getGame_title().equals(game_title)) {
                return true;
            }
        }

        return false;

    }


    // lấy ra danh sách game có trong danh sách của người dùng hiện tại
    private void readLogList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Logs").child(user_id);

        // lấy ra danh sách GameLog và sử dụng LogAdapter
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("playing")) {
                    playingList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.child("playing").getChildren()) {

                        Record record = snapshot.getValue(Record.class);
                        record.setRecord_id(snapshot.getKey());
                        playingList.add(record);

                    }
                }
                if (dataSnapshot.hasChild("finished")) {
                    finishedList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.child("finished").getChildren()) {

                        Record record = snapshot.getValue(Record.class);
                        record.setRecord_id(snapshot.getKey());
                        finishedList.add(record);

                    }
                }
                if (dataSnapshot.hasChild("retired")) {
                    retiredList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.child("retired").getChildren()) {

                        Record record = snapshot.getValue(Record.class);
                        record.setRecord_id(snapshot.getKey());
                        retiredList.add(record);

                    }
                }
                if (dataSnapshot.hasChild("backlog")) {
                    backlogList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.child("backlog").getChildren()) {

                        Record record = snapshot.getValue(Record.class);
                        record.setRecord_id(snapshot.getKey());
                        backlogList.add(record);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //nhận biết thay đổi khi chọn button trong RadioGroup
    /*private void onChangeStatusPicked(CompoundButton compoundButton, boolean isChecked) {
        RadioButton radio = (RadioButton) compoundButton;
        Log.d("Trạng thái: ", radio.getText().toString() + isChecked);
    }

     */

}
