# 📘 MÔ TẢ DỮ LIỆU FUNCTION (API/FUNCTION BACKEND)

## 1. Giới thiệu

Tài liệu này mô tả các Function (API) sử dụng trong hệ thống **HealthCare App**. Bao gồm:

* Tên function
* Mục đích
* Input (dữ liệu đầu vào)
* Output (dữ liệu đầu ra)
* Database liên quan

---

## 2. Danh sách Function

### 2.1. Đăng ký tài khoản

* **Tên function**: `registerUser`
* **URL**: `/api/register`
* **Method**: POST
* **Mục đích**: Tạo tài khoản người dùng mới

#### Input

```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

#### Output

```json
{
  "message": "Đăng ký thành công",
  "userId": "number"
}
```

#### Database

* Bảng: `users`

---

### 2.2. Đăng nhập

* **Tên function**: `loginUser`
* **URL**: `/api/login`
* **Method**: POST
* **Mục đích**: Xác thực người dùng

#### Input

```json
{
  "email": "string",
  "password": "string"
}
```

#### Output

```json
{
  "token": "string",
  "user": {
    "id": "number",
    "username": "string"
  }
}
```

#### Database

* Bảng: `users`

---

### 2.3. Lấy danh sách bác sĩ

* **Tên function**: `getDoctors`
* **URL**: `/api/doctors`
* **Method**: GET
* **Mục đích**: Lấy danh sách bác sĩ

#### Input

* Không có

#### Output

```json
[
  {
    "id": "number",
    "name": "string",
    "specialty": "string"
  }
]
```

#### Database

* Bảng: `doctors`

---

### 2.4. Đặt lịch khám

* **Tên function**: `bookAppointment`
* **URL**: `/api/appointments`
* **Method**: POST
* **Mục đích**: Đặt lịch khám bệnh

#### Input

```json
{
  "userId": "number",
  "doctorId": "number",
  "date": "string",
  "symptoms": "string"
}
```

#### Output

```json
{
  "message": "Đặt lịch thành công",
  "appointmentId": "number"
}
```

#### Database

* Bảng: `appointments`

---

### 2.5. Lấy lịch đã đặt

* **Tên function**: `getAppointments`
* **URL**: `/api/appointments`
* **Method**: GET
* **Mục đích**: Lấy danh sách lịch đã đặt

#### Input

```json
{
  "userId": "number"
}
```

#### Output

```json
[
  {
    "id": "number",
    "doctorName": "string",
    "date": "string",
    "status": "string"
  }
]
```

#### Database

* Bảng: `appointments`

---

## 3. Ghi chú

* Tất cả API trả về dữ liệu dạng JSON
* Sử dụng JWT cho xác thực (token)
* Các lỗi sẽ trả về dạng:

```json
{
  "error": "Mô tả lỗi"
}
```

---

## 4. Kết luận

Tài liệu này giúp định nghĩa rõ cách frontend và backend giao tiếp thông qua các function/API.

Bạn có thể mở rộng thêm:

* API tìm kiếm bác sĩ
* API chat với bác sĩ
* API thanh toán
