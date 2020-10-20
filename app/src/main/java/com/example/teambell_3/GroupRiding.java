package com.example.teambell_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class GroupRiding extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private Button mStartBtn, mStopBtn, mPauseBtn;
    private TextView mTimeTextView, nowSpeed, ridingDist, avgSpeed, findLocation;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    private LocationListener locationListener;
    private Context mContext;
    private double sum_dist, bef_lat = 0.0, bef_long = 0.0, cur_lat, cur_long;
    private Location mLastlocation = null;
    private Location mFirstlocation = null;
    public String result, mspeed;
    public double avspeed, final_avspeed;
    private LocationManager locationManager;

    Home_Fragment hf = new Home_Fragment();


    private GoogleMap mMap;
    private Marker currentMarker = null;
    private Marker startPointMarker = null;
    private Marker endPointMarker = null;
    private Marker othersMarker = null;
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;
    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    Location mCurrentLocatiion;
    LatLng currentPosition;
    LatLng mFirstPointPosition;
    LatLng mLastPointPosition;
    String startPointTitle;
    String endPointTitle;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)
    private LatLng startPoly;
    private LatLng endPoly;
    private List<Polyline> polylines;
    private Intent beforIntent;
    private String mJsonString;
    ArrayList<GroupLocationData> groups;
    private MqttClient mqttClient;
    private BluetoothSPP bt;
    String ReceivedMsg;
    String SendMsg;
    String gIdx;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.group_riding);
        beforIntent = getIntent();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLayout = findViewById(R.id.layout_group_riding);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        groups = new ArrayList<>();

        ///////////////////////////////////////////////////////////////////////////////////////
        /////////MQTT 통신연결/////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        bt = MyApplication.bt;
        Log.e("블루투스 ", bt.toString());

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        gIdx = beforIntent.getStringExtra("GroupIdx");
        try {
            mqttClient = new MqttClient("tcp://192.168.11.44:1883", "", new MemoryPersistence());
            mqttClient.connect();
            mqttClient.subscribe(String.format("%s", gIdx));
        } catch (MqttException e) {
            e.printStackTrace();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Log.e("버튼누름", message);
                Toast.makeText(GroupRiding.this, message, Toast.LENGTH_LONG).show();
                ReceivedMsg = message;
                try{
                    mqttClient.publish(String.format("%s", gIdx), new MqttMessage(ReceivedMsg.getBytes()));
                } catch(MqttException e){
                    e.printStackTrace();
                }

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (topic.equals(String.format("%s", gIdx))){
                                    SendMsg = new String(message.getPayload());
                                    Log.e("보낼 메세지", SendMsg);
                                    Toast.makeText(getApplication(), SendMsg, Toast.LENGTH_LONG).show();
                                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM,100);
                                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300);
                                    byte[] send = SendMsg.getBytes();
                                    bt.send(send, true);
                                }
                            }
                        });
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();

                try {
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                    ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

                    if(uuids != null) {
                        for (ParcelUuid uuid : uuids) {
                            Log.d(TAG, "UUID: " + uuid.getUuid().toString());
                        }
                    }else{
                        Log.d(TAG, "Uuids not found, be sure to enable Bluetooth!");
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });


        /////////////////////////////////////////////////////////////////////////////////////




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

        // LocationManager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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


        // 속도용 LocationListener
        locationListener = new SpeedActionListener();


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
                    try{
                        double deltaTime = (mLastlocation.getTime() - mFirstlocation.getTime()) / 1000.0;
                        double aspeed = (sum_dist / deltaTime) * 3.6;
                        final_avspeed = Double.parseDouble(String.format("%.1f", aspeed));

                        // 종료 지점 마커 생성
                        mLastPointPosition = new LatLng(mLastlocation.getLatitude(), mLastlocation.getLongitude());
                        endPointTitle = getCurrentAddress(mLastPointPosition);
                        String endPointSnippet = "위도:" + String.valueOf(mLastlocation.getLatitude()) + "경도:" + String.valueOf(mLastlocation.getLongitude());
                        setEndLocation(mLastlocation, endPointTitle, endPointSnippet);
                    } catch (NullPointerException e){
                        Log.e("널 포인트 예외 발생", e.toString());
                    }
                }
                Intent intent = new Intent(getApplicationContext(), PR_result.class);
                intent.putExtra("timer", result);
                intent.putExtra("AvgSpeed", final_avspeed);
                intent.putExtra("SumDist", sum_dist/1000);
                intent.putExtra("startADD", startPointTitle);
                intent.putExtra("endADD", endPointTitle);
                String gIdx = beforIntent.getStringExtra("GroupIdx");
                Log.e("그룹 인덱스 가져오기", gIdx);
                new STOPgroupTask().execute(String.format("http://192.168.11.44:3000/member/status/%s", gIdx));
                startActivity(intent);
                try{
                    mqttClient.disconnect();
                    bt.disconnect();
                } catch(MqttException e){
                    e.printStackTrace();
                }

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
            int sec = (msg.arg1) % 60;
            int min = (msg.arg1 / 60) % 60;
            int hour = (msg.arg1) / 3600;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
            result = String.format("%02d:%02d:%02d", hour, min, sec);
            mTimeTextView.setText(result);
        }
    };


    // 현재 속도용 위치 변화 감지
    public class SpeedActionListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double deltaTime = 0;
            double maxSpeed = 0;
            if (location != null) {
                if(location.hasSpeed()){
                    if(location.getAccuracy() < 10){
                        // 현 위치 저장하기.
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        endPoly = new LatLng(latitude, longitude);
                        drawPath();
                        startPoly = new LatLng(latitude, longitude);

                        // 현재 속도
                        double mySpeed = location.getSpeed() * 3.6;
                        // 최고 속도
                        if (maxSpeed < mySpeed){
                            maxSpeed = mySpeed;
                        }
                        mspeed = String.format("%.1f", mySpeed);
                        nowSpeed.setText(mspeed+"km/h");

                        // 누적 이동 거리
                        if (bef_lat != 0.0 && bef_long != 0.0){
                            cur_lat = latitude;
                            cur_long = longitude;
                            CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                            double dist = calDistance.getDistance();
                            dist = (int)(dist*100) / 100.0;
                            sum_dist += dist;
                            Log.e("누적거리 : ", String.valueOf(sum_dist));
                            ridingDist.setText(String.format("%.2f", sum_dist / 1000)+" km");
                        }


                        // 평균 속도
                        if (mFirstlocation != null){
                            deltaTime = (location.getTime() - mFirstlocation.getTime()) / 1000.0;
                            double aspeed = (sum_dist * 3.6) / deltaTime;
                            if(deltaTime == 0){
                                aspeed = 0;
                            }
                            avspeed = Double.parseDouble(String.format("%.1f", aspeed));
                            avgSpeed.setText(avspeed+" km/h");
                        }
                        bef_lat = cur_lat;
                        bef_long = cur_long;
                        mLastlocation = location;
                    }
                }
            }
        }
    }

    private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startPoly).add(endPoly).width(15).color(Color.BLACK).geodesic(true);
