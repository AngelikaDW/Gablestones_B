package com.aleaf.gablestones;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;
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
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback
         {

    private static final String TAG = "MapFragment";
    private final String LOG_TAG = AppCompatActivity.class.getSimpleName();
    GoogleMap mGoogleMap;
    View mView;
             MapView mMapView;
    public  MainActivity main_activity;
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
//        FloatingActionButton FAB3;
//        FAB3 = (FloatingActionButton) mView.findViewById(R.id.fab3);
//        FAB3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "TESTING FAB locate me", Toast.LENGTH_SHORT).show();
//            }
//        });

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        LatLng Amsterdam = new LatLng(52.372438, 4.900327);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /*Add markers where gable stones are located,
        number of marker = running number of gable stone*/
        googleMap.addMarker(new MarkerOptions()
                .position(Amsterdam)
                .title("Center of Amsterdam")
                .snippet("Maybe one day, I will be there with my family")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        Marker markerRun1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378123,4.884724))
                .title(getString(R.string.gablestone_1))
                .snippet(getString(R.string.street_address_1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue1))
                        //defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );



        Marker markerRun2 = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(52.374614, 4.883376))
                        .title(getString(R.string.gablestone_2))
                        .snippet(getString(R.string.street_address_2))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue2))
        );
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.385716, 4.887685))
                .title(getString(R.string.gablestone_3))
                .snippet(getString(R.string.street_address_3))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue3))
        );

//        Markers downloaded from: https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_[color][character].png
//        https://github.com/Concept211/Google-Maps-Markers
//        then put into drawable and called from there

        // https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android
        /*Get mRunNbr from Main Activity to be able to open correct InfoWindow*/
        /*TODO: need to use a swich statement or generate some way to load the right Markers*/
        main_activity = (MainActivity) getActivity();
        int runNbr = main_activity.mRunNbr;
        Log.i("MAP Log", String.valueOf(runNbr));

        String openWindowMarker = "markerRun"+String.valueOf(runNbr);
        if (runNbr == 1) {
            markerRun1.showInfoWindow();
        }
        else if (runNbr == 2) {
            markerRun2.showInfoWindow();
        }

        //markerRun1.showInfoWindow();

        /*Change inbetween Fragments from Mapto Mission
        * TODO: currently the MissionFragment is on top of the Map Fragment
        * (Filling Backgrounds is covering it)TODO: need stop/replace Mapfragment properly*/
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new MissionFragment();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.map, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        CameraPosition AmsterdamCenter = CameraPosition.builder()
                .target(Amsterdam)
                .zoom(13)
                .bearing(0)
                .tilt(0)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(AmsterdamCenter));

        mGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }
     /**
      * Enables the My Location layer if the fine location permission has been granted.
      * */

     private void enableMyLocation() {
         if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED) {
             // Permission to access the location is missing.
             PermissionUtils.requestPermission((AppCompatActivity) getActivity().getApplicationContext(), LOCATION_PERMISSION_REQUEST_CODE,
                     Manifest.permission.ACCESS_FINE_LOCATION, true);
         } else if (mGoogleMap != null) {
             // Access to the location has been granted to the app.
             mGoogleMap.setMyLocationEnabled(true);
         }
     }
     @Override
     public boolean onMyLocationButtonClick() {
         Toast.makeText(getActivity(), "The blue circle shows your current location on the map", Toast.LENGTH_SHORT).show();
     // Return false so that we don't consume the event and teh default behavior still occurs
         // (the camera animates to the user's current position).
         return false;
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

}
