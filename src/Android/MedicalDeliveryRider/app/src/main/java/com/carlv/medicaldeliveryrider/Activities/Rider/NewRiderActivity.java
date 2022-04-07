package com.carlv.medicaldeliveryrider.Activities.Rider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carlv.medicaldeliveryrider.Helpers.CommonMethod;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Models.Rider;
import com.carlv.medicaldeliveryrider.R;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRiderActivity extends AppCompatActivity {
    EditText edit_firstName, edit_lastName;
    Button btnAdd;
    String firstName, lastName;
    RestClient restClient;
    Service service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrider);
        edit_firstName = findViewById(R.id.newrider_edit_firstName);
        edit_lastName = findViewById(R.id.newrider_edit_lastName);
        btnAdd = findViewById(R.id.newrider_btn_add);
        service = restClient.getClient().create(Service.class);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(NewRiderActivity.this))
                        postRider(firstName, lastName);
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", NewRiderActivity.this);
                }
            }
        });
    }
    private void postRider(String firstName, String lastName) {
        final Rider medicine = new Rider(firstName, lastName);
        Call<Rider> call1 = service.createRider(medicine);
        call1.enqueue(new Callback<Rider>() {
            @Override
            public void onResponse(Call<Rider> call, Response<Rider> response) {
                Rider rider = response.body();

                if (rider != null) {
                    String responseCode = rider.getResponseCode();
                    Log.e("responseCode", "getResponseCode  -->  " + rider.getResponseCode());
                    Log.e("responseMessage", "getResponseMessage  -->  " + rider.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(NewRiderActivity.this, "Error: Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(NewRiderActivity.this, "New rider added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Rider> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }











    public boolean checkValidation() {
        firstName = edit_firstName.getText().toString();
        lastName = edit_lastName.getText().toString();
        if (edit_firstName.getText().toString().trim().equals("") || edit_lastName.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("First and last name cannot be left blank", NewRiderActivity.this);
            return false;
        }
        return true;
    }
}
