package com.aleaf.gablestones;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectTourActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tour);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button button_stones = (Button) findViewById(R.id.button_tour1);
        button_stones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Pressing Button opens Mission Screen of Tour selected

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("TourNbr", 1);
                editor.putString("Tour", "1");
                editor.commit();

                Intent tourIntent = new Intent(SelectTourActivity.this, MainActivity.class);
                tourIntent.putExtra("Tour", 1);
                startActivity(tourIntent);
            }
        });

        final Button button_stones_center = (Button) findViewById(R.id.button_tour2);
        button_stones_center.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Pressing Button opens Mission Screen of Tour selected

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("TourNbr", 2);
                editor.putString("Tour", "2");
                editor.commit();

                Intent tourIntent = new Intent(SelectTourActivity.this,MainActivity.class);
                tourIntent.putExtra("Tour", 2);
                startActivity(tourIntent);
            }
        });

    }
}
