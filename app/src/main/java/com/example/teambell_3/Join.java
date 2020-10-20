package com.example.teambell_3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Join extends AppCompatActivity {

    EditText joinName, joinEmail, joinPassword, checkPassword;
    Button register, cancel, checkButton;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_join);

        joinName = (EditText) findViewById(R.id.join_name);
        joinEmail = (EditText) findViewById(R.id.join_email);
        joinPassword = (EditText) findViewById(R.id.join_password);
        register = (Button) findViewById(R.id.join_button);
        cancel = (Button) findViewById(R.id.delete);
        checkPassword = (EditText) findViewById(R.id.join_pwck);
        checkButton = (Button) findViewById(R.id.check_button);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserEmail = joinEmail.getText().toString();
                Log.e("이메일 String화", UserEmail);
                if(validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                    dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                if (UserEmail.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                    dialog = builder.setMessage("아이디를 입력하세요").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            int status = jsonResponse.getInt("status");
                            Log.e("응답하다. ", response);
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                joinEmail.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                checkButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            }
                            else {
                                Log.e("반응", String.format(""+status));
                                AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Join.this);
                queue.add(validateRequest);
            }
        });



        //회원가입 버튼 클릭 시 수행
        register = findViewById( R.id.join_button );
        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserEmail = joinEmail.getText().toString();
                final String UserPwd = joinPassword.getText().toString();
                final String UserName = joinName.getText().toString();
                final String PassCk = checkPassword.getText().toString();


                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (UserEmail.equals("") || UserPwd.equals("") || UserName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //회원가입 성공시
                            if(UserPwd.equals(PassCk)) {
                                if (success) {

                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Join.this, Login.class);
                                    startActivity(intent);

                                    //회원가입 실패시
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley를 이용해서 요청
                RegisterRequest registerRequest = new RegisterRequest( UserEmail, UserPwd, UserName, responseListener);
                RequestQueue queue = Volley.newRequestQueue( Join.this );
                queue.add( registerRequest );
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
