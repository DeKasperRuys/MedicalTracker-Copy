package com.carlv.medicaldeliveryrider.Activities.Deliveries;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.carlv.medicaldeliveryrider.Helpers.NotificationService;
import com.carlv.medicaldeliveryrider.Helpers.Refresher;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.STATIC;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Menu.NavigationMenu;
import com.carlv.medicaldeliveryrider.Models.Delivery;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDeliveries extends AppCompatActivity {
    RestClient restClient;
    List<Delivery> deliveryList;
    List<Delivery> filterList;
    NavigationMenu navigationMenu;
    ListView listView;
    CustomAdapter customAdapter;
    Runnable runnable;
    NotificationService notificationService;
    Service service;
    Button btnInfo;
    NavigationView navigationView;
    static Menu nav_Menu;
    Boolean cancelled, delivered, issue, underway, waiting;
    CharSequence s;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydeliveries);
        Refresher.handler.removeCallbacksAndMessages(null);
        notificationService = new NotificationService(this);
        btnInfo = findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInfoDialog();
            }
        });
        navigationMenu = new NavigationMenu(this);
        listView = findViewById(R.id.delivery_list);
        customAdapter = new CustomAdapter();
        getDeliveries(STATIC.riderID);
        alertChecker();
        filterMenu();
        EditText searchFilter = findViewById(R.id.searchmedicine);
        s = "";
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                s = charSequence;
                updateFilter();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void getDeliveries(int ID) {
        Service service = restClient.getClient().create(Service.class);
        Call<JsonArray> jsonCall = service.readJsonArrayDeliveriesByRiderID(Integer.toString(ID));
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Delivery>>() {}.getType();
                deliveryList = new Gson().fromJson(jsonString, listType);
                filterList = deliveryList;
                listView.setAdapter(customAdapter);
                for (Delivery delivery : deliveryList) {
                    if (delivery.getResponse()) {
                        putNotification(delivery.getID());
                        String msg = "";
                        if(delivery.getStatus() == 1)
                            msg = "Order was marked as okay";
                        if(delivery.getStatus() == 5)
                            msg = "Order was cancelled";
                        notificationService.sendNotification("Delivery update for: " + delivery.getMedicine().getName(), msg);
                        putResponse(delivery.getID());
                    }
                    else if(delivery.getStatus() == 2 && delivery.getRiderNotification()){
                        notificationService.sendNotification("Delivery problem with: " + delivery.getMedicine().getName(), "Check for more information");
                        delivery.setRiderNotification(false);
                        putNotification(delivery.getID());
                    }
                }
                updateFilter();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
    private void alertChecker() {
        Refresher.handler.postDelayed(runnable = new Runnable() {
            public void run() {
                getDeliveries(STATIC.riderID);
                Refresher.handler.postDelayed(runnable, Refresher.apiDelayed);
            }
        }, Refresher.apiDelayed);

    }
    private void putResponse(int ID) {
        service = restClient.getClient().create(Service.class);
        Delivery delivery = new Delivery(ID);
        delivery.setResponse(false);
        Call<Delivery> calll = service.updateResponse(Integer.toString(ID), delivery);
        calll.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Delivery delivery = response.body();
                if (delivery != null) {
                    String responseCode = delivery.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + delivery.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + delivery.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(MyDeliveries.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.i("Fail1", "ffff");
                    } else {
                        Log.i("Success", "Success");
                    }
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                Log.i("Fail2", "fff");

                call.cancel();
            }
        });

    }
    private void putNotification(int ID) {
        service = restClient.getClient().create(Service.class);
        Delivery delivery = new Delivery(ID);
        delivery.setRiderNotification(false);
        Call<Delivery> calll = service.updateNotificationRider(Integer.toString(ID), delivery);
        calll.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Delivery delivery = response.body();
                if (delivery != null) {
                    String responseCode = delivery.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + delivery.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + delivery.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(MyDeliveries.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.i("Fail1", "ffff");
                    } else {
                        Log.i("Success", "Success");
                    }
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                Log.i("Fail2", "fff");

                call.cancel();
            }
        });

    }
    public class CustomAdapter extends BaseAdapter {
        public CustomAdapter() {
        }

        @Override
        public int getCount() {
            return filterList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_deliveries, viewGroup, false);
            LinearLayout lay = view.findViewById(R.id.lay_delivery);
            TextView txtName = view.findViewById(R.id.list_del_name);
            TextView txtID = view.findViewById(R.id.list_del_id);
            TextView txtHosp = view.findViewById(R.id.list_del_hosp);
            txtHosp.setText(filterList.get(i).getHospital().getName());
            txtID.setText(filterList.get(i).getAmount().toString());
            txtName.setText(filterList.get(i).getMedicine().getName());
            ImageButton btn = view.findViewById(R.id.list_del_statusBtn);
            switch (filterList.get(i).getStatus()) {
                case 0:
                    btn.setColorFilter(Color.rgb(255, 153, 51));
                    break;
                case 1:
                    btn.setColorFilter(Color.rgb(255, 255, 50));
                    break;
                case 2:
                    btn.setColorFilter(Color.rgb(255, 40, 40));
                    break;
                case 3:
                    btn.setColorFilter(Color.rgb(51, 255, 51));
                    break;
                case 4:
                    btn.setColorFilter(Color.rgb(160, 160, 160));
                    break;
            }
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(filterList.get(i).getStatus() == 0) {
                          Intent intent = new Intent(getBaseContext(), StartDelivery.class);
                         intent.putExtra("id", filterList.get(i).getID());
                         startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getBaseContext(), DeliveryDetails.class);
                        intent.putExtra("id", filterList.get(i).getID());
                        startActivity(intent);
                    }
                }
            });
            return view;
        }
    }
    private void createInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_colourinfo);
        ImageView btn0 = dialog.findViewById(R.id.dia_clrinfo_btn0);
        btn0.setColorFilter(Color.rgb(160, 160, 160));
        ImageView btn1 = dialog.findViewById(R.id.dia_clrinfo_btn1);
        btn1.setColorFilter(Color.rgb(255, 255, 50));
        ImageView btn2 = dialog.findViewById(R.id.dia_clrinfo_btn2);
        btn2.setColorFilter(Color.rgb(255, 165, 0));
        ImageView btn3 = dialog.findViewById(R.id.dia_clrinfo_btn3);
        btn3.setColorFilter(Color.rgb(51, 255, 51));
        ImageView btn4 = dialog.findViewById(R.id.dia_clrinfo_btn4);
        btn4.setColorFilter(Color.rgb(255, 40, 40));
        dialog.show();
    }
    @Override
    public void onBackPressed() {

    }
    private void filterMenu() {
        navigationView = findViewById(R.id.filtermenu);
        nav_Menu = navigationView.getMenu();
        waiting = true;
        underway = true;
        issue = true;
        delivered = true;
        cancelled = true;
        CompoundButton chbCancelled = (CompoundButton) MenuItemCompat.getActionView(nav_Menu.findItem(R.id.fnav_cancelled));
        chbCancelled.setChecked(true);
        chbCancelled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cancelled = isChecked;
                updateFilter();
            }
        });
        CompoundButton chbDelivered = (CompoundButton) MenuItemCompat.getActionView(nav_Menu.findItem(R.id.fnav_delivered));
        chbDelivered.setChecked(true);
        chbDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                delivered = isChecked;
                updateFilter();
            }
        });
        CompoundButton chbIssue = (CompoundButton) MenuItemCompat.getActionView(nav_Menu.findItem(R.id.fnav_issue));
        chbIssue.setChecked(true);
        chbIssue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                issue = isChecked;
                updateFilter();
            }
        });
        CompoundButton chbUnderway = (CompoundButton) MenuItemCompat.getActionView(nav_Menu.findItem(R.id.fnav_underway));
        chbUnderway.setChecked(true);
        chbUnderway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                underway = isChecked;
                updateFilter();
            }
        });
        CompoundButton chbWaiting = (CompoundButton) MenuItemCompat.getActionView(nav_Menu.findItem(R.id.fnav_waiting));
        chbWaiting.setChecked(true);
        chbWaiting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waiting = isChecked;
                updateFilter();
            }
        });
    }
    private void updateFilter() {
        filterList = new ArrayList<Delivery>();
        for(int c = 0; c < deliveryList.size(); c++) {
            if(deliveryList.get(c).getMedicine().getName().toLowerCase().contains(s.toString().toLowerCase()) ||deliveryList.get(c).getHospital().getName().toLowerCase().contains(s.toString().toLowerCase())) {
                if(waiting && deliveryList.get(c).getStatus() == 0 || underway && deliveryList.get(c).getStatus() == 1 || issue && deliveryList.get(c).getStatus() == 2 || delivered && deliveryList.get(c).getStatus() == 3 || cancelled && deliveryList.get(c).getStatus() == 4)
                    filterList.add(deliveryList.get(c));
            }
        }
        customAdapter.notifyDataSetChanged();
    }
}
