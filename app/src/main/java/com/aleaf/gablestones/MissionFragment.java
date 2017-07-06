package com.aleaf.gablestones;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;



public class MissionFragment extends ListFragment {
    private static final String TAG = "MissionFragment";

    private Button btnTEST;
    private Cursor c = null;

    @Override
    public void onActivityCreated(Bundle icicle) {
        //super.onActivityCreated(savedInstanceState);
        super.onActivityCreated(icicle);

        final DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        Toast.makeText(getActivity(), "DB Successfully Imported", Toast.LENGTH_SHORT).show();

        ArrayList<String> addresslist = new ArrayList<String>();
        ArrayList<String> description = new ArrayList<String>();

        c = myDbHelper.query("STONES", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                addresslist.add(c.getString(1)); //this adds an element to the list.
                description.add(c.getString(4));
//                Toast.makeText(getActivity(),
//                        "_id: " + c.getString(0) + "\n" +
//                                "ADDRESS: " + c.getString(1) + "\n" +
//                                "CATEGORY: " + c.getString(2) + "\n" +
//                                "HOUSENR:  " + c.getString(3),
//                        Toast.LENGTH_SHORT).show();

            } while (c.moveToNext());
        }


        MyStonesAdapter adapter = new MyStonesAdapter(getContext(), addresslist);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
          //      R.layout.list_item, R.id.street, description);
                //android.R.layout.simple_list_item_1, addresslist);
        setListAdapter(adapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement INTEND new Activity to open
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_SHORT).show();
        Intent clueDetailIntent = new Intent(getContext(), ClueDetailActiviy.class);
        startActivity(clueDetailIntent);
    }


}
