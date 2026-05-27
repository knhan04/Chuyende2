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

import com.example.healthcareapp.ApiClient;
import com.example.healthcareapp.ApiResponse;
import com.example.healthcareapp.ApiService;
import com.example.healthcareapp.Medicine;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyMedicineActivity extends AppCompatActivity {
    private String[][] packages = {};
    private String[] packages_details = {};
    HashMap<String,String> item;
    ArrayList<HashMap<String, String>> list;
    SimpleAdapter sa;
    Button btnGotoCart,btnBack;
    ListView listView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);
        
        apiService = ApiClient.getApiService();
        btnBack  = findViewById(R.id.button_BuyMedicine_back);
        btnGotoCart  = findViewById(R.id.button_BuyMedicine_gotocart);
        listView = findViewById(R.id.listview_BuyMedicine);

        btnBack.setOnClickListener(v -> startActivity(new Intent(BuyMedicineActivity.this, HomeActivity.class)));
        btnGotoCart.setOnClickListener(v -> startActivity(new Intent(BuyMedicineActivity.this, CartMedicineActivity.class)));

        loadMedicines();

        listView.setOnItemClickListener((parent, view, i, id) -> {
            Intent it = new Intent(BuyMedicineActivity.this, BuyMedicineDetailActivity.class);
            it.putExtra("text1", list.get(i).get("line1"));
            it.putExtra("text2", list.get(i).get("description")); // Lấy mô tả đã lưu
            it.putExtra("text3", list.get(i).get("price_raw"));
            startActivity(it);
        });
    }

    private void loadMedicines() {
        apiService.getAllMedicines().enqueue(new Callback<ApiResponse<List<Medicine>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Medicine>>> call, Response<ApiResponse<List<Medicine>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Medicine> medicines = response.body().getData();
                    list = new ArrayList<>();
                    for (Medicine m : medicines) {
                        item = new HashMap<>();
                        item.put("line1", m.getName());
                        item.put("line2", "Mô tả: " + m.getDescription());
                        item.put("line3", "");
                        item.put("line4", "");
                        item.put("line5", "Giá: " + m.getPrice() + " VNĐ");
                        item.put("description", m.getDescription());
                        item.put("price_raw", String.valueOf(m.getPrice()));
                        list.add(item);
                    }
                    sa = new SimpleAdapter(BuyMedicineActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1","line2","line3","line4","line5"},
                            new int[]{R.id.textview_line_a,R.id.textview_line_b,R.id.textview_line_c,R.id.textview_line_d,R.id.textview_line_e});
                    listView.setAdapter(sa);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Medicine>>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }
}