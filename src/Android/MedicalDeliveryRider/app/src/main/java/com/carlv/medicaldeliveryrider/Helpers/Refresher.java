package com.carlv.medicaldeliveryrider.Helpers;

import android.os.Handler;

public interface Refresher {
    Handler handler = new Handler();
    int apiDelayed = 15*1000;
}
