package com.aleaf.gablestones;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;


import com.aleaf.gablestones.data.StoneContract.StoneEntry;




/*
* Shows the user information about the stone
* */
public class ClueDetailActivity extends AppCompatActivity implements
                android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the stone data loader
     */
    private static final int EXISTING_STONE_LOADER = 0;

    /**
     * Content URI for the exitsing stone (null if it's a new stone)
     */
    private Uri mCurrentStoneUri;

    /*Textfield for address*/
    private TextView mNameText;
    private TextView mAddressText;
    private TextView mHousenumberText;
    private TextView mDescriptionText;
    private TextView mRunningNumberText;

    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mStoneHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail_activiy);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentStoneUri = intent.getData();

        if (mCurrentStoneUri != null) {
            setTitle("Clue Detail");
            getSupportLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);
        }
        // Kick off the loader
//        getSupportLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);


        // Find all relevant views that we will need to read user input from
        mNameText = (TextView) findViewById(R.id.clueName);
        mAddressText = (TextView) findViewById(R.id.clueAddress);
        mHousenumberText = (TextView) findViewById(R.id.clueHousenumber);
        mDescriptionText = (TextView) findViewById(R.id.clueDescription);
        mRunningNumberText = (TextView) findViewById(R.id.clueRunningNumber);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.image_clue_detail);
        imageView.setImageResource(R.drawable.image2);




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the stones table
        String[] projection = {
                StoneEntry._ID,
                StoneEntry.COLUMN_STONE_NAME,
                StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneEntry.COLUMN_STONE_DESCRIPTION,
                StoneEntry.COLUMN_STONE_ADDRESS,
                StoneEntry.COLUMN_STONE_HOUSENUMBER};

        return new CursorLoader(this,
                mCurrentStoneUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME);
            int runColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
            int descColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION);
            int addresColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_ADDRESS);
            int houseColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_HOUSENUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int run = cursor.getInt(runColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String addres = cursor.getString(addresColumnIndex);
            int housenumber = cursor.getInt(houseColumnIndex);

            // Update the views on the screen with the values from the database
            mNameText.setText(name);
            mRunningNumberText.setText(Integer.toString(run));
            mDescriptionText.setText(description);
            mAddressText.setText(addres);
            mHousenumberText.setText(Integer.toString(housenumber));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameText.setText("");
        mRunningNumberText.setText("");
        mAddressText.setText("");
    }

}
