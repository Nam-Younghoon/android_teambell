package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PR_result extends AppCompatActivity {

    TextView timer, speed, dist;
    Button record, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_r_result);

        timer = (TextView) findViewById(R.id.timer);
        speed = (TextView) findViewById(R.id.speed);
        dist = (TextView) findViewById(R.id.dist);

        record = (Button) findViewById(R.id.submit_record);
        cancel = (Button) findViewById(R.id.nosubmit_record);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String result = intent.getExtras().getString("timer");
        timer.setText(result);

        double sumDist = intent.getExtras().getDouble("SumDist");
        dist.setText(String.format("%.1f km", sumDist));

        double avspeed = intent.getExtras().getDouble("AvgSpeed");
        speed.setText(String.format(""+avspeed+" km/h"));





        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });



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

    // 뒤로가기 물리키 클릭 시
    @Override
    public void onBackPressed() {
        finish();
    }
}