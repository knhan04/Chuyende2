package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class DoctorShiftUpdate {
    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("shift")
    private String shift;

    public DoctorShiftUpdate(int doctorId, String shift) {
        this.doctorId = doctorId;
        this.shift = shift;
    }
}
