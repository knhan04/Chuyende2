package com.example.healthcareapp.findDoctor;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.DataBase;
import com.example.healthcareapp.HomeActivity;
import com.example.healthcareapp.RemoteDataBase;
import com.example.healthcareapp.R;

import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {
 EditText ed1,ed2,ed3,ed4;
 TextView tv1, tvSymptoms;
 Button btnDate,btnTime,btnReg,btnBack;
 private DatePickerDialog datePickerDialog;
 private TimePickerDialog timePickerDialog;
    private String title, name, address, contactnumber, fees, symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        tv1 = findViewById(R.id.textView_BookApp_tittle);
        tvSymptoms = findViewById(R.id.textView_symptoms);
        ed1 = findViewById(R.id.editText_BookApp_Name);
        ed2 = findViewById(R.id.editText_BookApp_Address);
        ed3 = findViewById(R.id.editText_BookApp_ContactNumber);
        ed4 = findViewById(R.id.editText_BookApp_Fees);
        btnDate = findViewById(R.id.button_BA_SL_Date);
        btnTime = findViewById(R.id.button_BA_SL_Time);
        btnReg = findViewById(R.id.button_BookApp_register);
        btnBack = findViewById(R.id.button_BookApp__back);

        ed1.setKeyListener(null);
        ed2.setKeyListener(null);
        ed3.setKeyListener(null);
        ed4.setKeyListener(null);

        Intent it = getIntent();
        title = it.getStringExtra("text1");
        name = it.getStringExtra("text2");
        address = it.getStringExtra("text3");
        contactnumber = it.getStringExtra("text4");
        fees = it.getStringExtra("text5");
        symptoms = it.getStringExtra("symptoms");

        tv1.setText(title);
        ed1.setText(name);
        ed2.setText(address);
        ed3.setText(contactnumber);
        ed4.setText(String.format("Chi Phí: %s VNĐ", fees));
        
        // Hiển thị triệu chứng nếu có
        if (symptoms != null && !symptoms.isEmpty()) {
            tvSymptoms.setText(String.format("Triệu chứng: %s", symptoms));
            tvSymptoms.setVisibility(View.VISIBLE);
        }
        
        //date picker
        initDatePicker();
        btnDate.setOnClickListener(v -> datePickerDialog.show());

        //time picker
        initTimePicker();
        btnTime.setOnClickListener(v -> timePickerDialog.show());

        btnBack.setOnClickListener(v -> startActivity(new Intent(BookAppointmentActivity.this, Find_Doctor_Activity.class)));

        btnReg.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            
            float feesValue = 0;
            try {
                if (fees != null) {
                    feesValue = Float.parseFloat(fees);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // 1. Lưu vào SQLite local
            try (DataBase db = new DataBase(getApplicationContext(), "health_care_db", null, 1)) {
                db.addOrder(username, title + "=>" + name, address, contactnumber, 0, btnDate.getText().toString(), btnTime.getText().toString(), feesValue, "appointment");
            }

            // 2. Gửi dữ liệu lên Server (SQL Server qua API)
            RemoteDataBase remoteDb = new RemoteDataBase(BookAppointmentActivity.this);
            remoteDb.addOrder(username, title + "=>" + name, address, contactnumber, 0, btnDate.getText().toString(), btnTime.getText().toString(), feesValue, "appointment", new RemoteDataBase.DatabaseCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(getApplicationContext(), "Đặt lịch thành công lên hệ thống!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BookAppointmentActivity.this, HomeActivity.class));
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "Lỗi đồng bộ Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BookAppointmentActivity.this, HomeActivity.class));
                }
            });
        });

    }
    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month+=1;
            btnDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month, year));
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Loại bỏ style cũ để tránh Deprecated warning
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()+86400000);
    }

    private void initTimePicker(){
       TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> 
               btnTime.setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute));
        Calendar calendar = Calendar.getInstance();
        int hrs = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        // Loại bỏ style cũ để tránh Deprecated warning
        timePickerDialog = new TimePickerDialog(this, timeSetListener, hrs, minute, true);
    }
}