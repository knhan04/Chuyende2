# HealthCare API — Node.js + SQL Server

## Yêu cầu

| Công cụ | Phiên bản |
|---------|-----------|
| Node.js | >= 18 |
| SQL Server | 2019 / 2022 / Express |
| npm | >= 9 |

---

## Cài đặt

```bash
cd api
npm install
```

---

## Cấu hình SQL Server

1. **Bật SQL Server Authentication** (Mixed Mode) trong SQL Server Management Studio:
   - Chuột phải vào server → Properties → Security → SQL Server and Windows Authentication mode
   - Khởi động lại SQL Server service

2. **Tạo user `sa`** hoặc tạo user riêng với quyền `dbcreator` + `sysadmin`

3. **Bật TCP/IP** trong SQL Server Configuration Manager:
   - SQL Server Network Configuration → Protocols for MSSQLSERVER → TCP/IP → Enable
   - Kiểm tra port 1433 trong IP Addresses tab

4. **Chạy schema** trong SSMS:
   ```
   Mở file: api/database/schema.sql
   Chạy toàn bộ script
   ```

---

## Cấu hình môi trường

Sửa file `api/.env`:

```env
DB_SERVER=localhost          # hoặc IP máy chủ SQL Server
DB_PORT=1433
DB_NAME=HealthCareDB
DB_USER=sa
DB_PASSWORD=YourPassword     # đổi thành mật khẩu thực
DB_ENCRYPT=false
DB_TRUST_SERVER_CERTIFICATE=true

JWT_SECRET=your_secret_key   # đổi thành chuỗi bí mật ngẫu nhiên
JWT_EXPIRES_IN=7d

PORT=3000
```

---

## Chạy server

```bash
# Production
npm start

# Development (auto-restart)
npm run dev
```

Kiểm tra: [http://localhost:3000/api/health](http://localhost:3000/api/health)

---

## Cấu trúc project

```
api/
├── server.js                   # Entry point
├── package.json
├── .env                        # Config (không commit lên git)
├── database/
│   └── schema.sql              # SQL Server schema + seed data
└── src/
    ├── config/
    │   └── db.js               # Kết nối SQL Server (mssql pool)
    ├── middleware/
    │   └── auth.js             # JWT middleware
    ├── controllers/
    │   ├── authController.js
    │   ├── doctorController.js
    │   ├── cartController.js
    │   ├── orderController.js
    │   ├── appointmentController.js
    │   ├── articleController.js
    │   ├── labController.js
    │   └── medicineController.js
    └── routes/
        ├── auth.js
        ├── doctors.js
        ├── cart.js
        ├── orders.js
        ├── appointments.js
        ├── articles.js
        ├── lab.js
        └── medicines.js
```

---

## API Endpoints

### Auth
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| POST | `/api/auth/register` | ❌ | Đăng ký tài khoản |
| POST | `/api/auth/login` | ❌ | Đăng nhập, nhận JWT token |
| GET | `/api/auth/profile` | ✅ | Xem hồ sơ cá nhân |
| PUT | `/api/auth/profile` | ✅ | Cập nhật hồ sơ |

**Login response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGci...",
    "user": { "id": 1, "username": "user1", "role": "patient" }
  }
}
```

### Doctors
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/doctors` | ❌ | Danh sách bác sĩ |
| GET | `/api/doctors?specialty=Tim mạch` | ❌ | Lọc theo chuyên khoa |
| GET | `/api/doctors/:id` | ❌ | Chi tiết bác sĩ |

### Cart (cần đăng nhập)
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/cart` | ✅ | Xem giỏ hàng |
| GET | `/api/cart?category=medicine` | ✅ | Giỏ theo loại |
| POST | `/api/cart` | ✅ | Thêm sản phẩm |
| DELETE | `/api/cart/:id` | ✅ | Xóa 1 sản phẩm |
| DELETE | `/api/cart/clear` | ✅ | Xóa toàn bộ giỏ |

### Orders (cần đăng nhập)
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/orders` | ✅ | Danh sách đơn hàng |
| GET | `/api/orders/:id` | ✅ | Chi tiết đơn hàng |
| POST | `/api/orders` | ✅ | Tạo đơn hàng |
| PATCH | `/api/orders/:id/cancel` | ✅ | Hủy đơn hàng |

### Appointments (cần đăng nhập)
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/appointments` | ✅ | Danh sách lịch hẹn |
| GET | `/api/appointments/:id` | ✅ | Chi tiết lịch hẹn |
| POST | `/api/appointments` | ✅ | Đặt lịch hẹn |
| PATCH | `/api/appointments/:id/cancel` | ✅ | Hủy lịch hẹn |

### Articles
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/articles` | ❌ | Danh sách bài viết |
| GET | `/api/articles?search=tim` | ❌ | Tìm kiếm |
| GET | `/api/articles/:id` | ❌ | Chi tiết bài viết |

### Lab Packages
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/lab-packages` | ❌ | Danh sách gói xét nghiệm |
| GET | `/api/lab-packages/:id` | ❌ | Chi tiết gói |

### Medicines
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/api/medicines` | ❌ | Danh sách thuốc |
| GET | `/api/medicines?search=paracetamol` | ❌ | Tìm kiếm thuốc |
| GET | `/api/medicines/:id` | ❌ | Chi tiết thuốc |

---

## Cấu hình Android

Trong `ApiClient.java`:
- **Emulator**: `http://10.0.2.2:3000/api/`
- **Thiết bị thật**: `http://192.168.x.x:3000/api/` (IP máy tính chạy server)

Đảm bảo `AndroidManifest.xml` có:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Nếu dùng HTTP (không phải HTTPS), thêm vào `AndroidManifest.xml`:
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

---

## Flow xác thực trên Android

```
1. Gọi POST /api/auth/login
2. Nhận token trong response
3. Lưu token vào SessionManager: SessionManager.getInstance().init(token, user)
4. ApiClient tự động gắn "Authorization: Bearer <token>" vào mọi request tiếp theo
```
