package com.carlv.medicaldeliverydoctor.Activities.Medicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carlv.medicaldeliverydoctor.Activities.Hospital.NewHospitalActivity;
import com.carlv.medicaldeliverydoctor.Helpers.CommonMethod;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
import com.carlv.medicaldeliverydoctor.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMedicineActivity extends Activity {
    EditText edit_name, edit_minTemp, edit_maxTemp, edit_minHumid, edit_maxHumid;
    Button btnAddMedicine;
    Double minTemp, maxTemp, minHumid, maxHumid;
    String name;
    RestClient restClient;
    Service service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_newmedicine);
        btnAddMedicine = findViewById(R.id.newmed_btn);
        edit_minTemp = findViewById(R.id.newmed_edit_minTemp);
        edit_maxTemp = findViewById(R.id.newmed_edit_maxTemp);
        edit_minHumid = findViewById(R.id.newmed_edit_minHumid);
        edit_maxHumid = findViewById(R.id.newmed_edit_maxHumid);
        edit_name = findViewById(R.id.newmed_edit_name);
        service = restClient.getClient().create(Service.class);
        btnAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(NewMedicineActivity.this)) {
                        name = edit_name.getText().toString();
                        minTemp = Double.valueOf(edit_minTemp.getText().toString());
                        maxTemp = Double.valueOf(edit_maxTemp.getText().toString());
                        minHumid = Double.valueOf(edit_minHumid.getText().toString());
                        maxHumid = Double.valueOf(edit_maxHumid.getText().toString());
                        postMedicine(name, minTemp, maxTemp, minHumid, maxHumid);
                    }
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", NewMedicineActivity.this);
                }
            }
        });

    }
    private void postMedicine(String name, Double minTemp, Double maxTemp, Double minHumid, Double maxHumid) {
        final Medicine medicine = new Medicine( name, minTemp, maxTemp, minHumid, maxHumid);
        Call<Medicine> call1 = service.createMedicine(medicine);
        call1.enqueue(new Callback<Medicine>() {
            @Override
            public void onResponse(Call<Medicine> call, Response<Medicine> response) {
                Medicine medicine = response.body();

                if (medicine != null) {
                    String responseCode = medicine.getResponseCode();
                    Log.e("responseCode", "getResponseCode  -->  " + medicine.getResponseCode());
                    Log.e("responseMessage", "getResponseMessage  -->  " + medicine.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(NewMedicineActivity.this, "Error: Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(NewMedicineActivity.this, "New medicine added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), MedicineActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Medicine> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
    public boolean checkValidation() {
        name = edit_name.getText().toString();
        if (edit_name.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Name Cannot be left blank", NewMedicineActivity.this);
            return false;
        } else if (edit_minTemp.getText().toString().trim().equals("") | edit_maxTemp.getText().toString().trim().equals("") | edit_minHumid.getText().toString().trim().equals("") | edit_maxHumid.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Values cannot be left null", NewMedicineActivity.this);
            return false;
        } else if(Integer.valueOf(edit_maxHumid.getText().toString()) > 100) {
            CommonMethod.showAlert("Humidity cannot be higher than 100", NewMedicineActivity.this);
            return false;
        }
        name = edit_name.getText().toString();
        minTemp = Double.valueOf(edit_minTemp.getText().toString());
        maxTemp = Double.valueOf(edit_maxTemp.getText().toString());
        minHumid = Double.valueOf(edit_minHumid.getText().toString());
        maxHumid = Double.valueOf(edit_maxHumid.getText().toString());
        return true;
    }
}
