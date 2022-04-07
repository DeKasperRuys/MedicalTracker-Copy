package com.carlv.medicaldeliverydoctor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.carlv.medicaldeliverydoctor.Activities.Delivery.DeliveryDetailActivity;
import com.carlv.medicaldeliverydoctor.Helpers.NotificationService;
import com.carlv.medicaldeliverydoctor.Helpers.Refresher;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.STATIC;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.carlv.medicaldeliverydoctor.Menu.NavigationMenu;
import com.carlv.medicaldeliverydoctor.Models.Delivery;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    NavigationMenu navigationMenu;
    List<Delivery> deliveryList;
    List<Delivery> deliveryFilter;
    RestClient restClient;
    CustomAdapter customAdapter;
    ListView listView;
    Runnable runnable;
    NotificationService notificationService;
    Service service;
    Button newDel;
    Medicine selectedMedicine;
    List<Medicine> filterList;
    List<Medicine> medicineList;
    NavigationView navigationView;
    static Menu nav_Menu;
    Boolean cancelled, delivered, issue, underway, waiting;
    CharSequence s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationMenu = new NavigationMenu(this);
        customAdapter = new CustomAdapter();
        listView = findViewById(R.id.delivery_list);
        newDel = findViewById(R.id.del_btnNewDel);
        Refresher.handler.removeCallbacksAndMessages(null);
        loadSize();
        alertChecker();
        notificationService = new NotificationService(this);
        service = restClient.getClient().create(Service.class);
        newDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNewMedicine();
            }
        });
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
                Log.d("TAG", charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadSize() {
        service = restClient.getClient().create(Service.class);
        Call<JsonArray> jsonCall = service.readJsonArrayDeliveries(Integer.toString(STATIC.hospitalID));
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Delivery>>() {
                }.getType();
                deliveryList = new Gson().fromJson(jsonString, listType);
                deliveryFilter = deliveryList;
                listView.setAdapter(customAdapter);
                for (Delivery delivery : deliveryList) {
                    if (delivery.getStatus() == 2 && delivery.getDoctorNotification()) {
                        notificationService.sendNotification("Delivery problem with: " + delivery.getMedicine().getName(), "Check for more information");
                        putNotification(delivery.getID(), false);
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
    private void putNotification(int ID, boolean notification) {
        service = restClient.getClient().create(Service.class);
        final Delivery delivery = new Delivery(ID);
        delivery.setDoctorNotification(false);
        Call<Delivery> calll = service.updateNotificationDoctor(Integer.toString(ID), delivery);
        calll.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Delivery delivery = response.body();
                if (delivery != null) {
                    String responseCode = delivery.getResponseCode();
                    Log.e("Response Code", "getResponseCode  -->  " + delivery.getResponseCode());
                    Log.e("Response Message", "getResponseMessage  -->  " + delivery.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                    }
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }
    private void alertChecker() {
        Refresher.handler.postDelayed(runnable = new Runnable() {
            public void run() {
                loadSize();
                Refresher.handler.postDelayed(runnable, Refresher.apiDelayed);
            }
        }, Refresher.apiDelayed);

    }
    private void dialogCreater(final Delivery delivery) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancelpickup);
        Button btnCancel = dialog.findViewById(R.id.dia_cancel_btnCancel);
        Button btnBack = dialog.findViewById(R.id.dia_cancel_btnBack);
        TextView txtCancel = dialog.findViewById(R.id.dia_cancel_txt);
        txtCancel.setText("Medicine has not been picked up yet \n Are you sure you want to cancel?");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrder(delivery);
                dialog.dismiss();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void dialogNewMedicine() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_newdelivery);
        loadMedicine();
        Button btnPickMedicine = dialog.findViewById(R.id.newdel_btnMedicine);
        Button btnCreate = dialog.findViewById(R.id.newdel_btnCreate);
        TextView txtHospName = dialog.findViewById(R.id.newdel_hospName);
        final EditText edit_amount = dialog.findViewById(R.id.newdel_amount);
        txtHospName.setText("For " + STATIC.hospitalName);
        btnCreate.setEnabled(false);
        if(selectedMedicine != null) {
            btnPickMedicine.setText(selectedMedicine.getName());
            btnCreate.setEnabled(true);
        }
        btnPickMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogMedicinePicker();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_amount.getText().toString().trim().equals("") && Integer.valueOf(edit_amount.getText().toString()) < 10 && Integer.valueOf(edit_amount.getText().toString()) > 0) {
                        postDelivery(selectedMedicine.getID(), STATIC.hospitalID, dialog, Integer.valueOf(edit_amount.getText().toString()));
                        selectedMedicine = null;
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                selectedMedicine = null;
            }
        });
        dialog.show();
    }
    private void postDelivery(int medicineID, int hospitalID, final Dialog dialog, int amount) {
        Delivery delivery = new Delivery();
        delivery.setAmount(amount);
        Call<Delivery> call1 = service.createDelivery(Integer.toString(hospitalID), Integer.toString(medicineID), delivery);
        call1.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Delivery delivery = response.body();

                if (delivery != null) {
                    String responseCode = delivery.getResponseCode();
                    Log.e("responseCode", "getResponseCode  -->  " + delivery.getResponseCode());
                    Log.e("responseMessage", "getResponseMessage  -->  " + delivery.getResponseMessage());
                    if (responseCode != null && responseCode.equals("404")) {
                        Toast.makeText(MainActivity.this, "Error: Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Success", "Success");
                        Toast.makeText(MainActivity.this, "New Delivery added", Toast.LENGTH_SHORT).show();
                        loadSize();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                Log.d("Error", t.toString());
                call.cancel();
            }
        });
    }
    private void loadMedicine() {
        Call<JsonArray> jsonCall = service.readJsonArrayMedicine();
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Medicine>>() {}.getType();
                medicineList = new Gson().fromJson(jsonString, listType);
                filterList = medicineList;
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }
    private void dialogMedicinePicker() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_medicinepicker);
        ListView listView = dialog.findViewById(R.id.dialog_medicine_scrollview);
        EditText searchFilter = dialog.findViewById(R.id.searchmedicine);
        final MedicineAdapter medicineAdapter = new MedicineAdapter(dialog);
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList = new ArrayList<Medicine>();
                for(int c = 0; c < medicineList.size(); c++) {
                    if(medicineList.get(c).getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(medicineList.get(c));
                    }
                }
                medicineAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listView.setAdapter(medicineAdapter);
        dialog.show();
    }
    private void setOrder(Delivery delivery) {
        delivery.setStatus(4);
        Service service = restClient.getClient().create(Service.class);
        Call<Delivery> jsonCall = service.updateDelivery(delivery.getID().toString(), delivery);
        jsonCall.enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                Toast.makeText(MainActivity.this, "Delivery cancelled", Toast.LENGTH_SHORT).show();
                loadSize();
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

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
        deliveryFilter = new ArrayList<Delivery>();
        for(int c = 0; c < deliveryList.size(); c++) {
            if(deliveryList.get(c).getMedicine().getName().toLowerCase().contains(s.toString().toLowerCase())) {
                if(waiting && deliveryList.get(c).getStatus() == 0 || underway && deliveryList.get(c).getStatus() == 1 || issue && deliveryList.get(c).getStatus() == 2 || delivered && deliveryList.get(c).getStatus() == 3 || cancelled && deliveryList.get(c).getStatus() == 4)
                    deliveryFilter.add(deliveryList.get(c));
            }
        }
        customAdapter.notifyDataSetChanged();
    }
    public class CustomAdapter extends BaseAdapter {
        public CustomAdapter() {
        }

        @Override
        public int getCount() {
            return deliveryFilter.size();
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
            TextView txtStatus = view.findViewById(R.id.list_del_status);
            txtID.setText(deliveryFilter.get(i).getAmount().toString());
            txtName.setText(deliveryFilter.get(i).getMedicine().getName());
            ImageButton btn = view.findViewById(R.id.list_del_statusBtn);
            switch (deliveryFilter.get(i).getStatus()) {
                case 0:
                    btn.setColorFilter(Color.rgb(255, 153, 51));
                    txtStatus.setText("Pending");
                    break;
                case 1:
                    btn.setColorFilter(Color.rgb(255, 255, 50));
                    txtStatus.setText("Underway");
                    break;
                case 2:
                    btn.setColorFilter(Color.rgb(255, 40, 40));
                    txtStatus.setText("Issue");
                    break;
                case 3:
                    btn.setColorFilter(Color.rgb(51, 255, 51));
                    txtStatus.setText("Delivered");
                    break;
                case 4:
                    btn.setColorFilter(Color.rgb(160, 160, 160));
                    txtStatus.setText("Cancelled");
                    break;
            }
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deliveryFilter.get(i).getStatus() == 0) {
                        dialogCreater(deliveryFilter.get(i));
                    } else {
                        Intent intent = new Intent(getBaseContext(), DeliveryDetailActivity.class);
                        intent.putExtra("id", deliveryFilter.get(i).getID());
                        startActivity(intent);
                    }
                }
            });
            return view;

        }
    }
    public class MedicineAdapter extends BaseAdapter {
        Dialog dialog;
        public MedicineAdapter(Dialog dialog) {
            this.dialog = dialog;
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_medicine, viewGroup, false);
            TextView txtName = view.findViewById(R.id.list_medname);
            txtName.setText(filterList.get(i).getName());
            LinearLayout lay = view.findViewById(R.id.lay_med);

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedMedicine = filterList.get(i);
                    dialog.dismiss();
                    dialogNewMedicine();
                }
            });
            return view;
        }
    }
}

