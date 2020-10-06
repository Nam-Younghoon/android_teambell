package com.example.teambell_3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<RecordData> sample;

    public RecordAdapter(Context context, ArrayList<RecordData> data){
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
        View view = mLayoutInflater.inflate(R.layout.record_list, null);

        TextView RDate = (TextView) view.findViewById(R.id.riding_date);
        TextView RTime = (TextView)view.findViewById(R.id.riding_time);
        TextView RSpeed = (TextView)view.findViewById(R.id.riding_speed);
        TextView RDistance = (TextView)view.findViewById(R.id.riding_distance);


        RDate.setText(sample.get(position).getRDate());
        RTime.setText(sample.get(position).getRTime());
        RSpeed.setText(sample.get(position).getRSpeed());
        RDistance.setText(sample.get(position).getRDistance());

        return view;
    }
}
