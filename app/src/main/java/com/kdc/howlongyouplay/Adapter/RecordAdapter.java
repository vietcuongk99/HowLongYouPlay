package com.kdc.howlongyouplay.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Record;
import com.kdc.howlongyouplay.TimeCorrect;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.opencensus.resource.Resource;

//cmt tương tự với ListAdapter và LogAdapter
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private Context mContext;
    private List<Record> recordList;

    private CheckBox playing, finished, retired, backlog;
    private MaterialEditText finished_date;
    private Toast toast;
    private int hour_format, minute_format, second_format;

    public RecordAdapter(Context mContext, List<Record> recordList) {
        this.mContext = mContext;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordAdapter.RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = new View(mContext);
        Resources resource = view.getResources();
        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.record_item_2, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.record_item, parent, false);
        }
        return new RecordAdapter.RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        final Record record = recordList.get(position);
        // key của một bản record tương ứng
        final String key = record.getRecord_id();
        //gán dữ liệu vào view
        holder.game_title.setText(record.getGame_title());
        Picasso.get().load(record.getIcon_url()).into(holder.game_icon);

        // xử lý sự kiện khi nhấn vào một record tương ứng
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                final View view = layoutInflater.inflate(R.layout.dialog_record, null);
                final View view_header = layoutInflater.inflate(R.layout.dialog_record_header, null);
                TextView play_time = view.findViewById(R.id.play_time);
                TextView note = view.findViewById(R.id.note);
                TextView date_created = view.findViewById(R.id.date_created);
                TextView date_modified = view.findViewById(R.id.date_modified);
                FitButton close_btn = view_header.findViewById(R.id.close_btn);
                TextView finished_date_value = view.findViewById(R.id.finished_date);
                TextView playing_status = view.findViewById(R.id.playing_status);
                TextView finished_status = view.findViewById(R.id.finished_status);
                TextView retired_status = view.findViewById(R.id.retired_status);
                TextView backlog_status = view.findViewById(R.id.backlog_status);

                if (record.getStatus().contains("playing")) {
                    playing_status.setVisibility(View.VISIBLE);
                }
                if (record.getStatus().contains("finished")) {
                    finished_status.setVisibility(View.VISIBLE);
                }
                if (record.getStatus().contains("retired")) {
                    retired_status.setVisibility(View.VISIBLE);
                }
                if (record.getStatus().contains("backlog")) {
                    backlog_status.setVisibility(View.VISIBLE);
                }

                date_created.setText(record.getDate_created());
                play_time.setText(mContext.getResources().getString(R.string.format_time,
                        record.getHour(), record.getMinute(), record.getSecond()));

                if(record.getDate_modified().equals("")) {
                    date_modified.setText("Chưa xác định");
                } else {
                    date_modified.setText(record.getDate_modified());
                }

                if(record.getFinished_date().equals("")) {
                    finished_date_value.setText("Chưa xác định");
                } else {
                    finished_date_value.setText(record.getFinished_date());
                }

                if (record.getNote().equals("")) {
                    note.setText("Không có ghi chú");
                } else {
                    note.setText(record.getNote());
                }

                builder.setCancelable(true);
                builder.setCustomTitle(view_header);
                builder.setView(view);
                final AlertDialog dialog = builder.show();
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });


        // xử lý sự kiện khi sửa 1 record trong danh sách
        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                final View view = layoutInflater.inflate(R.layout.dialog_edit_record, null);
                final View header_view = layoutInflater.inflate(R.layout.dialog_edit_record_header, null);

                FitButton accept_btn = view.findViewById(R.id.accept_btn);
                FitButton close_btn = view.findViewById(R.id.close_btn);

                final MaterialEditText hour = view.findViewById(R.id.hour);
                final MaterialEditText minute = view.findViewById(R.id.minute);
                final MaterialEditText second = view.findViewById(R.id.second);
                final MaterialEditText note = view.findViewById(R.id.note);
                finished_date = view.findViewById(R.id.finished_date);

                playing = view.findViewById(R.id.playing);
                finished = view.findViewById(R.id.finished);
                retired = view.findViewById(R.id.retired);
                backlog = view.findViewById(R.id.backlog);


                List<CheckBox> list = new ArrayList<CheckBox>();
                list.add(playing);
                list.add(finished);
                list.add(retired);
                list.add(backlog);

                String text = record.getStatus();
                for (CheckBox item: list) {
                    if (text.contains(item.getText().toString().toLowerCase())) {
                        item.setChecked(true);
                    }
                }


                final List<CheckBox> checkBoxList = new ArrayList<>();
                checkBoxList.add(playing);
                checkBoxList.add(finished);
                checkBoxList.add(retired);
                checkBoxList.add(backlog);


                //hiển thị dialog
                builder.setCustomTitle(header_view);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();

                if (record.getStatus().contains("finished")) {
                    finished_date.setVisibility(View.VISIBLE);
                }

                // xử lý sự kiện khi nút xác nhận sửa record
                accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!playing.isChecked() && !finished.isChecked() && !retired.isChecked() && !backlog.isChecked()) {
                            Toast.makeText(mContext, "Bạn chưa chọn box nào", Toast.LENGTH_SHORT).show();
                        } else {
                            StringBuilder status = new StringBuilder();
                            attachCheckListener(checkBoxList);
                            for (CheckBox item: checkBoxList) {
                                if (item.isChecked()) {
                                    status.append(item.getText().toString().toLowerCase());
                                }
                            }


                            dialog.dismiss();
                            toast = Toast.makeText(mContext, "Sửa thành công", Toast.LENGTH_SHORT);
                            toast.show();


                            String new_hour_value, new_minute_value, new_second_value, new_note_detail,  date_modified, new_finished_date;

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy" + ", " + "HH:mm:ss a");
                            date_modified = timeFormat.format(calendar.getTime());
                            new_note_detail = note.getText().toString();


                            if (hour.getText().toString().equals("") || Integer.parseInt(hour.getText().toString()) == 0) {
                                new_hour_value = "00";
                            } else {
                                hour_format = Integer.parseInt(hour.getText().toString());
                            }
                            if (minute.getText().toString().equals("") || Integer.parseInt(minute.getText().toString()) == 0) {
                                new_minute_value = "00";
                            } else {
                                minute_format = Integer.parseInt(minute.getText().toString());
                            }
                            if (second.getText().toString().equals("") || Integer.parseInt(second.getText().toString()) == 0) {
                                new_second_value = "00";
                            } else {
                                second_format = Integer.parseInt(second.getText().toString());
                            }

                            TimeCorrect timeCorrect = new TimeCorrect(hour_format, minute_format, second_format);
                            timeCorrect.correctTimeInput();

                            new_hour_value = String.format("%02d", timeCorrect.getHour());
                            new_minute_value = String.format("%02d", timeCorrect.getMinute());
                            new_second_value = String.format("%02d", timeCorrect.getSecond());


                            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                    .child("Logs").child(user_id);

                            if (record.getStatus().contains("finished")) {
                                new_finished_date = finished_date.getText().toString();

                                final HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("hour", new_hour_value);
                                hashMap.put("minute", new_minute_value);
                                hashMap.put("second", new_second_value);
                                hashMap.put("note", new_note_detail);
                                hashMap.put("status", status.toString());
                                hashMap.put("date_modified", date_modified);
                                hashMap.put("finished_date", new_finished_date);

                                databaseReference.child(record.getGame_id()).child("records").child(key)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                toast.cancel();
                                                Toast.makeText(mContext, "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            } else {

                                final HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("hour", new_hour_value);
                                hashMap.put("minute", new_minute_value);
                                hashMap.put("second", new_second_value);
                                hashMap.put("note", new_note_detail);
                                hashMap.put("status", status.toString());
                                hashMap.put("date_modified", date_modified);

                                databaseReference.child(record.getGame_id()).child("records").child(key)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                toast.cancel();
                                                Toast.makeText(mContext, "Cập nhật danh sách thành công", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });

                            }
                        }

                    }
                });

                // xử lý sự kiện cho nút hủy thay đổi record
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



            }
        });


        // xử lý sự kiện khi xóa 1 record trong danh sách
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Logs").child(user_id);

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Xóa log").setMessage("Bạn muốn xóa bản record này ?");

                //xử lý sự kiện khi đồng ý xóa record
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        toast = Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_SHORT);
                        toast.show();


                        databaseReference.child(record.getGame_id()).child("records").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    if(snapshot.getKey().equals(key)) {
                                        databaseReference.child(record.getGame_id()).child("records").child(key)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    toast.cancel();
                                                    Toast.makeText(mContext, "Cập nhật danh sách thành công", Toast.LENGTH_SHORT).show();

                                                }
                                                else {
                                                    toast.cancel();
                                                    Toast.makeText(mContext, "Xóa lỗi", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });


                // xử lý sự kiện khi từ chối xóa record
                alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setCancelable(true);
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        private TextView game_title;
        private ImageView game_icon;
        private FitButton edit_btn;
        private FitButton delete_btn;

        public RecordViewHolder(View itemView) {
            super(itemView);
            game_title = itemView.findViewById(R.id.game_title);
            game_icon = itemView.findViewById(R.id.game_icon);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }



    CompoundButton.OnCheckedChangeListener m_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };


    public void attachCheckListener(List<CheckBox> checkBoxes) {

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener(m_listener);
        }


    }

    public void detachCheckListener(CheckBox playing, CheckBox finished, CheckBox retired, CheckBox backlog) {
        playing.setOnCheckedChangeListener(null);
        finished.setOnCheckedChangeListener(null);
        retired.setOnCheckedChangeListener(null);
        backlog.setOnCheckedChangeListener(null);
    }
}
