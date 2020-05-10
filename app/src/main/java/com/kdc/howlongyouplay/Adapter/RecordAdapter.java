package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdc.howlongyouplay.R;
import com.kdc.howlongyouplay.Record;

import java.util.List;

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

        holder.play_time.setText(record.getHour());
        holder.play_status.setText(record.getStatus());
//        holder.date_modified.setText(record.getDate_modified());
//        holder.date_created.setText(record.getDate_created());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, record.getRecord_id(), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, record.getDate_created(), Toast.LENGTH_SHORT).show();
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
//        private TextView date_created;
//        private TextView date_modified;

        public RecordViewHolder(View itemView) {
            super(itemView);
            play_status = itemView.findViewById(R.id.status);
            play_time = itemView.findViewById(R.id.time);
//            date_created = itemView.findViewById(R.id.date_created);
//            date_modified = itemView.findViewById(R.id.date_modified);
        }
    }

}
