package com.example.healthcareapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteDataBase {
    private static final String TAG = "RemoteDataBase";
    private ApiService apiService;

    public RemoteDataBase() {
        this.apiService = ApiClient.getApiService();
    }

    public RemoteDataBase(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    // Asynchronous methods - need callbacks or listeners in calling code

    public void register(String username, String email, String password, final DatabaseCallback<Void> callback) {
        User user = new User(username, email, password);
        Call<ApiResponse> call = apiService.register(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Registration failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void login(String username, String password, final DatabaseCallback<LoginResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<ApiResponse<LoginResponse>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Login failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void addtoCart(String username, String product, Float price, String otype, final DatabaseCallback<Void> callback) {
        CartItem cartItem = new CartItem(username, product, price, otype);
        Call<ApiResponse> call = apiService.addToCart(cartItem);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Add to cart failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void checkCart(String username, String product, final DatabaseCallback<Integer> callback) {
        Call<ApiResponse<Integer>> call = apiService.checkCart(username, product);
        call.enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Check cart failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void removeCart(String username, String otype, final DatabaseCallback<Void> callback) {
        Call<ApiResponse> call = apiService.removeCart(username, otype);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Remove cart failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getCart(String username, String otype, final DatabaseCallback<ArrayList<String>> callback) {
        Call<ApiResponse<List<String>>> call = apiService.getCart(username, otype);
        call.enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(new ArrayList<>(response.body().getData()));
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Get cart failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void addOrder(String username, String fullname, String address, String contact, int pincode, String date, String time, float price, String otype, final DatabaseCallback<Void> callback) {
        Order order = new Order(username, fullname, address, contact, pincode, date, time, price, otype);
        Call<ApiResponse> call = apiService.addOrder(order);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Add order failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void gerOrderData(String username, final DatabaseCallback<ArrayList<String>> callback) {
        Call<ApiResponse<List<String>>> call = apiService.getOrderData(username);
        call.enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(new ArrayList<>(response.body().getData()));
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Get order data failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void checkAppointmentExists(String username, String fullname, String address, String contact, String date, String time, final DatabaseCallback<Integer> callback) {
        Call<ApiResponse<Integer>> call = apiService.checkAppointmentExists(username, fullname, address, contact, date, time);
        call.enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Check appointment failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void removeOrder(String fullname, String otype, String address, final DatabaseCallback<Void> callback) {
        Call<ApiResponse> call = apiService.removeOrder(fullname, otype, address);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(response.body().getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Remove order failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Callback interface
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onFailure(Throwable t);
    }
}
