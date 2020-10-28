package com.example.teambell_3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<GroupData> sample;
    String info;

    public GroupAdapter(Context context, ArrayList<GroupData> data){
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
        return sample.get(position).getGIndex();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.group_list, null);

        TextView GTitle = (TextView)view.findViewById(R.id.group_title);
        TextView GNumber = (TextView)view.findViewById(R.id.group_number);
        TextView GLeader = (TextView)view.findViewById(R.id.group_leader);


        GTitle.setText(sample.get(position).getGTitle());
        GNumber.setText(String.format(sample.get(position).getGNumber()+""));
        GLeader.setText(sample.get(position).getGLeader());



        return view;
    }


}
