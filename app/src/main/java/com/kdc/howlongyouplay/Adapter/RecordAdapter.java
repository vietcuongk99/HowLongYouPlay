package com.kdc.howlongyouplay.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.GameLog;
import com.kdc.howlongyouplay.GameRecordsActivity;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//cmt tương tự với ListAdapter và LogAdapter
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private Context mContext;
    private List<Record> recordList;

    public RecordAdapter(Context mContext, List<Record> recordList) {
        this.mContext = mContext;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordAdapter.RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.record_item, parent, false);
        return new RecordAdapter.RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        final Record record = recordList.get(position);
        // key của một bản record tương ứng
        final String key = record.getRecord_id();
        //gán dữ liệu vào view
        holder.play_time.setText(mContext.getResources()
                .getString(R.string.format_time, record.getHour(), record.getMinute(), record.getSecond()));
        holder.play_status.setText(record.getStatus());

        // xử lý sự kiện khi nhấn vào một record tương ứng
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                final View view = layoutInflater.inflate(R.layout.dialog_record_detail, null);
                TextView play_time = view.findViewById(R.id.play_time);
                TextView status = view.findViewById(R.id.status);
                TextView note = view.findViewById(R.id.note);
                TextView date_created = view.findViewById(R.id.date_created);
                TextView date_modified = view.findViewById(R.id.date_modified);

                play_time.setText(mContext.getResources().getString(R.string.format_time,
                        record.getHour(), record.getMinute(), record.getSecond()));

                status.setText(record.getStatus());
                date_created.setText(record.getDate_created());
                note.setText(record.getNote());

                if(record.getDate_modified().equals("")) {
                    date_modified.setText("Chưa xác định");
                } else {
                    date_modified.setText(record.getDate_modified());
                }
                builder.setCancelable(true);
                builder.setTitle("Chi tiết");
                builder.setView(view);
                AlertDialog dialog = builder.show();
                dialog.show();

            }
        });


        // xử lý sự kiện khi sửa 1 record trong danh sách
        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                final View view = layoutInflater.inflate(R.layout.dialog_add_record, null);

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
                builder.setTitle("Thay đổi bản ghi");
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.show();

                // xử lý sự kiện khi nút xác nhận sửa record
                accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_hour_value, new_minute_value, new_second_value, new_note_detail,  date_modified;

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy" + ", " + "HH:mm:ss a");
                        date_modified = timeFormat.format(calendar.getTime());
                        new_note_detail = note.getText().toString();

                        if(hour.getText().toString().equals("")) {
                            new_hour_value = "00";
                        } else {
                            new_hour_value = hour.getText().toString();
                        }
                        if(minute.getText().toString().equals("")) {
                            new_minute_value = "00";
                        } else {
                            new_minute_value = minute.getText().toString();
                        }
                        if(second.getText().toString().equals("")) {
                            new_second_value = "00";
                        } else {
                            new_second_value = second.getText().toString();
                        }


                        int i = statusGroup.getCheckedRadioButtonId();
                        RadioButton checkedButton = view.findViewById(i);
                        String new_status_picked = checkedButton.getText().toString();


                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("hour", new_hour_value);
                        hashMap.put("minute", new_minute_value);
                        hashMap.put("second", new_second_value);
                        hashMap.put("status", new_status_picked);
                        hashMap.put("note", new_note_detail);
                        hashMap.put("date_modified", date_modified);

                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Logs").child(user_id);

                        databaseReference.child(record.getLog_id()).child("records").child(key)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(mContext, "Cập nhật thành công", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

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

                        databaseReference.child(record.getLog_id()).child("records").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    if(snapshot.getKey().equals(key)) {
                                        databaseReference.child(record.getLog_id()).child("records").child(key)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_SHORT).show();

                                                }
                                                else {
                                                    dialogInterface.dismiss();
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

                        // trừ đi 1 bản ghi vào tổng số bản ghi và cập nhật total_record
                        int total_record = recordList.size();
                        int new_records = total_record - 1;
                        String new_record = String.valueOf(new_records);
                        final HashMap<String, Object> hashMap_2 = new HashMap<>();
                        hashMap_2.put("total_record", new_record);
                        databaseReference.child(record.getLog_id()).updateChildren(hashMap_2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, "Cập nhật số bản ghi thành công", Toast.LENGTH_SHORT)
                                                .show();
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

        private TextView play_time;
        private TextView play_status;
        private ImageButton edit_btn;
        private ImageButton delete_btn;

        public RecordViewHolder(View itemView) {
            super(itemView);
            play_status = itemView.findViewById(R.id.status);
            play_time = itemView.findViewById(R.id.time);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }

    //nhận biết thay đổi khi chọn button trong RadioGroup
    private void onChangeStatusPicked(CompoundButton compoundButton, boolean isChecked) {
        RadioButton radio = (RadioButton) compoundButton;
        Log.d("Trạng thái: ", radio.getText().toString() + isChecked);
    }



}
