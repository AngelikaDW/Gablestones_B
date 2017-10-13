package com.aleaf.gablestones;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.aleaf.gablestones.data.StoneContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MapFragment extends Fragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    /**
     * Identifier for the stone data loader
     */
    private static final int EXISTING_STONE_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    StoneCursorAdapter mCursorAdapter;

    //for location extraction
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mCurrentLocation;

    //private static final String TAG = "MapFragment";
    private final String LOG_TAG = AppCompatActivity.class.getSimpleName();
    GoogleMap mGoogleMap;
    View mView;
    MapView mMapView;
    //save markers in list
    private ArrayList<Marker> markersLibrary = new ArrayList<>();

    public MainActivity main_activity;
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


    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_fragment, container, false);

        // Kick off the loader
        getLoaderManager().initLoader(EXISTING_STONE_LOADER, null, this);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        configureLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        LatLng Amsterdam = new LatLng(52.376376, 4.887343);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        CameraPosition AmsterdamCenter = CameraPosition.builder()
                .target(new LatLng(52.378777, 4.892577))
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(AmsterdamCenter));

        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }


    private void requestLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    //     @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragment resumes.
            mPermissionDenied = true;
        }
    }

    public void onResume() {
        super.onResume();
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
                .newInstance(true).show(getChildFragmentManager(), "dialog");
    }


    public void openInfoWindow() {
         /*Open InfoWindow of Marker that has been viewed in ClueDetailActivity
        * Based on runnumber which is sent by intent from ClueDetailActivity to MainActivity
        * MainActivity then stores the runNbr in a global Variable mRunNbr, which is accessed by
        * MapFragment
        * https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android
        * As the ArrayList starts with index 0, and the runNbr with 1, adjusted RunNbr (-1)!
        * */
        main_activity = (MainActivity) getActivity();

        int runNbr = main_activity.mRunNbr;
        if (runNbr == 0) {
            //do nothing
        } else {
            int runNbrAdjusted = runNbr - 1;
            markersLibrary.get(runNbrAdjusted).showInfoWindow();
        }
    }

    private void configureLocationUpdates() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); //Update location every 10 sec
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //This check necessary for API 23 or higher (Android 6.0)
        int permissionCheck = ActivityCompat.checkSelfPermission(
                getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            }

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    requestLocationUpdate();
                } else {
                    Log.i("No permission", "sorry");
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }

    /*Getting current Location LatLng of device, to be used in checking if gablestone has been found*/
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStart() {
        super.onStart();
        //Connect the Client
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        //Disconnect the Client
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains columns in all languages from the stones table
        String[] projection = {
                StoneContract.StoneEntry._ID,
                StoneContract.StoneEntry.COLUMN_STONE_NAME,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_DE,
                StoneContract.StoneEntry.COLUMN_STONE_NAME_NL,
                StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_ADDRESS,
                StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER,
                StoneContract.StoneEntry.COLUMN_STONE_LAT,
                StoneContract.StoneEntry.COLUMN_STONE_LNG,
                StoneContract.StoneEntry.COLUMN_STONE_MATCH};

        return new CursorLoader(getActivity(),
                StoneContract.StoneEntry.CONTENT_URI,
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

        Marker m = null;
        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME);
            int nameNLColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME_NL);
            int nameDEColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_NAME_DE);
            int runColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_RUNNINGNUMBER);
            int addresColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_ADDRESS);
            int houseColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_HOUSENUMBER);
            int latColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_LAT);
            int lngColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_LNG);
            int matchColumnIndex = cursor.getColumnIndex(StoneContract.StoneEntry.COLUMN_STONE_MATCH);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String stoneNameNL = cursor.getString(nameNLColumnIndex);
            String stoneNameDE = cursor.getString(nameDEColumnIndex);
            final int run = cursor.getInt(runColumnIndex);
            String addres = cursor.getString(addresColumnIndex);
            int housenumber = cursor.getInt(houseColumnIndex);
            double lat = cursor.getDouble(latColumnIndex);
            double lng = cursor.getDouble(lngColumnIndex);
            final int match = cursor.getInt(matchColumnIndex);

            /*Depended if the stone has been located by user (DB Match 0 or 1) the marker is colored blue
            * (case 0) or green (case 1)
            */
            String uri = "";
            if (match == 1) {
                uri = "@drawable/marker_green" + Integer.toString(run);
            } else {
                uri = "@drawable/marker_blue" + Integer.toString(run);
            }
            int markerResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());

            // Update the views on the screen with the values from the database
            // depended on the language of the device select EN, NL or DE content
            // Get the system language of user's device
            String language = Locale.getDefault().getLanguage();

            if (language.equals("en")) {
                name = name;
            } else if (language.equals("nl")) {
                name = stoneNameNL;
            } else if (language.equals("de")) {
                name = stoneNameDE;
            }

            // Put the markers to the map and save marker in ArrayList markersLibrary
/*            Markers downloaded from: https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_[color][character].png
            https://github.com/Concept211/Google-Maps-Markers
            then put into drawable and called from there*/
            m = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(run + " " + name)
                    .snippet(addres + " " + housenumber)
                    .icon(BitmapDescriptorFactory.fromResource(markerResource))
            );
            markersLibrary.add(m);
        }
        //Log.i("arrayList MarkerLibr", "Length:" + markersLibrary.size());
        openInfoWindow();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
