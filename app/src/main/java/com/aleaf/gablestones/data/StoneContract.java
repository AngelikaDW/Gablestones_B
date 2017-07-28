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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Gablestones app.
 */

public class StoneContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StoneContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.aleaf.gablestones";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_STONES = "stones"; //String PATH_PETS = "pets";

    public static final String PATH_STONES_EN = "stones_en";
    public static final String PATH_STONES_DE = "stones_de";
    public static final String PATH_STONES_NL = "stones_nl";


    /**
     * Inner class that defines constant values for the stones database table.
     * Each entry in the table represents a single pet.
     */
    public static final class StoneEntry implements BaseColumns {
        /**
         * The content URI to access the stones data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STONES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of stones.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STONES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STONES;


        /**
         * Name of database table for stones
         */
        public final static String TABLE_NAME = "stones";


        /*
        * Name of database table for stones_en
        * */
        public final static String TABLE_NAME_EN = "stones_en";

        /*
        * Name of database table for stones german translation
        * */
        public final static String TABLE_NAME_DE = "stones_de";

        /*
        * Name of database table for stones dutch translation
        * */
        public final static String TABLE_NAME_NL = "stones_nl";


        /**
         * Unique ID number for the stone (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the stone.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_STONE_NAME = "name";
        /**
         * Address of the stone.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_STONE_ADDRESS = "street";

        /**
         * Housenumber of the stone.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_STONE_HOUSENUMBER = "housenumber";

        /**
         * Description of the stone
         * <p>
         *     Type: Text
         * </p>
         */
        public final static String COLUMN_STONE_DESCRIPTION = "description";

        /*
        * running Number of the stone
        * <p>
        *     Type: INTEGER
        *     */
        public final static String COLUMN_STONE_RUNNINGNUMBER ="runningNumber";


    }

}