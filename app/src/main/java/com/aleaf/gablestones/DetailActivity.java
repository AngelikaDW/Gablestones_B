package com.aleaf.gablestones;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.aleaf.gablestones.data.StoneContract;
import com.aleaf.gablestones.data.StoneDbHelper;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Locale;

import javax.security.auth.login.LoginException;

public class DetailActivity extends AppCompatActivity {
    int mNumberTour;
    //save markers in list
    private ArrayList<Marker> markersLibrary = new ArrayList<>();
    /**
     * Database helper object
     */
    public StoneDbHelper mDbHelper;
    public ArrayList<String> mStonesArrayList = new ArrayList<>();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Get Tournumber from SelectTourActivity
        SharedPreferences tourselected = getSharedPreferences(SelectTourActivity.PREFS_NAME, Context.MODE_PRIVATE);
        mNumberTour = tourselected.getInt("TourNbr", MODE_PRIVATE);
        Log.i("TourNbr DetailAc", String.valueOf(mNumberTour));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        queryDatabase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.madeByColofon:
                Intent infoIntent = new Intent(DetailActivity.this, ColofonActivity.class);
                startActivity(infoIntent);
                return true;
            case R.id.intro_tour:
                Intent tourIntent = new Intent(DetailActivity.this, Introslider.class);
                startActivity(tourIntent);
                return true;
//            case R.id.confetti:
//                Intent confettiIntent = new Intent(MainActivity.this, ConfettiActivity.class);
//                startActivity(confettiIntent);
//                return true;
            case R.id.select_tour:
                Intent selectTourIntent = new Intent(DetailActivity.this, SelectTourActivity.class);
                startActivity(selectTourIntent);
                return true;
            case R.id.open_map:
                Intent openMapIntent = new Intent(DetailActivity.this, MapsActivity.class);
                startActivity(openMapIntent);
                return true;
            case R.id.open_detailFragment:
                Intent clueDetailIntent = new Intent(DetailActivity.this, MissionActivity.class);
                startActivity(clueDetailIntent);
                return true;
            default:
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.madeByColofon) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public DetailActivity detail_activity;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            detail_activity = (DetailActivity) getActivity();

            ArrayList stonesArrayList = detail_activity.mStonesArrayList;

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            TextView nameTextView = (TextView) rootView.findViewById(R.id.tv_stone_name);
            TextView descriptionTextView = (TextView) rootView.findViewById(R.id.tv_stone_description);

            int runNbr = (getArguments().getInt(ARG_SECTION_NUMBER)-1);
            String name = stonesArrayList.get(runNbr).toString().split(",, ")[2];
            nameTextView.setText(name);
            String description = stonesArrayList.get(runNbr).toString().split(",, ")[4];
            descriptionTextView.setText(description);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 20 total pages.
            return 20;
        }
    }

    // Queries DB to create ArrayList where data for textviews are stored
    public void queryDatabase() {
        //TODO: create DB Helper Class and access readable DB to get all info in the arraylist, which should then be used to:
        // - get data for Clue detail activiy (arraylist.get(i)
        mDbHelper = new StoneDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                StoneContract.StoneEntry._ID,
                StoneContract.StoneEntry.COLUMN_STONE_NAME,
                StoneContract.StoneEntry.COLUMN_STONE_ADDRESS,
                StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_NL,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_DE,
                StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION,
                StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION_DE,
                StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION_NL,
                StoneContract.StoneEntry.COLUMN_STONE_LAT,
                StoneContract.StoneEntry.COLUMN_STONE_LNG,
                StoneContract.StoneEntry.COLUMN_STONE_MATCH,
                StoneContract.StoneEntry.COLUMN_STONE_TOUR};


        String selection = StoneContract.StoneEntry.COLUMN_STONE_TOUR + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mNumberTour)};

        Cursor cursor = db.query(StoneContract.StoneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME);
        int nameNLColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME_NL);
        int nameDEColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME_DE);
        int runColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
        int addresColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_ADDRESS);
        int houseColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER);
        int descriptionColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION);
        int descriptionDEColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION_DE);
        int descriptionNLColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_DESCRIPTION_NL);
        int latColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_LAT);
        int lngColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_LNG);
        int matchColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_MATCH);

        while (cursor.moveToNext()) {
            int run = cursor.getInt(runColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String stoneNameNL = cursor.getString(nameNLColumnIndex);
            String stoneNameDE = cursor.getString(nameDEColumnIndex);
            String addres = cursor.getString(addresColumnIndex);
            int housenumber = cursor.getInt(houseColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String descriptionDE = cursor.getString(descriptionDEColumnIndex);
            String descriptionNL = cursor.getString(descriptionNLColumnIndex);
            double lat = cursor.getDouble(latColumnIndex);
            double lng = cursor.getDouble(lngColumnIndex);
            final int match = cursor.getInt(matchColumnIndex);

            // Update the content displayed in the infowindow with the values from the database
            // depended on the language of the device select EN, NL or DE content
            // Get the system language of user's device
            String language = Locale.getDefault().getLanguage();
            if (language.equals("en")) {
//                name = name;
//                description = description;
            } else if (language.equals("nl")) {
                name = stoneNameNL;
                description = descriptionNL;
            } else if (language.equals("de")) {
                name = stoneNameDE;
                description = descriptionDE;
            }
            mStonesArrayList.add(run + ",, " + name + ",, " + addres + ",, " + housenumber + ",, " + description +",, " + lat + ",, " + lng + ",, " + match);
        }

    }
}
