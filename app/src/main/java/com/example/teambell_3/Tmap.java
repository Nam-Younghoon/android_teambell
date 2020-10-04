package com.example.teambell_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapView;

public class Tmap extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private TMapGpsManager tmapgps ;
    private boolean m_bTrackingMode = true;
    private  TMapView tMapView;


    /*
     *
     *
     * 해당 클래스는
     * 티맵 테스트용 클래스임.
     *
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey( "" );
        linearLayoutTmap.addView( tMapView );

        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(tMapView.LANGUAGE_KOREAN);


        tmapgps = new TMapGpsManager(Tmap.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        tmapgps.OpenGps();
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);



    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode){
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}