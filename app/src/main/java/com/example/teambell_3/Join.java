package com.example.teambell_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class Join extends AppCompatActivity {

    EditText joinName, joinID, joinPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinName = (EditText) findViewById(R.id.join_name);
        joinID = (EditText) findViewById(R.id.join_id);
        joinPassword = (EditText) findViewById(R.id.join_password);


    }
}