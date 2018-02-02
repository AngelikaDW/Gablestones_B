package com.aleaf.gablestones;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SelectTourActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public Context context;
    RadioButton radioButtonTour1, radioButtonTour2;
    FloatingActionButton startTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tour);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioButtonTour1 = (RadioButton) findViewById(R.id.button_tour1);
        radioButtonTour2 = (RadioButton) findViewById(R.id.button_tour2);

        startTour = (FloatingActionButton) findViewById(R.id.button_start_tour);
        startTour.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (radioButtonTour1.isChecked()) {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("TourNbr", 1);
                    editor.putString("Tour", "1");
                    editor.commit();

                    Intent tourIntent = new Intent(SelectTourActivity.this, MissionActivity.class);
                    tourIntent.putExtra("Tour", 1);
                    startActivity(tourIntent);
                } else if (radioButtonTour2.isChecked()) {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("TourNbr", 2);
                    editor.putString("Tour", "2");
                    editor.commit();

                    Intent tourIntent = new Intent(SelectTourActivity.this,MissionActivity.class);
                    tourIntent.putExtra("Tour", 2);
                    startActivity(tourIntent);
                } else {
                    Toast.makeText(SelectTourActivity.this, "Select a tour by clicking on one round circle", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button button_how_works = (Button) findViewById(R.id.button_how_does_it_work);
        button_how_works.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent howWorksIntent = new Intent(SelectTourActivity.this, Introslider.class);
                startActivity(howWorksIntent);
            }
        });
    }

}
