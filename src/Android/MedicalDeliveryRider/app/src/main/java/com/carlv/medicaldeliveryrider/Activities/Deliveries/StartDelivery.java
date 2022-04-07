package com.carlv.medicaldeliveryrider.Activities.Deliveries;

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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.carlv.medicaldeliveryrider.Helpers.ConnectedThread;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Models.Delivery;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartDelivery extends AppCompatActivity implements OnMapReadyCallback {
    Intent intent;
    int ID;
    GoogleMap mMap;
    LatLng latLng;
    SupportMapFragment mapFragment;
    RestClient restClient;
    Delivery delivery;
    TextView txtHospName, txtMedName, txtErrorChecker;
    Button btnDeviceSelect, btnConnect, btnSendParams, btnStart;
    private static final int READ_REQUEST_CODE = 42;
    String deviceName, address;
    private BluetoothAdapter bluetoothAdapter = null;
    Set<BluetoothDevice> pairedDevices;
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startdelivery);
        intent = getIntent();
        ID = intent.getIntExtra("id", 0);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        txtHospName = findViewById(R.id.startdel_txtHeader);
        txtMedName = findViewById(R.id.startdel_txtHostname);
        txtErrorChecker = findViewById(R.id.startdel_errorChecker);
        btnDeviceSelect = findViewById(R.id.startdel_btnSelect);
        btnConnect = findViewById(R.id.startdel_btnConnect);
        btnSendParams = findViewById(R.id.startdel_btnSend);
        btnStart = findViewById(R.id.startdel_btnStart);
        btnConnect.setEnabled(false);
        btnStart.setEnabled(false);
        btnSendParams.setEnabled(false);
        loadDelivery();
        bluetooth();
        btnsBluetooth();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDelivery();
            }
        });
    }
    private void loadDelivery() {
        Service service = restClient.getClient().create(Service.class);
        Call<Delivery> jsonCall = service.getDelivery(Integer.toString(ID));
        jsonCall.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
               delivery = response.body();
                latLng = new LatLng(delivery.getHospital().getLat(), delivery.getHospital().getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16));
                mMap.addMarker((new MarkerOptions().position(latLng).title(delivery.getHospital().getName())));
                txtHospName.setText("Delivery to: \n" + delivery.getHospital().getName());
                txtMedName.setText("New Delivery for: \n" + delivery.getMedicine().getName());
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    private void startDelivery() {
            Service service = restClient.getClient().create(Service.class);
            delivery.setStatus(1);
            Call<Delivery> jsonCall = service.updateDelivery(delivery.getID().toString(), delivery);
            jsonCall.enqueue(new Callback<Delivery>() {
                @Override
                public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                    Toast.makeText(StartDelivery.this, "Started delivery", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), MyDeliveries.class);
                    startActivity(intent);
                     finish();
                }

                @Override
                public void onFailure(Call<Delivery> call, Throwable t) {
                    Log.e("onFailure", t.toString());
                }
            });

        }
    private void btnsBluetooth() {
        btnDeviceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                final Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StartDelivery.this, android.R.layout.select_dialog_singlechoice);
                if (pairedDevice.size() > 0) {
                    for (BluetoothDevice device : pairedDevice) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress();
                        arrayAdapter.add(deviceName + " " + deviceHardwareAddress);
                    }
                }
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(StartDelivery.this);
                builderSingle.setIcon(R.drawable.ic_launcher_background);
                builderSingle.setTitle("Select One Name:-");
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        address = strName.substring(strName.length() - 17);
                        Log.d("MAC Address", address);
                        Toast.makeText(getApplicationContext(), "Selected: " + strName, Toast.LENGTH_SHORT).show();
                        btnConnect.setEnabled(true);
                    }
                });
                builderSingle.show();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                try {
                    btSocket = createBluetoothSocket(device);
                    btnSendParams.setEnabled(true);

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
                    try {
                        btSocket.close();
                        btnSendParams.setEnabled(false);

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    try {
                        btSocket.close();
                        btnSendParams.setEnabled(false);

                    } catch (IOException e2) {

                    }
                }
                mConnectedThread = new ConnectedThread(btSocket, bluetoothIn, StartDelivery.this);
                mConnectedThread.start();
            }
        });
        btnSendParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BLUETOOTH", "Writing: " + delivery.getID().toString() + "," +  delivery.getMedicine().getMinTemp() + "," + delivery.getMedicine().getMaxTemp() + "," + delivery.getMedicine().getMinHumid() + "," + delivery.getMedicine().getMaxHumid());
                mConnectedThread.write(delivery.getID().toString() + "," +  delivery.getMedicine().getMinTemp() + "," + delivery.getMedicine().getMaxTemp() + "," + delivery.getMedicine().getMinHumid() + "," + delivery.getMedicine().getMaxHumid());
                btnStart.setEnabled(true);
                txtErrorChecker.setVisibility(View.GONE);
            }
        });
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    private void bluetooth () {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            btnDeviceSelect.setEnabled(false);
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, READ_REQUEST_CODE);
                Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            }
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                }
            }
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            ActivityCompat.requestPermissions(StartDelivery.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            bluetoothIn = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    try {
                        if (msg.what == handlerState) {
                            String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                            recDataString.append(readMessage);
                            int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                            if (endOfLineIndex > 0) {                                           // make sure there data before ~
                                String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                                int dataLength = dataInPrint.length();                          //get length of data received
                            }
                        }
                    } catch (OutOfMemoryError e1) {
                        e1.printStackTrace();
                        Log.e("Memory exceptions", "exceptions" + e1);
                    } catch (StringIndexOutOfBoundsException e) {
                        Log.d("Error", "caught");
                    }
                }
            };
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
