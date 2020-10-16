package com.example.teambell_3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group_list);

        new GetData().execute("http://192.168.11.44:3000/user/myGroup");
        groups = new ArrayList<>();
        listview = (ListView) findViewById(R.id.mygroup_list);
        adapter = new GroupAdapter(this, groups);
        listview.setAdapter(adapter);



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
                httpURLConnection.setDoOutput(true);
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

}