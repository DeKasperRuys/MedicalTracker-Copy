package com.carlv.medicaldeliveryrider.Menu;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.carlv.medicaldeliveryrider.Activities.Deliveries.MyDeliveries;
import com.carlv.medicaldeliveryrider.Activities.Deliveries.NewDeliveries;
import com.carlv.medicaldeliveryrider.Activities.Hospital.HospitalActivity;
import com.carlv.medicaldeliveryrider.Activities.Medicine.MedicineActivity;
import com.carlv.medicaldeliveryrider.R;
import com.google.android.material.navigation.NavigationView;

public class NavigationMenu {
    NavigationView navigationView;
    static Menu nav_Menu;
    Intent intent;

    public NavigationMenu(final Activity activity) {
        navigationView = activity.findViewById(R.id.menu);
        nav_Menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_deliveries:
                        intent = new Intent(activity.getBaseContext(), MyDeliveries.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.nav_newDeliveries:
                        intent = new Intent(activity.getBaseContext(), NewDeliveries.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.nav_hospitals:
                        intent = new Intent(activity.getBaseContext(), HospitalActivity.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.nav_medicine:
                        intent = new Intent(activity.getBaseContext(), MedicineActivity.class);
                        activity.startActivity(intent);
                        break;
                }
                return false;
            }
        });

    }

    public static Menu getNav_Menu() {
        return nav_Menu;
    }
}
