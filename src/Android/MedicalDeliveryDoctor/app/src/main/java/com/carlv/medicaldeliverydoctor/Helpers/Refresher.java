package com.carlv.medicaldeliverydoctor.Helpers;

import android.os.Handler;

import com.carlv.medicaldeliverydoctor.Models.Delivery;

import java.util.List;

public interface Refresher {
    Handler handler = new Handler();
    int apiDelayed = 10*1000;
}
