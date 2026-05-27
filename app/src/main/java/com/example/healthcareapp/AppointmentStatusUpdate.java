package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class AppointmentStatusUpdate {
    @SerializedName("appointment_id")
    private int appointmentId;

    @SerializedName("status")
    private String status;

    public AppointmentStatusUpdate(int appointmentId, String status) {
        this.appointmentId = appointmentId;
        this.status = status;
    }
}
