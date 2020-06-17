package com.kdc.howlongyouplay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kdc.howlongyouplay.Model.GameRecord;
import com.kdc.howlongyouplay.R;

import java.util.HashMap;
import java.util.List;

public class StatisticAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "StatisticAdapter";
    private Context mContext;
    private List<String> mHeaderGroup;
    private HashMap<String, List<GameRecord>> mDataChild;

    public StatisticAdapter(Context context, List<String> headerGroup, HashMap<String, List<GameRecord>> data) {
        mContext = context;
        mHeaderGroup = headerGroup;
        mDataChild = data;
    }

    @Override
    public int getGroupCount() {
        return mHeaderGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDataChild.get(mHeaderGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mHeaderGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDataChild.get(mHeaderGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.header_statistic_group, parent, false);
        }

        TextView header = convertView.findViewById(R.id.group_header);
        header.setText(mContext.getResources()
                .getString(R.string.header_group,
                        mHeaderGroup.get(groupPosition),
                        String.valueOf(mDataChild.get(mHeaderGroup.get(groupPosition)).size())));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.child_statistic_group, parent, false);
        }

        TextView game_title = convertView.findViewById(R.id.game_title);
        //TextView play_time = convertView.findViewById(R.id.play_time);
        game_title.setText(((GameRecord) getChild(groupPosition, childPosition)).getGame_title());
        /*
        play_time.setText(mContext.getResources().getString(R.string.format_time,
                ((GameRecord) getChild(groupPosition, childPosition)).getHour(),
                ((GameRecord) getChild(groupPosition, childPosition)).getMinute(),
                ((GameRecord) getChild(groupPosition, childPosition)).getSecond()));

         */

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
