package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MyGroupList extends AppCompatActivity {

    ListView listview;
    ArrayList<GroupData> groups;
    GroupAdapter adapter;
    private String mJsonString;
    String mIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_group_list);

        new GetData().execute("http://192.168.11.44:3000/user/myGroup");
        groups = new ArrayList<>();
        listview = (ListView) findViewById(R.id.mygroup_list);
        adapter = new GroupAdapter(this, groups);
        listview.setAdapter(adapter);

        // 상단바
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                if (! MyGroupList.this.isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupList.this);
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String idx = (String) listview.getAdapter().getItem(position);
                                    Log.e("확인", idx);
                                    mIdx = idx;
                                    new JSONTaskDel().execute(String.format("http://192.168.11.44:3000/group/delete/%s", idx));
                                    groups.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();

                }
                // 이벤트 처리 종료 , 여기만 리스너 적용시키고 싶으면 true , 아니면 false
                return true;
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String idx = (String) listview.getAdapter().getItem(position);
                Log.e("확인", idx);
                mIdx = idx;
                new JSONTask().execute(String.format("http://192.168.11.44:3000/member/status/%s", idx));
                Intent intent = new Intent(MyGroupList.this, GroupRiding.class);
                intent.putExtra("GroupIdx", idx);
                startActivity(intent);
                finish();
            }
        });



    }


    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyGroupList.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e("응답 ", "response - " + result);
            mJsonString = result;
            showResult();
        }



        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                String token = SaveSharedPreference.getUserName(getApplication());
                Log.e("토큰 ", token);
                httpURLConnection.setRequestProperty("token", token);
                httpURLConnection.setDoInput(true);


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e("응답", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                Log.e("버퍼리더", sb.toString().trim());

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e("오류", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="data";
        String TAG_LEADER = "leader";
        String TAG_NAME = "name";
        String TAG_COUNT ="count";
        String TAG_GROUPIDX = "groupIdx";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUPIDX);
                String name = item.getString(TAG_NAME);
                String count = item.getString(TAG_COUNT);
                String leader = item.getString(TAG_LEADER);

                groups.add(new GroupData(name,count,leader, groupIdx));
                adapter.notifyDataSetChanged();//변경내용 반영
            }



        } catch (JSONException e) {

            Log.e("JSONException 발생", "showResult : ", e);
        }

    }

    public class JSONTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url;
                HttpURLConnection conn = null;
                OutputStream os = null;

                try {
                    url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String token = SaveSharedPreference.getUserName(getApplication());
                    conn.setRequestProperty("token", token);
                    Log.e("JSONTask 실행됨", "ok");

                    JSONObject put = new JSONObject();
                    put.put("available", 2);

                    os = conn.getOutputStream();
                    os.write(put.toString().getBytes());
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
                                Toast.makeText(getApplication(), "그룹에 참여합니다.", Toast.LENGTH_LONG).show();
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

                } catch (ProtocolException e) {
                    e.printStackTrace();
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

    public class JSONTaskDel extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection conn = null;
            OutputStream os = null;

            try {
                url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                String token = SaveSharedPreference.getUserName(getApplication());
                conn.setRequestProperty("token", token);
                Log.e("JSONTask 실행됨", "ok");

                os = conn.getOutputStream();
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
                            Toast.makeText(getApplication(), "그룹에서 탈퇴했습니다.", Toast.LENGTH_LONG).show();
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

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
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

}