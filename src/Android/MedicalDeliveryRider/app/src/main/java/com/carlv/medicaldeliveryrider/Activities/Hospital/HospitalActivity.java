package com.carlv.medicaldeliveryrider.Activities.Hospital;

import android.content.Intent;
import android.os.Bundle;
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

import com.carlv.medicaldeliveryrider.Activities.Medicine.MedicineActivity;
import com.carlv.medicaldeliveryrider.Activities.Medicine.MedicineDetailActivity;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.Menu.NavigationMenu;
import com.carlv.medicaldeliveryrider.Models.Hospital;
import com.carlv.medicaldeliveryrider.Models.Medicine;
import com.carlv.medicaldeliveryrider.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalActivity extends AppCompatActivity {
    NavigationMenu navigationMenu;
    List<Hospital> hospitalList;
    ListView listview_hospital;
    CustomAdapter customAdapter;
    RestClient restClient;
    List<Hospital> filterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        navigationMenu = new NavigationMenu(this);
        customAdapter = new CustomAdapter();
        listview_hospital= findViewById(R.id.hospital_list);
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
                listview_hospital.setAdapter(customAdapter);
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_hospital, viewGroup, false);
            TextView txtName = view.findViewById(R.id.list_hospitalname);
            txtName.setText(filterList.get(i).getName());
            TextView txtAddress = view.findViewById(R.id.list_hospitalAddress);
            txtAddress.setText(filterList.get(i).getAddress());
            LinearLayout lay = view.findViewById(R.id.lay_hosp);
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), HospitalDetailActivity.class);
                    intent.putExtra("id", filterList.get(i).getID());
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
