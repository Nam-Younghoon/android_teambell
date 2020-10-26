package com.example.teambell_3;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Stat_Week extends Fragment {
    ArrayList<StatData> records;
    StatAdapter adapter;
    ListView listview;
    private String mJsonString;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stat_fragment, container, false);
        setHasOptionsMenu(true);

        records = new ArrayList<>();
        new GetData().execute("http://192.168.11.58:3000/record/week");
        listview = (ListView) v.findViewById(R.id.stat_listView);
        adapter = new StatAdapter(getContext(), records);
        listview.setAdapter(adapter);

        return v;
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
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
                String token = SaveSharedPreference.getUserToken(getContext());
                Log.e("토큰 ", token);
                httpURLConnection.setRequestProperty("token", token);
                //httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e("응답", "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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

    private void showResult() {
        String TAG_JSON = "data";
        String TAG_DATE = "date";
        String TAG_DISTANCE = "distance";
        String TAG_RTIME = "time";
        String TAG_RSPEED = "avgSpeed";
        String TAG_RCOUNT = "count";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(TAG_DATE);
                String time = item.getString(TAG_RTIME);
                String distance = item.getString(TAG_DISTANCE);
                String avgSpeed = item.getString(TAG_RSPEED);
                String count = item.getString(TAG_RCOUNT);
                records.add(new StatData(date, distance, time, avgSpeed, count));
                adapter.notifyDataSetChanged();//변경내용 반영

            }
        } catch(JSONException e){

            Log.e("JSONException 발생", "showResult : ", e);
        }

    }}