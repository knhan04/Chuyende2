package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("username")
    private String username;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("address")
    private String address;

    @SerializedName("contact")
    private String contact;

    @SerializedName("pincode")
    private int pincode;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("amount")
    private float amount;

    @SerializedName("otype")
    private String otype;

    public Order() {
    }

    public Order(String username, String fullname, String address, String contact, int pincode, String date, String time, float amount, String otype) {
        this.username = username;
        this.fullname = fullname;
        this.address = address;
        this.contact = contact;
        this.pincode = pincode;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.otype = otype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getOtype() {
        return otype;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }
}
