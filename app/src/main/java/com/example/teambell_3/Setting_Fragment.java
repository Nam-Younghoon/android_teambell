package com.example.teambell_3;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Setting_Fragment extends Fragment {

    private Button serviceOK, personalOK, locationOK, helpOK, logoutOK, deleteOK, changePW;
    private EditText writePassword;
    private boolean success;
    private String mJsonString;
    private FirebaseAuth mAuth;
    private TextView a,b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.setting__fragment, container, false);

        a = (TextView) v.findViewById(R.id.username);
        b = (TextView) v.findViewById(R.id.useremail);

        a.setText(SaveSharedPreference.getUserName(getContext()));
        b.setText(SaveSharedPreference.getUserEmail(getContext()));

        mAuth = FirebaseAuth.getInstance();
        serviceOK = v.findViewById(R.id.serviceOK);
        serviceOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ServiceOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        personalOK = v.findViewById(R.id.personalOK);
        personalOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        locationOK = v.findViewById(R.id.locationOK);
        locationOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        helpOK = v.findViewById(R.id.helpOK);
        helpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HelpOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        changePW = v.findViewById(R.id.change_pw);
        changePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePW.class);
                startActivity(intent);
             }
        });

        logoutOK = v.findViewById(R.id.logoutOK);
        logoutOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("로그아웃");
                ad.setMessage("로그아웃 하시겠습니까?");
                ad.setPositiveButton("네", (dialog, which) -> {
                    SaveSharedPreference.clearUserName(getContext());
                    Intent logoutIntent = new Intent(getContext(), Login.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    dialog.dismiss();
                });
                ad.setNegativeButton("아니요", null);
                ad.show();
            }
        });

        deleteOK = v.findViewById(R.id.deleteOK);
        deleteOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("회원탈퇴");
                ad.setMessage("회원탈퇴 시 저장된 기록들이 삭제됩니다.\n그래도 회원탈퇴 하시겠습니까?");
                ad.setPositiveButton("네", ((dialog, which) -> {
                    try {
                        new DELETETask().execute("http://106.243.128.187:3000/user/withdrawal").get();
                        SaveSharedPreference.clearUserName(getContext());
                        if(mAuth.getCurrentUser().equals(true)){
                            revokeAccess();
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                        else if (UserManagement.getInstance().equals(true)){
                            UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                @Override
                                public void onFailure(ErrorResult errorResult) { //회원탈퇴 실패 시
                                    int result = errorResult.getErrorCode(); //에러코드 받음

                                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) { //클라이언트 에러인 경우 -> 네트워크 오류
                                        Toast.makeText(getContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    } else { //클라이언트 에러가 아닌 경우 -> 기타 오류
                                        Toast.makeText(getContext(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onSessionClosed(ErrorResult errorResult) { //처리 도중 세션이 닫힌 경우
                                    Toast.makeText(getContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onNotSignedUp() { //가입된 적이 없는 계정에서 탈퇴를 요구하는 경우
                                    Toast.makeText(getContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onSuccess(Long result) { //회원탈퇴에 성공한 경우
                                    Toast.makeText(getContext(), "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
                ad.setNegativeButton("아니요", null);
                ad.show();
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
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle(R.string.title_setting);
        }
    }

    public class DELETETask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserToken(getContext());
                    Log.e("탈퇴중 토큰", token);
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    final int status = conn.getResponseCode();
                    InputStream inputStream;
                    if (status != HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getErrorStream();

                        Log.e("에러", inputStream.toString());
                    } else {
                        inputStream = conn.getInputStream();
                    }
                    Log.e("응답코드", String.format("" + status));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status < 400) {
                                Toast.makeText(getContext(), "회원탈퇴 정상적으로 성공.", Toast.LENGTH_LONG).show();
                            } else if (status == 600) {
                                Toast.makeText(getContext(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500) {
                                Toast.makeText(getContext(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405) {
                                Toast.makeText(getContext(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404) {
                                Toast.makeText(getContext(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400) {
                                Toast.makeText(getContext(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void revokeAccess(){
        mAuth.getCurrentUser().delete();
    }
}