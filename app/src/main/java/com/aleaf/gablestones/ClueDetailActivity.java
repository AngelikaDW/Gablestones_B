package com.aleaf.gablestones;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aleaf.gablestones.data.StoneContract.StoneEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/*
* Shows the user detailed information and image about the stone selected in the overview
* */
public class ClueDetailActivity extends AppCompatActivity implements
                android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                com.google.android.gms.location.LocationListener{

    /**
     * Identifier for the stone data loader
     */
    private static final int EXISTING_STONE_LOADER = 0;


    /**
     * Content URI for the existing stone (null if it's a new stone)
     */
    private Uri mCurrentStoneUri;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST = 1;


    /*Textfield and ImageView for name, address, housenumber, description and running Number and
    image of the stone*/

    private TextView mNameText;
    private TextView mAddressText;
    private TextView mHousenumberText;
    private TextView mDescriptionText;
    private TextView mRunningNumberText;
    private ImageView mClueImage;
    public int mRun;
    public Double mLat;
    public Double mLng;
    private TextView mCurrentLocationText;
    Location mCurrentLocation;
    int mMatchResult;
    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mStoneHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail_activiy);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentStoneUri = intent.getData();

        if (mCurrentStoneUri != null) {
            setTitle(R.string.stone_detail);
            getSupportLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);

        }
        // Define GoogleApiClient which was initiated onStart()
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        configureLocationUpdates();

        // Find all relevant views that we will need to show content
        mNameText = (TextView) findViewById(R.id.clueName);
        mAddressText = (TextView) findViewById(R.id.clueAddress);
        mHousenumberText = (TextView) findViewById(R.id.clueHousenumber);
        mDescriptionText = (TextView) findViewById(R.id.clueDescription);
        mRunningNumberText = (TextView) findViewById(R.id.clueRunningNumber);
        mCurrentLocationText = (TextView) findViewById(R.id.currentLocMatch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClueImage = (ImageView) findViewById(R.id.image_clue_detail);

        // Set up FAB Button: when clicked gets current Location and calculates distance between
        // current location of user and location of stone
        // Depending on the distance, the db is being updated
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestLocationUpdate();
                /*WATCH OUT CURRRENTLY 1000m distance!*/
                distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        mLat, mLng);
                // depending if user is close enough to the gable stone, the database is being
                // updated and the image in the ListView in the MissionFragment is changed from
                // unchecked to checked box
                updateStone();

