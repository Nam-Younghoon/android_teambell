package com.example.teambell_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

public class Splash extends Activity {

    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

        if (SaveSharedPreference.getUserName(Splash.this).length() == 0) {
            // call Login Activity
            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 2000); // 1초 후에 hd handler 실행  3000ms = 3초
        }
        else {
            Handler hd2 = new Handler();
            hd2.postDelayed(new splashhandler2(), 2000);
        }
    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), Login.class)); //로딩이 끝난 후, ChoiceFunction 이동
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    private class splashhandler2 implements Runnable{
        public void run(){
            Intent intent = new Intent(Splash.this, MainActivity.class);
            intent.putExtra("Token", SaveSharedPreference.getUserName(mContext).toString());
            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }



}