//        polylines.add(mMap.addPolyline(options));
        mMap.addPolyline(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoly, 15));
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
                        Thread.sleep(1000);
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
                        String gIdx = beforIntent.getStringExtra("GroupIdx");
                        Log.e("그룹 인덱스 가져오기", gIdx);
                        new STOPgroupTask().execute(String.format("http://192.168.11.44:3000/member/status/%s", gIdx));
                        try{
                            mqttClient.disconnect();
                            bt.disconnect();
                        } catch(MqttException e){
                            e.printStackTrace();
                        }
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
                String gIdx = beforIntent.getStringExtra("GroupIdx");
                Log.e("그룹 인덱스 가져오기", gIdx);
                new STOPgroupTask().execute(String.format("http://192.168.11.44:3000/member/status/%s", gIdx));
                try{
                    mqttClient.disconnect();
                    bt.disconnect();
                } catch(MqttException e){
                    e.printStackTrace();
                }
                finish();
            }
        });
        builder.show();
    }




    //////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( GroupRiding.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                if(mFirstlocation == null){
                    if(location.getAccuracy() < 10){
                        mFirstlocation = location;
                        startPoly = new LatLng(mFirstlocation.getLatitude(), mFirstlocation.getLongitude());
                        Log.e("첫 위치 : ", mFirstlocation.toString());
                        bef_lat = location.getLatitude();
                        bef_long = location.getLongitude();
                        findLocation.setText("첫 위치 잡음. 시작하세요.");

                        // 출발점 마커 생성
                        mFirstPointPosition = new LatLng(mFirstlocation.getLatitude(), mFirstlocation.getLongitude());
                        startPointTitle = getCurrentAddress(mFirstPointPosition);
                        String startPointSnippet = "위도:" + String.valueOf(mFirstlocation.getLatitude()) +
                                "경도:" + String.valueOf(mFirstlocation.getLongitude());
                        setFirstLocation(mFirstlocation, startPointTitle, startPointSnippet);
                    }
                }

                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                // 다른 사용자의 위치 표시
