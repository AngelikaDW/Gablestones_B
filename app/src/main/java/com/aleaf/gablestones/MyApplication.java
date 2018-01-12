package com.aleaf.gablestones;

import android.app.Application;

/**
 * Created by angelika on 09/01/2018.
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}