package com.carlv.medicaldeliverydoctor.Activities.Hospital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.carlv.medicaldeliverydoctor.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class PinHospitalActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng latLng;
    SupportMapFragment mapFragment;
    String name;
    Intent intent;
    int ID;
    Boolean edit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mappin);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        latLng = new LatLng(51.220631, 4.401904);
        intent = getIntent();
        name =  intent.getStringExtra("name");
        ID = intent.getIntExtra("id",0);
        edit = intent.getBooleanExtra("edit", false);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16));
    }
    public void confirm(View v){
        LatLng current = mMap.getCameraPosition().target;
        Intent i;
        if(edit) {
            i = new Intent(getApplicationContext(), EditHospitalActivity.class);
        }
        else {
            i = new Intent(getApplicationContext(), NewHospitalActivity.class);
        }
        i.putExtra("lat", current.latitude);
        i.putExtra("lon", current.longitude);
        i.putExtra("id", ID);
        i.putExtra("name", name);
        startActivity(i);
    }
}
