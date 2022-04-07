package com.carlv.medicaldeliveryrider.Activities.Rider;
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

import com.carlv.medicaldeliveryrider.Activities.Deliveries.MyDeliveries;
import com.carlv.medicaldeliveryrider.Helpers.RestClient;
import com.carlv.medicaldeliveryrider.Helpers.STATIC;
import com.carlv.medicaldeliveryrider.Helpers.Service;
import com.carlv.medicaldeliveryrider.MainActivity;
import com.carlv.medicaldeliveryrider.Models.Rider;
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

public class RiderSelectActivity extends AppCompatActivity {
    List<Rider> riderList;
    List<Rider> filterList;

    ListView listview_Riders;
    CustomAdapter customAdapter;
    RestClient restClient;
    Button btnNewRider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderselect);
        btnNewRider = findViewById(R.id.riderselect_btnNew);
        listview_Riders = findViewById(R.id.list_riders);
        customAdapter = new CustomAdapter();
        loadSize();
        btnNewRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NewRiderActivity.class);
                startActivity(intent);
            }
        });
        EditText searchFilter = findViewById(R.id.searchrider);

        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList = new ArrayList<Rider>();
                for(int c = 0; c < riderList.size(); c++) {
                    if(riderList.get(c).getFirstName().toLowerCase().contains(charSequence.toString().toLowerCase()) || riderList.get(c).getLastName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(riderList.get(c));
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
        Call<JsonArray> jsonCall = service.readJsonArrayRiders();
        jsonCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonString = response.body().toString();
                Log.i("onResponse", jsonString);
                Type listType = new TypeToken<List<Rider>>() {
                }.getType();
                riderList = new Gson().fromJson(jsonString, listType);
                filterList = riderList;
                listview_Riders.setAdapter(customAdapter);
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_riders, viewGroup, false);
            TextView txtName = view.findViewById(R.id.list_ridername);
            txtName.setText(filterList.get(i).getFirstName() + "  " + filterList.get(i).getLastName());
            LinearLayout lay = view.findViewById(R.id.lay_rider);

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), MyDeliveries.class);
                    STATIC.riderID =  filterList.get(i).getID();
                    STATIC.rider = filterList.get(i);
                    startActivity(intent);
                    finish();
                }
            });
            return view;
        }
    }
}

