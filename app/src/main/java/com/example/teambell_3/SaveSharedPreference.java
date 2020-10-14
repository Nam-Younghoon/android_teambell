package com.example.teambell_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserName(Context ctx, String userID, String userPW) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("ID", userID);
        editor.putString("PW", userPW);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString("ID", "");
    }

    // 로그아웃
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}