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

public class DoctorDetailActivity extends AppCompatActivity {
TextView tv1;
TextView tvSymptoms;
Button btn;
private  String[][] doctors1 = {
        {"Bác sĩ: Nguyễn Khắc Nhẫn","Nơi làm việc: Hà Nội","Kinh nghiệm: 5 năm","SĐT: 0335620803","600.000"},
        {"Bác sĩ: Vũ Ngọc Thức","Nơi làm việc: Nghệ An","Kinh nghiệm: 3 năm","SĐT: 0347779999","789.000"},
        {"Bác sĩ: Nguyễn Chí Đức Anh","Nơi làm việc: Ninh Bình","Kinh nghiệm: 4 năm","SĐT: 0335612345","700.000"},
        {"Bác sĩ: Nguyễn Tuấn Anh","Nơi làm việc: Thanh Hóa","Kinh nghiệm: 1 năm","SĐT: 0123456789","400.000"},
        {"Bác sĩ: Trần Đức Hải","Nơi làm việc: Cao Bằng","Kinh nghiệm: 2 năm","SĐT: 0334447777","450.000"},
        {"Bác sĩ: Lê Thị Mai","Nơi làm việc: Hải Phòng","Kinh nghiệm: 6 năm","SĐT: 0335987654","650.000"}
};
    private  String[][] doctors2 = {
            {"Bác sĩ: Phạm Thanh Sơn","Nơi làm việc: Hà Nội","Kinh nghiệm: 5 năm","SĐT: 0335620804","600.000"},
            {"Bác sĩ: Đào Trung Hiếu","Nơi làm việc: Nghệ An","Kinh nghiệm: 3 năm","SĐT: 0347779998","789.000"},
            {"Bác sĩ: Lò Khánh Hiếu","Nơi làm việc: Ninh Bình","Kinh nghiệm: 4 năm","SĐT: 0335612346","700.000"},
            {"Bác sĩ: Nguyễn Lưu Tiến Đạt","Nơi làm việc: Thanh Hóa","Kinh nghiệm: 1 năm","SĐT: 0123456787","400.000"},
            {"Bác sĩ: Nguyễn Tài Dương","Nơi làm việc: Cao Bằng","Kinh nghiệm: 2 năm","SĐT: 0334447770","450.000"},
            {"Bác sĩ: Trịnh Thị Ngọc","Nơi làm việc: Đà Nẵng","Kinh nghiệm: 8 năm","SĐT: 0335123456","820.000"}
    };
    private  String[][] doctors3 = {
            {"Bác sĩ: Chu Thị Lý","Nơi làm việc: Hà Nội","Kinh nghiệm: 5 năm","SĐT: 0335620803","600.000"},
            {"Bác sĩ: Lê Hồng Thái","Nơi làm việc: Nghệ An","Kinh nghiệm: 3 năm","SĐT: 0347779999","789.000"},
            {"Bác sĩ: Nguyễn Đức Trường","Nơi làm việc: Ninh Bình","Kinh nghiệm: 4 năm","SĐT: 0335612345","700.000"},
            {"Bác sĩ: Nguyễn Văn Minh","Nơi làm việc: Thanh Hóa","Kinh nghiệm: 1 năm","SĐT: 0123456789","400.000"},
            {"Bác sĩ: Phạm Quốc Minh","Nơi làm việc: Cao Bằng","Kinh nghiệm: 2 năm","SĐT: 0334447777","450.000"},
            {"Bác sĩ: Trần Thị Hà","Nơi làm việc: Huế","Kinh nghiệm: 7 năm","SĐT: 0335765432","680.000"}
    };
    private   String[][] doctors4 = {
            {"Bác sĩ: Lê Ngọc Huyền","Nơi làm việc: Hà Nội","Kinh nghiệm: 5 năm","SĐT: 0335620803","600.000"},
            {"Bác sĩ: Nguyễn Trọng Cường","Nơi làm việc: Nghệ An","Kinh nghiệm: 3 năm","SĐT: 0347779999","789.000"},
            {"Bác sĩ: Nguyễn Huy Hoàng","Nơi làm việc: Ninh Bình","Kinh nghiệm: 4 năm","SĐT: 0335612345","700.000"},
            {"Bác sĩ: Lê Hoàng Anh Tuấn","Nơi làm việc: Thanh Hóa","Kinh nghiệm: 1 năm","SĐT: 0123456789","400.000"},
            {"Bác sĩ: Lê Ngọc Thảo","Nơi làm việc: Cao Bằng","Kinh nghiệm: 2 năm","SĐT: 0334447777","450.000"},
            {"Bác sĩ: Bùi Văn Long","Nơi làm việc: Nam Định","Kinh nghiệm: 6 năm","SĐT: 0335987123","710.000"}
    };
    private  String[][] doctors5 = {
            {"Bác sĩ: Phạm Đức Long","Nơi làm việc: Hà Nội","Kinh nghiệm: 5 năm","SĐT: 0335620803","600.000"},
            {"Bác sĩ: Nguyễn Văn Thành Đạt","Nơi làm việc: Nghệ An","Kinh nghiệm: 3 năm","SĐT: 0347779999","789.000"},
            {"Bác sĩ: Nguyễn Đức Phú","Nơi làm việc: Ninh Bình","Kinh nghiệm: 4 năm","SĐT: 0335612345","700.000"},
            {"Bác sĩ: Nguyễn Hồng Quang","Nơi làm việc: Thanh Hóa","Kinh nghiệm: 1 năm","SĐT: 0123456789","400.000"},
            {"Bác sĩ: Nguyễn Hồng Phương","Nơi làm việc: Cao Bằng","Kinh nghiệm: 2 năm","SĐT: 0334447777","450.000"},
            {"Bác sĩ: Vũ Thị Giang","Nơi làm việc: Bắc Ninh","Kinh nghiệm: 7 năm","SĐT: 0335678901","730.000"}
    };
    String[][] doctors_detail  ={};
    HashMap<String,String> item;
  ArrayList list ;

