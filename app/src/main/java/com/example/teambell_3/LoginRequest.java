package com.example.teambell_3;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;

public class LoginRequest extends StringRequest {

    //서버 URL 설정(php 파일 연동)
    final static private String URL = "http://183.111.253.176:3000/user/signin";
    private Map<String, String> map;

    public LoginRequest(String UserEmail, String UserPwd, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);

        map = new HashMap<>();
        map.put("email", UserEmail);
        map.put("password", UserPwd);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

    @Nullable
    @Override
    public Response.ErrorListener getErrorListener() {
        return super.getErrorListener();

    }
}
