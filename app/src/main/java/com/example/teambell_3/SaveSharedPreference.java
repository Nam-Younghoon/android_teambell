package com.example.teambell_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.constraintlayout.widget.ConstraintLayout;

public class SaveSharedPreference {

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserToken(Context ctx, String userName, String userEmail, String userToken) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("Name", userName);
        editor.putString("Email", userEmail);
        editor.putString("Token", userToken);
        editor.commit();
    }



    // 저장된 정보 가져오기
    public static String getUserToken(Context ctx) {
        return getSharedPreferences(ctx).getString("Token", "");
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString("Name", "");
    }

    public static String getUserEmail(Context ctx) {
        return getSharedPreferences(ctx).getString("Email", "");
    }

    // 로그아웃
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}