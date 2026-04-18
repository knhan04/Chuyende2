package com.example.healthcareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.*;

public class LoginActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    Button btnLogin;
    TextView tvRegister;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.editText_Name);
        edPassword = findViewById(R.id.editText_pass);
        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.textView_register);

        apiService = ApiClient.getApiService();

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();

        LoginRequest request = new LoginRequest(username, password);

        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        LoginResponse loginData = response.body().getData();
                        if (loginData != null && loginData.getToken() != null && !loginData.getToken().isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("token", loginData.getToken());
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            return;
                        }
                    }
                    String message = response.body().getMessage();
                    Toast.makeText(LoginActivity.this, message != null ? message : "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                } else {
                    String error = "Login failed";
                    if (response.errorBody() != null) {
                        try {
                            error += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            error += ": unable to read error body";
                        }
                    }
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

