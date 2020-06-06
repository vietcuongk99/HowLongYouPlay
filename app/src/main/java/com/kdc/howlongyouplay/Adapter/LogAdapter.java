package com.kdc.howlongyouplay.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kdc.howlongyouplay.GameLog;
import com.kdc.howlongyouplay.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private Context mContext;
    private List<GameLog> gameLogList;
    private Toast toast;

    public LogAdapter(Context mContext, List<GameLog> gameLogList) {
        this.mContext = mContext;
        this.gameLogList = gameLogList;
    }

    @NonNull
    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = new View(mContext);

        Resources resource = view.getResources();
        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.gamelog_item_2, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.gamelog_item, parent, false);
        }
        return new LogAdapter.LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

        final GameLog gameLog = gameLogList.get(position);
        final String key = gameLog.getId_game();

        Picasso.get().load(gameLog.getImage_url()).into(holder.game_image);
        holder.game_title.setText(gameLog.getGame_title());
        holder.playthrough.setText(mContext.getResources().getString(R.string.playthrough, gameLog.getRecords().size()));

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Logs").child(user_id);

                // tạo builder và các phần tử liên quan
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Xóa game").setMessage("Xóa game này đồng nghĩa với tất cả các record liên quan cũng sẽ bị xóa, bạn chắc chứ ?");

                //xử lý sự kiện khi đồng ý xóa record
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        toast = Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_SHORT);
                        toast.show();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    if(snapshot.getKey().equals(key)) {
                                        databaseReference.child(key)
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
        return gameLogList.size();
    }


    public static class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView game_title;
        private TextView playthrough;
        private ImageView game_image;
        private FitButton delete_btn;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            game_title = itemView.findViewById(R.id.game_title);
            playthrough = itemView.findViewById(R.id.playthrough);
            game_image = itemView.findViewById(R.id.game_image);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }


}
