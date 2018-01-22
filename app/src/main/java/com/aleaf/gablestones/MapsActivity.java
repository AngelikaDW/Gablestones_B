package com.aleaf.gablestones;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.aleaf.gablestones.data.StoneContract;
import com.aleaf.gablestones.data.StoneDbHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    int mNumberTour;
    private Marker mMarker;
    private LatLng mMarkerPosition;


    //save markers in list
    private ArrayList<Marker> markersLibrary = new ArrayList<>();
    private AdView mAdView;
    private int mRunNbr;
    private int mMatch;

    /**
     * Database helper object
     */
    public StoneDbHelper mDbHelper;
    ArrayList<String> stonesArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Get Tournumber from SelectTourActivity
        SharedPreferences tourselected = getSharedPreferences(SelectTourActivity.PREFS_NAME, Context.MODE_PRIVATE);
        mNumberTour = tourselected.getInt("TourNbr", MODE_PRIVATE);

        /*Intent sent by ClueDetailActivity gets information about runNbr of Gablestone to open infoWindow*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("Run#")) {
                mRunNbr = bundle.getInt("Run#");

                if (bundle.containsKey("LocMatch")) {
                    mMatch = bundle.getInt("LocMatch");
                }
            }
        }

        queryDatabase();

        // TODO: implement Banner AD
//         // Display the Admob Ad
//        mAdView = (AdView) findViewById(R.id.adViewMapAct);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        CameraPosition AmsterdamCenter = CameraPosition.builder()
                .target(new LatLng(52.378777, 4.892577))
                .zoom(14)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(AmsterdamCenter));

        drawMarkers();

        mMap.setOnMyLocationButtonClickListener(this);
        //mMap.setOnMyLocationClick(this);
        enableMyLocation();
        //onLocationChanged();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                //Gets title of marker, splits it and parses into an int
                String markerTitle = marker.getTitle();
                String[] separated = markerTitle.split(" ");
                int markerRunnbr = Integer.parseInt(separated[0]);

                // Opens the Detail activity with the corresponding stone (based on Nbr)
                Intent i = new Intent(MapsActivity.this, DetailActivity.class);
                // From the content URI that represents the specific stone that was clicked on,
                // by appending the "id" (passed as input to this method) onto the Detail intent
                Uri currentStoneUri = ContentUris.withAppendedId(
                        StoneContract.StoneEntry.CONTENT_URI, markerRunnbr);
                i.putExtra("Fragment", markerRunnbr-1);

                // Set the URI on the data field of the intent
                i.setData(currentStoneUri);
                startActivity(i);
            }
        });
        openInfoWindow();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                                              Manifest.permission.ACCESS_FINE_LOCATION, true
            );
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, R.string.locate_on_map, Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                                                Manifest.permission.ACCESS_FINE_LOCATION
        )) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    //Getting current Location LatLng of device, to be used in checking if gablestone has been found/*
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    private void configureLocationUpdates() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(15 * 10000); //Update location every 15 sec
    }

    // Opens InfoWindow when coming from ClueDetailActivity
    public void openInfoWindow() {
        int runNbr = mRunNbr;
        if (runNbr == 0) {
            //do nothing
        } else {
            int runNbrAdjusted = runNbr - 1;
            markersLibrary.get(runNbrAdjusted).showInfoWindow();
            Log.i("position of Marker", String.valueOf(markersLibrary.get(runNbrAdjusted).getPosition()));
            LatLng markerPosition = markersLibrary.get(runNbrAdjusted).getPosition();
            double markerPosLat = markerPosition.latitude;
            double markerPosLng = markerPosition.longitude;

            CameraPosition markerClicked = CameraPosition.builder()
                    .target(new LatLng(markerPosLat, markerPosLng))
                    .zoom(16)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(markerClicked));
        }
    }

    // Queries DB to create ArrayList where drawMarkers() reads from and gets markers on the map
    private void queryDatabase() {
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
            double lat = cursor.getDouble(latColumnIndex);
            double lng = cursor.getDouble(lngColumnIndex);
            final int match = cursor.getInt(matchColumnIndex);

            // Update the content displayed in the infowindow with the values from the database
            // depended on the language of the device select EN, NL or DE content
            // Get the system language of user's device
            String language = Locale.getDefault().getLanguage();
            switch (language) {
                case "en":
                    name = name;
                    break;
                case "nl":
                    name = stoneNameNL;
                    break;
                case "de":
                    name = stoneNameDE;
                    break;
            }
            stonesArrayList.add(run + ", " + name + ", " + addres + ", " + housenumber + ", " + lat + ", " + lng + ", " + match);
        }
        cursor.close();

    }
    // Draw markers to map
    private void drawMarkers() {
        for (int i = 0; i < stonesArrayList.size(); i++) {
            int run = Integer.parseInt(stonesArrayList.get(i).split(",")[0]);
            String name = stonesArrayList.get(i).split(",")[1];
            String address = stonesArrayList.get(i).split(",")[2];
            String housenumber = stonesArrayList.get(i).split(",")[3];
            double lat = Double.parseDouble(stonesArrayList.get(i).split(",")[4]);
            double lng = Double.parseDouble(stonesArrayList.get(i).split(",")[5]);
            String match = stonesArrayList.get(i).split(",")[6].substring(1, 2);
            String uri;
            if (match.equals("1")){
                uri = "@drawable/marker_green" + Integer.toString(run);
            } else {
                uri = "@drawable/marker_blue" + Integer.toString(run);
            }

            int markerResource = getResources().getIdentifier(uri, null, this.getPackageName());

            mMarker = mMap.addMarker(new MarkerOptions()
                                             .position(new LatLng(lat, lng))
                                             .title(run + " " + name)
                                             .snippet(address + " " + housenumber)
                                     .icon(BitmapDescriptorFactory.fromResource(markerResource))
            );
            //Add marker to ArrayList to be able to call later in openInfoWindow()
            markersLibrary.add(mMarker);
        }
    }
}






