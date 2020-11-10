package com.example.teambell_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class Splash extends Activity {

    private FirebaseAuth mAuth = null;
    Context mContext;
    String idToken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);


        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        if (SaveSharedPreference.getUserToken(Splash.this).length() == 0) {
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
            intent.putExtra("Token", SaveSharedPreference.getUserToken(mContext).toString());
            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }


    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }



}
