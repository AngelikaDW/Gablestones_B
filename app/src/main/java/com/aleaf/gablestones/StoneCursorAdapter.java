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
package com.aleaf.gablestones;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aleaf.gablestones.data.StoneContract.StoneEntry;

import java.util.ArrayList;
import java.util.Locale;

import static com.aleaf.gablestones.SelectTourActivity.PREFS_NAME;


public class StoneCursorAdapter extends CursorAdapter{
    ArrayList<String> matches = new ArrayList<>();
    public ArrayList<String> stones = new ArrayList<String>();
    private Cursor mCursor;

    public StoneCursorAdapter(Context context, Cursor c) {
        super(context,c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the stone data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current stone can be set on the name TextView
     * in the list item layout.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that I want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView runNbrTextView = (TextView) view.findViewById(R.id.runningNumber);
        TextView addressTextView = (TextView) view.findViewById(R.id.address);
        TextView housenumberTextView = (TextView) view.findViewById(R.id.housenumber);
        ImageView stoneImageView = (ImageView) view.findViewById(R.id.image_list_view);
        ImageView checkboxImageView = (ImageView) view.findViewById(R.id.checkbox);

        // Find the columns of stone attributes that I'm interested in
        int nameColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME);
        int nameNLColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_NL);
        int nameDEColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_DE);
        int runNbrColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
        int addressColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_ADDRESS);
        int housenumberColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_HOUSENUMBER);
        int matchColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_MATCH);
        int tourColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_TOUR);

        // Read the stone attributes from the Cursor for the current stone
        String stoneName = cursor.getString(nameColumnIndex);
        String stoneNameNL = cursor.getString(nameNLColumnIndex);
        String stoneNameDE = cursor.getString(nameDEColumnIndex);
        String stoneRunNbr = cursor.getString(runNbrColumnIndex);
        String stoneAddress = cursor.getString(addressColumnIndex);
        String stoneHousenumber = cursor.getString(housenumberColumnIndex);
        String stoneMatch = cursor.getString(matchColumnIndex);
        String tourNbr = cursor.getString(matchColumnIndex);

        //Get the system language of user's device
        String language = Locale.getDefault().getLanguage();

        // Update the TextViews with the attributes for the current stone
        if (language.equals("en")) {
            nameTextView.setText(stoneName);
        } else if (language.equals("nl")) {
            nameTextView.setText(stoneNameNL);
        } else if (language.equals("de")) {
            nameTextView.setText(stoneNameDE);
        }
        runNbrTextView.setText(stoneRunNbr);
        addressTextView.setText(stoneAddress);
        housenumberTextView.setText(stoneHousenumber);


        // Set image in the detail view from drawable folder, based on the running Number
        // as extracted from the database

        /*Get Tournumber from SelectTourActivity*/
        SharedPreferences tourselected = MyApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int numberTour = tourselected.getInt("TourNbr", 0);
        //Log.i("TourNbr StoneClass", String.valueOf(numberTour));

        String uri = "@drawable/image" + numberTour + "_" + stoneRunNbr;
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        stoneImageView.setImageResource(imageResource);

        //Image of Checkbox, if the data is 0, no match yet --> unchecked box
        // if data in db is 1, location of user and stone matched --> checked box
        if (stoneMatch.equals("0")) {
            checkboxImageView.setImageResource(R.drawable.match_grey);

        } else {
            checkboxImageView.setImageResource(R.drawable.match_green);
            // Extract how many matches there are
            matches.add(stoneMatch);
        }

        //Log.i("Count stones", String.valueOf(cursor.getCount()));
        int countStones = cursor.getCount();
        SharedPreferences settings = MyApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("countStones", countStones);
        editor.commit();


        }

    }

