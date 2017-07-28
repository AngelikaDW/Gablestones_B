package com.aleaf.gablestones;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment,container,false);
        // Find the ListView which will be populated with the pet data
        ListView stoneListView = (ListView) view.findViewById(R.id.list);
        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new StoneCursorAdapter(getActivity(), null);
        stoneListView.setAdapter(mCursorAdapter);

        //Get the system language of user's device
        String language = Locale.getDefault().getDisplayLanguage();
        Log.i("Device MissionFrg", language);

        // Setup the item click listener
        stoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(getActivity(), ClueDetailActivity.class);

                /*// Attempt to go back to Mission Fragment TODO
                intent.putExtra("fragmentName", 2);*/

                // Form the content URI that represents the specific stone that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentStoneUri = ContentUris.withAppendedId(
                        StoneContract.StoneEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentStoneUri);

                // Launch the ClueDetailActivity to display the information for the current stone.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(STONE_LOADER, null, this);

        // Attempt to go back to MainActivity/Mission Fragment upon click on Back Arrow TODO
        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.commit();*/

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
                StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER
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
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

}


