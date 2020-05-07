package com.kdc.howlongyouplay.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.gson.internal.$Gson$Preconditions;
import com.kdc.howlongyouplay.EditLogActivity;
import com.kdc.howlongyouplay.GameLog;
import com.kdc.howlongyouplay.MainActivity;
import com.kdc.howlongyouplay.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;

// class có nhiệm vụ sắp xếp từng bản ghi trong danh sách theo layout có sẵn
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
        final View view = LayoutInflater.from(mContext).inflate(R.layout.gamelog_item, parent, false);
//        view.findViewById(R.id.group_action).setVisibility(View.VISIBLE);

        return new LogAdapter.MyLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.MyLogViewHolder holder, int position) {
        final GameLog gameLog = LogList.get(position);
        //gán nội dung cho từng phần tử trong holder
        holder.header_title_card.setText(gameLog.getGame_title());
        Picasso.get().load(gameLog.getImg_url()).into(holder.background);

        // key của bản log cho game tương ứng
        final String key = gameLog.getId_log();


        // hiển thị dialog chứa nội dung khi click vào một game trong danh sách cá nhân
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());

                //gán custom layout và header cho dialog
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View item_dialog_header = layoutInflater.inflate(R.layout.item_dialog_header, null);
                View item_dialog_view = layoutInflater.inflate(R.layout.item_dialog_view, null);
                dialog.setCustomTitle(item_dialog_header);
                dialog.setView(item_dialog_view);

                // khai báo và gán nội dung cho từng phần tử trong dialog
                TextView title = (TextView) item_dialog_header.findViewById(R.id.title_text);
                ImageButton edit_btn = item_dialog_view.findViewById(R.id.edit_btn);
                ImageButton delete_btn =  item_dialog_view.findViewById(R.id.delete_btn);
                TextView played_time = (TextView) item_dialog_view.findViewById(R.id.played_time);
                TextView status = (TextView) item_dialog_view.findViewById(R.id.play_status);
                TextView device = (TextView) item_dialog_view.findViewById(R.id.device);


                title.setText(gameLog.getGame_title());
                status.setText(mContext.getResources().getString(R.string.status, mContext.getResources().getString(R.string.zero)));
                device.setText(mContext.getResources().getString(R.string.device, mContext.getResources().getString(R.string.zero)));
                if (gameLog.getPlayed_time().equals("")) {
                    played_time.setText(mContext.getResources().getString(R.string.played_time,
                            mContext.getResources().getString(R.string.zero)));
                } else {
                    played_time.setText(mContext.getResources().getString(R.string.played_time, gameLog.getPlayed_time()));
                }

                final AlertDialog builder = dialog.show();
                builder.setCanceledOnTouchOutside(true);


                // xử lý sự kiện khi nhất nút sửa
                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // dùng intent để pass dữ liệu cho từng bản ghi trong danh sách
                        Intent intent = new Intent(mContext, EditLogActivity.class);
                        intent.putExtra("game_title", gameLog.getGame_title());
                        intent.putExtra("play_time", gameLog.getPlayed_time());
                        intent.putExtra("id", gameLog.getId_log());
                        intent.putExtra("img_url", gameLog.getImg_url());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(intent);

                    }
                });


                // xử lý sự kiện khi nhất nút xóa
                delete_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // đóng giao diện dialog
                        builder.dismiss();
                        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Logs").child(user_id);

                        // tạo giao diện xác nhận thao tác xóa
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                        alertDialog.setTitle("Xóa log").setMessage("Bạn muốn xóa bản log này ?");

                        //tạo nút đồng ý xóa và xử lý sự kiện
                        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int which) {

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                            if(snapshot.getKey().equals(key)) {
                                                databaseReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                            }
                        });


                        // tạo nút hủy và xử lý sự kiện
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
        });
    }






    @Override
    public int getItemCount() {
        return LogList.size();
    }

    public static class MyLogViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView header_title_card;

        public MyLogViewHolder(View itemView) {
            super(itemView);

            header_title_card = itemView.findViewById(R.id.header_title_card);
            background = itemView.findViewById(R.id.item_bg);

        }
    }

}
