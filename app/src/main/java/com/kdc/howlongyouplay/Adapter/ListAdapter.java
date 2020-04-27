package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdc.howlongyouplay.GameInfoActivity;
import com.kdc.howlongyouplay.GameLog;
import com.kdc.howlongyouplay.R;

import java.util.ArrayList;
import java.util.List;

// class có nhiệm vụ sắp xếp dữ liệu theo layout có sẵn
// chưa hiểu lắm

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements Filterable{

    private Context mContext;
    private List<GameLog> gameLogList;
    private List<GameLog> searchResultFull;

    public ListAdapter(Context mContext, List<GameLog> gameLogList) {
        this.mContext = mContext;
        this.gameLogList = gameLogList;
        searchResultFull = new ArrayList<>(gameLogList);
    }

    public ListAdapter(List<GameLog> gameLogList) {
        this.gameLogList = gameLogList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gamelog_item, parent, false);

        return new ListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        final GameLog gameLog = gameLogList.get(position);
        holder.year.setText(gameLogList.get(position).getYear());
        holder.game_title.setText(gameLogList.get(position).getGame_title());
        final String key = gameLogList.get(position).getId_log();

        // xử lý sự kiện khi click vào một log trong danh sách
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dùng intent để pass dữ liệu sang GameInfoActivity
                Intent intent = new Intent(mContext, GameInfoActivity.class);
                intent.putExtra("game_title", gameLog.getGame_title());
                intent.putExtra("year", gameLog.getYear());
                intent.putExtra("id", key);

                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return gameLogList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<GameLog> searchList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                searchList.addAll(searchResultFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GameLog gameLog : searchResultFull) {
                    if (gameLog.getGame_title().toLowerCase().contains(filterPattern)) {
                        searchList.add(gameLog);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = searchList;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            gameLogList.clear();
            gameLogList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView game_title;
        private TextView year;

        public MyViewHolder(View itemView) {
            super(itemView);

            game_title = itemView.findViewById(R.id.game_title);
            year = itemView.findViewById(R.id.time);
        }
    }



}
