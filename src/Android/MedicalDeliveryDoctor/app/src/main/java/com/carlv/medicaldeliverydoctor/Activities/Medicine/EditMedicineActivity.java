package com.carlv.medicaldeliverydoctor.Activities.Medicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carlv.medicaldeliverydoctor.Helpers.CommonMethod;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
import com.carlv.medicaldeliverydoctor.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMedicineActivity extends Activity {
    EditText edit_name, edit_minTemp, edit_maxTemp, edit_minHumid, edit_maxHumid;
    Button btnAddMedicine;
    String name;
    RestClient restClient;
    Service service;
    TextView txtTitle;
    Double minTemp, maxTemp, minHumid, maxHumid;
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_newmedicine);
        btnAddMedicine = findViewById(R.id.newmed_btn);
        txtTitle = findViewById(R.id.newmed_txt);
        edit_minTemp = findViewById(R.id.newmed_edit_minTemp);
        edit_maxTemp = findViewById(R.id.newmed_edit_maxTemp);
        edit_minHumid = findViewById(R.id.newmed_edit_minHumid);
        edit_maxHumid = findViewById(R.id.newmed_edit_maxHumid);
        edit_name = findViewById(R.id.newmed_edit_name);
        Intent intent = getIntent();
        minTemp = intent.getDoubleExtra("minTemp",0);
        maxTemp = intent.getDoubleExtra("maxTemp",0);
        maxHumid = intent.getDoubleExtra("maxHumid",0);
        minHumid = intent.getDoubleExtra("minHumid",0);
        name = intent.getStringExtra("name");
        ID = intent.getIntExtra("id", 0);
        edit_minTemp.setText(minTemp.toString());
        edit_maxTemp.setText(maxTemp.toString());
        edit_minHumid.setText(minHumid.toString());
        edit_maxHumid.setText(maxHumid.toString());
        edit_name.setText(name);
        txtTitle.setText("Update " + name);
        btnAddMedicine.setText("Update Medicine");
        service = restClient.getClient().create(Service.class);
        btnAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                minTemp = Double.valueOf(edit_minTemp.getText().toString());
                maxTemp = Double.valueOf(edit_maxTemp.getText().toString());
                minHumid = Double.valueOf(edit_minHumid.getText().toString());
                maxHumid = Double.valueOf(edit_maxHumid.getText().toString());
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(EditMedicineActivity.this)) {
                        putMedicine(name, minTemp, maxTemp, minHumid, maxHumid); }
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", EditMedicineActivity.this);
                }
            }
        });
    }
    private void putMedicine(String name, Double mintemp, Double maxTemp, Double minHumid, Double maxHumid) {
        final Medicine medicine = new Medicine(name, mintemp, maxTemp, minHumid, maxHumid);
        Call<Medicine> call1 = service.updateMedicine(String.valueOf(ID), medicine);
        call1.enqueue(new Callback<Medicine>() {
            @Override
            public void onResponse(Call<Medicine> call, Response<Medicine> response) {

                Medicine medicine = response.body();

                if (medicine != null) {

                    String responseCode = medicine.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + medicine.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + medicine.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(EditMedicineActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(EditMedicineActivity.this, "Updated medicine", Toast.LENGTH_SHORT).show();
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
        minTemp = Double.valueOf(edit_minTemp.getText().toString());
        maxTemp = Double.valueOf(edit_maxTemp.getText().toString());
        minHumid = Double.valueOf(edit_minHumid.getText().toString());
        maxHumid = Double.valueOf(edit_maxHumid.getText().toString());
        if (edit_name.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Name Cannot be left blank", EditMedicineActivity.this);
            return false;
        } else if (edit_minTemp.getText().toString().trim().equals("") | edit_maxTemp.getText().toString().trim().equals("") | edit_minHumid.getText().toString().trim().equals("") | edit_maxHumid.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("Values cannot be left null", EditMedicineActivity.this);
            return false;
        } else if(maxHumid > 100) {
            CommonMethod.showAlert("Humidity cannot be higher than 100", EditMedicineActivity.this);
        }
        return true;
    }
}
