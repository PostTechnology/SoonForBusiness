package com.soon.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.soon.android.fragments.DishesFragment;
import com.soon.android.fragments.HomeFragment;
import com.soon.android.fragments.OrdersFragment;
import com.soon.android.fragments.PersonalInformationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;


public class MainActivity extends AppCompatActivity {
    LineChart mLineChart;

    @BindView(R.id.content_container)
    FrameLayout contentContainer;

    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        replaceFragment( new PersonalInformationFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            default:break;
        }

    }

    private void init(){
        final HomeFragment homeFragment = new HomeFragment();
        final DishesFragment dishesFragment = new DishesFragment();
        final OrdersFragment ordersFragment = new OrdersFragment();
        final PersonalInformationFragment personalInformationFragment = new PersonalInformationFragment();
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                SharedPreferences preferences = getSharedPreferences("store", Context.MODE_PRIVATE);
                String state = preferences.getString("state","");
                if(state.equals("已认证")){
                    switch (tabId) {
                        case R.id.tab_home:
                            replaceFragment(homeFragment);
                            break;
                        case R.id.tab_dishes:
                            replaceFragment(dishesFragment);
                            break;
                        case R.id.tab_orders:
                            replaceFragment(ordersFragment);
                            break;
                        case R.id.tab_personal_information:
                            replaceFragment(personalInformationFragment);
                            break;
                        default:
                            break;
                    }
                }else if (state.equals("未认证")){
                    switch (tabId) {
                        case R.id.tab_home:
//                                replaceFragment(new HomeFragment());
                            Toast.makeText(MainActivity.this, "请先完成认证！", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.tab_dishes:
//                                replaceFragment(new DishesFragment());
                            Toast.makeText(MainActivity.this, "请先完成认证！", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.tab_orders:
//                                replaceFragment(new OrdersFragment());
                            Toast.makeText(MainActivity.this, "请先完成认证！", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.tab_personal_information:
                            replaceFragment(personalInformationFragment);
                            break;
                        default:
                            break;
                    }
                }

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_container, fragment);
                transaction.addToBackStack(null);
        transaction.commit();
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}