package com.carlv.medicaldeliveryrider.Helpers;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineDataSet;

import static android.graphics.Color.rgb;

public class ChartStyler {
    int accentcolor = rgb(219, 41, 85);
    int textcolor = rgb(219, 41, 85);
    int background = rgb(255,255,255);

    public void SetChartColor(LineDataSet lineDataSet){
        lineDataSet.setColor(accentcolor);
        lineDataSet.setCircleColor(accentcolor);
        lineDataSet.setDrawCircles(false);
    }

    public void SetBarChartColor(BarDataSet barDataSet){
        barDataSet.setColor(accentcolor);
    }

    public void SetChartLegendColor(LineChart lineChart){
        lineChart.getXAxis().setTextColor(textcolor);
        lineChart.getLegend().setTextColor(textcolor);
        lineChart.getAxisLeft().setTextColor(textcolor);
        lineChart.getAxisRight().setTextColor(textcolor);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(background);
        lineChart.getDescription().setEnabled(false);
    }

    public void SetBarChartLegendColor(BarChart barChart){
        barChart.getXAxis().setTextColor(textcolor);
        barChart.getLegend().setTextColor(textcolor);
        barChart.getAxisLeft().setTextColor(textcolor);
        barChart.getAxisRight().setTextColor(textcolor);
        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(background);
        barChart.getDescription().setEnabled(false);
    }
}
