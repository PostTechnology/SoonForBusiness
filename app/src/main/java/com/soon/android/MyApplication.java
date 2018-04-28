package com.soon.android;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Created by LYH on 2018/4/23.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        Fresco.initialize(this);//初始化Fresco
        Bmob.initialize(this, "84aaecd322d3f4afa028222b754f2f98");//初始化Bmob
    }

    public static Context getContext(){
        return context;
    }
}
