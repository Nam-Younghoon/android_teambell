package com.example.teambell_3;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;


public class Setting_Fragment extends Fragment {

    private Button serviceOK, personalOK, locationOK, helpOK, logoutOK;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.setting__fragment, container, false);


        serviceOK = v.findViewById(R.id.serviceOK);
        serviceOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ServiceOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        personalOK = v.findViewById(R.id.personalOK);
        personalOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        locationOK = v.findViewById(R.id.locationOK);
        locationOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        helpOK = v.findViewById(R.id.helpOK);
        helpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HelpOK.class);
                startActivityForResult(intent, 1001);
            }
        });

        logoutOK = v.findViewById(R.id.logoutOK);
        logoutOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return v;
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
            ((MainActivity) activity).setActionBarTitle(R.string.title_setting);
        }
    }

    public void getList(View v){
        switch(v.getId()){
            case R.id.serviceOK:
                Intent intent = new Intent(getActivity(), ServiceOK.class);
                startActivity(intent);
                break;
        }
    }


}