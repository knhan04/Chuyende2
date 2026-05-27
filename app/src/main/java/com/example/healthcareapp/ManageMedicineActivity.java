package com.example.healthcareapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class ManageMedicineActivity extends AppCompatActivity {

    private EditText edName, edDescription, edPrice;
    private Button btnAdd, btnBack;
    private ListView listViewMedicine;
    private ApiService apiService;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_medicine);

        // Sử dụng context để có Token Authorization
        apiService = ApiClient.getApiService(this);

        listViewMedicine = findViewById(R.id.listViewMedicine);
        edName = findViewById(R.id.edMedicineName);
        edDescription = findViewById(R.id.edMedicineDescription);
        edPrice = findViewById(R.id.edMedicinePrice);
        btnAdd = findViewById(R.id.btnAddMedicine);
        btnBack = findViewById(R.id.btnBack);

        btnAdd.setOnClickListener(v -> addMedicine());
        btnBack.setOnClickListener(v -> finish());

        loadMedicines();
    }

    private void loadMedicines() {
        apiService.getAllMedicines().enqueue(new Callback<ApiResponse<List<Medicine>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Medicine>>> call, Response<ApiResponse<List<Medicine>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Medicine> medicines = response.body().getData();
                    list = new ArrayList<>();
                    if (medicines != null) {
                        for (Medicine m : medicines) {
                            HashMap<String, String> item = new HashMap<>();
                            item.put("line1", "Tên thuốc: " + m.getName());
                            item.put("line2", "Mô tả: " + m.getDescription());
                            item.put("line3", "Giá: " + m.getPrice() + " VNĐ");
                            list.add(item);
                        }
                    }

                    sa = new SimpleAdapter(ManageMedicineActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1", "line2", "line3"},
                            new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c});
                    listViewMedicine.setAdapter(sa);
                } else {
                    Log.e("ManageMedicine", "Error code: " + response.code());
                    Toast.makeText(ManageMedicineActivity.this, "Server không phản hồi dữ liệu (Lỗi " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Medicine>>> call, Throwable t) {
                Log.e("ManageMedicine", "Failure: " + t.getMessage());
                Toast.makeText(ManageMedicineActivity.this, "Lỗi kết nối Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMedicine() {
        String name = edName.getText().toString();
        String description = edDescription.getText().toString();
        String priceStr = edPrice.getText().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Medicine medicine = new Medicine(name, description, price);

        apiService.addMedicine(medicine).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManageMedicineActivity.this, "Thêm thuốc thành công", Toast.LENGTH_SHORT).show();
                    edName.setText("");
                    edDescription.setText("");
                    edPrice.setText("");
                    loadMedicines(); // Tải lại danh sách sau khi thêm
                } else {
                    Toast.makeText(ManageMedicineActivity.this, "Lỗi từ Server khi thêm thuốc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ManageMedicineActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
