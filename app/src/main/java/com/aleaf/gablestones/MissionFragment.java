package com.aleaf.gablestones;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aleaf.gablestones.data.StoneContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MissionFragment extends Fragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MissionFragment";

    /** Identifier for the stone data loader */
    private static final int STONE_LOADER = 0;


    /** Adapter for the ListView */
    StoneCursorAdapter mCursorAdapter;

    int mPositionClicked;

    String mLanguage;
    ListView mStoneListView;
    int mTourOpen;

    // Stores the scroll position of the ListView
    private static  Parcelable mListViewScrollPos = null;

    public  MainActivity main_activity;

    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment,container,false);

        // Find the ListView which will be populated with the stone data
        ListView stoneListView = (ListView) view.findViewById(R.id.list);
        mStoneListView = stoneListView;
        // Setup an Adapter to create a list item for each row of stone data in the Cursor.
        mCursorAdapter = new StoneCursorAdapter(getActivity(), null);
        stoneListView.setAdapter(mCursorAdapter);


        //Get the system language of user's device
        mLanguage = Locale.getDefault().getDisplayLanguage();


        // Setup the item click listener
        stoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to ClueDetailActivity
                Intent intent = new Intent();
                intent.setClass(getActivity(),ClueDetailActivity.class);
                intent.putExtra("Fragment", TAG);

                // From the content URI that represents the specific stone that was clicked on,
                // by appending the "id" (passed as input to this method) onto the.
                Uri currentStoneUri = ContentUris.withAppendedId(
                        StoneContract.StoneEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentStoneUri);
                intent.putExtra("Tour", mTourOpen);

                // Launch the ClueDetailActivity to display the information for the current stone.
                startActivity(intent);

                mPositionClicked = position;

                           }
        });

        // Kick off the loader
        getLoaderManager().initLoader(STONE_LOADER, null, this);

        // Display the Admob Ad
        mAdView = (AdView) view.findViewById(R.id.adViewMission);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //GEt Shared Preferences
        /*Get Tournumber from SelectTourActivity*/
        SharedPreferences tourselected = getActivity().getSharedPreferences(SelectTourActivity.PREFS_NAME, MODE_PRIVATE);
        int numberTour = tourselected.getInt("TourNbr", MODE_PRIVATE);
        Log.i("TourNbr Mission", String.valueOf(numberTour));

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Restore the ListView position
        if (mListViewScrollPos != null) {
            mStoneListView.onRestoreInstanceState(mListViewScrollPos);}
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the ListView position
        mListViewScrollPos = mStoneListView.onSaveInstanceState();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                StoneContract.StoneEntry._ID,
                StoneContract.StoneEntry.COLUMN_STONE_NAME,
                StoneContract.StoneEntry.COLUMN_STONE_ADDRESS,
                StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_NL,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_DE,
                StoneContract.StoneEntry.COLUMN_STONE_LAT,
                StoneContract.StoneEntry.COLUMN_STONE_LNG,
                StoneContract.StoneEntry.COLUMN_STONE_MATCH,
                StoneContract.StoneEntry.COLUMN_STONE_TOUR
         };
        String selection = StoneContract.StoneEntry.COLUMN_STONE_TOUR + "=?";

        /*Get mTourOpen from MainActivity to set which Tour should be displayed*/
        main_activity = (MainActivity) getActivity();
        mTourOpen = main_activity.mTourOpen;
        Log.i("Mission Tour",String.valueOf(mTourOpen));

        String[] selectionArgs = new String[]{String.valueOf(mTourOpen)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),   // Parent activity context
                                StoneContract.StoneEntry.CONTENT_URI,   // Provider content URI to query
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

}


