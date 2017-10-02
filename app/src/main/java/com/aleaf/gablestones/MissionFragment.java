package com.aleaf.gablestones;

import android.content.ContentUris;
import android.net.Uri;
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

import java.util.Locale;


public class MissionFragment extends Fragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MissionFragment";

    /** Identifier for the stone data loader */
    private static final int STONE_LOADER = 0;

    /** Adapter for the ListView */
    StoneCursorAdapter mCursorAdapter;

    int mPositionClicked;

    String mLanguage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment,container,false);
        // Find the ListView which will be populated with the pet data
        ListView stoneListView = (ListView) view.findViewById(R.id.list);
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

                // Launch the ClueDetailActivity to display the information for the current stone.
                startActivity(intent);

                Log.i("Position in List", String.valueOf(position));
                mPositionClicked = position;


            }
        });
//        //ToDo: Scroll doesn't work
//        //Scroll to the position clicked before doesn't work!!
//        Log.i("MissionFrag Pos", String.valueOf((mPositionClicked)));
//        stoneListView.smoothScrollToPosition(mPositionClicked);

        // Kick off the loader
        getLoaderManager().initLoader(STONE_LOADER, null, this);

        return view;
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
                StoneContract.StoneEntry.COLUMN_STONE_NAME_DE
         };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),   // Parent activity context
                StoneContract.StoneEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
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


