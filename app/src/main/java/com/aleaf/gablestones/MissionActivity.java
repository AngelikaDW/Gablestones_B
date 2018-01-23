package com.aleaf.gablestones;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.util.Locale;

import static com.aleaf.gablestones.data.StoneContract.*;

public class MissionActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private ListView mStoneListView;
    private StoneCursorAdapter mCursorAdapter;
    private String mLanguage;
    private int mTourNbr;

    /** Identifier for the stone data loader */
    private static final int STONE_LOADER = 0;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find the ListView which will be populated with the stone data
        ListView stoneListView = (ListView) findViewById(R.id.listView);
        mStoneListView = stoneListView;
        // Setup an Adapter to create a list item for each row of stone data in the Cursor.
        mCursorAdapter = new StoneCursorAdapter(this, null);
        stoneListView.setAdapter(mCursorAdapter);

        //Get the system language of user's device
        mLanguage = Locale.getDefault().getDisplayLanguage();

        // Setup the item click listener
        stoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to ClueDetailActivity
                Intent intent = new Intent();
                intent.setClass(MissionActivity.this, DetailActivity.class);

                // From the content URI that represents the specific stone that was clicked on,
                // by appending the "id" (passed as input to this method) onto the.
                Uri currentStoneUri = ContentUris.withAppendedId(
                        StoneEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentStoneUri);
                intent.putExtra("Tour", mTourNbr);
                intent.putExtra("Fragment", position);
                Log.i("MissionAct Position", "Position clicked: " + position);

                // Launch the ClueDetailActivity to display the information for the current stone.
                startActivity(intent);

            }
        });

        //Get Shared Preferences
        /*Get number of Tour selected from SelectTourActivity*/
        SharedPreferences tourSelected = this.getSharedPreferences(SelectTourActivity.PREFS_NAME, MODE_PRIVATE);
        mTourNbr = tourSelected.getInt("TourNbr", MODE_PRIVATE);

        // Kick off the loader
        getSupportLoaderManager().initLoader(STONE_LOADER, null, this);

        // Display the Admob Ad
        mAdView = (AdView) findViewById(R.id.adViewMission);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                StoneEntry._ID,
                StoneEntry.COLUMN_STONE_NAME,
                StoneEntry.COLUMN_STONE_ADDRESS,
                StoneEntry.COLUMN_STONE_HOUSENUMBER,
                StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneEntry.COLUMN_STONE_NAME_NL,
                StoneEntry.COLUMN_STONE_NAME_DE,
                StoneEntry.COLUMN_STONE_LAT,
                StoneEntry.COLUMN_STONE_LNG,
                StoneEntry.COLUMN_STONE_MATCH,
                StoneEntry.COLUMN_STONE_TOUR
        };
        String selection = StoneEntry.COLUMN_STONE_TOUR + "=?";

        String[] selectionArgs = new String[]{String.valueOf(mTourNbr)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                                StoneEntry.CONTENT_URI,   // Provider content URI to query
                                projection,             // Columns to include in the resulting Cursor
                                selection,              // Selection based on TOUR
                                selectionArgs,          // Tournumber (1, 2)
                                null);        // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update StoneAdapter with this new cursor containing updated stone data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.madeByColofon:
                Intent infoIntent = new Intent(MissionActivity.this, ColofonActivity.class);
                startActivity(infoIntent);
                return true;
            case R.id.intro_tour:
                Intent tourIntent = new Intent(MissionActivity.this, Introslider.class);
                startActivity(tourIntent);
                return true;
//            case R.id.confetti:
//                Intent confettiIntent = new Intent(MainActivity.this, ConfettiActivity.class);
//                startActivity(confettiIntent);
//                return true;
            case R.id.select_tour:
                Intent selectTourIntent = new Intent(MissionActivity.this, SelectTourActivity.class);
                startActivity(selectTourIntent);
                return true;
            case R.id.open_map:
                Intent openMapIntent = new Intent(MissionActivity.this, MapsActivity.class);
                startActivity(openMapIntent);
                return true;
//            case R.id.open_detailFragment:
//                Intent clueDetailIntent = new Intent(MissionActivity.this, DetailActivity.class);
//                startActivity(clueDetailIntent);
//                return true;
            default:
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.madeByColofon) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }


}
