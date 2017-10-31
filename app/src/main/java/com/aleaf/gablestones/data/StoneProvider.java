/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aleaf.gablestones.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;


/**
 * {@link ContentProvider} for GableStones app.
 */
public class StoneProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = StoneProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the stones table */
    private static final int STONES = 100;

    /** URI matcher code for the content URI for a single stone in the stones table */
    private static final int STONE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. !!!!This URI is used to provide access to MULTIPLE rows
        // of the pets table.!!!!
        sUriMatcher.addURI(StoneContract.CONTENT_AUTHORITY, StoneContract.PATH_STONES, STONES);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(StoneContract.CONTENT_AUTHORITY, StoneContract.PATH_STONES + "/#", STONE_ID);
    }

    /** Database helper object */
    private StoneDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new StoneDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STONES:
                // For the STONES code, query the stones table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(
                        StoneContract.StoneEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STONE_ID:
                // For the STONE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = StoneContract.StoneEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the stones table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(
                        StoneContract.StoneEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STONES:
                return updateStone(uri, values, selection, selectionArgs);
            case STONE_ID:
                // For the STONE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = StoneContract.StoneEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStone(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }
    /**
     * Update stones in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateStone(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the COLUMN_STONE_MATCH key is present, check tha the value is not null.
        if (values.containsKey(StoneContract.StoneEntry.COLUMN_STONE_MATCH)) {
            Integer match = values.getAsInteger(StoneContract.StoneEntry.COLUMN_STONE_MATCH);
            if (match == null) {
                throw new IllegalArgumentException("Requires input");
            }
        }
            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }
            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(StoneContract.StoneEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;

        //return 0;
    }


}