package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("doctor_name")
    private String doctorName;

    @SerializedName("doctor_specialty")
    private String doctorSpecialty;

    @SerializedName("appointment_date")
    private String appointmentDate;

    @SerializedName("appointment_time")
    private String appointmentTime;

    @SerializedName("symptoms")
    private String symptoms;

    @SerializedName("fee")
    private float fee;

    @SerializedName("status")
    private String status;

    @SerializedName("shift")
    private String shift;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("patient_phone")
    private String patientPhone;

    // Getters and Setters
    public int getId() { return id; }
    public String getDoctorName() { return doctorName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getShift() { return shift; }
    public String getPatientName() { return patientName; }
}
