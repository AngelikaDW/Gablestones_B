package com.aleaf.gablestones;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;


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
        googleMap.addMarker(new MarkerOptions()
                .position(Amsterdam)
                .title("Center of Amsterdam")
                .snippet("Maybe one day, I will be there with my family")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.378123,4.884724))
                .title("Reading Chicken")
                .snippet("Anjelierstraat 40")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


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
