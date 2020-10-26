package com.example.teambell_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{
    private BottomNavigationView mBottomNV;
    private Context context = this;
    ListView listview = null;

    final Fragment fragment1 = new Home_Fragment();
    final Fragment fragment2 = new Record_Fragment();
    final Fragment fragment3 = new Statistic_Fragment();
    final Fragment fragment4 = new Group_Fragment();
    final Fragment fragment5 = new Setting_Fragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);



        mBottomNV = findViewById(R.id.bottom_navi);
        mBottomNV.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNV.setSelectedItemId(R.id.home_navigation);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        fm.beginTransaction().add(R.id.content_layout, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.content_layout, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.content_layout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.content_layout, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.content_layout,fragment1, "1").commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_navigation:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.record_navigation:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.statistic_navigation:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;

                case R.id.group_navigation:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    return true;

                case R.id.setting_navigation:
                    fm.beginTransaction().hide(active).show(fragment5).commit();
                    active = fragment5;
                    return true;
            }
            return false;
        }
    };


//    private void BottomNavigate(int id) {  //BottomNavigation 페이지 변경
//        String tag = String.valueOf(id);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
//        if (currentFragment != null) {
//            fragmentTransaction.hide(currentFragment);
//        }
//        Fragment fragment = fragmentManager.findFragmentByTag(tag);
//        if (fragment == null) {
//            if (id == R.id.home_navigation) {
//                fragment = new Home_Fragment();
//            } else if (id == R.id.setting_navigation) {
//                fragment = new Setting_Fragment();
//            } else if (id == R.id.statistic_navigation){
//                fragment = new Statistic_Fragment();
//            } else if (id == R.id.record_navigation){
//                fragment = new Record_Fragment();
//            } else
//                fragment = new Group_Fragment();
//            fragmentTransaction.add(R.id.content_layout, fragment, tag);
//        } else {
//            fragmentTransaction.show(fragment);
//        }
//        fragmentTransaction.setPrimaryNavigationFragment(fragment);
//        fragmentTransaction.setReorderingAllowed(true);
//        fragmentTransaction.commitNow();
//
//
//    }


    public void setActionBarTitle(int title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }


}