    SimpleAdapter sa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        tv1 = findViewById(R.id.textView_Doctor_detail_title);
        tvSymptoms = findViewById(R.id.textView_symptoms);
        btn = findViewById(R.id.button_doctor_detail);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        String symptoms = it.getStringExtra("symptoms");
       tv1.setText(title);
       
       // Hiển thị triệu chứng nếu có
       if (symptoms != null && !symptoms.isEmpty()) {
           tvSymptoms.setText("Triệu chứng: " + symptoms);
           tvSymptoms.setVisibility(View.VISIBLE);
       }

        if(title.compareTo("Bác sĩ gia đình")==0){
            doctors_detail = doctors1;
        }
        else if(title.compareTo("Chuyên gia dinh dưỡng")==0){
            doctors_detail = doctors2;
        }
        else if(title.compareTo("Nha sĩ")==0){
            doctors_detail = doctors3;
        }
       else if(title.compareTo("Bác sĩ phẫu thuật")==0){
            doctors_detail = doctors4;
        }
        else{
            doctors_detail = doctors5;
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorDetailActivity.this, Find_Doctor_Activity.class));
            }
        });
        list = new ArrayList();
        for(int i=0;i<doctors_detail.length;i++){
            item = new HashMap<String,String>();
            item.put("line1",doctors_detail[i][0]); // chua toan bo ten
            item.put("line2",doctors_detail[i][1]); // chua toan bo address
            item.put("line3",doctors_detail[i][2]);
            item.put("line4",doctors_detail[i][3]);
            item.put("line5","Chi phí:"+doctors_detail[i][4]+"VNĐ");
            list.add(item);
        }
        sa = new SimpleAdapter(this,
                list,
                R.layout.multi_lines,
                new String[]{"line1","line2","line3","line4","line5"},
                new int[]{R.id.textview_line_a,R.id.textview_line_b,R.id.textview_line_c,R.id.textview_line_d,R.id.textview_line_e});
        ListView lv ;
        lv = findViewById(R.id.listview_doctor_detail);
        lv.setAdapter(sa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent it = new Intent(DoctorDetailActivity.this, BookAppointmentActivity.class);
                it.putExtra("text1",title);
                it.putExtra("text2",doctors_detail[i][0]);
                it.putExtra("text3",doctors_detail[i][1]);
                it.putExtra("text4",doctors_detail[i][3]);
                it.putExtra("text5",doctors_detail[i][4]);
                it.putExtra("symptoms", symptoms);
                startActivity(it);
            }
        });

    }
}