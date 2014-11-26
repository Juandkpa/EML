package com.example.juan.eml;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by juan on 24/11/14.
 */
public class SearchDetails extends ListActivity implements LocationListener {

    private static final String NAME = "name";
    private static final String VECINITY = "vicinity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Intent in = getIntent();

        String name = in.getStringExtra(NAME);
        String horario = in.getStringExtra(VECINITY);


        TextView lblName = (TextView) findViewById(R.id.place_Name);
        TextView lblHorario = (TextView) findViewById(R.id.horario);


        lblName.setText(name);
        lblHorario.setText(horario);


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
}
