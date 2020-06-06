package com.kdc.howlongyouplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nikartm.button.FitButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.TimeCorrect;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.skydoves.transformationlayout.TransformationLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

// hiển thị nội dung khi click vào 1 log
public class GameActivity extends AppCompatActivity {

    private TextView title_value, game_title, year, genre, developer, pulisher, play_on, finished_time;

    private Intent intent;

    private ImageView imageView;
    private FirebaseAuth mAuth;

    private String title, year_release, user_id, id_game, img_url, icon_url,
            genre_name, developer_name, publisher_name, play_on_device,
            hour_value, minute_value, second_value, note_detail, date_created, finished_date;

    private int hour_format, minute_format, second_format;

    private FitButton add_playing, add_finished, add_retired, add_backlog, add_record_btn, statistical_btn;

    private TransformationLayout button_1, button_2;
    private ConstraintLayout group_action, group_divider, view1, view2, view3, view4;
    private RelativeLayout top_content, finished_time_layout;


    private DatabaseReference databaseReference;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


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
        finished_time_layout = findViewById(R.id.finished_time_layout);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);

        button_1 = findViewById(R.id.button_transform_1);
        button_2 = findViewById(R.id.button_transform_2);
        add_record_btn = findViewById(R.id.add_record_btn);
        statistical_btn = findViewById(R.id.show_statistic_btn);
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

        statistical_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_2.startTransform();
            }
        });

        finished_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_2.finishTransform();
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

        //Hiển thị id của game đã chọn qua GameLog
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



        // xử lý sự kiện cho nút add
        add_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                if (checkSameLog(playingList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {
                 */


                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);
                final View header_view = layoutInflater.inflate(R.layout.dialog_add_record_header, null);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);


                //hiển thị dialog
                builder.setCustomTitle(header_view);
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


                        final HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("game_title", title);
                        hashMap2.put("icon_url", icon_url);
                        hashMap2.put("image_url", img_url);

                        final HashMap<String, Object> hashMap3 = new HashMap<>();
                        hashMap3.put("status", "playing");
                        hashMap3.put("hour", hour_value);
                        hashMap3.put("minute", minute_value);
                        hashMap3.put("second", second_value);


                        //kiểm tra dữ liệu game của người dùng trong GameLog
                        //dùng addListenerForSingleValueEvent để kiểm tra dữ liệu 1 lần
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //nếu log của người dùng đã có game cần thêm record
                                if (dataSnapshot.hasChild(id_game)) {
                                    DatabaseReference blank_record_3 = databaseReference.child(id_game).child("records").push();
                                    final String key_push = blank_record_3.getKey();
                                    blank_record_3.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                    .child(id_game).child("records").child(key_push);

                                            blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    //nếu chưa có game cần thêm record
                                    databaseReference.child(id_game).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            toast.cancel();
                                            DatabaseReference blank_record_2 = databaseReference.child(id_game).child("records").push();
                                            final String key_push = blank_record_2.getKey();
                                            blank_record_2.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                            .child(id_game).child("records").child(key_push);

                                                    blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                //}
            }

        });



        add_backlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);
                final View header_view = layoutInflater.inflate(R.layout.dialog_add_record_header, null);


                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);


                //hiển thị dialog
                builder.setCustomTitle(header_view);
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
                        hashMap.put("status", "backlog");
                        hashMap.put("finished_date", "");

                        final HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("game_title", title);
                        hashMap2.put("icon_url", icon_url);
                        hashMap2.put("image_url", img_url);

                        final HashMap<String, Object> hashMap3 = new HashMap<>();
                        hashMap3.put("status", "backlog");
                        hashMap3.put("hour", hour_value);
                        hashMap3.put("minute", minute_value);
                        hashMap3.put("second", second_value);

                        //kiểm tra dữ liệu game của người dùng trong GameLog
                        //dùng addListenerForSingleValueEvent để kiểm tra dữ liệu 1 lần
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //nếu log của người dùng đã có game cần thêm record
                                if (dataSnapshot.hasChild(id_game)) {
                                    DatabaseReference blank_record_3 = databaseReference.child(id_game).child("records").push();
                                    final String key_push = blank_record_3.getKey();
                                    blank_record_3.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                    .child(id_game).child("records").child(key_push);

                                            blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    //nếu chưa có game cần thêm record
                                    databaseReference.child(id_game).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            toast.cancel();
                                            DatabaseReference blank_record_2 = databaseReference.child(id_game).child("records").push();
                                            final String key_push = blank_record_2.getKey();
                                            blank_record_2.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                            .child(id_game).child("records").child(key_push);

                                                    blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            //}

        });



        add_finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);
                final View header_view = layoutInflater.inflate(R.layout.dialog_add_record_header, null);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);
                final MaterialEditText date_completed = view.findViewById(R.id.finished_date);

                date_completed.setVisibility(View.VISIBLE);


                //hiển thị dialog
                builder.setCustomTitle(header_view);
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

                        final HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("game_title", title);
                        hashMap2.put("icon_url", icon_url);
                        hashMap2.put("image_url", img_url);

                        final HashMap<String, Object> hashMap3 = new HashMap<>();
                        hashMap3.put("status", "finished");
                        hashMap3.put("hour", hour_value);
                        hashMap3.put("minute", minute_value);
                        hashMap3.put("second", second_value);

                        //kiểm tra dữ liệu game của người dùng trong GameLog
                        //dùng addListenerForSingleValueEvent để kiểm tra dữ liệu 1 lần
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //nếu log của người dùng đã có game cần thêm record
                                if (dataSnapshot.hasChild(id_game)) {
                                    DatabaseReference blank_record_3 = databaseReference.child(id_game).child("records").push();
                                    final String key_push = blank_record_3.getKey();
                                    blank_record_3.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                    .child(id_game).child("records").child(key_push);

                                            blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });
                                } else {
                                    //nếu chưa có game cần thêm record
                                    databaseReference.child(id_game).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            toast.cancel();
                                            DatabaseReference blank_record_2 = databaseReference.child(id_game).child("records").push();
                                            final String key_push = blank_record_2.getKey();
                                            blank_record_2.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                            .child(id_game).child("records").child(key_push);

                                                    blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            //}

        });



        add_retired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                if (checkSameLog(retiredList, title)) {
                    Toast.makeText(GameActivity.this,
                            "Bạn đã thêm game này vào danh sách rồi, vui lòng kiểm tra lại", Toast.LENGTH_SHORT)
                            .show();
                } else {
                 */


                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
                final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);
                final View header_view = layoutInflater.inflate(R.layout.dialog_add_record_header, null);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);


                //hiển thị dialog
                builder.setCustomTitle(header_view);
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

                        final HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("game_title", title);
                        hashMap2.put("icon_url", icon_url);
                        hashMap2.put("image_url", img_url);

                        final HashMap<String, Object> hashMap3 = new HashMap<>();
                        hashMap3.put("status", "retired");
                        hashMap3.put("hour", hour_value);
                        hashMap3.put("minute", minute_value);
                        hashMap3.put("second", second_value);


                        //kiểm tra dữ liệu game của người dùng trong GameLog
                        //dùng addListenerForSingleValueEvent để kiểm tra dữ liệu 1 lần
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //nếu log của người dùng đã có game cần thêm record
                                //bổ sung record mới trong records
                                if (dataSnapshot.hasChild(id_game)) {
                                    DatabaseReference blank_record_3 = databaseReference.child(id_game).child("records").push();
                                    final String key_push = blank_record_3.getKey();
                                    blank_record_3.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                    .child(id_game).child("records").child(key_push);

                                            blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });

                                } else {
                                    //nếu chưa có game cần thêm record
                                    //thêm game và record tương ứng
                                    databaseReference.child(id_game).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            toast.cancel();
                                            DatabaseReference blank_record_2 = databaseReference.child(id_game).child("records").push();
                                            final String key_push = blank_record_2.getKey();
                                            blank_record_2.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DatabaseReference blank_record = FirebaseDatabase.getInstance().getReference("List")
                                                            .child(id_game).child("records").child(key_push);

                                                    blank_record.setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Cập nhật danh sách thành công",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            //}

        });



        finished_time = findViewById(R.id.finished_time);




    }



    //nhận biết thay đổi khi chọn button trong RadioGroup
    /*private void onChangeStatusPicked(CompoundButton compoundButton, boolean isChecked) {
        RadioButton radio = (RadioButton) compoundButton;
        GameLog.d("Trạng thái: ", radio.getText().toString() + isChecked);
    }
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}