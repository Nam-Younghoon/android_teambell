package com.example.teambell_3;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

public class Statistic_Fragment extends Fragment {

    private TextView dayStat, weekStat, monthStat, yearStat;
    ViewPager vp;
    LinearLayout ll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.statistic_fragment, container, false);

        dayStat = v.findViewById(R.id.button_day);
        weekStat = v.findViewById(R.id.button_week);
        monthStat = v.findViewById(R.id.button_month);
        yearStat = v.findViewById(R.id.button_year);
        vp = v.findViewById(R.id.vp);
        ll = v.findViewById(R.id.ll);

        final SwipeRefreshLayout refreshLayout = v.findViewById(R.id.swipe_refresh3);

        vp.setAdapter(new pagerAdapter(getChildFragmentManager()));
        vp.setCurrentItem(0);


        dayStat.setOnClickListener(movePageListener);
        dayStat.setTag(0);

        weekStat.setOnClickListener(movePageListener);
        weekStat.setTag(1);

        monthStat.setOnClickListener(movePageListener);
        monthStat.setTag(2);

        yearStat.setOnClickListener(movePageListener);
        yearStat.setTag(3);

        dayStat.setSelected(true);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Statistic_Fragment.this).attach(Statistic_Fragment.this).commit();
                refreshLayout.setRefreshing(false);

            }
        });

        // 상단바
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        myToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);


        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = 0;
                while (i<4){
                    if(position == i){
                        ll.findViewWithTag(i).setSelected(true);
                    }
                    else {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    View.OnClickListener movePageListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            int i = 0;
            while(i<4){
                if(tag == i){
                    ll.findViewWithTag(i).setSelected(true);
                }
                else {
                    ll.findViewWithTag(i).setSelected(false);
                }
                i++;
            }
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter{
        public pagerAdapter(FragmentManager fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Stat_Day();
                case 1:
                    return new Stat_Week();
                case 2:
                    return new Stat_Month();
                case 3:
                    return new Stat_Year();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater =  ((MainActivity)getActivity()).getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Statistic_Fragment.this).attach(Statistic_Fragment.this).commit();
        }
        return super.onOptionsItemSelected(item);
    }
}