package com.example.teambell_3;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Record_Fragment extends Fragment {

    ArrayList<RecordData> records;
    RecordAdapter adapter;
    ListView listview;
    private String mJsonString;
    String mIdx;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record_fragment, container, false);
        setHasOptionsMenu(true);

        final SwipeRefreshLayout refreshLayout = v.findViewById(R.id.swipe_refresh2);
        records = new ArrayList<>();
        new GetData().execute("http://106.243.128.187:3000/record/today");
        listview = (ListView) v.findViewById(R.id.record_listView);
        adapter = new RecordAdapter(getContext(), records);
        listview.setAdapter(adapter);

        // 상단바
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        myToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Record_Fragment.this).attach(Record_Fragment.this).commit();
                refreshLayout.setRefreshing(false);

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final long idx =  listview.getAdapter().getItemId(position);
                Log.e("확인", String.format(""+idx));
                String dist = records.get((int)idx).getRDistance();
                String time = records.get((int)idx).getRTime();
                String avgSpeed = records.get((int)idx).getRSpeed();
                String dep = records.get((int)idx).getRDep();
                String arr = records.get((int)idx).getRArr();
                String path = records.get((int)idx).getRGpx();
                Log.e("주소", path);

                Intent intent = new Intent(getContext(), RecordDetail.class);
                intent.putExtra("dist", dist);
                intent.putExtra("time", time);
                intent.putExtra("avgSpeed", avgSpeed);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });


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
        String TAG_DATE = "date";
        String TAG_DISTANCE = "distance";
        String TAG_RTIME ="time";
        String TAG_RSPEED = "avgSpeed";
        String TAG_RDEP ="dep";
        String TAG_RARR = "arr";
        String TAG_RIDX = "idx";
        String TAG_RPATH = "gpx";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(TAG_DATE);
                String time = item.getString(TAG_RTIME);
                String distance = item.getString(TAG_DISTANCE);
                String avgSpeed = item.getString(TAG_RSPEED);
                String dep = item.getString(TAG_RDEP);
                String arr = item.getString(TAG_RARR);
                String idx = item.getString(TAG_RIDX);
                String gpx = item.getString(TAG_RPATH);

                records.add(new RecordData(date, distance, time, avgSpeed, dep, arr, idx, gpx));

            }

            adapter.notifyDataSetChanged();//변경내용 반영
        } catch (JSONException e) {

            Log.e("JSONException 발생", "showResult : ", e);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if(activity != null){
            ((MainActivity) activity).setActionBarTitle(R.string.title_record);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Record_Fragment.this).attach(Record_Fragment.this).commit();
        }
        return super.onOptionsItemSelected(item);
    }
}