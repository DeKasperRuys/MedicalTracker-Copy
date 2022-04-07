package com.carlv.medicaldeliverydoctor.Activities.Hospital;

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

public class HospitalActivity extends AppCompatActivity {
    NavigationMenu navigationMenu;
    List<Hospital> hospitalList;
    ListView listview_Hospitals;
    List<Hospital> filterList;

    CustomAdapter customAdapter;
    Button btnNewHosp;
    RestClient restClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals);
        navigationMenu = new NavigationMenu(this);
        customAdapter = new CustomAdapter();
        listview_Hospitals = findViewById(R.id.hospital_list);
        btnNewHosp = findViewById(R.id.hosp_btn);
        btnNewHosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NewHospitalActivity.class);
                startActivity(intent);
            }
        });
        loadSize();
        EditText searchFilter = findViewById(R.id.searchhospital);
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList = new ArrayList<Hospital>();
                for(int c = 0; c < hospitalList.size(); c++) {
                    if(hospitalList.get(c).getName().toLowerCase().contains(charSequence.toString().toLowerCase()) || hospitalList.get(c).getAddress().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(hospitalList.get(c));
                    }
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
            loadSize();
    }
    private void loadSize() {
        Service service = restClient.getClient().create(Service.class);
        Call<JsonArray> jsonCall = service.readJsonArray();
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Hospital>>() {}.getType();
                hospitalList = new Gson().fromJson(jsonString, listType);
                filterList = hospitalList;
                listview_Hospitals.setAdapter(customAdapter);
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_hospitals, viewGroup, false);
            TextView txtName = view.findViewById(R.id.list_hospitalname);
            txtName.setText(filterList.get(i).getName());
            TextView txtAddress = view.findViewById(R.id.list_hospitalAddress);
            txtAddress.setText(filterList.get(i).getAddress());
            LinearLayout lay = view.findViewById(R.id.lay_hosp);

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), EditHospitalActivity.class);
                    intent.putExtra("name", filterList.get(i).getName());
                    intent.putExtra("lat", filterList.get(i).getLat());
                    intent.putExtra("id", filterList.get(i).getID());
                    intent.putExtra("lon", filterList.get(i).getLon());
                    startActivity(intent);
                }
            });
            return view;
        }
}
}