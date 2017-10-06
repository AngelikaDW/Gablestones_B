package com.aleaf.gablestones;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


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



public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

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


    Marker mMarkerRun1;
    Marker mMarkerRun2;
    Marker mMarkerRun3;
    Marker mMarkerRun4;
    Marker mMarkerRun5;
    Marker mMarkerRun6;
    Marker mMarkerRun7;
    Marker mMarkerRun8;
    Marker mMarkerRun9;
    Marker mMarkerRun10;
    Marker mMarkerRun11;
    Marker mMarkerRun12;
    Marker mMarkerRun13;
    Marker mMarkerRun14;
    Marker mMarkerRun15;
    Marker mMarkerRun16;
    Marker mMarkerRun17;
    Marker mMarkerRun18;
    Marker mMarkerRun19;
    Marker mMarkerRun20;


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


        /*Add markers where gable stones are located,
        number of marker = running number of gable stone*/
        addMarkersToMap();
        openInfoWindow();


        /*Change in between Fragments from Mapto Mission
        * TODO: currently the MissionFragment is on top of the Map Fragment
        * (Filling Backgrounds is covering it)TODO: need stop/replace Mapfragment properly*/

//        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                FragmentManager fm = getFragmentManager();
//                MissionFragment fragment = new MissionFragment();
//                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                fragmentTransaction.replace(R.id.map, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                MapFragment mapFragment = new MapFragment();
//                FragmentTransaction transaction = fm.beginTransaction();
//                transaction.add(mapFragment).commit();
//            }
//        });

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
                getContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)
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

    public void addMarkersToMap() {
//        Markers downloaded from: https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_[color][character].png
//        https://github.com/Concept211/Google-Maps-Markers
//        then put into drawable and called from there

        mMarkerRun1 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.379295, 4.892488))
                .title(getString(R.string.gablestone_1))
                .snippet(getString(R.string.street_address_1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue1))
        );

        mMarkerRun2 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378777, 4.892577))
                .title(getString(R.string.gablestone_2))
                .snippet(getString(R.string.street_address_2))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue2))
        );
        mMarkerRun3 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378512, 4.892351))
                .title(getString(R.string.gablestone_3))
                .snippet(getString(R.string.street_address_3))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue3))
        );

        mMarkerRun4 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378485, 4.892216))
                .title(getString(R.string.gablestone_4))
                .snippet(getString(R.string.street_address_4))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue4))
        );
        mMarkerRun5 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.377991, 4.891739))
                .title(getString(R.string.gablestone_5))
                .snippet(getString(R.string.street_address_5))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue5))
        );
        mMarkerRun6 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376880, 4.890647))
                .title(getString(R.string.gablestone_6))
                .snippet(getString(R.string.street_address_6))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue6))
        );
        mMarkerRun7 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376611, 4.889971))
                .title(getString(R.string.gablestone_7))
                .snippet(getString(R.string.street_address_7))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue7))
        );
        mMarkerRun8 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.377286, 4.887302))
                .title(getString(R.string.gablestone_8))
                .snippet(getString(R.string.street_address_8))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue8))
        );
        mMarkerRun9 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.375631, 4.885798))
                .title(getString(R.string.gablestone_9))
                .snippet(getString(R.string.street_address_9))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue9))
        );
        mMarkerRun10 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376715, 4.884386))
                .title(getString(R.string.gablestone_10))
                .snippet(getString(R.string.street_address_10))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue10))
        );
        mMarkerRun11 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376364, 4.883624))
                .title(getString(R.string.gablestone_11))
                .snippet(getString(R.string.street_address_11))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue11))
        );
        mMarkerRun12 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376299, 4.882291))
                .title(getString(R.string.gablestone_12))
                .snippet(getString(R.string.street_address_12))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue12))
        );
        mMarkerRun13 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.376503, 4.882205))
                .title(getString(R.string.gablestone_13))
                .snippet(getString(R.string.street_address_13))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue13))
        );
        mMarkerRun14 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.377482, 4.883778))
                .title(getString(R.string.gablestone_14))
                .snippet(getString(R.string.street_address_14))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue14))
        );
        mMarkerRun15 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.377441, 4.883877))
                .title(getString(R.string.gablestone_15))
                .snippet(getString(R.string.street_address_15))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue15))
        );
        mMarkerRun16 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378059, 4.881117))
                .title(getString(R.string.gablestone_16))
                .snippet(getString(R.string.street_address_16))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue16))
        );
        mMarkerRun17 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378745, 4.884467))
                .title(getString(R.string.gablestone_17))
                .snippet(getString(R.string.street_address_17))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue17))
        );
        mMarkerRun18 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.379347, 4.885459))
                .title(getString(R.string.gablestone_18))
                .snippet(getString(R.string.street_address_18))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue18))
        );
        mMarkerRun19 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.379957, 4.886873))
                .title(getString(R.string.gablestone_19))
                .snippet(getString(R.string.street_address_19))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue19))
        );
        mMarkerRun20 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.379190, 4.886257))
                .title(getString(R.string.gablestone_20))
                .snippet(getString(R.string.street_address_20))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue20))
        );
    }

    public void openInfoWindow() {
         /*Open InfoWindow of Marker that has been viewed in ClueDetailActivity
        * Based on runnumber which is sent by intent from ClueDetailActivity to MainActivity
        * MainActivity then stores the runNbr in a global Variable mRunNbr, which is accessed by
        * MapFragment
        * https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android
        * */
        main_activity = (MainActivity) getActivity();
        int runNbr = main_activity.mRunNbr;
        Log.i("MAP Log", String.valueOf(runNbr));

        switch (runNbr) {
            //case 0: markerRun0.showInfoWindow(); break;
            case 1:
                mMarkerRun1.showInfoWindow();
                break;
            case 2:
                mMarkerRun2.showInfoWindow();
                break;
            case 3:
                mMarkerRun3.showInfoWindow();
                break;
            case 4:
                mMarkerRun4.showInfoWindow();
                break;
            case 5:
                mMarkerRun5.showInfoWindow();
                break;
            case 6:
                mMarkerRun6.showInfoWindow();
                break;
            case 7:
                mMarkerRun7.showInfoWindow();
                break;
            case 8:
                mMarkerRun8.showInfoWindow();
                break;
            case 9:
                mMarkerRun9.showInfoWindow();
                break;
            case 10:
                mMarkerRun10.showInfoWindow();
                break;
            case 11:
                mMarkerRun11.showInfoWindow();
                break;
            case 12:
                mMarkerRun12.showInfoWindow();
                break;
            case 13:
                mMarkerRun13.showInfoWindow();
                break;
            case 14:
                mMarkerRun14.showInfoWindow();
                break;
            case 15:
                mMarkerRun15.showInfoWindow();
                break;
            case 16:
                mMarkerRun16.showInfoWindow();
                break;
            case 17:
                mMarkerRun17.showInfoWindow();
                break;
            case 18:
                mMarkerRun18.showInfoWindow();
                break;
            case 19:
                mMarkerRun19.showInfoWindow();
                break;
            case 20:
                mMarkerRun20.showInfoWindow();
                break;

            default:
                Log.e("", "no Marker");
                return;
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
        //Log.v("CURRENT LOCATION", location.toString());
        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.v("CURRENT LOCATION", currentLoc.toString());
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

    // Container Activity to communicate currentLocation with MainActivity
    public interface CurrentLocationListener {
        //https://developer.android.com/training/basics/fragments/communicating.html
        }
}


