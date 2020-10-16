package com.example.teambell_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;

public class AddGroup extends AppCompatActivity {

    EditText groupName, groupDate, groupPW;
    Button mkGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        groupName = (EditText) findViewById(R.id.group_name);
        groupPW = (EditText) findViewById(R.id.group_password);
        mkGroup = (Button) findViewById(R.id.makeGroup);

        mkGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("http://192.168.11.44:3000/group/make");
                finish();
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            final String groupN = groupName.getText().toString();
            final String groupP = groupPW.getText().toString();
            try {
                JSONObject group = new JSONObject();
                group.put("name", groupN);
                group.put("password", groupP);

                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserName(AddGroup.this);
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    os = conn.getOutputStream();
                    os.write(group.toString().getBytes());
                    Log.e("그룹 : ", group.toString());
                    os.flush();
                    os.close();


                    final int status = conn.getResponseCode();
                    InputStream inputStream;
                    if( status != HttpURLConnection.HTTP_OK ) {
                        inputStream = conn.getErrorStream();

                        Log.e("에러", inputStream.toString());
                    } else {
                        inputStream = conn.getInputStream();
                    }
                    Log.e("응답코드", String.format(""+status));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status < 400){
                                Toast.makeText(getApplication(), "그룹에 가입되었습니다.", Toast.LENGTH_LONG).show();
                            } else if (status == 600){
                                Toast.makeText(getApplication(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500){
                                Toast.makeText(getApplication(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405){
                                Toast.makeText(getApplication(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404){
                                Toast.makeText(getApplication(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400){
                                Toast.makeText(getApplication(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else{
                                Toast.makeText(getApplication(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });




                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}