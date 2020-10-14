package com.example.teambell_3;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;


public class Group_Fragment extends Fragment {

    EditText search;

    ArrayList<GroupData> groups;
    GroupAdapter adapter;
    ListView listview;
    private ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.group_fragment, container, false);
        String url="http://192.168.11.44:3000/group/groupList";
        search = v.findViewById(R.id.group_search);
        new loadDB().execute(url);
        groups = new ArrayList<>();
        //groups.add(new GroupData("금천라이딩", 5,"홍아무개", "2020-10-08 17:43"));

        listview = (ListView) v.findViewById(R.id.group_listview);
        adapter = new GroupAdapter(getContext(), groups);
        listview.setAdapter(adapter);

        return v;
    }
    private class loadDB extends AsyncTask<String,Void,String> {

        //주요 내용 실행
        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String)downloadUrl((String)urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }

        //ui변경 작업 실행
        @Override
        protected void onPostExecute(String result) {

            try {

                JSONObject json = new JSONObject(result);
                JSONArray jArr = json.getJSONArray("data");

                for (int i=0; i<jArr.length(); i++) {

                    json = jArr.getJSONObject(i);

                    String name = json.getString("name");
                    String createdAt = json.getString("createdAt");
                    String count = json.getString("count");
                    String leader = json.getString("leader");
                    //name을 ArrayList에 추가
                    groups.add(new GroupData(name,count,leader,createdAt));
                    adapter.notifyDataSetChanged();//변경내용 반영
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle(R.string.title_group);
        }
    }
}