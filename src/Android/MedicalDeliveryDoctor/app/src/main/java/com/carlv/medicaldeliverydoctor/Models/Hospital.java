package com.carlv.medicaldeliverydoctor.Models;

import com.google.gson.annotations.SerializedName;

public class Hospital {
    @SerializedName("ID")
    Integer ID;
    @SerializedName("lat")
    Double lat;
    @SerializedName("lon")
    Double lon;
    @SerializedName("name")
    String name;
    @SerializedName("address")
    String address;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;
    public Hospital(Integer ID, String name, Double lat, Double lon, String address) {
        this.ID = ID;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }
    public Hospital(String name, Double lat, Double lon, String address) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }
  public void setName(String name) {
        this.name = name;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public Integer getID() {
        return ID;
    }
    public String getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
