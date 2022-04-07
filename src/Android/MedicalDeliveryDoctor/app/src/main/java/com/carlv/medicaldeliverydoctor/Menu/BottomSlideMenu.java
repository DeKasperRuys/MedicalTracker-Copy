package com.carlv.medicaldeliverydoctor.Menu;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.carlv.medicaldeliverydoctor.Models.Delivery;
import com.carlv.medicaldeliverydoctor.Models.Update;
import com.carlv.medicaldeliverydoctor.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;

public class BottomSlideMenu {
    Activity activity;
    TextView txtTemp, txtHumid, txtOrientation, txtMovement;
    SlidingUpPanelLayout bottomPanel;
    ImageButton btnBack;
    DecimalFormat df2;
    public BottomSlideMenu(Activity activity) {
        this.activity = activity;
        df2 = new DecimalFormat("#.##");
        bottomPanel = activity.findViewById(R.id.sliding_layout);
        bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        txtTemp = activity.findViewById(R.id.deldetmenu_temp);
        txtHumid = activity.findViewById(R.id.deldetmenu_humid);
        txtMovement = activity.findViewById(R.id.deldetmenu_movement);
        txtOrientation = activity.findViewById(R.id.deldetmenu_orientation);
        btnBack = activity.findViewById(R.id.deldetmenu_btnBack);
    }
    public void setBottomPanel(Delivery delivery, Update update) {
        bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        txtTemp.setText("Temperature: " + df2.format(update.getTemp()) + "℃");
        txtHumid.setText("Humidity: " + df2.format(update.getHumid()) + "%");
        if(!update.getMovement())
            txtMovement.setText("Movement: Okay");
        else
            txtMovement.setText("Movement: Issue");
        if(!update.getOrientation())
            txtOrientation.setText("Orientation: Okay");
        else
            txtOrientation.setText("Orientation: Issue");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePanel();
            }
        });
    }
    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return bottomPanel;
    }
    public void closePanel() {
        if(bottomPanel.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            bottomPanel.setPanelHeight(0);
        }
    }
}
