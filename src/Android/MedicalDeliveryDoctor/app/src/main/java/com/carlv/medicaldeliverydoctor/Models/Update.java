package com.carlv.medicaldeliverydoctor.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Update {
    @SerializedName("ID")
    Integer ID;
    @SerializedName("temp")
    Double temp;
    @SerializedName("humid")
    Double humid;
    @SerializedName("delivery")
    Delivery delivery;
    @SerializedName("timeStamp")
    String timeStamp;
    @SerializedName("lat")
    Float lat;
    @SerializedName("lon")
    Float lon;
    @SerializedName("movement")
    Boolean movement;
    @SerializedName("orientation")
    Boolean orientation;
    public Update(Integer ID, Delivery delivery, String timeStamp, Float lat, Float lon, Double temp, Double humid, Boolean movement, Boolean orientation) {
        this.ID = ID;
        this.delivery = delivery;
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.humid = humid;
        this.movement = movement;
        this.orientation = orientation;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void setOrientation(Boolean orientation) {
        this.orientation = orientation;
    }

    public void setMovement(Boolean movement) {
        this.movement = movement;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void setHumid(Double humid) {
        this.humid = humid;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public Integer getID() {
        return ID;
    }

    public Boolean getOrientation() {
        return orientation;
    }

    public Boolean getMovement() {
        return movement;
    }

    public Float getLat() {
        return lat;
    }

    public Float getLon() {
        return lon;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Double getHumid() {
        return humid;
    }

    public Double getTemp() {
        return temp;
    }
}
