package com.example.healthcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAppointmentsActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private ApiService apiService;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_appointments);

        listView = findViewById(R.id.listViewAdminApp);
        btnBack = findViewById(R.id.buttonAdminBack);
        apiService = ApiClient.getApiService(this);

        btnBack.setOnClickListener(v -> finish());

        loadAllAppointments();
    }

    private void loadAllAppointments() {
        apiService.getAllAppointments().enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Appointment>>> call, Response<ApiResponse<List<Appointment>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Appointment> appointments = response.body().getData();
                    list = new ArrayList<>();
                    for (Appointment app : appointments) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put("line1", "BN: " + (app.getPatientName() != null ? app.getPatientName() : "N/A") + " - BS: " + app.getDoctorName());
                        item.put("line2", "Ngày: " + app.getAppointmentDate() + " (" + app.getShift() + ")");
                        item.put("line3", "Giờ: " + app.getAppointmentTime() + " - Trạng thái: " + app.getStatus());
                        list.add(item);
                    }

                    sa = new SimpleAdapter(AdminAppointmentsActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1", "line2", "line3"},
                            new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c});
                    listView.setAdapter(sa);
                } else {
                    Toast.makeText(AdminAppointmentsActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Appointment>>> call, Throwable t) {
                Toast.makeText(AdminAppointmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
