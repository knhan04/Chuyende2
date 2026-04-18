package com.example.healthcareapp.buymedicine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.healthcareapp.cart.CartMedicineActivity;
import com.example.healthcareapp.HomeActivity;
import com.example.healthcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BuyMedicineActivity extends AppCompatActivity {
private String[][] packages = {
        { "Paracetamol (acetaminophen)","","","","15.000"},
        { "Ibuprofen","","","","43.000 "},
        { "Aspirin","","","","20.000 "},
        { "Simvastatin","","","","50.000 "},
        { "Metformin","","","","65.000 "},
        { "Atorvastatin","","","","13.000 "},
        { "Omeprazole","","","","67.000 "},
        { "Metoprolol","","","","23.000 "},
        { "Amoxicillin","","","","26.000 "},
        { "Fluoxetine","","","","34.000 "},
};
private String[] packages_details = {
  "Công dụng: Giảm đau và hạ sốt.\n" +
          "Tên thương hiệu phổ biến: Tylenol, Panadol.\n" +
          "Lưu ý: Sản phẩm được coi là an toàn khi sử dụng theo hướng dẫn.",
        "Công dụng: Thuốc kháng viêm không steroid (NSAID) được sử dụng để giảm đau, hạ sốt và giảm viêm.\n" +
                "Tên thương hiệu phổ biến: Advil, Motrin.\n" +
                "Lưu ý: Nên sử dụng thận trọng, đặc biệt là khi sử dụng lâu dài.",
        "Công dụng: Thuốc kháng viêm không steroid (NSAID) được sử dụng để giảm đau, hạ sốt và làm loãng máu.\n" +
                "Tên thương hiệu phổ biến: Bayer, Ecotrin.Tên thương hiệu phổ biến: Bayer, Ecotrin.\n" +
                "Lưu ý: Tránh cho trẻ em hoặc thanh thiếu niên dùng aspirin vì nguy cơ mắc hội chứng Reye.",
        "Công dụng: Thuốc hạ cholesterol giúp giảm nguy cơ mắc bệnh tim mạch và đột quỵ.\n" +
                "Tên thương hiệu phổ biến: Zocor.\n" +
                "Ghi chú: Thường được kê đơn kèm theo chế độ ăn uống lành mạnh và tập thể dục.",
        "Công dụng: Thuốc uống điều trị bệnh tiểu đường loại 2.\n" +
                "Tên thương hiệu phổ biến: Glucophage, Fortamet.\n" +
                "Ghi chú: Giúp kiểm soát lượng đường trong máu và cũng có thể mang lại những lợi ích sức khỏe khác.",
        "Công dụng: Thuốc hạ cholesterol giúp giảm nguy cơ mắc bệnh tim mạch và đột quỵ.\n" +
                "Tên thương hiệu phổ biến: Lipitor.\n" +
                "Ghi chú: Thường được kê đơn cùng với những thay đổi lối sống, chẳng hạn như chế độ ăn uống và tập thể dục.",
        "Công dụng: Thuốc ức chế bơm proton (PPI) được sử dụng để giảm axit dạ dày và điều trị các bệnh như trào ngược axit và loét dạ dày.\n" +
                "Tên thương hiệu phổ biến: Prilosec, Losec.\n" +
                "Lưu ý: Nên dùng theo chỉ dẫn và không nên dùng trong thời gian dài mà không có sự giám sát của bác sĩ.",
        "Công dụng: Thuốc chẹn beta được sử dụng để điều trị huyết áp cao, đau thắt ngực và các bệnh lý liên quan đến tim.\n" +
                "Tên thương hiệu phổ biến: Lopressor, Toprol XL.\n" +
                "Lưu ý: Không nên ngừng sử dụng đột ngột mà không tham khảo ý kiến \u200B\u200Bchuyên gia y tế.",
        "Công dụng: Thuốc kháng sinh dùng để điều trị các bệnh nhiễm trùng do vi khuẩn, chẳng hạn như nhiễm trùng đường hô hấp và nhiễm trùng đường tiết niệu.\n" +
                "Tên thương hiệu phổ biến: Amoxil, Trimox.\n" +
                "Lưu ý: Nên dùng thuốc đủ thời gian theo chỉ định để đảm bảo điều trị khỏi hoàn toàn nhiễm trùng.",
        "Công dụng: Thuốc ức chế tái hấp thu serotonin có chọn lọc (SSRI) được sử dụng để điều trị trầm cảm, rối loạn lo âu và một số bệnh lý tâm thần khác.\n" +
                "Tên thương hiệu phổ biến: Prozac, Sarafem.\n" +
                "Lưu ý: Có thể mất vài tuần để thấy được hiệu quả đầy đủ, và không nên ngừng sử dụng đột ngột."
};
    HashMap<String,String> item;
    ArrayList list ;
    SimpleAdapter sa;
    Button btnGotoCart,btnBack;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);
        btnBack  = findViewById(R.id.button_BuyMedicine_back);
        btnGotoCart  = findViewById(R.id.button_BuyMedicine_gotocart);
        listView = findViewById(R.id.listview_BuyMedicine);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuyMedicineActivity.this, HomeActivity.class));
            }
        });
        btnGotoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuyMedicineActivity.this, CartMedicineActivity.class));
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
                Intent it = new Intent(BuyMedicineActivity.this, BuyMedicineDetailActivity.class);
                it.putExtra("text1",packages[i][0]);
                it.putExtra("text2",packages_details[i]);
                it.putExtra("text3",packages[i][4]);

                startActivity(it);
            }
        });
    }
}