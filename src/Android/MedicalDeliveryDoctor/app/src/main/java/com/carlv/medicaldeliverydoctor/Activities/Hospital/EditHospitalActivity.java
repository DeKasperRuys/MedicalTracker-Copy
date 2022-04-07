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
import android.widget.TextView;
import android.widget.Toast;

import com.carlv.medicaldeliverydoctor.Activities.Medicine.EditMedicineActivity;
import com.carlv.medicaldeliverydoctor.Helpers.CommonMethod;
import com.carlv.medicaldeliverydoctor.Models.Hospital;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
import com.carlv.medicaldeliverydoctor.R;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditHospitalActivity extends Activity {
    EditText edit_name, edit_lat, edit_lon, edit_address;
    Button btnAddHospital, btnGoogleMaps;
    Double lon, lat;
    String name, city, address;
    RestClient restClient;
    Service service;
    TextView txtTitle;
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhospital);
        edit_lat = findViewById(R.id.newhosp_edit_lat);
        edit_lon = findViewById(R.id.newhosp_edit_lon);
        edit_name = findViewById(R.id.newhosp_edit_name);
        edit_address = findViewById(R.id.newhosp_edit_address);
        btnAddHospital = findViewById(R.id.newhost_btn);
        txtTitle = findViewById(R.id.newhosp_txt);
        btnGoogleMaps = findViewById(R.id.newhospgmaps);
        Intent intent = getIntent();
        lon = intent.getDoubleExtra("lon",0);
        lat = intent.getDoubleExtra("lat",0);
        name = intent.getStringExtra("name");
        ID = intent.getIntExtra("id", 0);
        edit_lat.setText(lat.toString());
        edit_lon.setText(lon.toString());
        edit_name.setText(name);
        txtTitle.setText("Update " + name);
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
        btnAddHospital.setText("Update hospital");
        service = restClient.getClient().create(Service.class);
        btnAddHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                lat = Double.valueOf(edit_lat.getText().toString());
                lon = Double.valueOf(edit_lon.getText().toString());
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(EditHospitalActivity.this)) {
                        putHospital(name, lat, lon, address); }
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", EditHospitalActivity.this);
                    }
            }
        });
        btnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                Intent intent = new Intent(getBaseContext(), PinHospitalActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("edit", true);
                intent.putExtra("id", ID);
                startActivity(intent);
            }
        });
    }
    private void putHospital(String name, Double lat, Double lon, String address) {
        final Hospital hospital = new Hospital(name, lat, lon, address);
        Call<Hospital> call1 = service.updateHospital(String.valueOf(ID), hospital);
        call1.enqueue(new Callback<Hospital>() {
            @Override
            public void onResponse(Call<Hospital> call, Response<Hospital> response) {

                Hospital hospita1 = response.body();
                if (hospita1 != null) {

                    String responseCode = hospita1.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + hospita1.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + hospita1.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(EditHospitalActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(EditHospitalActivity.this, "Updated Hospital", Toast.LENGTH_SHORT).show();
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
        lon = Double.valueOf(edit_lon.getText().toString());;

        if (edit_name.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Name Cannot be left blank", EditHospitalActivity.this);
            return false;
        } else if (edit_lat.getText().toString().trim().equals("") | edit_lon.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Coords Cannot be left blank", EditHospitalActivity.this);
            return false;
        }
        return true;
    }
}
