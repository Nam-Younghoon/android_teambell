package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PR_result extends AppCompatActivity {

    TextView timer, speed, dist, startPoint, endPoint;
    Button record, cancel;
    String result, startLocation, endLocation;
    double sumDist, avgSpeed;
    ArrayList<LatLng> mLocationList = null;
    String fileName;

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
            startLocation = "     위치를 잡지 못했습니다.";
            startLocation = startLocation.substring(5);
        } else {startLocation = startLocation.substring(5);}
        Log.e("start: ", startLocation);
        startPoint.setText(startLocation);

        endLocation = intent.getExtras().getString("endADD");
        if (endLocation == null){
            endLocation = "     위치를 잡지 못했습니다.";
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

        mLocationList = intent.getParcelableArrayListExtra("mLocationRecord");
        Log.e("위치 기록 리스트 ", mLocationList.toString());
        Log.e("위치 기록 크기", String.format(""+mLocationList.size()));

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd_HHmss");
                String time = mFormat.format(date);
                Log.e("directory", getExternalFilesDir(null).toString());
                fileName = String.format("/storage/emulated/0/Android/data/com.example.teambell_3/files/%s.gpx", time);
                try{
                    File file = new File(fileName);
                    if(!file.exists()){
                        try{
                            file.createNewFile();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    FileWriter fw = new FileWriter(file, true);
                    StringBuilder sb = new StringBuilder();
                    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                            "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" " +
                            "xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"mapstogpx.com\" " +
                            "version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                            "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                            "http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3" +
                            " http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 " +
                            "http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n");
                    sb.append("<trk>\n<trkseg>\n");
                    for(int i=0; i<mLocationList.size(); i++){
                        sb.append(String.format("<trkpt lat=\"%s\" lon=\"%s\">\n" +
                                "  </trkpt>", mLocationList.get(i).latitude, mLocationList.get(i).longitude));
                    }
                    sb.append("</trkseg></trk></gpx>\n");
                    fw.write(sb.toString());
                    fw.flush();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                new JSONTask().execute("http://106.243.128.187:3000/member/personal");
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
                record.put("gpx", fileName);

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
                    String token = SaveSharedPreference.getUserToken(PR_result.this);
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