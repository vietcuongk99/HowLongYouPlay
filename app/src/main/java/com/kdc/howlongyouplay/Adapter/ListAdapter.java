package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdc.howlongyouplay.Activity.GameActivity;
import com.kdc.howlongyouplay.GameDetail;
import com.kdc.howlongyouplay.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// class có nhiệm vụ sắp xếp dữ liệu theo layout có sẵn
// chưa hiểu lắm
//giải thích tương tự với LogAdapter

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements Filterable{

    private Context mContext;
    private List<GameDetail> gameDetailList;
    private List<GameDetail> searchResultFull;

    public ListAdapter(Context mContext, List<GameDetail> gameDetailList) {
        this.mContext = mContext;
        this.gameDetailList = gameDetailList;
        searchResultFull = new ArrayList<>(gameDetailList);
    }

    public ListAdapter(List<GameDetail> gameDetailList) {
        this.gameDetailList = gameDetailList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.game_item, parent, false);

        return new ListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        final GameDetail gameDetail = gameDetailList.get(position);
        holder.header_title_card.setText(gameDetail.getGame_title());
        Picasso.get().load(gameDetail.getImg_url()).into(holder.background);


        // xử lý sự kiện khi click vào một game trong danh sách tìm kiếm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dùng intent để pass dữ liệu sang GameInfoActivity
                Intent intent = new Intent(mContext, GameActivity.class);
                intent.putExtra("game_title", gameDetail.getGame_title());
                intent.putExtra("year", gameDetail.getYear());
                intent.putExtra("genre", gameDetail.getGenre());
                intent.putExtra("developer", gameDetail.getDeveloper());
                intent.putExtra("pulisher", gameDetail.getPulisher());
                intent.putExtra("play_on", gameDetail.getPlayable_device());
                intent.putExtra("game_id", gameDetail.getId_game());
                intent.putExtra("img_url", gameDetail.getImg_url());
                intent.putExtra("icon_url", gameDetail.getIcon_url());

                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return gameDetailList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<GameDetail> searchList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                searchList.addAll(searchResultFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GameDetail gameDetail : searchResultFull) {
                    if (gameDetail.getGame_title().toLowerCase().contains(filterPattern)) {
                        searchList.add(gameDetail);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = searchList;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            gameDetailList.clear();
            gameDetailList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView header_title_card;

        public MyViewHolder(View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.item_bg);
            header_title_card = itemView.findViewById(R.id.header_title_card);
        }
    }



}
