package com.carlv.medicaldeliveryrider.Models;

import com.google.gson.annotations.SerializedName;

public class Rider {
    @SerializedName("ID")
    Integer ID;
    @SerializedName("firstName")
    String firstName;
    @SerializedName("lastName")
    String lastName;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;
    public Rider(Integer ID, String firstName, String lastName) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Rider(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
