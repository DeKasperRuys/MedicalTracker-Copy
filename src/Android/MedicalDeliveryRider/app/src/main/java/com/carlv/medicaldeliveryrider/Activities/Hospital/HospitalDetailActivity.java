package com.carlv.medicaldeliveryrider.Activities.Hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Menu.NavigationMenu;
import com.carlv.medicaldeliveryrider.Models.Hospital;
import com.carlv.medicaldeliveryrider.Models.Medicine;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView txtAddress, txtName;
    Button btnBack;
    int ID;
    RestClient restClient;
    Hospital hospital;
    private GoogleMap mMap;
    private LatLng latLng;
    SupportMapFragment mapFragment;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitaldetails);
        intent = getIntent();
        ID = intent.getIntExtra("id", 0);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        txtAddress = findViewById(R.id.hosp_detail_address);
        txtName = findViewById(R.id.hosp_detail_name);
        btnBack = findViewById(R.id.hosp_detail_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadHospital();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void loadHospital() {
        Service service = restClient.getClient().create(Service.class);
        Call<Hospital> jsonCall = service.getHospital(Integer.toString(ID));
        jsonCall.enqueue(new Callback<Hospital>() {
            @Override
            public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                hospital = response.body();
                txtName.setText(hospital.getName());
                txtAddress.setText(hospital.getAddress());
                latLng = new LatLng(hospital.getLat(), hospital.getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16));
                mMap.addMarker((new MarkerOptions().position(latLng).title(hospital.getName())));
            }

            @Override
            public void onFailure(Call<Hospital> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
}
