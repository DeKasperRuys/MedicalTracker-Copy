package com.carlv.medicaldeliveryrider.Activities.Deliveries;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Models.Hospital;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryHospitalMap extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION = 1;

    GoogleMap mMap;
    LatLng latLng;
    SupportMapFragment mapFragment;
    int ID;
    RestClient restClient;
    Hospital hospital;
    TextView txtHospName;
    Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitalmap);
        intent = getIntent();
        ID = intent.getIntExtra("id", 0);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        txtHospName = findViewById(R.id.hospmap_name);
        loadDelivery();
    }
    private void loadDelivery() {
        Service service = restClient.getClient().create(Service.class);
        Call<Hospital> jsonCall = service.getHospital(Integer.toString(ID));
        jsonCall.enqueue(new Callback<Hospital>() {
            @Override
            public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                hospital = response.body();
                latLng = new LatLng(hospital.getLat(), hospital.getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16));
                mMap.addMarker((new MarkerOptions().position(latLng).title(hospital.getName())));
                txtHospName.setText(hospital.getName());
            }

            @Override
            public void onFailure(Call<Hospital> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    public void back(View v) {
        finish();
    }
}
