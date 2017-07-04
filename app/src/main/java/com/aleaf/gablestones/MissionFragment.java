package com.aleaf.gablestones;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

public class MissionFragment extends Fragment {
    private static final String TAG = "MissionFragment";

    private Button btnTEST;
    private Cursor c = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mission_fragment, container, false);
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

        btnTEST = (Button) view.findViewById(R.id.btnTEST2);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = myDbHelper.query("STONES", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    do {
                        Toast.makeText(getActivity(),
                                "_id: " + c.getString(0) + "\n" +
                                        "ADDRESS: " + c.getString(1) + "\n" +
                                        "CATEGORY: " + c.getString(2) + "\n" +
                                        "HOUSENR:  " + c.getString(3),
                                Toast.LENGTH_LONG).show();
                    } while (c.moveToNext());
                }
            }

        });
        // Find ListView to populate
        ListView lvItems = (ListView) view.findViewById(R.id.list);
        // Setup cursor adapter using cursor from last step
        StonesCursorAdapter stonesAdapter = new StonesCursorAdapter(getActivity(), c);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(stonesAdapter);
        return view;
    }

}
