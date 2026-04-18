package com.example.healthcareapp.findDoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.healthcareapp.HomeActivity;
import com.example.healthcareapp.R;

public class Find_Doctor_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_doctor);
        CardView exit = findViewById(R.id.cardFD_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Find_Doctor_Activity.this, HomeActivity.class));
            }
        });

        // Nút nhập triệu chứng (gợi ý bác sĩ)
        Button btnSymptom = findViewById(R.id.btn_search_by_symptom);
        if (btnSymptom != null) {
            btnSymptom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Find_Doctor_Activity.this, SymptomActivity.class));
                }
            });
        }

        CardView family = findViewById(R.id.cardFD_Family_Physical);
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Find_Doctor_Activity.this, DoctorDetailActivity.class);
                it.putExtra("title","Bác sĩ gia đình");
                startActivity(it);
            }
        });

        CardView dietcian = findViewById(R.id.cardFD_Dietcian);
        dietcian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Find_Doctor_Activity.this, DoctorDetailActivity.class);
                it.putExtra("title","Chuyên gia dinh dưỡng");
                startActivity(it);
            }
        });

        CardView dentist = findViewById(R.id.cardFD_Dentist);
        dentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Find_Doctor_Activity.this, DoctorDetailActivity.class);
                it.putExtra("title","Nha sĩ");
                startActivity(it);
            }
        });
        CardView surgeon = findViewById(R.id.cardFD_Surgeon);
        surgeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Find_Doctor_Activity.this, DoctorDetailActivity.class);
                it.putExtra("title","Bác sĩ phẫu thuật");
                startActivity(it);
            }
        });
        CardView cardiologists = findViewById(R.id.cardFD_Cardiologists);
        cardiologists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Find_Doctor_Activity.this, DoctorDetailActivity.class);
                it.putExtra("title","Bác sĩ tim mạch");
                startActivity(it);
            }
        });
    }
}