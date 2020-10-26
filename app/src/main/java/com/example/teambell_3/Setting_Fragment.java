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

    private Button serviceOK, personalOK, locationOK, helpOK, logoutOK, deleteOK;
    private EditText writePassword;
    private boolean success;
    private String mJsonString;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.setting__fragment, container, false);
//        final View layout = inflater.inflate(R.layout.get_in_group_dialog, (ViewGroup) v.findViewById(R.id.layout_root));
        mAuth = FirebaseAuth.getInstance();
//        writePassword = (EditText) layout.findViewById(R.id.getinpassword);
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

        logoutOK = v.findViewById(R.id.logoutOK);
        logoutOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreference.clearUserName(getContext());
                Intent logoutIntent = new Intent(getContext(), Login.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
            }
        });

        deleteOK = v.findViewById(R.id.deleteOK);
        deleteOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        try {
                            new DELETETask().execute("http://192.168.11.58:3000/user/withdrawal").get();
                            SaveSharedPreference.clearUserName(getContext());
                            revokeAccess();
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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