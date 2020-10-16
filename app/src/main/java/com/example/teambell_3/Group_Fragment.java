package com.example.teambell_3;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;


public class Group_Fragment extends Fragment {

    EditText search;

    ArrayList<GroupData> groups;
    GroupAdapter adapter;
    ListView listview;
    private ArrayAdapter<String> arrayAdapter;
    Button createGroup;
    SwipeRefreshLayout refreshLayout;
    EditText writePassword;
    private AlertDialog dialog2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View v = inflater.inflate(R.layout.group_fragment, container, false);
        final View layout = inflater.inflate(R.layout.get_in_group_dialog, (ViewGroup) v.findViewById(R.id.layout_root));
        final String url="http://192.168.11.44:3000/group/groupList";
        search = v.findViewById(R.id.group_search);
        createGroup = v.findViewById(R.id.group_add_icon);
        writePassword = (EditText)layout.findViewById(R.id.getinpassword);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);

        new loadDB().execute(url);
        groups = new ArrayList<>();

        listview = (ListView) v.findViewById(R.id.group_listview);
        adapter = new GroupAdapter(getContext(), groups);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//여기서buttontest는 패키지이름
                builder.setTitle("방 입장하기");
                builder.setView(layout);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String idx = (String) listview.getAdapter().getItem(position);
                                Log.e("확인", idx);
                                new JSONTask().execute(String.format("http://192.168.11.44:3000/group/join/%s",idx));
                                dialog.dismiss();
                                ((ViewGroup)layout.getParent()).removeAllViews();
                            }
                        });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        ((ViewGroup)layout.getParent()).removeAllViews();
                    }
                });
                builder.show();
            }

        });




        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddGroup.class);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Group_Fragment.this).attach(Group_Fragment.this).commit();
                refreshLayout.setRefreshing(false);
            }
        });

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
                    Log.e("가져옴", json.toString());
                    String name = json.getString("name");
                    String count = json.getString("count");
                    String leader = json.getString("leader");
                    String index = json.getString("groupIdx");
                    //name을 ArrayList에 추가
                    groups.add(new GroupData(name,count,leader, index));
                    adapter.notifyDataSetChanged();//변경내용 반영
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class JSONTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            final String password = writePassword.getText().toString();

            try {
                JSONObject group = new JSONObject();
                group.put("password", password);

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
                    String token = SaveSharedPreference.getUserName(getContext());
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    os = conn.getOutputStream();
                    os.write(group.toString().getBytes());
                    Log.e("패스워드 : ", group.toString());
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status < 400){
                                Toast.makeText(getContext(), "그룹에 가입되었습니다.", Toast.LENGTH_LONG).show();
                            } else if (status == 600){
                                Toast.makeText(getContext(), "데이터베이스 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 500){
                                Toast.makeText(getContext(), "서버 에러..", Toast.LENGTH_LONG).show();
                            } else if (status == 405){
                                Toast.makeText(getContext(), "메소드 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 404){
                                Toast.makeText(getContext(), "경로 에러.", Toast.LENGTH_LONG).show();
                            } else if (status == 400){
                                Toast.makeText(getContext(), "데이터 오류.", Toast.LENGTH_LONG).show();
                            } else{
                                Toast.makeText(getContext(), "알 수 없는 오류.", Toast.LENGTH_LONG).show();
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