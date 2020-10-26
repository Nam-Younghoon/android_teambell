package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.internal.http2.ErrorCode;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button register, signin;
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    private LoginButton kakao;
    String idToken;

    private SessionCallback sessionCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText)findViewById(R.id.login_email);
        loginPassword = (EditText)findViewById(R.id.login_password);
        register = (Button) findViewById(R.id.join_button);
        signin = (Button) findViewById(R.id.login_button);

        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        kakao = (LoginButton) findViewById(R.id.kakaoLogin);
        //카카오 로그인 콜백받기
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);



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
                                SaveSharedPreference.setUserToken(Login.this, jsonObject.getJSONObject("data").getString("accessToken"));
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

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        kakao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, Login.this);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }

        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            Log.e("받", mUser.toString());

            mUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    idToken = task.getResult().getToken();
                    Log.e("받2", task.getResult().toString());
                    Log.e("구글토큰", idToken);
                    Response.Listener<String> responseListener = response -> {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );
                            Log.e("토큰값 받았음", jsonObject.getJSONObject("data").getString("accessToken"));
                            Log.e("리플래시 토큰값", jsonObject.getJSONObject("data").getString("refreshToken"));
                            if(success) {//로그인 성공시
                                Intent intent1 = new Intent( Login.this, MainActivity.class );
                                startActivity(intent1);
                                Log.e("토큰값 받았음", jsonObject.getJSONObject("data").getString("accessToken"));
                                SaveSharedPreference.setUserToken(Login.this, jsonObject.getJSONObject("data").getString("accessToken"));
                                finish();

                            } else {//로그인 실패시
                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    };
                    SocialGoogleLoginRequest SocialgoogleLoginRequest = new SocialGoogleLoginRequest( idToken, responseListener );
                    RequestQueue queue = Volley.newRequestQueue( Login.this );
                    queue.add( SocialgoogleLoginRequest );
                }else {
                    // Handle error -> task.getException();
                }
            });
            startActivity(intent);
            finish();
        }
    }

    //카카오 로그인 콜백
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() { //세션이 성공적으로 열린 경우
            UserManagement.getInstance().me(new MeV2ResponseCallback() { //유저 정보를 가져온다.
                @Override
                public void onFailure(ErrorResult errorResult) { //유저 정보를 가져오는 데 실패한 경우
                    int result = errorResult.getErrorCode(); //오류 코드를 받아온다.

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) { //클라이언트 에러인 경우: 네트워크 오류
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { //클라이언트 에러가 아닌 경우: 기타 오류
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) { //세션이 도중에 닫힌 경우
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) { //유저 정보를 가져오는데 성공한 경우
                    String needsScopeAutority = ""; //이메일, 성별, 연령대, 생일 정보 가져오는 권한 체크용
                    String result2 = result.toString();
                    Log.e("유저 정보 가져오기 성공", result2);
                    if(result.getKakaoAccount().needsScopeAccountEmail()) { //이메일 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + "이메일";
                    }
                    if(result.getKakaoAccount().needsScopeGender()) { //성별 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 성별";
                    }
                    if(result.getKakaoAccount().needsScopeAgeRange()) { //연령대 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 연령대";
                    }
                    if(result.getKakaoAccount().needsScopeBirthday()) { //생일 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 생일";
                    }

                    if(needsScopeAutority.length() != 0) { //거절된 권한이 있는 경우
                        //거절된 권한을 허용해달라는 Toast 메세지 출력
                        if(needsScopeAutority.charAt(0) == ',') {
                            needsScopeAutority = needsScopeAutority.substring(2);
                        }
                        Toast.makeText(getApplicationContext(), needsScopeAutority+"에 대한 권한이 허용되지 않았습니다. 개인정보 제공에 동의해주세요.", Toast.LENGTH_SHORT).show();

                        //회원탈퇴 수행
                        //회원탈퇴에 대한 자세한 내용은 MainActivity의 회원탈퇴 버튼 참고
                        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                int result = errorResult.getErrorCode();

                                if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                    Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                Toast.makeText(getApplicationContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNotSignedUp() {
                                Toast.makeText(getApplicationContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Long result) { }
                        });
                    } else { //모든 정보를 가져오도록 허락받았다면
                        //MainActivity로 넘어가면서 유저 정보를 같이 넘겨줌
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        String id = String.format(""+result.getId());
                        Log.e("아이디", id);
                        String name = result.getNickname();
                        Log.e("이름 ", name);
                        String email = result.getKakaoAccount().getEmail();
                        Log.e("이메일", email);
                        String refresh = Session.getCurrentSession().getTokenInfo().getRefreshToken();
                        Log.e("리프레시", refresh);
//                        Log.e("refresh", refresh);
                        Response.Listener<String> responseListener = response -> {
                            try {
                                JSONObject jsonObject = new JSONObject( response );
                                boolean success = jsonObject.getBoolean( "success" );
                                Log.e("토큰값 받았음", jsonObject.getJSONObject("data").getString("accessToken"));
                                Log.e("리플래시 토큰값", jsonObject.getJSONObject("data").getString("refreshToken"));
                                if(success) {//로그인 성공시
                                    Intent intent1 = new Intent( Login.this, MainActivity.class );
                                    startActivity(intent1);
                                    Log.e("토큰값 받았음", jsonObject.getJSONObject("data").getString("accessToken"));
                                    SaveSharedPreference.setUserToken(Login.this, jsonObject.getJSONObject("data").getString("accessToken"));
                                    finish();

                                } else {//로그인 실패시
                                    Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                        SocialKakaoLoginRequest SocialkakaoLoginRequest = new SocialKakaoLoginRequest(id, email, name, refresh, responseListener );
                        RequestQueue queue = Volley.newRequestQueue( Login.this );
                        queue.add( SocialkakaoLoginRequest );

                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) { //세션을 여는 도중 오류가 발생한 경우 -> Toast 메세지를 띄움.
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("카카오 오류", e.toString());
        }
    }





}