package com.carlv.medicaldeliveryrider.Activities.Deliveries;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.carlv.medicaldeliveryrider.Helpers.ChartStyler;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.MainActivity;
import com.carlv.medicaldeliveryrider.Menu.BottomSlideMenu;
import com.carlv.medicaldeliveryrider.Models.Delivery;
import com.carlv.medicaldeliveryrider.Models.Update;
import com.carlv.medicaldeliveryrider.R;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetails extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private LatLng latLng;
    SupportMapFragment mapFragment;
    int ID;
    Intent intent;
    List<Update> updateList;
    RestClient restClient;
    Delivery delivery;
    Button btnIssue, btnBack;
    LinearLayout lay_main, lay_temp, lay_humid;
    Boolean movement = false;
    Boolean orientation = false;
    Double maxTemp = 0.0;
    Double minTemp = 99999.99;
    Double minHumid = 99999.99;
    Double maxHumid = 0.0;
    Double avgHumid = 0.0;
    Double avgTemp = 0.0;
    DecimalFormat df2;
    Service service;
    TextView txtMedName, txtStatus, txtStart, txtTemp, txtHumid, txtTempCurrent, txtTempAvg, txtTempMin, txtTempMax, txtHumidCurrent, txtHumidAvg, txtHumidMin, txtHumidMax;
    BottomSlideMenu bottomSlideMenu;
    List<Marker> markers = new ArrayList<>();
    Marker mark;
    SwipeButton enableButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverydetails);
        df2 = new DecimalFormat("#.##");
        declaration();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        latLng = new LatLng(51.220631, 4.401904);
        intent = getIntent();
        ID = intent.getIntExtra("id", 0);
        btnIssue.setVisibility(View.GONE);
        loadDelivery();
        loadSize();
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
                txtMedName.setText(delivery.getMedicine().getName() + " x " + delivery.getAmount().toString());
                switch (delivery.getStatus()) {
                    case 0:
                        txtStatus.setText("Status: Waiting for pickup");
                        txtStatus.setTextColor(Color.rgb(160, 160, 160));
                        break;
                    case 1:
                        txtStatus.setText("Status: Underway");
                        txtStatus.setTextColor(Color.rgb(255, 255, 50));
                        enableButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        txtStatus.setText("Status: Issue");
                        txtStatus.setTextColor(Color.rgb(255, 128, 0));
                        btnIssue.setVisibility(View.VISIBLE);
                        btnIssue.setText("View Issue");
                        enableButton.setVisibility(View.VISIBLE);
                        btnIssue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCreater();
                            }
                        });
                        break;
                    case 3:
                        txtStatus.setText("Status: Delivered");
                        txtStatus.setTextColor(Color.BLUE);
                        break;
                    case 4:
                        txtStatus.setText("Status: Cancelled");
                        txtStatus.setTextColor(Color.RED);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }

    private void loadSize() {
        service = restClient.getClient().create(Service.class);
        Call<JsonArray> jsonCall = service.readJsonArrayUpdates(Integer.toString(ID));
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Update>>() {
                }.getType();
                updateList = new Gson().fromJson(jsonString, listType);
                setFields();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 16));
        mMap.setOnMarkerClickListener(this);

    }

    private void declaration() {
        txtMedName = findViewById(R.id.deldet_medname);
        txtStatus = findViewById(R.id.deldet_status);
        txtStart = findViewById(R.id.deldet_start);
        txtTempCurrent = findViewById(R.id.deldet_temp_current);
        txtTempAvg = findViewById(R.id.deldet_temp_avg);
        txtTempMin = findViewById(R.id.deldet_temp_min);
        txtTempMax = findViewById(R.id.deldet_temp_max);
        txtHumidMin = findViewById(R.id.deldet_humid_min);
        txtHumidMax = findViewById(R.id.deldet_humid_max);
        txtHumidCurrent = findViewById(R.id.deldet_humid_current);
        txtHumidAvg = findViewById(R.id.deldet_humid_avg);
        btnBack = findViewById(R.id.deldet_btn_back);
        lay_humid = findViewById(R.id.deldet_lay_humidity);
        lay_temp = findViewById(R.id.deldet_lay_temp);
        lay_main = findViewById(R.id.deldet_lay_main);
        txtTemp = findViewById(R.id.deldet_txt_temp);
        txtHumid = findViewById(R.id.deldet_txt_humid);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MyDeliveries.class);
                startActivity(intent);
                finish();
            }
        });
        enableButton = (SwipeButton) findViewById(R.id.deldet_swipebtn);
        enableButton.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                setCompleted();
            }
        });
        btnIssue = findViewById(R.id.deldet_btn_issue);
        btnIssue.setVisibility(View.GONE);
        bottomSlideMenu = new BottomSlideMenu(this);
        lay_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSlideMenu.closePanel();
            }
        });
        lay_humid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphCreater("humidity", 2);
            }
        });
        lay_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphCreater("temperature", 1);

            }
        });
    }

    private void setCompleted() {
        Service service = restClient.getClient().create(Service.class);
        delivery.setStatus(3);
        Call<Delivery> jsonCall = service.updateDelivery(delivery.getID().toString(), delivery);
        jsonCall.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Toast.makeText(DeliveryDetails.this, "Delivery Completed", Toast.LENGTH_SHORT).show();
                enableButton.setVisibility(View.GONE);
                loadDelivery();
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Log.e("onFailure", t.toString());
                Toast.makeText(DeliveryDetails.this, "Connection Failure", Toast.LENGTH_SHORT).show();
                enableButton.toggleState();
            }
        });

    }

    private void setFields() {
        if (updateList.size() > 0) {
            txtStart.setText("Started: " + getDateFromFullDate(updateList.get(0).getTimeStamp()) + " - " + getTimeFromFullDate(updateList.get(0).getTimeStamp()));
            txtTempCurrent.setText("Current: " + df2.format(updateList.get(updateList.size() - 1).getTemp()) + " ℃");
            txtHumidCurrent.setText("Current: " + df2.format(updateList.get(updateList.size() - 1).getHumid()) + "%");
            for (Update update : updateList) {
                if (update.getTemp() < minTemp)
                    minTemp = update.getTemp();
                if (update.getHumid() < minHumid)
                    minHumid = update.getHumid();
                if (update.getTemp() > maxTemp)
                    maxTemp = update.getTemp();
                if (update.getHumid() > maxHumid)
                    maxHumid = update.getHumid();
                if (update.getMovement())
                    movement = true;
                if (update.getOrientation())
                    orientation = true;
                avgHumid = avgHumid + update.getHumid();
                avgTemp = avgTemp + update.getTemp();
            }
            txtTempAvg.setText("Avg: " + df2.format(avgTemp / updateList.size()) + " ℃");
            txtHumidAvg.setText("Avg: " + df2.format(avgHumid / updateList.size()) + "%");
            txtTempMax.setText("Max: " + df2.format(maxTemp) + "℃");
            txtTempMin.setText("Min: " + df2.format(minTemp) + " ℃");
            txtHumidMax.setText("Max: " + df2.format(maxHumid) + "%");
            txtHumidMin.setText("Min: " + df2.format(minHumid) + "%");
            setMap();
        } else {
            txtStart.setText("No updates received");
            txtHumid.setVisibility(View.GONE);
            txtTemp.setVisibility(View.GONE);
        }
    }

    private void dialogCreater() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_issue);
        TextView txtInfo = dialog.findViewById(R.id.dia_issue_txtInfo);
        Button btnBack = dialog.findViewById(R.id.dia_issue_btnBack);
        String issue = "";
        if (maxTemp > delivery.getMedicine().getMaxTemp())
            issue = issue + "Max temperature for " + delivery.getMedicine().getName() + ": " + df2.format(delivery.getMedicine().getMaxTemp()) + " ℃\nDelivery reached " + df2.format(maxTemp) + " ℃ in temperature\n";
        if (minTemp < delivery.getMedicine().getMinTemp())
            issue = issue + "Min temperature for " + delivery.getMedicine().getName() + ": " + df2.format(delivery.getMedicine().getMinTemp()) + " ℃\nDelivery reached " + df2.format(minTemp) + " ℃ in temperature\n";
        if (maxHumid > delivery.getMedicine().getMaxHumid())
            issue = issue + "Max humid for " + delivery.getMedicine().getName() + ": " + df2.format(delivery.getMedicine().getMaxHumid()) + "%\nDelivery reached " + df2.format(maxHumid) + "% in humidity\n";
        if (minHumid < delivery.getMedicine().getMinHumid())
            issue = issue + "Min humid for " + delivery.getMedicine().getName() + ": " + df2.format(delivery.getMedicine().getMinHumid()) + "%\nDelivery reached " + df2.format(minHumid) + "% in humidity\n";
        if (orientation)
            issue = issue + "Delivery had a bad orientation \n";
        if (movement)
            issue = issue + "Delivery moved around too much";
        txtInfo.setText(issue);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void graphCreater(String text, int select) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ChartStyler chartStyler = new ChartStyler();
        dialog.setContentView(R.layout.dialog_graph);
        TextView txtTitle = dialog.findViewById(R.id.dia_graph_txtTitle);
        LineChart chart = dialog.findViewById(R.id.dia_graph_graph);
        Button btnBack = dialog.findViewById(R.id.dia_graph_btnBack);
        btnBack.setText("Back");
        txtTitle.setText("Showing graph for: " + text);
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < updateList.size(); i++) {
            double d;
            if (select == 1)
                d = updateList.get(i).getTemp();
            else
                d = updateList.get(i).getHumid();
            float f = (float) d;
            entries.add(new Entry(i, f));
        }
        LineDataSet dataSet = new LineDataSet(entries, text);
        chartStyler.SetChartColor(dataSet);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chartStyler.SetChartLegendColor(chart);
        chart.invalidate();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void setMap() {
        LatLng latLng = new LatLng(updateList.get(0).getLat(), updateList.get(0).getLon());
        for (int i = 0; i < updateList.size(); i++) {
            if (updateList.get(i).getLat() != 0 && updateList.get(i).getLon() != 0) {
                mark = mMap.addMarker((new MarkerOptions().position(new LatLng(updateList.get(i).getLat(), updateList.get(i).getLon())).title(getDateFromFullDate(updateList.get(i).getTimeStamp()) + ":" + getTimeFromFullDate(updateList.get(i).getTimeStamp()))));
                markers.add(mark);
                latLng = new LatLng(updateList.get(i).getLat(), updateList.get(i).getLon());
            }
        }
        List<Update> polyList = new ArrayList<>();
        for (int i = 0; i < updateList.size(); i++) {
            if (updateList.get(i).getLat() != 0 && updateList.get(i).getLon() != 0) {
                polyList.add(updateList.get(i));
            }
        }
        for (int i = 0; i < polyList.size() - 1; i++) {
            mMap.addPolyline(new PolylineOptions()
                    .add(
                            new LatLng(polyList.get(i).getLat(), polyList.get(i).getLon()),
                            new LatLng(polyList.get(i + 1).getLat(), polyList.get(i + 1).getLon())));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 16));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int i = 0; i < markers.size(); i++) {
            if (marker.equals(markers.get(i))) {
                bottomSlideMenu.setBottomPanel(delivery, updateList.get(i));
            }
            //selectedMarker = marker;
        }
        return false;
    }

    private String getDateFromFullDate(String fulldate) {
        String year = fulldate.substring(0, 4);
        String day = fulldate.substring(5, 7);
        String month = fulldate.substring(8, 10);
        String date = year + "/" + day + "/" + month;
        return date;
    }

    private String getTimeFromFullDate(String fulldate) {
        String hour = fulldate.substring(11, 13);
        String mins = fulldate.substring(14, 16);
        String time = hour + ":" + mins;
        return time;
    }
}
