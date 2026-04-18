package com.example.healthcareapp;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/api/"; // For Android emulator, use 10.0.2.2 for localhost

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }

    private static class AuthInterceptor implements Interceptor {
        private final Context context;

        public AuthInterceptor(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            SharedPreferences preferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String token = preferences.getString("token", null);

            if (token != null && !token.isEmpty()) {
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(request);
            }
            return chain.proceed(original);
        }
    }
}