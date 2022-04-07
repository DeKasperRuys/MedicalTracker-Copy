package com.carlv.medicaldeliverydoctor.Menu;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.carlv.medicaldeliverydoctor.Activities.Hospital.HospitalActivity;
import com.carlv.medicaldeliverydoctor.Activities.Medicine.MedicineActivity;
import com.carlv.medicaldeliverydoctor.MainActivity;
import com.carlv.medicaldeliverydoctor.R;

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
                        intent = new Intent(activity.getBaseContext(), MainActivity.class);
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
