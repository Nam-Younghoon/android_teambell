package com.example.teambell_3;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.concurrent.ExecutionException;


public class Group_Fragment extends Fragment{

    EditText search;

    ArrayList<GroupData> groups, save;
    GroupAdapter adapter;
    ListView listview;
    Button createGroup;
    SwipeRefreshLayout refreshLayout;
    EditText writePassword;
    String mJsonString;
    boolean success;
    private String idx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View v = inflater.inflate(R.layout.group_fragment, container, false);
        final View layout = inflater.inflate(R.layout.get_in_group_dialog, (ViewGroup) v.findViewById(R.id.layout_root));
        final String url="http://192.168.11.58:3000/group/groupList";
        search = v.findViewById(R.id.group_search);
        createGroup = v.findViewById(R.id.group_add_icon);
        writePassword = (EditText)layout.findViewById(R.id.getinpassword);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);


        new loadDB().execute(url);

        groups = new ArrayList<>();
        save = new ArrayList<>();

        listview = (ListView) v.findViewById(R.id.group_listview);
        adapter = new GroupAdapter(getContext(), groups);
        listview.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                idx = (String) listview.getAdapter().getItem(position);

                try {
                    String result = new checkTask().execute(String.format("http://192.168.11.58:3000/group/member/%s", idx)).get();
                    mJsonString = result;
                    JSONObject jsonObject = new JSONObject(mJsonString);
                    success = jsonObject.getBoolean( "data" );
                    Log.e("완료함", String.format(""+success));
                    }
                catch (JSONException e) {
                    Log.e("JSONException 발생", "showResult : ", e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.e("받아옴", "받아옴");

                Log.e("444444", "4444444444");
                if(!success){
                    Log.e("111111111", "11111111111");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//여기서buttontest는 패키지이름
                    builder.setTitle("방 입장하기");
                    builder.setView(layout);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("확인", idx);
                            new JSONTask().execute(String.format("http://192.168.11.58:3000/group/join/%s",idx));
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
                } else {
                    Log.e("111122222211111", "11112222222221111111");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//여기서buttontest는 패키지이름
                    builder.setTitle("방 입장하기");
                    builder.setMessage("이미 속해있는 그룹입니다.");
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }

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

    public void searchUser(String search){
        groups.clear();
        for(int i=0; i<save.size(); i++){
            if(save.get(i).getGTitle().contains(search)){
                groups.add(save.get(i));
            }
            adapter.notifyDataSetChanged();
        }
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
                    save.add(new GroupData(name, count, leader, index));
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
                    String token = SaveSharedPreference.getUserToken(getContext());
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
        refresh();
    }

    public void refresh(){
        adapter.notifyDataSetChanged();
    }

    private class checkTask extends AsyncTask<String, Void, String>{

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
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                success = jsonObject.getBoolean( "data" );


            } catch (JSONException e) {

                Log.e("JSONException 발생", "showResult : ", e);
            }
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
}