package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class Personal_record extends AppCompatActivity {

    ArrayList<RecordData> records;
    RecordAdapter adapter;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_record);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        listview = (ListView) findViewById(R.id.personal_record_listView);
        adapter = new RecordAdapter(this, records);
        listview.setAdapter(adapter);
    }

    // 상단 뒤로가기 클릭 시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}