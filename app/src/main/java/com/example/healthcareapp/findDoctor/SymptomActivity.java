package com.example.healthcareapp.findDoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.healthcareapp.HomeActivity;
import com.example.healthcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomActivity extends AppCompatActivity {

    private EditText edtCustomSymptom;
    private CheckBox cbHeadache, cbFever, cbCold, cbObesity, cbDiabetes, cbToothache,
            cbBleeding, cbHighBP, cbChestPain, cbBreathless, cbInjury;
    private Button btnContinue, btnBack;
    
    // Map triệu chứng với loại bác sĩ
    private Map<String, String> symptomDoctorMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);
        
        initUI();
        initSymptomDoctorMap();
        setupListeners();
    }
    
    private void initUI() {
        edtCustomSymptom = findViewById(R.id.edt_custom_symptom);
        
        cbHeadache = findViewById(R.id.cb_headache);
        cbFever = findViewById(R.id.cb_fever);
        cbCold = findViewById(R.id.cb_cold);
        cbObesity = findViewById(R.id.cb_obesity);
        cbDiabetes = findViewById(R.id.cb_diabetes);
        cbToothache = findViewById(R.id.cb_toothache);
        cbBleeding = findViewById(R.id.cb_bleeding);
        cbHighBP = findViewById(R.id.cb_high_bp);
        cbChestPain = findViewById(R.id.cb_chest_pain);
        cbBreathless = findViewById(R.id.cb_breathless);
        cbInjury = findViewById(R.id.cb_injury);
        
        btnContinue = findViewById(R.id.btn_symptom_continue);
        btnBack = findViewById(R.id.btn_symptom_back);
    }
    
    private void initSymptomDoctorMap() {
        symptomDoctorMap = new HashMap<>();
        // Bác sĩ gia đình
        symptomDoctorMap.put("Đau đầu", "Bác sĩ gia đình");
        symptomDoctorMap.put("Sốt", "Bác sĩ gia đình");
        symptomDoctorMap.put("Cảm cúm", "Bác sĩ gia đình");
        
        // Chuyên gia dinh dưỡng
        symptomDoctorMap.put("Béo phì", "Chuyên gia dinh dưỡng");
        symptomDoctorMap.put("Tiểu đường", "Chuyên gia dinh dưỡng");
        
        // Nha sĩ
        symptomDoctorMap.put("Đau răng", "Nha sĩ");
        symptomDoctorMap.put("Chảy máu chân răng", "Nha sĩ");
        
        // Bác sĩ tim mạch
        symptomDoctorMap.put("Huyết áp cao", "Bác sĩ tim mạch");
        symptomDoctorMap.put("Đau tim", "Bác sĩ tim mạch");
        symptomDoctorMap.put("Khó thở", "Bác sĩ tim mạch");
        
        // Bác sĩ phẫu thuật
        symptomDoctorMap.put("Chấn thương", "Bác sĩ phẫu thuật");
    }
    
    private void setupListeners() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleContinue();
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SymptomActivity.this, Find_Doctor_Activity.class));
            }
        });
    }
    
    private void handleContinue() {
        List<String> selectedSymptoms = getSelectedSymptoms();
        
        if (selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn hoặc nhập triệu chứng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Tìm loại bác sĩ được gợi ý
        String recommendedDoctor = findBestDoctor(selectedSymptoms);
        
        if (recommendedDoctor == null) {
            Toast.makeText(this, "Không tìm thấy bác sĩ phù hợp", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Chuyển sang DoctorDetailActivity với loại bác sĩ được gợi ý
        Intent intent = new Intent(SymptomActivity.this, DoctorDetailActivity.class);
        intent.putExtra("title", recommendedDoctor);
        intent.putExtra("symptoms", String.join(", ", selectedSymptoms));
        startActivity(intent);
    }
    
    private List<String> getSelectedSymptoms() {
        List<String> symptoms = new ArrayList<>();
        
        // Kiểm tra checkbox được chọn
        if (cbHeadache.isChecked()) symptoms.add("Đau đầu");
        if (cbFever.isChecked()) symptoms.add("Sốt");
        if (cbCold.isChecked()) symptoms.add("Cảm cúm");
        if (cbObesity.isChecked()) symptoms.add("Béo phì");
        if (cbDiabetes.isChecked()) symptoms.add("Tiểu đường");
        if (cbToothache.isChecked()) symptoms.add("Đau răng");
        if (cbBleeding.isChecked()) symptoms.add("Chảy máu chân răng");
        if (cbHighBP.isChecked()) symptoms.add("Huyết áp cao");
        if (cbChestPain.isChecked()) symptoms.add("Đau tim");
        if (cbBreathless.isChecked()) symptoms.add("Khó thở");
        if (cbInjury.isChecked()) symptoms.add("Chấn thương");
        
        // Kiểm tra custom symptom
        String customSymptom = edtCustomSymptom.getText().toString().trim();
        if (!customSymptom.isEmpty()) {
            symptoms.add(customSymptom);
        }
        
        return symptoms;
    }
    
    private String findBestDoctor(List<String> symptoms) {
        // Đếm số lần mỗi loại bác sĩ được gợi ý
        Map<String, Integer> doctorCount = new HashMap<>();
        
        for (String symptom : symptoms) {
            String doctor = symptomDoctorMap.get(symptom);
            if (doctor != null) {
                doctorCount.put(doctor, doctorCount.getOrDefault(doctor, 0) + 1);
            }
        }
        
        // Tìm loại bác sĩ được gợi ý nhiều nhất
        if (doctorCount.isEmpty()) {
            return "Bác sĩ gia đình"; // Mặc định nếu không tìm thấy
        }
        
        String bestDoctor = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : doctorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                bestDoctor = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        
        return bestDoctor;
    }
}
