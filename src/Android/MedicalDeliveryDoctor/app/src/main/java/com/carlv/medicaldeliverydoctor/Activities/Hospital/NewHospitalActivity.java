package com.carlv.medicaldeliverydoctor.Activities.Hospital;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.carlv.medicaldeliverydoctor.Helpers.CommonMethod;
import com.carlv.medicaldeliverydoctor.Models.Hospital;
import com.carlv.medicaldeliverydoctor.R;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewHospitalActivity extends Activity {
    EditText edit_name, edit_lat, edit_lon,  edit_address;
    Button btnAddHospital, btnGoogleMaps;
    Double lon, lat;
    String name, address;
    RestClient restClient;
    Service service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhospital);
        edit_lat = findViewById(R.id.newhosp_edit_lat);
        edit_lon = findViewById(R.id.newhosp_edit_lon);
        edit_name = findViewById(R.id.newhosp_edit_name);
        edit_address = findViewById(R.id.newhosp_edit_address);
        btnAddHospital = findViewById(R.id.newhost_btn);
        btnGoogleMaps = findViewById(R.id.newhospgmaps);
        Intent intent = getIntent();
        lon = intent.getDoubleExtra("lon",0);
        lat = intent.getDoubleExtra("lat",0);
        name = intent.getStringExtra("name");
        edit_lat.setText(lat.toString());
        edit_lon.setText(lon.toString());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            if(lat != 0 || lon != 0) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                address = addresses.get(0).getAddressLine(0);
                edit_address.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        edit_name.setText(name);
        btnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                Intent intent = new Intent(getBaseContext(), PinHospitalActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        service = restClient.getClient().create(Service.class);
        btnAddHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                lat = Double.valueOf(edit_lat.getText().toString());
                lon = Double.valueOf(edit_lon.getText().toString());
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(NewHospitalActivity.this))
                        postHospital(name, lat, lon, address);
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", NewHospitalActivity.this);
                }
            }
        });

    }
    private void postHospital(String name, Double lat, Double lon, String address) {
        final Hospital hospital = new Hospital( name, lat,lon, address);
        Call<Hospital> call1 = service.createHospital(hospital);
        call1.enqueue(new Callback<Hospital>() {
            @Override
            public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                Hospital hospital = response.body();

                if (hospital != null) {
                    String responseCode = hospital.getResponseCode();
                    Log.e("responseCode", "getResponseCode  -->  " + hospital.getResponseCode());
                    Log.e("responseMessage", "getResponseMessage  -->  " + hospital.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(NewHospitalActivity.this, "Error: Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(NewHospitalActivity.this, "New hospital added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Hospital> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
    public boolean checkValidation() {
        name = edit_name.getText().toString();
        lat = Double.valueOf(edit_lat.getText().toString());
        lon = Double.valueOf(edit_lon.getText().toString());

        if (edit_name.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Name Cannot be left blank", NewHospitalActivity.this);
            return false;
        } else if (edit_lat.getText().toString().trim().equals("") | edit_lon.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Coords Cannot be left blank", NewHospitalActivity.this);
            return false;
        }
        return true;
    }
}
