package com.example.healthcareapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageDoctorsActivity extends AppCompatActivity {

    private EditText edName;
    private Spinner spinnerSpecialty, spinnerShift;
    private Button btnAdd, btnBack, btnUpdate, btnDelete;
    private ListView listViewDoctors;
    private ApiService apiService;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;
    private List<Doctor> doctorsList;
    private int selectedDoctorId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_doctors);

        apiService = ApiClient.getApiService(this);

        listViewDoctors = findViewById(R.id.listViewDoctors);
        edName = findViewById(R.id.edDoctorName);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        spinnerShift = findViewById(R.id.spinnerShift);
        btnAdd = findViewById(R.id.btnAddDoctor);
        btnUpdate = findViewById(R.id.btnUpdateDoctor);
        btnDelete = findViewById(R.id.btnDeleteDoctor);
        btnBack = findViewById(R.id.btnBack);

        String[] specialties = {"Bác sĩ gia đình", "Chuyên gia dinh dưỡng", "Nha sĩ", "Bác sĩ phẫu thuật", "Bác sĩ tim mạch"};
        ArrayAdapter<String> specAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, specialties);
        spinnerSpecialty.setAdapter(specAdapter);

        String[] shifts = {"Sáng", "Chiều", "Cả ngày"};
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shifts);
        spinnerShift.setAdapter(shiftAdapter);

        btnAdd.setOnClickListener(v -> addDoctor());
        btnUpdate.setOnClickListener(v -> updateDoctor());
        btnDelete.setOnClickListener(v -> deleteDoctor());
        btnBack.setOnClickListener(v -> finish());

        listViewDoctors.setOnItemClickListener((parent, view, position, id) -> {
            if (doctorsList != null && position < doctorsList.size()) {
                Doctor d = doctorsList.get(position);
                selectedDoctorId = d.getId();
                edName.setText(d.getName());
                
                for (int i = 0; i < specialties.length; i++) {
                    if (specialties[i].equals(d.getSpecialty())) spinnerSpecialty.setSelection(i);
                }
                for (int i = 0; i < shifts.length; i++) {
                    if (shifts[i].equals(d.getShift())) spinnerShift.setSelection(i);
                }

                btnAdd.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
        });

        loadDoctors();
    }

    private void loadDoctors() {
        apiService.getAllDoctors().enqueue(new Callback<ApiResponse<List<Doctor>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Doctor>>> call, Response<ApiResponse<List<Doctor>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    doctorsList = response.body().getData();
                    list = new ArrayList<>();
                    if (doctorsList != null) {
                        for (Doctor d : doctorsList) {
                            HashMap<String, String> item = new HashMap<>();
                            item.put("line1", d.getName());
                            item.put("line2", "Chuyên khoa: " + d.getSpecialty());
                            item.put("line3", "Ca làm: " + d.getShift());
                            item.put("line4", "");
                            item.put("line5", "");
                            list.add(item);
                        }
                    }

                    sa = new SimpleAdapter(ManageDoctorsActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1", "line2", "line3", "line4", "line5"},
                            new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c, R.id.textview_line_d, R.id.textview_line_e});
                    listViewDoctors.setAdapter(sa);
                } else {
                    Toast.makeText(ManageDoctorsActivity.this, "Không thể tải danh sách bác sĩ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Doctor>>> call, Throwable t) {
                Toast.makeText(ManageDoctorsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDoctor() {
        String name = edName.getText().toString();
        String specialty = spinnerSpecialty.getSelectedItem().toString();
        String shift = spinnerShift.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bác sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor doctor = new Doctor(name, specialty, "Hà Nội", "0123456789", 500000, shift);

        apiService.addDoctor(doctor).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManageDoctorsActivity.this, "Đã thêm bác sĩ thành công", Toast.LENGTH_SHORT).show();
                    resetForm();
                    loadDoctors();
                } else {
                    Toast.makeText(ManageDoctorsActivity.this, "Lỗi khi thêm bác sĩ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ManageDoctorsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDoctor() {
        if (selectedDoctorId == -1) return;

        String name = edName.getText().toString();
        String specialty = spinnerSpecialty.getSelectedItem().toString();
        String shift = spinnerShift.getSelectedItem().toString();

        Doctor doctor = new Doctor(name, specialty, "Hà Nội", "0123456789", 500000, shift);
        
        apiService.updateDoctor(selectedDoctorId, doctor).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManageDoctorsActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    resetForm();
                    loadDoctors();
                } else {
                    Toast.makeText(ManageDoctorsActivity.this, "Lỗi từ Server khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ManageDoctorsActivity.this, "Lỗi kết nối hệ thống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDoctor() {
        if (selectedDoctorId == -1) return;

        apiService.deleteDoctor(selectedDoctorId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManageDoctorsActivity.this, "Xóa bác sĩ thành công", Toast.LENGTH_SHORT).show();
                    resetForm();
                    loadDoctors();
                } else {
                    Toast.makeText(ManageDoctorsActivity.this, "Lỗi khi xóa bác sĩ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ManageDoctorsActivity.this, "Lỗi kết nối hệ thống khi xóa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        edName.setText("");
        spinnerSpecialty.setSelection(0);
        spinnerShift.setSelection(0);
        selectedDoctorId = -1;
        btnAdd.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
    }
}
