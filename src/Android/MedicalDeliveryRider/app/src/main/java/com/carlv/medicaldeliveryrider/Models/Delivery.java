package com.carlv.medicaldeliveryrider.Models;

import com.google.gson.annotations.SerializedName;

public class Delivery {
    @SerializedName("ID")
    Integer ID;
    @SerializedName("status")
    Integer status;
    @SerializedName("amount")
    Integer amount;
    @SerializedName("delivery")
    Delivery delivery;
    @SerializedName("hospital")
    Hospital hospital;
    @SerializedName("rider")
    Rider rider;
    @SerializedName("medicine")
    Medicine medicine;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;
    @SerializedName("notification")
    Boolean notification;
    @SerializedName("riderNotification")
    Boolean riderNotification;
    @SerializedName("response")
    Boolean response;
    public Delivery(Integer ID, Integer status, Hospital hospital, Medicine medicine, Rider rider, int amount, Boolean riderNotification, Boolean response) {
        this.ID = ID;
        this.status = status;
        this.hospital = hospital;
        this.rider = rider;
        this.medicine = medicine;
        this.amount = amount;
    }
    public Delivery(Integer ID, Integer status, Hospital hospital, Medicine medicine, Rider rider, Delivery delivery, int amount,  Boolean riderNotification, Boolean response) {
        this.ID = ID;
        this.status = status;
        this.hospital = hospital;
        this.rider = rider;
        this.medicine = medicine;
        this.delivery = delivery;
        this.amount = amount;
    }
    public Delivery(Integer ID) {
        this.ID = ID;
    }
    public Delivery(Integer ID, Rider rider) {
        this.ID = ID;
        this.rider = rider;

    }
    public void setID(Integer ID) {
        this.ID = ID;
    }


    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getID() {
        return ID;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public Integer getStatus() {
        return status;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public Rider getRider() {
        return rider;
    }
    public String getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public Boolean getNotification() {
        return notification;
    }

    public Boolean getResponse() {
        return response;
    }

    public Boolean getRiderNotification() {
        return riderNotification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public void setRiderNotification(Boolean riderNotification) {
        this.riderNotification = riderNotification;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
