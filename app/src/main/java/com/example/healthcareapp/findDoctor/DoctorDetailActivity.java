package com.example.healthcareapp.findDoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.healthcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.healthcareapp.ApiClient;
import com.example.healthcareapp.ApiResponse;
import com.example.healthcareapp.ApiService;
import com.example.healthcareapp.Doctor;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorDetailActivity extends AppCompatActivity {
TextView tv1;
TextView tvSymptoms;
Button btn;
    String[][] doctors_detail  ={};
    HashMap<String,String> item;
    ArrayList<HashMap<String, String>> list;
    SimpleAdapter sa;
    private ApiService apiService;
    private String title, symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        tv1 = findViewById(R.id.textView_Doctor_detail_title);
        tvSymptoms = findViewById(R.id.textView_symptoms);
        btn = findViewById(R.id.button_doctor_detail);
        apiService = ApiClient.getApiService();

        Intent it = getIntent();
        title = it.getStringExtra("title");
        symptoms = it.getStringExtra("symptoms");
        tv1.setText(title);
       
       if (symptoms != null && !symptoms.isEmpty()) {
           tvSymptoms.setText("Triệu chứng: " + symptoms);
           tvSymptoms.setVisibility(View.VISIBLE);
       }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorDetailActivity.this, Find_Doctor_Activity.class));
            }
        });

        loadDoctors();
    }

    private void loadDoctors() {
        apiService.getAllDoctors().enqueue(new Callback<ApiResponse<List<Doctor>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Doctor>>> call, Response<ApiResponse<List<Doctor>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Doctor> doctors = response.body().getData();
                    list = new ArrayList<>();
                    for (Doctor d : doctors) {
                        // Lọc bác sĩ theo chuyên khoa (title)
                        if (d.getSpecialty().equals(title) || title.contains(d.getSpecialty())) {
                            item = new HashMap<>();
                            item.put("line1", "Bác sĩ: " + d.getName());
                            item.put("line2", "Nơi làm việc: " + d.getAddress());
                            item.put("line3", "Ca làm: " + d.getShift());
                            item.put("line4", "SĐT: " + d.getPhone());
                            item.put("line5", "Chi phí: " + d.getFee() + " VNĐ");
                            item.put("fees", String.valueOf(d.getFee()));
                            item.put("shift", d.getShift());
                            list.add(item);
                        }
                    }
                    sa = new SimpleAdapter(DoctorDetailActivity.this,
                            list,
                            R.layout.multi_lines,
                            new String[]{"line1","line2","line3","line4","line5"},
                            new int[]{R.id.textview_line_a,R.id.textview_line_b,R.id.textview_line_c,R.id.textview_line_d,R.id.textview_line_e});
                    ListView lv = findViewById(R.id.listview_doctor_detail);
                    lv.setAdapter(sa);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                            Intent it = new Intent(DoctorDetailActivity.this, BookAppointmentActivity.class);
                            it.putExtra("text1", title);
                            it.putExtra("text2", list.get(i).get("line1"));
                            it.putExtra("text3", list.get(i).get("line2"));
                            it.putExtra("text4", list.get(i).get("line4"));
                            it.putExtra("text5", list.get(i).get("fees"));
                            it.putExtra("shift", list.get(i).get("shift"));
                            it.putExtra("symptoms", symptoms);
                            startActivity(it);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Doctor>>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }
}