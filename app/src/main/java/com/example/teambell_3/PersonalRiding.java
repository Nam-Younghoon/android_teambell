package com.example.teambell_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

public class PersonalRiding extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private Button mStartBtn, mStopBtn, mPauseBtn;
    private TextView mTimeTextView, nowSpeed, ridingDist, avgSpeed, findLocation;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    private TMapGpsManager tmapgps;
    private TMapView tMapView;
    private boolean m_bTrackingMode = true;
    private LocationListener locationListener;
    private Context mContext;
    private double sum_dist, bef_lat, bef_long, cur_lat, cur_long;
    private Location mLastlocation = null;
    private Location mFirstlocation = null;
    public String result, mspeed;
    public double avspeed, final_avspeed;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_riding);


        // 현재속도
        nowSpeed = (TextView) findViewById(R.id.now_speed);
        ridingDist = (TextView) findViewById(R.id.riding_dist);
        avgSpeed = (TextView) findViewById(R.id.avg_speed);
        mTimeTextView = (TextView) findViewById(R.id.stopwatch);
        findLocation = (TextView) findViewById(R.id.find_location);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStartBtn = (Button) findViewById(R.id.btn_start);
        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mContext = this;

        // Tmap 불러오기
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);

        // Tmap 키
        tMapView.setSKTMapApiKey("l7xx7d4b54bafdb64e8089e1e1876637c9c1");
        // Tmap 보이기
        linearLayoutTmap.addView(tMapView);
        // Tmap 설정
        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(tMapView.LANGUAGE_KOREAN);

        // LocationManager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // GPS 비활성화시, 켜기
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            findLocation.setText("※ 위치 꺼짐, 사용 불가. 위치를 켜주세요.");
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.gps_not_found_title)  // GPS not found
                    .setMessage(R.string.gps_not_found_message) // Want to enable?
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            findLocation.setText("위치 잡는중... 기다려주세요..");
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
        // TMap용 GPS 매니저
        tmapgps = new TMapGpsManager(PersonalRiding.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.GPS_PROVIDER);
        // 권한 설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        tmapgps.OpenGps();

        // 속도용 LocationListener
        locationListener = new SpeedActionListener();
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);


        // 라이딩 시작버튼
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mStopBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.VISIBLE);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });

        // 라이딩 중지버튼
        mStopBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            v.setVisibility(View.GONE);
                                            mStartBtn.setVisibility(View.VISIBLE);
                                            mPauseBtn.setVisibility(View.GONE);
                                            locationManager.removeUpdates(locationListener);
                                            timeThread.interrupt();
                                            if (mLastlocation != null){
                                                double deltaTime = (mLastlocation.getTime() - mFirstlocation.getTime()) / 1000.0;
                                                double aspeed = mLastlocation.distanceTo(mFirstlocation) / deltaTime;
                                                final_avspeed = Double.parseDouble(String.format("%.1f", aspeed));
                                            }
                                            Intent intent = new Intent(getApplicationContext(), PR_result.class);
                                            intent.putExtra("timer", result);
                                            intent.putExtra("AvgSpeed", final_avspeed);
                                            intent.putExtra("SumDist", sum_dist);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

        // 라이딩 일시정지 버튼
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    mPauseBtn.setText("일시정지");
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {
                    mPauseBtn.setText("시작");
                    locationManager.removeUpdates(locationListener);
                }
            }
        });


    }

    // 스탑워치 핸들러
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
            result = String.format("%02d:%02d:%02d", hour, min, sec);
            mTimeTextView.setText(result);
        }
    };

    // 티맵 위치변화 감지
    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode) {
            // 첫 위치에 대한 Log
            TMapPoint tp = new TMapPoint(location.getLongitude(), location.getLatitude());
            Log.d("debug", tp.toString());
            mFirstlocation = location;
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            findLocation.setText("※ 위치 최신화 완료.. 시작 가능합니다.");
            bef_lat = location.getLatitude();
            bef_long = location.getLongitude();
        }

    }

    // 현재 속도용 위치 변화 감지
    public class SpeedActionListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double deltaTime = 0;
            if (location != null) {
                if(location.hasSpeed()){
                    // 현 위치 저장하기.
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // 바뀌는 위치에 대한 Log
                    TMapPoint tp = new TMapPoint(location.getLongitude(), location.getLatitude());
                    Log.d("debug", tp.toString());

                    // 현재 속도
                    double mySpeed = location.getSpeed() * 3.6;
                    mspeed = String.format("%.0f", mySpeed);
                    nowSpeed.setText(mspeed+"km/h");

                    // 누적 이동 거리
                    cur_lat = latitude;
                    cur_long = longitude;
                    CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                    double dist = calDistance.getDistance();
                    dist = (int)(dist*100) / 100.0;
                    sum_dist += dist;
                    ridingDist.setText(String.format("%.1f", sum_dist)+" m");

                    if (mLastlocation != null){
                        deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
                        double aspeed = mLastlocation.distanceTo(location) / deltaTime;
                        avspeed = Double.parseDouble(String.format("%.1f", aspeed));
                        avgSpeed.setText(avspeed+"m/s");
                    }
                    bef_lat = cur_lat;
                    bef_long = cur_long;
                    mLastlocation = location;
                }


            }
        }
    }



    // 스탑워치용 쓰레드
    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    handler.sendMessage(msg);
                    msg.arg1 = i++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeTextView.setText("");
                                mTimeTextView.setText("00:00:00");
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

    // 상단 뒤로가기 클릭 시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림");
                builder.setMessage("라이딩을 종료하시겠습니까?");
                builder.setNegativeButton("취소", null);
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 물리키 클릭 시
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("라이딩을 종료하시겠습니까?");
        builder.setNegativeButton("취소", null);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }
}