package com.carlv.medicaldeliverydoctor.Activities.Medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carlv.medicaldeliverydoctor.Menu.NavigationMenu;
import com.carlv.medicaldeliverydoctor.Models.Hospital;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
import com.carlv.medicaldeliverydoctor.R;
import com.carlv.medicaldeliverydoctor.Helpers.RestClient;
import com.carlv.medicaldeliverydoctor.Helpers.Service;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineActivity extends AppCompatActivity {
    NavigationMenu navigationMenu;
    List<Medicine> medicineList;
    ListView listview_medicine;
    CustomAdapter customAdapter;
    Button btnNewMedicine;
    RestClient restClient;
    List<Medicine> filterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        navigationMenu = new NavigationMenu(this);
        customAdapter = new CustomAdapter();
        listview_medicine = findViewById(R.id.med_list);
        btnNewMedicine = findViewById(R.id.med_btn);
        btnNewMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NewMedicineActivity.class);
                startActivity(intent);
            }
        });
        loadSize();
        EditText searchFilter = findViewById(R.id.searchmedicine);

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
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void loadSize() {
        Service service = restClient.getClient().create(Service.class);
        Call<JsonArray> jsonCall = service.readJsonArrayMedicine();
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Medicine>>() {}.getType();
                medicineList = new Gson().fromJson(jsonString, listType);
                filterList = medicineList;
                listview_medicine.setAdapter(customAdapter);
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_medicine, viewGroup, false);
            TextView txtName = view.findViewById(R.id.list_medname);
            txtName.setText(filterList.get(i).getName());
            LinearLayout lay = view.findViewById(R.id.lay_med);
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), EditMedicineActivity.class);
                    intent.putExtra("name", filterList.get(i).getName());
                    intent.putExtra("minTemp", filterList.get(i).getMinTemp());
                    intent.putExtra("maxTemp", filterList.get(i).getMaxTemp());
                    intent.putExtra("minHumid", filterList.get(i).getMinHumid());
                    intent.putExtra("maxHumid", filterList.get(i).getMaxHumid());
                    intent.putExtra("id", filterList.get(i).getID());
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
