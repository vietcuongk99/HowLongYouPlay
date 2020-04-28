package com.kdc.howlongyouplay.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.EditLogActivity;
import com.kdc.howlongyouplay.GameInfoActivity;
import com.kdc.howlongyouplay.GameLog;
import com.kdc.howlongyouplay.MainActivity;
import com.kdc.howlongyouplay.R;

import java.util.ArrayList;
import java.util.List;

// class có nhiệm vụ sắp xếp dữ liệu theo layout có sẵn
// chưa hiểu lắm

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyLogViewHolder>{

    private Context mContext;
    private List<GameLog> LogList;

    public LogAdapter(Context mContext, List<GameLog> LogList) {
        this.mContext = mContext;
        this.LogList = LogList;
    }

    public LogAdapter(List<GameLog> LogList) {
        this.LogList = LogList;
    }

    @NonNull
    @Override
    public MyLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gamelog_item, parent, false);

        view.findViewById(R.id.group_action).setVisibility(View.VISIBLE);

        return new LogAdapter.MyLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.MyLogViewHolder holder, int position) {
        final GameLog gameLog = LogList.get(position);
        holder.time.setText(gameLog.getPlayed_time());
        holder.game_title.setText(gameLog.getGame_title());
        final String key = gameLog.getId_log();

        // xử lý sự kiện khi click vào nút sửa
        holder.itemView.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dùng intent để pass dữ liệu sang GameInfoActivity
                Intent intent = new Intent(mContext, EditLogActivity.class);
                intent.putExtra("game_title", gameLog.getGame_title());
                intent.putExtra("play_time", gameLog.getPlayed_time());
                intent.putExtra("id", key);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);

            }
        });

        // xử lý sự kiện khi nhất nút xóa
        holder.itemView.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("Xóa log").setMessage("Bạn muốn xóa bản log này ?");

                //tạo nút xác nhận xóa và xử lý sự kiện
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Logs").child(user_id);

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    if(snapshot.getKey().equals(key)) {
                                        databaseReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
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
                // tạo nút hủy và xử lý sự kiện
                alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.setCancelable(true);
                    }
                });

                alertDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return LogList.size();
    }

    public static class MyLogViewHolder extends RecyclerView.ViewHolder {

        private TextView game_title;
        private TextView time;

        public MyLogViewHolder(View itemView) {
            super(itemView);

            game_title = itemView.findViewById(R.id.game_title);
            time = itemView.findViewById(R.id.time);
        }
    }



}
