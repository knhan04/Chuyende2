package com.example.healthcareapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Body;

public interface ApiService {

    // ================= USER =================

    // Register
    @POST("auth/register")
    Call<ApiResponse> register(
            @Body User user
    );

    // Login
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(
            @Body LoginRequest loginRequest
    );

    // ================= CART =================

    // Add to cart
    @POST("cart")
    Call<ApiResponse> addToCart(
            @Body CartItem cartItem
    );

    // Check cart (kiểm tra sản phẩm đã tồn tại chưa)
    @GET("cart/check")
    Call<ApiResponse<Integer>> checkCart(
            @Query("username") String username,
            @Query("product") String product
    );

    // Get cart list
    @GET("cart")
    Call<ApiResponse<List<String>>> getCart(
            @Query("username") String username,
            @Query("otype") String otype
    );

    // Remove item from cart (FIX: dùng POST thay DELETE)
    @POST("cart/remove")
    Call<ApiResponse> removeCart(
            @Query("username") String username,
            @Query("otype") String otype
    );

    // ================= ORDER =================

    // Add order (đặt lịch / đặt thuốc)
    @POST("orders")
    Call<ApiResponse> addOrder(
            @Body Order order
    );

    // Get order list
    @GET("orders")
    Call<ApiResponse<List<String>>> getOrderData(
            @Query("username") String username
    );

    // Check appointment exists
    @GET("orders/check")
    Call<ApiResponse<Integer>> checkAppointmentExists(
            @Query("username") String username,
            @Query("fullname") String fullname,
            @Query("address") String address,
            @Query("contact") String contact,
            @Query("date") String date,
            @Query("time") String time
    );

    // Remove order (FIX: dùng POST thay DELETE)
    @POST("orders/remove")
    Call<ApiResponse> removeOrder(
            @Query("fullname") String fullname,
            @Query("otype") String otype,
            @Query("address") String address
    );
}