package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdc.howlongyouplay.EditLogActivity;
import com.kdc.howlongyouplay.GameInfoActivity;
import com.kdc.howlongyouplay.GameLog;
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

        return new LogAdapter.MyLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.MyLogViewHolder holder, int position) {
        final GameLog gameLog = LogList.get(position);
        holder.time.setText(gameLog.getPlayed_time());
        holder.game_title.setText(gameLog.getGame_title());
        final String key = gameLog.getId_log();

        // xử lý sự kiện khi click vào một log trong danh sách
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