//                LatLng someone = new LatLng (37.466573, 126.889245);
//                LatLng someone2 = new LatLng (37.467161, 126.889294);
//                setOthersLocation(someone, "사용자1");
//                setOthersLocation(someone2, "사용자2");
                if(groups != null){
                    for(int i=0; i<groups.size(); i++){
                        if(othersMarker != null){
                            othersMarker.remove();
                        }
                        LatLng others = new LatLng(Double.parseDouble(groups.get(i).getLatitude()), Double.parseDouble(groups.get(i).getLongitude()));
                        setOthersLocation(others, groups.get(i).getName());
                        Log.e("그룹 사람 데려옴", others.toString());
                    }
                }



                //이동 위치 변경
                setCurrentLocation(location);

                mCurrentLocatiion = location;


                String gIdx = beforIntent.getStringExtra("GroupIdx");
                Log.e("그룹 인덱스 가져오기", gIdx);
                new LOCATIONTask().execute(String.format("http://192.168.11.44:3000/member/input/%s", gIdx));
                new GetLocationGroupTask().execute(String.format("http://192.168.11.44:3000/member/output/%s", gIdx));

            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }

        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location) {

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }

    public void setOthersLocation(LatLng latlng, String name){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(name);
        markerOptions.alpha(0.5f);
        othersMarker = mMap.addMarker(markerOptions);
    }

    public void setFirstLocation(Location location, String markerTitle, String markerSnippet){
        LatLng startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(startLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        startPointMarker = mMap.addMarker(markerOptions);
    }

    public void setEndLocation(Location location, String markerTitle, String markerSnippet){
        LatLng endLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(endLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        endPointMarker = mMap.addMarker(markerOptions);
    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupRiding.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    // 그룹 참여 종료
    public class STOPgroupTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserName(getApplication());
                    conn.setRequestProperty("token", token);
                    Log.e("STOPridingTask 실행됨", "ok");

                    JSONObject put = new JSONObject();
                    put.put("available", 1);

                    os = conn.getOutputStream();
                    os.write(put.toString().getBytes());
                    os.flush();
                    os.close();

                    final int status = conn.getResponseCode();
                    InputStream inputStream;
                    if( status != HttpURLConnection.HTTP_OK ) {
                        inputStream = conn.getErrorStream();

                        Log.e("에러", inputStream.toString());
                    } else {
                        inputStream = conn.getInputStream();
                    }
                    Log.e("응답코드", String.format(""+status));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status < 400){
                                Toast.makeText(getApplication(), "그룹 참여를 종료합니다.", Toast.LENGTH_LONG).show();
                            } else if (status == 600){
                                Toast.makeText(getApplication(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500){
                                Toast.makeText(getApplication(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405){
                                Toast.makeText(getApplication(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404){
                                Toast.makeText(getApplication(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400){
                                Toast.makeText(getApplication(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else{
                                Toast.makeText(getApplication(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    // 실시간 위치 DB에 입력
    public class LOCATIONTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserName(getApplication());
                    conn.setRequestProperty("token", token);
                    Log.e("LOCATIONTask 실행됨", "ok");

                    JSONObject put = new JSONObject();
                    put.put("lat", mCurrentLocatiion.getLatitude());
                    put.put("lon", mCurrentLocatiion.getLongitude());

                    os = conn.getOutputStream();
                    os.write(put.toString().getBytes());
                    os.flush();
                    os.close();

                    final int status = conn.getResponseCode();
                    InputStream inputStream;
                    if( status != HttpURLConnection.HTTP_OK ) {
                        inputStream = conn.getErrorStream();

                        Log.e("에러", inputStream.toString());
                    } else {
                        inputStream = conn.getInputStream();
                    }
                    Log.e("응답코드", String.format(""+status));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status < 400){

                            } else if (status == 600){
                                Toast.makeText(getApplication(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500){
                                Toast.makeText(getApplication(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405){
                                Toast.makeText(getApplication(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404){
                                Toast.makeText(getApplication(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400){
                                Toast.makeText(getApplication(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else{
                                Toast.makeText(getApplication(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    ////////////////////////////////////////////////////////////////
    // 회원 위치정보 받아오기
    ////////////////////////////////////////////////////////////////
    private class GetLocationGroupTask extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e("응답 ", "response - " + result);
            mJsonString = result;
            showResult();
        }



        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                String token = SaveSharedPreference.getUserName(getApplication());
                Log.e("토큰 ", token);
                httpURLConnection.setRequestProperty("token", token);
                httpURLConnection.setDoInput(true);


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e("응답", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                Log.e("버퍼리더", sb.toString().trim());

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e("오류", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="data";

        String TAG_NAME = "name";
        String TAG_GROUPIDX = "groupIdx";
        String TAG_LAT = "lat";
        String TAG_LON = "lon";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUPIDX);
                String name = item.getString(TAG_NAME);
                String latitude = item.getString(TAG_LAT);
                String longitude = item.getString(TAG_LON);

                groups.add(new GroupLocationData(groupIdx,name, latitude, longitude));

                Log.e("그룹 회원 위치 갱신", "");

            }



        } catch (JSONException e) {

            Log.e("JSONException 발생", "showResult : ", e);
        }

    }


}