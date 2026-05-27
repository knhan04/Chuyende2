package com.example.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    HashMap<String, String> item;
    SimpleAdapter sa;
    ListView lv;
    Button btn;
    ArrayList<HashMap<String, String>> list;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        btn = findViewById(R.id.button_orderdetail);
        lv = findViewById(R.id.listview_orderdetail);
        apiService = ApiClient.getApiService(this);

        btn.setOnClickListener(v -> startActivity(new Intent(OrderDetailActivity.this, HomeActivity.class)));

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        loadOrderDataFromServer(username);
    }

    private void loadOrderDataFromServer(String username) {
        apiService.getOrderData(username).enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<String> dbdata = response.body().getData();
                    list = new ArrayList<>();

                    for (String data : dbdata) {
                        String[] split = data.split(java.util.regex.Pattern.quote("$"));
                        if (split.length < 8) continue;

                        String fullname = split[0];
                        String address = split[1];
                        String contact = split[2];
                        String date = split[4];
                        String time = split[5];
                        String amount = split[6];
                        String otype = split[7];

                        item = new HashMap<>();
                        item.put("line1", fullname);
                        item.put("line2", "Địa chỉ: " + address);
                        
                        if (otype.equals("appointment")) {
                            item.put("line3", "SĐT: " + contact);
                            item.put("line4", "Ngày: " + date + "  Giờ: " + time);
                            item.put("line5", "Lịch hẹn");
                        } else {
                            item.put("line3", "Giá: " + amount + " VNĐ");
                            item.put("line4", "Ngày: " + date + " " + time);
                            item.put("line5", otype.equals("medicine") ? "Mua thuốc" : "Xét nghiệm");
                        }

                        item.put("raw_fullname", fullname);
                        item.put("raw_address", address);
                        item.put("raw_contact", contact);
                        item.put("raw_otype", otype);
                        item.put("raw_time", time);
                        item.put("raw_date", date);
                        list.add(item);
                    }

                    sa = new SimpleAdapter(OrderDetailActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1", "line2", "line3", "line4", "line5"},
                            new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c, R.id.textview_line_d, R.id.textview_line_e});
                    lv.setAdapter(sa);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemClickListener((parent, view, position, id) -> showDetailsDialog(position));
    }

    private void showCFDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy mục này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    HashMap<String, String> selectedItem = list.get(position);
                    String fullname = selectedItem.get("raw_fullname");
                    String address = selectedItem.get("raw_address");
                    String otype = selectedItem.get("raw_otype");
                    
                    apiService.removeOrder(fullname, otype, address).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                list.remove(position);
                                sa.notifyDataSetChanged();
                                Toast.makeText(OrderDetailActivity.this, "Đã hủy thành công", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(OrderDetailActivity.this, "Lỗi khi hủy", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void showDetailsDialog(int position) {
        HashMap<String, String> selectedItem = list.get(position);
        String fullname = selectedItem.get("raw_fullname");
        String address = selectedItem.get("raw_address");
        String contact = selectedItem.get("raw_contact");
        String date = selectedItem.get("raw_date");
        String time = selectedItem.get("raw_time");
        String type = selectedItem.get("raw_otype");
        
        String message;
        if (type != null && type.equals("appointment")) {
            message = "Bác sĩ: " + fullname + "\n"
                    + "Địa chỉ: " + address + "\n"
                    + "SĐT: " + contact + "\n"
                    + "Ngày: " + date + "\n"
                    + "Giờ: " + time + "\n"
                    + "Loại: Lịch hẹn";
        } else {
            message = "Tên: " + fullname + "\n"
                    + "Địa chỉ: " + address + "\n"
                    + "SĐT: " + contact + "\n"
                    + "Ngày: " + date + "\n"
                    + "Giờ: " + time + "\n"
                    + "Loại: " + (type.equals("medicine") ? "Mua thuốc" : "Xét nghiệm");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chi tiết")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton("Hủy lịch/Đơn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCFDialog(position);
                    }
                })
                .show();
    }
}
