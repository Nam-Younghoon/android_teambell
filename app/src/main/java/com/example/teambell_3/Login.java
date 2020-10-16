package com.example.teambell_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button register, signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText)findViewById(R.id.login_email);
        loginPassword = (EditText)findViewById(R.id.login_password);
        register = (Button) findViewById(R.id.join_button);
        signin = (Button) findViewById(R.id.login_button);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String UserEmail = loginEmail.getText().toString();
                final String UserPwd = loginPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {//로그인 성공시
                                Intent intent = new Intent( Login.this, MainActivity.class );
                                startActivity( intent );
                                SaveSharedPreference.setUserName(Login.this, UserEmail, UserPwd, jsonObject.getJSONObject("data").getString("accessToken"));
                                Log.e("토큰값 받았음", jsonObject.getJSONObject("data").getString("accessToken"));
                                finish();

                            } else {//로그인 실패시
                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest( UserEmail, UserPwd, responseListener );
                RequestQueue queue = Volley.newRequestQueue( Login.this );
                queue.add( loginRequest );
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Join.class));
            }
        });

    }
}