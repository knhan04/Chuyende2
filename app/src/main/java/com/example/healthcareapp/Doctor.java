package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class Doctor {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("specialty")
    private String specialty;

    @SerializedName("location")
    private String address;

    @SerializedName("experience")
    private String experience;

    @SerializedName("phone")
    private String phone;

    @SerializedName("price")
    private float fee;

    @SerializedName("shift")
    private String shift;

    public Doctor(String name, String specialty, String address, String phone, float fee, String shift) {
        this.name = name;
        this.specialty = specialty;
        this.address = address;
        this.phone = phone;
        this.fee = fee;
        this.shift = shift;
        this.experience = "5 years"; // Giá trị mặc định hoặc có thể thêm vào constructor
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getAddress() { return address; }
    public String getExperience() { return experience; }
    public String getPhone() { return phone; }
    public float getFee() { return fee; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}
