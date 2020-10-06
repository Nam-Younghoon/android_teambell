package com.example.teambell_3;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Statistic_Fragment extends Fragment {

    private Button dayStat, weekStat, monthStat, yearStat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.statistic_fragment, container, false);

        dayStat = v.findViewById(R.id.button_day);
        weekStat = v.findViewById(R.id.button_week);
        monthStat = v.findViewById(R.id.button_month);
        yearStat = v.findViewById(R.id.button_year);

        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction ftrans = fragmentManager.beginTransaction();
        final Fragment fragment = fragmentManager.findFragmentById(getId());

        dayStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.call_stat, new stat_day()).commit();
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
            ((MainActivity) activity).setActionBarTitle(R.string.title_statistic);
        }
    }
}