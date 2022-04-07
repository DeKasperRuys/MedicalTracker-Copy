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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.STATIC;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Menu.NavigationMenu;
import com.carlv.medicaldeliveryrider.Models.Delivery;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDeliveries extends AppCompatActivity{
    RestClient restClient;
    Service service;
    List<Delivery> deliveryList;
    List<Delivery> filterList;

    NavigationMenu navigationMenu;
    ListView listView;
    CustomAdapter customAdapter;
    NavigationView navigationView;
    static Menu nav_Menu;
    Boolean cancelled, delivered, issue, underway, waiting;
    CharSequence s;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdeliveries);
        navigationMenu = new NavigationMenu(this);
        listView = findViewById(R.id.delivery_list);
        customAdapter = new CustomAdapter();
        service = restClient.getClient().create(Service.class);
        getDeliveries();
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
    private void getDeliveries() {
        Call<JsonArray> jsonCall = service.readJsonArrayNewDeliveries();
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Delivery>>() {}.getType();
                deliveryList = new Gson().fromJson(jsonString, listType);
                Iterator<Delivery> iter = deliveryList.iterator();
                while(iter.hasNext()) {
                    Delivery del = iter.next();
                    if(del.getRider() != null) {
                        iter.remove();
                    }
                }
                filterList = deliveryList;
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("onFailure", t.toString());
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
            btn.setColorFilter(Color.rgb(160, 160, 160));

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog(i);
                }
            });
            return view;
        }
    }
    private void createDialog(final int ID) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_acceptdelivery);
        Button btnYes = dialog.findViewById(R.id.dia_acceptdel_btnyes);
        Button btnNo = dialog.findViewById(R.id.dia_acceptdel_btnno);
        Button btnMaps = dialog.findViewById(R.id.dia_acceptdel_btnmap);
        TextView txtMedname = dialog.findViewById(R.id.dia_acceptdel_medname);
        TextView txtHospname = dialog.findViewById(R.id.dia_acceptdel_hospname);
        txtMedname.setText("Medicine: " + deliveryList.get(ID).getMedicine().getName());
        txtHospname.setText("Hospital: " + deliveryList.get(ID).getHospital().getName());
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putDelivery(deliveryList.get(ID).getID(), dialog);
            }
        });
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DeliveryHospitalMap.class);
                intent.putExtra("id", deliveryList.get(ID).getHospital().getID());
                startActivity(intent);
            }
        });
        dialog.show();
    }
    private void putDelivery(int ID, final Dialog  dialog) {
        final Delivery delivery = new Delivery(ID, STATIC.rider);
        Call<Delivery> call1 = service.updateDelivery(String.valueOf(ID), delivery);
        Log.d("TAG", Integer.toString(ID));
        call1.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {

                Delivery delivery = response.body();
                if (delivery != null) {

                    String responseCode = delivery.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + delivery.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + delivery.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(NewDeliveries.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(NewDeliveries.this, "Delivery added to your list", Toast.LENGTH_SHORT).show();
                        getDeliveries();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Log.d("TAG", "FAIL");

                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
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
                Log.d("Delivery", deliveryList.get(c).getHospital().getName());
                if(waiting && deliveryList.get(c).getStatus() == 0 || underway && deliveryList.get(c).getStatus() == 1 || issue && deliveryList.get(c).getStatus() == 2 || delivered && deliveryList.get(c).getStatus() == 3 || cancelled && deliveryList.get(c).getStatus() == 4)
                    filterList.add(deliveryList.get(c));
            }
        }
        customAdapter.notifyDataSetChanged();
    }
}
