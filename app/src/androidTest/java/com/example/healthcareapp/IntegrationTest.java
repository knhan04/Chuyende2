package com.example.healthcareapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import android.content.Context;
import android.content.SharedPreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;



@RunWith(AndroidJUnit4.class)
public class IntegrationTest {

    private RemoteDataBase remoteDB;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        remoteDB = new RemoteDataBase(context);
    }

    @Test
    public void testLoginIntegration() throws InterruptedException {
        // Kịch bản: Kiểm tra đăng nhập với tài khoản admin
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        final StringBuilder errorMsg = new StringBuilder();

        remoteDB.login("admin", "admin123", new RemoteDataBase.DatabaseCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                if (result != null && result.getToken() != null) {
                    success[0] = true;
                    // Save token to SharedPreferences for subsequent requests
                    SharedPreferences preferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    preferences.edit().putString("token", result.getToken()).apply();
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                errorMsg.append(t.getMessage());
                latch.countDown();
            }
        });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue("Timeout chờ phản hồi từ server", completed);
        assertTrue("Kết nối Backend thất bại hoặc tài khoản không đúng: " + errorMsg.toString(), success[0]);
    }

    @Test
    public void testGetOrderDataIntegration() throws InterruptedException {
        // Kịch bản: Kiểm tra lấy dữ liệu đơn hàng từ Server
        // Đảm bảo đã đăng nhập trước khi lấy dữ liệu
        final CountDownLatch loginLatch = new CountDownLatch(1);
        remoteDB.login("admin", "admin123", new RemoteDataBase.DatabaseCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                if (result != null && result.getToken() != null) {
                    SharedPreferences preferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    preferences.edit().putString("token", result.getToken()).apply();
                }
                loginLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                loginLatch.countDown();
            }
        });
        loginLatch.await(5, TimeUnit.SECONDS);

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] dataReceived = {false};

        final StringBuilder errorMsg = new StringBuilder();

        remoteDB.gerOrderData("admin", new RemoteDataBase.DatabaseCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                dataReceived[0] = (result != null);
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                errorMsg.append(t.getMessage());
                latch.countDown();
            }
        });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue("Timeout chờ phản hồi từ server", completed);
        assertTrue("Không thể lấy dữ liệu từ Server: " + errorMsg.toString(), dataReceived[0]);
    }
}
