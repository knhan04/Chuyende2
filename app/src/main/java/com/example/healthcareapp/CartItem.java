package com.example.healthcareapp;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("username")
    private String username;

    @SerializedName("product")
    private String product;

    @SerializedName("price")
    private float price;

    @SerializedName("otype")
    private String otype;

    public CartItem() {
    }

    public CartItem(String username, String product, float price, String otype) {
        this.username = username;
        this.product = product;
        this.price = price;
        this.otype = otype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOtype() {
        return otype;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }
}
