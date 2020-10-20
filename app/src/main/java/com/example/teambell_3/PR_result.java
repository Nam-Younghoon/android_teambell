package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PR_result extends AppCompatActivity {

    TextView timer, speed, dist, startPoint, endPoint;
    Button record, cancel;
    String result, startLocation, endLocation;
    double sumDist, avgSpeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_p_r_result);

        timer = (TextView) findViewById(R.id.timer);
        speed = (TextView) findViewById(R.id.speed);
        dist = (TextView) findViewById(R.id.dist);
        startPoint = (TextView) findViewById(R.id.startPoint);
        endPoint = (TextView) findViewById(R.id.endPoint);

        record = (Button) findViewById(R.id.submit_record);
        cancel = (Button) findViewById(R.id.nosubmit_record);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        result = intent.getExtras().getString("timer");
        if (result==null){
            result = "00:00:00";
        }
        Log.d("result: ", result);
        timer.setText(result);

        startLocation = intent.getExtras().getString("startADD");
        if (startLocation == null){
            startLocation = "대한민국 서울특별시 금천구";
            startLocation = startLocation.substring(5);
        } else {startLocation = startLocation.substring(5);}
        Log.e("start: ", startLocation);
        startPoint.setText(startLocation);

        endLocation = intent.getExtras().getString("endADD");
        if (endLocation == null){
            endLocation = "대한민국 서울특별시 성북구";
            endLocation = endLocation.substring(5);
        } else {endLocation = endLocation.substring(5);}
        Log.d("end: ", endLocation);
        endPoint.setText(endLocation);

        sumDist = intent.getExtras().getDouble("SumDist");
        if (String.valueOf(sumDist) == null){
            sumDist = 0;
        }
        Log.d("distance: ", String.valueOf(sumDist));
        dist.setText(String.format("%.1f", sumDist));

        avgSpeed = intent.getExtras().getDouble("AvgSpeed");
        if (String.valueOf(avgSpeed) == null){
            avgSpeed = 0;
        }
        Log.d("avgspeed: ", String.valueOf(avgSpeed));
        speed.setText(String.format(String.valueOf(avgSpeed)));
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("http://192.168.11.44:3000/member/personal");
                finish();
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {
                JSONObject record= new JSONObject();
                record.put("distance", sumDist);
                record.put("time", result);
                record.put("avgSpeed", avgSpeed);
                record.put("dep", startLocation);
                record.put("arr", endLocation);

                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserName(PR_result.this);
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    os = conn.getOutputStream();
                    os.write(record.toString().getBytes());
                    Log.e("개인기록 : ", String.valueOf(record));
                    os.flush();
                    os.close();


                    final int status = conn.getResponseCode();
                    InputStream inputStream;
                    if (status != HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getErrorStream();

                        Log.e("에러", inputStream.toString());
                    } else {
                        inputStream = conn.getInputStream();
                    }
                    Log.e("응답코드", String.format("" + status));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status < 400) {
                                Toast.makeText(getApplication(), "기록이 저장되었습니다.", Toast.LENGTH_LONG).show();
                            } else if (status == 600) {
                                Toast.makeText(getApplication(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500) {
                                Toast.makeText(getApplication(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405) {
                                Toast.makeText(getApplication(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404) {
                                Toast.makeText(getApplication(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400) {
                                Toast.makeText(getApplication(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplication(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
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