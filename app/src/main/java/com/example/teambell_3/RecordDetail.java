package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RecordDetail extends AppCompatActivity implements OnMapReadyCallback {

    TextView dist, avgSpeed, time;
    private GoogleMap mMap;
    private List<Location> gpxList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        dist = findViewById(R.id.record_dist);
        avgSpeed = findViewById(R.id.record_avgspeed);
        time = findViewById(R.id.record_time);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String myDist = intent.getStringExtra("dist");
        String mySpeed = intent.getStringExtra("avgSpeed");
        String myGpx = intent.getStringExtra("path");
        String myTime = intent.getStringExtra("time");
        File gpxFile = new File(myGpx);
        gpxList = decodeGPX(gpxFile);
        Log.e("", gpxList.toString());

        if(gpxList.size() > 0){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);//
            builder.setTitle("오류");
            builder.setMessage("이동 기록이 없어 종료합니다.");
            builder.setPositiveButton("확인", (dialog, which) -> {

                dialog.dismiss();
                finish();
            });
            builder.show();
        }


        double d = Double.parseDouble(myDist);
        dist.setText(String.format("%.1f km", d));
        avgSpeed.setText(mySpeed+" km/h");
        time.setText(myTime);



    }

    private List<Location> decodeGPX(File file){
        List<Location> list = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }

            fileInputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpxList.get(0).getLatitude(), gpxList.get(0).getLongitude()), 15));
        drawPath();
    }

    private void drawPath(){//polyline을 그려주는 메소드
        for(int i=1; i<gpxList.size()-1; i++){
            LatLng startPoly = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());
            LatLng endPoly = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
            PolylineOptions options = new PolylineOptions().add(startPoly).add(endPoly).width(15).color(Color.BLACK).geodesic(true);
            mMap.addPolyline(options);
        }
        int last = gpxList.size()-1;
        MarkerOptions marker = new MarkerOptions();
        MarkerOptions marker2 = new MarkerOptions();
        marker.position(new LatLng(gpxList.get(0).getLatitude(), gpxList.get(0).getLongitude()))
                .title("시작점").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker2.position(new LatLng(gpxList.get(last).getLatitude(), gpxList.get(last).getLongitude())).title("종료지점").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(marker).showInfoWindow();
        mMap.addMarker(marker2).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpxList.get(0).getLatitude(), gpxList.get(0).getLongitude()), 15));
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
