package com.aleaf.gablestones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    //private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if App is launched for first time
        // show intro Slides Tour
        // if not: show MainActivity

        PrefManager prefManager = new PrefManager(getApplicationContext());
        Intent i;
        if (prefManager.isFirstTimeLaunch()) {
            i = new Intent(SplashActivity.this, Introslider.class);
            prefManager.setFirstTimeLaunch(false);
        } else {
            i = new Intent(SplashActivity.this, SelectTourActivity.class);
        }

        // Start home activity
        startActivity(i);

        // close splash activity
        finish();
    }
}
