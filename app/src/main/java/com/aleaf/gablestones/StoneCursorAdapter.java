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
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aleaf.gablestones.data.StoneContract;


public class StoneCursorAdapter extends CursorAdapter{
    public StoneCursorAdapter(Context context, Cursor c) {
        super(context,c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView runNbrTextView = (TextView) view.findViewById(R.id.runningNumber);
        TextView addressTextView = (TextView) view.findViewById(R.id.address);
        TextView housenumberTextView = (TextView) view.findViewById(R.id.housenumber);

        // Find the columns of stone attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME);
        int runNbrColumnIndex = cursor.getColumnIndex(
                StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
        int addressColumnIndex = cursor.getColumnIndex(
                StoneContract.StoneEntry.COLUMN_STONE_ADDRESS);
        int housenumberColumnIdex = cursor.getColumnIndex(
                StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER);

        // Read the pet attributes from the Cursor for the current stone
        String stoneName = cursor.getString(nameColumnIndex);
        String stoneRunNbr = cursor.getString(runNbrColumnIndex);
        String stoneAddress = cursor.getString(addressColumnIndex);
        String stoneHousenumber = cursor.getString(housenumberColumnIdex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(stoneName);
        runNbrTextView.setText(stoneRunNbr);
        addressTextView.setText(stoneAddress);
        housenumberTextView.setText(stoneHousenumber);
    }
}
