package com.example.teambell_3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StatAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<StatData> sample;

    public StatAdapter(Context context, ArrayList<StatData> data){
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public Object getItem(int position) {
        return sample.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.statistic_day_list, null);

        TextView RDate = (TextView) view.findViewById(R.id.stat_day_date);
        TextView RTime = (TextView)view.findViewById(R.id.stat_day_time);
        TextView RSpeed = (TextView)view.findViewById(R.id.stat_day_avgspeed);
        TextView RDistance = (TextView)view.findViewById(R.id.stat_day_distance);
        TextView RCount = (TextView)view.findViewById(R.id.stat_day_count);

        RDate.setText(sample.get(position).getRDate());
        RDistance.setText("거리: "+sample.get(position).getRDistance()+"km");
        RTime.setText("시간: "+sample.get(position).getRTime());
        RSpeed.setText("평균 속도: "+sample.get(position).getRSpeed()+"km/h");
        RCount.setText("횟수: "+sample.get(position).getRCount()+"회");
        return view;
    }

}