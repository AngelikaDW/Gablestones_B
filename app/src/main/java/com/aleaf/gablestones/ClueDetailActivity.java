package com.aleaf.gablestones;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import com.aleaf.gablestones.data.StoneContract.StoneEntry;

import java.util.Locale;

/*
* Shows the user detailed information and image about the stone selected in the overview
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


    /*Textfield for name, address, housenumber, description and running Number*/
    private TextView mNameText;
    private TextView mAddressText;
    private TextView mHousenumberText;
    private TextView mDescriptionText;
    private TextView mRunningNumberText;
    private ImageView mClueImage;

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
        Bundle fragmentLoaded = intent.getExtras();

        if (mCurrentStoneUri != null) {
            setTitle(R.string.stone_detail);
            getSupportLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);
        }

        // Find all relevant views that we will need to show content
        mNameText = (TextView) findViewById(R.id.clueName);
        mAddressText = (TextView) findViewById(R.id.clueAddress);
        mHousenumberText = (TextView) findViewById(R.id.clueHousenumber);
        mDescriptionText = (TextView) findViewById(R.id.clueDescription);
        mRunningNumberText = (TextView) findViewById(R.id.clueRunningNumber);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClueImage = (ImageView) findViewById(R.id.image_clue_detail);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains columns in all languages from the stones table
        String[] projection = {
                StoneEntry._ID,
                StoneEntry.COLUMN_STONE_NAME,
                StoneEntry.COLUMN_STONE_NAME_DE,
                StoneEntry.COLUMN_STONE_NAME_NL,
                StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneEntry.COLUMN_STONE_DESCRIPTION,
                StoneEntry.COLUMN_STONE_DESCRIPTION_DE,
                StoneEntry.COLUMN_STONE_DESCRIPTION_NL,
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
            int nameNLColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_NL);
            int nameDEColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_DE);
            int runColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
            int descColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION);
            int descNLColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION_NL);
            int descDEColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION_DE);
            int addresColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_ADDRESS);
            int houseColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_HOUSENUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String stoneNameNL = cursor.getString(nameNLColumnIndex);
            String stoneNameDE = cursor.getString(nameDEColumnIndex);
            int run = cursor.getInt(runColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String descriptionNL = cursor.getString(descNLColumnIndex);
            String descriptionDE = cursor.getString(descDEColumnIndex);
            String addres = cursor.getString(addresColumnIndex);
            int housenumber = cursor.getInt(houseColumnIndex);

            // Update the views on the screen with the values from the database
            // depended on the language of the device select EN, NL or DE content
            // Get the system language of user's device
            String language = Locale.getDefault().getLanguage();

            if (language.equals("en")) {
                mNameText.setText(name);
                mDescriptionText.setText(description);
            } else if (language.equals("nl")) {
                mNameText.setText(stoneNameNL);
                mDescriptionText.setText(descriptionNL);
            } else if (language.equals("de")) {
                mNameText.setText(stoneNameDE);
                mDescriptionText.setText(descriptionDE);
            }

            //No translation required for this fields
            mRunningNumberText.setText(Integer.toString(run));
            mAddressText.setText(addres);
            mHousenumberText.setText(Integer.toString(housenumber));

            // Set image in the detail view from drawable folder, based on the running Number
            // as extracted from the database
            String uri = "@drawable/image" + Integer.toString(run);

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            mClueImage.setImageResource(imageResource);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameText.setText("");
        mRunningNumberText.setText("");
        mAddressText.setText("");
    }


    //ToDo: Write Content in the DB and update the DB (with new name!!!)
}

