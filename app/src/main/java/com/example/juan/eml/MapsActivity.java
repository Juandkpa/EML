package com.example.juan.eml;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private ProgressDialog pDialog;
    double mLatitude=0;
    double mLongitude=0;
    String name;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //private UiSettings mapSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
      //  mapSettings = mMap.getUiSettings();

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

        Intent in = getIntent();
        name = in.getStringExtra("name");
        mLatitude = Double.parseDouble(in.getStringExtra("lat"));
        mLongitude = Double.parseDouble(in.getStringExtra("lng"));



        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        // Setting the position for the marker
        markerOptions.position(latLng);
        markerOptions.title(name);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13 ));
    }



    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }




    private void setUpMap() {
       mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
