package com.carlv.medicaldeliverydoctor.Models;

import com.google.gson.annotations.SerializedName;

public class Medicine {
    @SerializedName("ID")
    Integer ID;
    @SerializedName("minTemp")
    Double minTemp;
    @SerializedName("maxTemp")
    Double maxTemp;
    @SerializedName("minHumid")
    Double minHumid;
    @SerializedName("maxHumid")
    Double maxHumid;
    @SerializedName("orientation")
    Boolean orientation;
    @SerializedName("name")
    String name;
    @SerializedName("movement")
    Boolean movement;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;
    public Medicine(Integer ID, String name, Double minTemp, Double maxTemp, Double minHumid, Double maxHumid, Boolean orientation, Boolean movement) {
        this.ID = ID;
        this.name = name;
        this.minTemp = minTemp;
        this.minHumid = minHumid;
        this.maxTemp = maxTemp;
        this.maxHumid = maxHumid;
        this.orientation = orientation;
        this.movement = movement;
    }
    public Medicine(String name, Double minTemp, Double maxTemp, Double minHumid, Double maxHumid) {
        this.name = name;
        this.minTemp = minTemp;
        this.minHumid = minHumid;
        this.maxTemp = maxTemp;
        this.maxHumid = maxHumid;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setMaxHumid(Double maxHumid) {
        this.maxHumid = maxHumid;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinHumid(Double minHumid) {
        this.minHumid = minHumid;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public void setMovement(Boolean movement) {
        this.movement = movement;
    }

    public void setOrientation(Boolean orientation) {
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public Integer getID() {
        return ID;
    }

    public Boolean getMovement() {
        return movement;
    }

    public Double getMaxHumid() {
        return maxHumid;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public Double getMinHumid() {
        return minHumid;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public Boolean getOrientation() {
        return orientation;
    }
    public String getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }
}
