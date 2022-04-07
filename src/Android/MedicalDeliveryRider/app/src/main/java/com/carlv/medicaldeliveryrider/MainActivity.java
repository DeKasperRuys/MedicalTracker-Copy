package com.carlv.medicaldeliveryrider;

import android.os.Bundle;

import com.carlv.medicaldeliveryrider.Menu.NavigationMenu;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    NavigationMenu navigationMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationMenu = new NavigationMenu(this);
    }
}
