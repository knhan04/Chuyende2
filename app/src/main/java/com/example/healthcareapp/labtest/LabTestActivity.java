package com.example.healthcareapp.labtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.healthcareapp.cart.CartLabActivity;
import com.example.healthcareapp.HomeActivity;
import com.example.healthcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LabTestActivity extends AppCompatActivity {
private String [][] packages = {
        {"Gói 1: Khám sức khỏe toàn thân","","","","999.000"},
        {"Gói 2: Đường huyết lúc đói","","","","299.000"},
        {"Gói 3: Kháng thể Covid - IgG","","","","899.000"},
        {"Gói 4: Kiểm tra tuyến giáp","","","","399.000"},
        {"Gói 5: Kiểm tra hệ miễn dịch","","","","799.000"},
        {"Gói 6: Kiểm tra chức năng gan","","","","499.000"},
        {"Gói 7: Kiểm tra tim mạch cơ bản","","","","549.000"}
};
private String[] package_details = {
        "Xét nghiệm đường huyết lúc đói\n"+
                "HbA1C\n"+
                "Xét nghiệm đánh giá tình trạng sắt\n"+
                "Kiểm tra chức năng thận\n"+
                "LDH Lactate Dehydrogenase, Serum\n"+
                "Hồ sơ lipid\n"+
                "Kiểm tra chức năng gan",

        "Xét nghiệm đường huyết lúc đói",
        "Xét nghiệm kháng thể IgG Covid",
        "Xét nghiệm FT3, FT4, TSH trong chẩn đoán bệnh lý tuyến giáp",

        "Xét nghiệm công thức máu toàn phần\n"+
                "Xét nghiệm CRP (C-Reactive Protein)\n"+
                "Xét nghiệm đánh giá tình trạng sắt\n"+
                "Xét nghiệm chức năng thận\n"+
                "Xét nghiệm Vitamin D Total-25 Hydroxy (hay 25(OH)D)\n"+
                "Bộ xét nghiệm mỡ máu\n"+
                "Xét nghiệm chức năng gan",

        "Xét nghiệm men gan AST\n"+
                "Xét nghiệm men gan ALT\n"+
                "Xét nghiệm bilirubin toàn phần\n"+
                "Xét nghiệm albumin\n"+
                "Protein toàn phần",

        "Xét nghiệm điện tâm đồ (ECG)\n"+
                "Xét nghiệm cholesterol toàn phần\n"+
                "Xét nghiệm triglyceride\n"+
                "Xét nghiệm HDL, LDL\n"+
                "Xét nghiệm tạo máu cơ bản"
};
    HashMap<String,String> item;
    ArrayList list ;
    SimpleAdapter sa;
    Button btnGotoCart,btnBack;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test);
        btnBack =findViewById(R.id.button_LT_back);
        btnGotoCart =findViewById(R.id.button_LT_gotocart);
        listView =findViewById(R.id.listview_LT);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestActivity.this, HomeActivity.class));
            }
        });

        list = new ArrayList();
        for(int i=0;i<packages.length;i++){
            item = new HashMap<String,String>();
            item.put("line1",packages[i][0]);
            item.put("line2",packages[i][1]);
            item.put("line3",packages[i][2]);
            item.put("line4",packages[i][3]);
            item.put("line5","Chi Phí "+packages[i][4]+"VNĐ");
            list.add(item);
        }

        sa = new SimpleAdapter(this,
                list,
                R.layout.multi_lines,
                new String[]{"line1","line2","line3","line4","line5"},
                new int[]{R.id.textview_line_a,R.id.textview_line_b,R.id.textview_line_c,R.id.textview_line_d,R.id.textview_line_e});
        listView.setAdapter(sa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent it = new Intent(LabTestActivity.this, LabTestDetailActivity.class);
                it.putExtra("text1",packages[i][0]);
                it.putExtra("text2",package_details[i]);
                it.putExtra("text3",packages[i][4]);

                startActivity(it);
            }
        });
        btnGotoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestActivity.this, CartLabActivity.class));
            }
        });
    }
}