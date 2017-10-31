package com.aleaf.gablestones;

import android.content.Intent;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aleaf.gablestones.data.StoneContract;

public class IntroFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = "IntroFragment";

    private Button btnTEST;
    /**
     * Identifier for the stone data loader
     */
    private static final int EXISTING_STONE_LOADER = 0;
    int mNbrMatches;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_fragment,container,false);

        // Kick off the loader
        getLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains columns in all languages from the stones table
        String[] projection = {
                StoneContract.StoneEntry._ID,
                StoneContract.StoneEntry.COLUMN_STONE_MATCH};
        String selection = StoneContract.StoneEntry.COLUMN_STONE_MATCH + "=?";
        String[] selectionArgs = new String[]{String.valueOf(StoneContract.StoneEntry.MATCH_TRUE)};

        return new CursorLoader(getActivity(),
                StoneContract.StoneEntry.CONTENT_URI, // Parent activity context
                projection, // Columns to include in the resulting Cursor
                selection, // No selection clause
                selectionArgs, // Selection Arguments
                null);  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // When user located all gable stones (total 20) ConfettiActivity opens
        mNbrMatches = cursor.getCount();
        Log.i("Number of Matches True", String.valueOf(cursor.getCount()));

        if (mNbrMatches >=20) {
            Intent confettiIntent = new Intent(getContext(), ConfettiActivity.class);
            startActivity(confettiIntent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
