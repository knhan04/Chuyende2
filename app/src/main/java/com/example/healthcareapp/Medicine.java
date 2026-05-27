package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class Medicine {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private float price;

    public Medicine(String name, String description, float price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public float getPrice() { return price; }
}
