package com.example.healthcareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        CardView cardDoctors = findViewById(R.id.cardManageDoctors);
        CardView cardMedicine = findViewById(R.id.cardManageMedicine);
        CardView cardAppointments = findViewById(R.id.cardManageAppointments);
        CardView cardUsers = findViewById(R.id.cardManageUsers);
        Button btnLogout = findViewById(R.id.btnAdminLogout);

        cardDoctors.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageDoctorsActivity.class));
        });

        cardMedicine.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageMedicineActivity.class));
        });

        cardAppointments.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAppointmentsActivity.class));
        });

        cardUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }
}
