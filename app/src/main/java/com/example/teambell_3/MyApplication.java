package com.example.teambell_3;

import android.app.Application;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class MyApplication extends Application {

    public static BluetoothSPP bt;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