//                Snackbar.make(view, "You found it - congrats!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains columns in all languages from the stones table
        String[] projection = {
                StoneEntry._ID,
                StoneEntry.COLUMN_STONE_NAME,
                StoneEntry.COLUMN_STONE_NAME_DE,
                StoneEntry.COLUMN_STONE_NAME_NL,
                StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneEntry.COLUMN_STONE_DESCRIPTION,
                StoneEntry.COLUMN_STONE_DESCRIPTION_DE,
                StoneEntry.COLUMN_STONE_DESCRIPTION_NL,
                StoneEntry.COLUMN_STONE_ADDRESS,
                StoneEntry.COLUMN_STONE_HOUSENUMBER,
                StoneEntry.COLUMN_STONE_LAT,
                StoneEntry.COLUMN_STONE_LNG,
                StoneEntry.COLUMN_STONE_MATCH};

        return new CursorLoader(this,
                mCurrentStoneUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME);
            int nameNLColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_NL);
            int nameDEColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_NAME_DE);
            int runColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
            int descColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION);
            int descNLColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION_NL);
            int descDEColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_DESCRIPTION_DE);
            int addresColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_ADDRESS);
            int houseColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_HOUSENUMBER);
            int latColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_LAT);
            int lngColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_LNG);
            int matchColumnIndex = cursor.getColumnIndex(StoneEntry.COLUMN_STONE_MATCH);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String stoneNameNL = cursor.getString(nameNLColumnIndex);
            String stoneNameDE = cursor.getString(nameDEColumnIndex);
            final int run = cursor.getInt(runColumnIndex);
            String description = cursor.getString(descColumnIndex);
            String descriptionNL = cursor.getString(descNLColumnIndex);
            String descriptionDE = cursor.getString(descDEColumnIndex);
            String addres = cursor.getString(addresColumnIndex);
            int housenumber = cursor.getInt(houseColumnIndex);
            double lat = cursor.getDouble(latColumnIndex);
            mLat = lat;
            double lng = cursor.getDouble(lngColumnIndex);
            mLng = lng;
            int match = cursor.getInt(matchColumnIndex);

            // Update the views on the screen with the values from the database
            // depended on the language of the device select EN, NL or DE content
            // Get the system language of user's device
            String language = Locale.getDefault().getLanguage();

            if (language.equals("en")) {
                mNameText.setText(name);
                mDescriptionText.setText(description);
            } else if (language.equals("nl")) {
                mNameText.setText(stoneNameNL);
                mDescriptionText.setText(descriptionNL);
            } else if (language.equals("de")) {
                mNameText.setText(stoneNameDE);
                mDescriptionText.setText(descriptionDE);
            }

            //No translation required for this fields
            mRunningNumberText.setText(Integer.toString(run));
            mAddressText.setText(addres);
            mHousenumberText.setText(Integer.toString(housenumber));
            mCurrentLocationText.setText(Integer.toString(match));

            // Set image in the detail view from drawable folder, based on the running Number
            // as extracted from the database
            String uri = "@drawable/image" + Integer.toString(run);
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            mClueImage.setImageResource(imageResource);

            //Set OnClickListener on the ImageView to open full screen image
            //https://github.com/juliomarcos/ImageViewPopUpHelper
            mClueImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   ImageViewPopUpHelper.enablePopUpOnClick(ClueDetailActivity.this, mClueImage,
                           mClueImage.getDrawable());
                    Toast.makeText(ClueDetailActivity.this, getText(R.string.click_on_img),
                            Toast.LENGTH_SHORT).show();
                }
            });

            mRun = run;
            // When Button "show on Map" is clicked, open the info window of the marker in the
            // MapFragment
            Button markerLocation = (Button) findViewById(R.id.locate_on_map);
            markerLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(ClueDetailActivity.this,MainActivity.class);
                    mapIntent.putExtra("Fragment_ID", 1);
                    mapIntent.putExtra("Run#", run);
                    startActivity(mapIntent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameText.setText("");
        mRunningNumberText.setText("");
        mAddressText.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Connect the Client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        //Disconnect the Client
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void requestLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //This check necessary for API 23 or higher (Android 6.0)
        int permissionCheck = ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            }

        } else {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    requestLocationUpdate();
                } else {
                    //txtOutput.setText("No permission to get the location");
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.v("Loc in DetailClue", location.toString());
        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.i("LatLn in DetailClue", currentLoc.toString());
        mCurrentLocation = location;
    }
    private void configureLocationUpdates() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(15*10000); //Update location every 15 sec
    }

    // Compares the current Location (Lat and Lng) of user with the location of the stone
    // as saved in db
    // if distance is less than 1000m (!!!) positive result gets written into db
    // if distance is more than !!!! 1000m !!!! db is not updated
    public void distanceBetween(double startLatitude,
                                double startLongitude,
                                double endLatitude,
                                double endLongitude) {

        float[] distance = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distance);
        if (distance[0] < 1000.0) {
            Log.i("Is user within 1000m?", "YES");
            Toast.makeText(this, getString(R.string.stone_located),
                    Toast.LENGTH_SHORT).show();
            mMatchResult = 1;
        } else {
            Log.i("is user near?", "No");
            Toast.makeText(this, getString(R.string.stone_too_far),
                    Toast.LENGTH_LONG).show();
            mMatchResult = 0;
        }
    }
    // Get update from location of user and save it to stones database
    private void updateStone(){
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(StoneEntry.COLUMN_STONE_MATCH, mMatchResult);

        //Update the row of the stone with 0 or 1 in COLUMN_STONE_MATCH
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because mCurrentStoneUri will already identify the correct row in the database that
        // we want to modify.
        int rowsAffected = getContentResolver().update(mCurrentStoneUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, "UPDATE FAILED",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
//            Toast.makeText(this, "DB updated successfully",
//                    Toast.LENGTH_SHORT).show();
        }
    }

}

