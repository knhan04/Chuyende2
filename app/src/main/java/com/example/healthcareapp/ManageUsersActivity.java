package com.example.healthcareapp;

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

public class ManageUsersActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private ApiService apiService;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        listView = findViewById(R.id.listViewUsers);
        btnBack = findViewById(R.id.btnBack);
        apiService = ApiClient.getApiService(this);

        btnBack.setOnClickListener(v -> finish());

        loadUsers();
    }

    private void loadUsers() {
        apiService.getAllUsers().enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<User> users = response.body().getData();
                    list = new ArrayList<>();
                    for (User user : users) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put("line1", user.getUsername());
                        item.put("line2", "Email: " + user.getEmail());
                        item.put("line3", "Vai trò: " + user.getRole());
                        item.put("line4", "");
                        item.put("line5", "");
                        list.add(item);
                    }

                    sa = new SimpleAdapter(ManageUsersActivity.this, list,
                            R.layout.multi_lines,
                            new String[]{"line1", "line2", "line3", "line4", "line5"},
                            new int[]{R.id.textview_line_a, R.id.textview_line_b, R.id.textview_line_c, R.id.textview_line_d, R.id.textview_line_e});
                    listView.setAdapter(sa);
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Không thể lấy danh sách người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
