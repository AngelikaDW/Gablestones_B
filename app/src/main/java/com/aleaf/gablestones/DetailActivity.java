package com.aleaf.gablestones;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aleaf.gablestones.data.StoneContract;
import com.aleaf.gablestones.data.StoneDbHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    static int mNumberTour;
    private Uri mCurrentStoneUri;
    int mPosition;

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
    private AdView mAdView;

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

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentStoneUri = intent.getData();
        mPosition = 0;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("Fragment")) {
                mPosition = bundle.getInt("Fragment");
            }
        }

        queryDatabase();
        //Open Fragment, that was selected in the Mission Activity
        openFragment(mPosition);

        // Display AdMob Banner Ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mission, menu);
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
    public static class PlaceholderFragment extends Fragment implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            com.google.android.gms.location.LocationListener {

        public DetailActivity detail_activity;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        protected static final String TAG = "Location Services";
        protected GoogleApiClient mGoogleApiClient;

        Location mCurrentLocation;
        int mMatchResult;
        int mRunNbrStone;

        protected LocationRequest mLocationRequest;

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
            int mNumberTour = DetailActivity.mNumberTour;

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            //Find all views and populate views with data from ArrayList
            TextView runNbrtextView = (TextView) rootView.findViewById(R.id.tv_stone_runNbr);
            TextView nameTextView = (TextView) rootView.findViewById(R.id.tv_stone_name);
            TextView addressTextView = (TextView) rootView.findViewById(R.id.tv_stone_address);
            TextView descriptionTextView = (TextView) rootView.findViewById(R.id.tv_stone_description);
            descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
            final ImageView stoneImageView = (ImageView) rootView.findViewById(R.id.imageView_stone_detail);
            Button showMapButton = (Button) rootView.findViewById(R.id.button_show_on_map);

            // runNbrArrayList to extract from Arraylist (Arraylist starts with 0, runNbr Stone with 1)
            int runNbrArrayList = (getArguments().getInt(ARG_SECTION_NUMBER) - 1);
            mRunNbrStone = (getArguments().getInt(ARG_SECTION_NUMBER));

            runNbrtextView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            String name = stonesArrayList.get(runNbrArrayList).toString().split(",, ")[1];
            nameTextView.setText(name);

            String address = stonesArrayList.get(runNbrArrayList).toString().split(",, ")[2];
            String housenumber = stonesArrayList.get(runNbrArrayList).toString().split(",, ")[3];
            addressTextView.setText(address + " " + housenumber);

            String description = stonesArrayList.get(runNbrArrayList).toString().split(",, ")[4];
            descriptionTextView.setText(description);

            String uri = "@drawable/image" + String.valueOf(mNumberTour) + "_" + String.valueOf(mRunNbrStone);
            int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
            stoneImageView.setImageResource(imageResource);

            final int matchStone = Integer.parseInt(stonesArrayList.get(runNbrArrayList).toString().split(",, ")[7]);
            final Double latStone = Double.parseDouble(stonesArrayList.get(runNbrArrayList).toString().split(",, ")[5]);
            final Double lngStone = Double.parseDouble(stonesArrayList.get(runNbrArrayList).toString().split(",, ")[6]);


            buildGoogleApiClient();

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                                    latStone, lngStone);
                    // depending if user is close enough to the gable stone, the database is being
                    // updated and the image in the ListView in the MissionActivity is changed from
                    // unchecked to checked box

                    checkNbrMatches();
                }
            });

            // When Button "show on Map" is clicked, open the info window of the marker in the
            // MapsActivity
            showMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(getContext(), MapsActivity.class);
                    mapIntent.putExtra("Run#", mRunNbrStone);
                    mapIntent.putExtra("LocMatch", matchStone);
                    startActivity(mapIntent);
                }
            });

            //Set OnClickListener on the ImageView to open full screen image
            //https://github.com/juliomarcos/ImageViewPopUpHelper
            stoneImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewPopUpHelper.enablePopUpOnClick(getActivity(), stoneImageView,
                                                            stoneImageView.getDrawable());
                    Toast.makeText(getActivity(), getText(R.string.click_on_img),
                                   Toast.LENGTH_SHORT).show();
                }
            });


            return rootView;
        }

        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        @Override
        public void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        /**
         * Runs when a GoogleApiClient object successfully connects.
         */
        @Override
        public void onConnected(Bundle connectionHint) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000*30); //every 30 seconds
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);


        }

        @Override
        public void onLocationChanged(Location location) {
            //Log.i(TAG, location.toString());
            mCurrentLocation = location;
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
            // onConnectionFailed.
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }

        /*
        * Called by Google Play services if the connection to GoogleApiClient drops because of an
        * error.
        */
        public void onDisconnected() {
            //Log.i(TAG, "Disconnected");
        }

        @Override
        public void onConnectionSuspended(int cause) {
            // The connection to Google Play services was lost for some reason. We call connect() to
            // attempt to re-establish the connection.
            Log.i(TAG, "Connection suspended");
            mGoogleApiClient.connect();
        }

         /*
         * Compares the current Location (Lat and Lng) of user with the location of the stone as saved in db
         * if distance is less than 50m match (1) gets written into db
         * if distance is more than 50m db is not updated*/
        public void distanceBetween(double startLatitude, double startLongitude, double endLatitude,
                                    double endLongitude) {

            float[] distance = new float[1];
            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distance);
            if (distance[0] < 50.0) {
                Toast.makeText(getContext(), getString(R.string.stone_located),
                               Toast.LENGTH_SHORT).show();
                mMatchResult = 1;
            } else {
                Toast.makeText(getContext(), getString(R.string.stone_too_far),
                               Toast.LENGTH_LONG).show();
                mMatchResult = 0;
            }
            updateStoneInDb();

        }

        // Get update from location of user and save new matcg value to stones database
        public void updateStoneInDb() {

            StoneDbHelper dBHelper = new StoneDbHelper(getContext());
            SQLiteDatabase db = dBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StoneContract.StoneEntry.COLUMN_STONE_MATCH, mMatchResult);
            String selection = StoneContract.StoneEntry.COLUMN_STONE_TOUR + "=?" + "AND " + StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(mNumberTour), String.valueOf(mRunNbrStone)};

            db.update(StoneContract.StoneEntry.TABLE_NAME, values, selection , selectionArgs);

        }

        // Get Number of Matches, if all stones  are match = 1 --> open Confetti Activity
        public void checkNbrMatches() {
            StoneDbHelper dbHelper = new StoneDbHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
                    StoneContract.StoneEntry._ID,
                    StoneContract.StoneEntry.COLUMN_STONE_TOUR,
                    StoneContract.StoneEntry.COLUMN_STONE_MATCH};
            String selection = StoneContract.StoneEntry.COLUMN_STONE_TOUR + "=?" + "AND " + StoneContract.StoneEntry.COLUMN_STONE_MATCH + "=?";
            String[] selectionArgs = new String[]{String.valueOf(mNumberTour), String.valueOf(StoneContract.StoneEntry.MATCH_TRUE)};

            Cursor cursor = db.query(StoneContract.StoneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if(cursor.getCount()>=20) {
                Intent confettiIntent = new Intent(getContext(), ConfettiActivity.class);
                startActivity(confettiIntent);
            } else {}
            cursor.close();


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
            String address = cursor.getString(addresColumnIndex);
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
            switch (language) {
                case "de":
                    name = stoneNameDE;
                    description = descriptionDE;
                    break;
                case "nl":
                    name = stoneNameNL;
                    description = descriptionNL;
                    break;
                default:
                    break;
            }

            mStonesArrayList.add(run + ",, " + name + ",, " + address + ",, " + housenumber + ",, " + description + ",, " + lat + ",, " + lng + ",, " + match);
        }
        cursor.close();
    }

    // Open Fragment based on selected Stone in Mission Activity
    public void openFragment(int position) {
        mViewPager.setCurrentItem(position, true);
    }

}