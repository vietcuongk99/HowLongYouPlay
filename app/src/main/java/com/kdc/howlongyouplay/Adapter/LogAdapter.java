package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.google.firebase.database.DataSnapshot;
import com.kdc.howlongyouplay.Log;
import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Record;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private Context mContext;
    private List<Log> logList;

    public LogAdapter(Context mContext, List<Log> logList) {
        this.mContext = mContext;
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = new View(mContext);

        Resources resource = view.getResources();
        if (resource.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.log_item_2, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.log_item, parent, false);
        }
        return new LogAdapter.LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

        final Log log = logList.get(position);
        final String key = log.getId_game();
        
        Picasso.get().load(log.getImage_url()).into(holder.game_image);
        holder.game_title.setText(log.getGame_title());
        holder.playthrough.setText(mContext.getResources().getString(R.string.playthrough, log.getRecords().size()));

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return logList.size();
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
