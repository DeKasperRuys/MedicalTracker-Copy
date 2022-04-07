package com.carlv.medicaldeliveryrider.Activities.Medicine;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carlv.medicaldeliveryrider.Helpers.ConnectedThread;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Models.Medicine;
import com.carlv.medicaldeliveryrider.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineDetailActivity extends AppCompatActivity {
    Button btnBack;
    Medicine medicine;
    private RestClient restClient;
    TextView txtName, txtTempMin, txtTempMax, txtHumidMin, txtHumidMax;
    Intent intent;
    Integer ID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinedetail);
        btnBack = findViewById(R.id.meddet_btn_back);
        txtName = findViewById(R.id.meddet_name);
        txtTempMin = findViewById(R.id.meddet_tempMin);
        txtTempMax = findViewById(R.id.meddet_tempMax);
        txtHumidMin = findViewById(R.id.meddet_humidMin);
        txtHumidMax = findViewById(R.id.meddet_humidMax);
        intent = getIntent();
        ID = intent.getIntExtra("id", 0);
        loadMedicine();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadMedicine() {
        Service service = restClient.getClient().create(Service.class);
        Call<Medicine> jsonCall = service.getMedicine(Integer.toString(ID));
        jsonCall.enqueue(new Callback<Medicine>() {
            @Override
            public void onResponse(Call<Medicine> call, Response<Medicine> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                medicine = response.body();
                txtName.setText(medicine.getName());
                txtTempMin.setText(Double.toString(medicine.getMinTemp())+"℃");
                txtTempMax.setText(Double.toString(medicine.getMaxTemp())+ "℃");
                txtHumidMin.setText(Double.toString(medicine.getMinHumid())+ "%");
                txtHumidMax.setText(Double.toString(medicine.getMaxHumid()) + "%");
                }

            @Override
            public void onFailure(Call<Medicine> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
    }
