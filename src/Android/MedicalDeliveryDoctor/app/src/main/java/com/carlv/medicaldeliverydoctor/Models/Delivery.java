package com.carlv.medicaldeliverydoctor.Models;

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
    @SerializedName("notification")
    Boolean notification;
    @SerializedName("doctorNotification")
    Boolean doctorNotification;
    @SerializedName("response")
    Boolean response;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;
    public Delivery(Integer ID, Integer status, Hospital hospital, Medicine medicine, Rider rider, int amount, Boolean notification, Boolean doctorNotification, Boolean response) {
        this.ID = ID;
        this.status = status;
        this.hospital = hospital;
        this.rider = rider;
        this.medicine = medicine;
        this.notification = notification;
        this.doctorNotification = doctorNotification;
        this.response = response;
        this.amount = amount;
    }
    public Delivery(Integer ID, Integer status, Hospital hospital, Medicine medicine, Rider rider, int amount, Delivery delivery, Boolean notification, Boolean doctorNotification, Boolean response) {
        this.ID = ID;
        this.status = status;
        this.hospital = hospital;
        this.rider = rider;
        this.medicine = medicine;
        this.delivery = delivery;
        this.notification = notification;
        this.doctorNotification = doctorNotification;
        this.response = response;
        this.amount = amount;
    }
    public Delivery(Integer ID) {
        this.ID = ID;
    }
    public Delivery() {}
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

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }
    public String getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public Boolean getDoctorNotification() {
        return doctorNotification;
    }

    public void setDoctorNotification(Boolean doctorNotification) {
        this.doctorNotification = doctorNotification;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
