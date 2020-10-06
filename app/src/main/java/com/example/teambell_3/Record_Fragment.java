package com.example.teambell_3;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;


public class Record_Fragment extends Fragment {

    ArrayList<RecordData> records;
    RecordAdapter adapter;
    ListView listview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record_fragment, container, false);
        setHasOptionsMenu(true);

        records = new ArrayList<>();
        records.add(new RecordData("2020.00.00", "30km", "00:00:01", "4.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        records.add(new RecordData("2020.00.00", "40km", "00:00:11", "5.8km/h"));
        listview = (ListView) v.findViewById(R.id.record_listView);
        adapter = new RecordAdapter(getContext(), records);
        listview.setAdapter(adapter);



        return v;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if(activity != null){
            ((MainActivity) activity).setActionBarTitle(R.string.title_record);
        }
    }

}