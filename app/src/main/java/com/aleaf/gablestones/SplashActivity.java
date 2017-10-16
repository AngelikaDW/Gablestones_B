package com.aleaf.gablestones;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    //private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        Intent i;
        if (prefManager.isFirstTimeLaunch()) {
            i = new Intent(SplashActivity.this, Introslider.class);
            prefManager.setFirstTimeLaunch(false);
        } else {
            i = new Intent(SplashActivity.this, MainActivity.class);
        }

        // Start home activity
        startActivity(i);
        //startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // close splash activity
        finish();
    }
}
