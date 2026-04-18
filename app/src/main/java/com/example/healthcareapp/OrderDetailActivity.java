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

public class OrderDetailActivity extends AppCompatActivity {
    String[][] order_details;
    HashMap<String, String> item;
    SimpleAdapter sa;
    ListView lv;
    Button btn;
    ArrayList<HashMap<String, String>> list;
    ArrayList<String> dbdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        btn = findViewById(R.id.button_orderdetail);
        lv = findViewById(R.id.listview_orderdetail);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetailActivity.this, HomeActivity.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Sử dụng DataBase (SQLite local) để đồng bộ với phần đặt lịch
        DataBase db = new DataBase(getApplicationContext(), "health_care_db", null, 1);
        dbdata = db.gerOrderData(username);
        
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        list = new ArrayList<>(); // Khởi tạo list để tránh NullPointerException
        order_details = new String[dbdata.size()][8];

        for (int i = 0; i < dbdata.size(); i++) {
            String data = dbdata.get(i);
            String[] split = data.split(java.util.regex.Pattern.quote("$"));

            if (split.length < 8) continue;

            String fullname = split[0];
            String address = split[1];
            String contact = split[2];
            String date = split[4];
            String time = split[5];
            String amount = split[6];
            String otype = split[7];

            String line1 = fullname;
            String line2 = "Địa chỉ: " + address;
            String line3, line4, line5;

            if (otype.equals("appointment")) {
                line3 = "SĐT: " + contact;
                line4 = "Ngày: " + date + "  Giờ: " + time;
                line5 = "Lịch hẹn";
            } else {
                line3 = "Giá: " + amount + " VNĐ";
                line4 = "Ngày: " + date + " " + time;
                line5 = otype.equals("medicine") ? "Mua thuốc" : "Xét nghiệm";
            }

            item = new HashMap<>();
            item.put("line1", line1);
            item.put("line2", line2);
            item.put("line3", line3);
            item.put("line4", line4);
            item.put("line5", line5);
            
            // Lưu dữ liệu thô để dùng cho Dialog chi tiết hoặc xóa
            item.put("raw_fullname", fullname);
            item.put("raw_address", address);
            item.put("raw_contact", contact);
            item.put("raw_otype", otype);
            item.put("raw_time", time);
            item.put("raw_date", date);
            list.add(item);
        }

        sa = new SimpleAdapter(this,
                list,
                R.layout.multi_lines,
                new String[]{"line1", "line2", "line3", "line4", "line5"},
                new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c, R.id.textview_line_d, R.id.textview_line_e});

        lv.setAdapter(sa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailsDialog(position);
            }
        });
    }

    private void showCFDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy mục này không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> selectedItem = list.get(position);
                        String fullname = selectedItem.get("raw_fullname");
                        String address = selectedItem.get("raw_address");
                        String otype = selectedItem.get("raw_otype");
                        
                        DataBase db = new DataBase(getApplicationContext(), "health_care_db", null, 1);
                        db.removeOrder(fullname, otype, address);
                        
                        list.remove(position);
                        sa.notifyDataSetChanged();
                        Toast.makeText(OrderDetailActivity.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    }
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
