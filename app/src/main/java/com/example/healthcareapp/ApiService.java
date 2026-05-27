package com.example.healthcareapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

public interface ApiService {

    // ================= USER =================

    // Register
    @POST("auth/register")
    Call<ApiResponse> register(@Body User user);

    // Login
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    // ================= CART =================

    // Add to cart
    @POST("cart")
    Call<ApiResponse> addToCart(@Body CartItem cartItem);

    // Check cart
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

    // Remove item from cart
    @POST("cart/remove")
    Call<ApiResponse> removeCart(
            @Query("username") String username,
            @Query("otype") String otype
    );

    // ================= ORDER =================

    @POST("orders")
    Call<ApiResponse> addOrder(@Body Order order);

    @GET("orders")
    Call<ApiResponse<List<String>>> getOrderData(@Query("username") String username);

    @GET("appointments/check")
    Call<ApiResponse<Integer>> checkAppointmentExists(
            @Query("username") String username,
            @Query("fullname") String fullname,
            @Query("address") String address,
            @Query("contact") String contact,
            @Query("date") String date,
            @Query("time") String time
    );

    @GET("appointments/count")
    Call<ApiResponse<Integer>> getAppointmentCount(
            @Query("doctorName") String doctorName,
            @Query("date") String date
    );

    @POST("orders/remove")
    Call<ApiResponse> removeOrder(
            @Query("fullname") String fullname,
            @Query("otype") String otype,
            @Query("address") String address
    );

    // ================= DOCTORS =================

    @GET("doctors")
    Call<ApiResponse<List<Doctor>>> getAllDoctors();

    @POST("doctors")
    Call<ApiResponse> addDoctor(@Body Doctor doctor);

    @PUT("doctors/{id}")
    Call<ApiResponse> updateDoctor(@Path("id") int id, @Body Doctor doctor);

    @DELETE("doctors/{id}")
    Call<ApiResponse> deleteDoctor(@Path("id") int id);

    // ================= MEDICINES =================

    @GET("medicines")
    Call<ApiResponse<List<Medicine>>> getAllMedicines();

    @POST("medicines")
    Call<ApiResponse> addMedicine(@Body Medicine medicine);

    // ================= ADMIN (Special) =================

    @GET("admin/appointments")
    Call<ApiResponse<List<Appointment>>> getAllAppointments();

    @GET("auth/users")
    Call<ApiResponse<List<User>>> getAllUsers();
}