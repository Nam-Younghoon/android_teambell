package com.example.teambell_3;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.eclipse.paho.client.mqttv3.MqttClient;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class Home_Fragment extends Fragment {

    private Button personal_button;
    private Button group_button;
    private BluetoothSPP bt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        bt = new BluetoothSPP(getContext());



        personal_button = v.findViewById(R.id.button_single);
        personal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalRiding.class);
                startActivityForResult(intent, 1001);
            }
        });

        group_button = v.findViewById(R.id.button_group);
        group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
                    public void onDeviceConnected(String name, String address) {
                        Toast.makeText(getActivity()
                                , "Connected to " + name + "\n" + address
                                , Toast.LENGTH_SHORT).show();
                        Log.e("블루투스 ", bt.toString());
                    }

                    public void onDeviceDisconnected() { //연결해제
                        Toast.makeText(getActivity()
                                , "Connection lost", Toast.LENGTH_SHORT).show();
                    }

                    public void onDeviceConnectionFailed() { //연결실패
                        Toast.makeText(getActivity()
                                , "Unable to connect", Toast.LENGTH_SHORT).show();
                    }
                });
                if (!bt.isServiceAvailable()){
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_OTHER);
                } else if(!bt.isBluetoothEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                } else if(bt.getServiceState() != BluetoothState.STATE_CONNECTED){
                    Intent intent2 = new Intent(getActivity(), DeviceList.class);
                    startActivityForResult(intent2, BluetoothState.REQUEST_CONNECT_DEVICE);
                } else {
                    Intent intent = new Intent(getActivity(), MyGroupList.class);
                    MyApplication.bt = bt;
                    startActivity(intent);
                }

            }
        });

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
            ((MainActivity) activity).setActionBarTitle(R.string.title_home);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getActivity()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

}