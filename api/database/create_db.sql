-- Tạo database
CREATE DATABASE IF NOT EXISTS healthcare_db;

-- Sử dụng database
USE healthcare_db;

-- Tạo bảng users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(255),
    role VARCHAR(50) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng cart
CREATE TABLE IF NOT EXISTS cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    quantity INT DEFAULT 1,
    category VARCHAR(255) DEFAULT 'medicine',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tạo bảng orders
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    contact VARCHAR(255) NOT NULL,
    pincode VARCHAR(255),
    order_date DATE NOT NULL,
    order_time VARCHAR(255) NOT NULL,
    amount FLOAT NOT NULL,
    category VARCHAR(255) DEFAULT 'medicine',
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tạo bảng order_items
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Tạo bảng doctors
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    specialty VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    experience VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    price VARCHAR(255) NOT NULL
);

-- Tạo bảng health_articles
CREATE TABLE IF NOT EXISTS health_articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    image VARCHAR(255) DEFAULT NULL
);

-- Tạo bảng lab_packages
CREATE TABLE IF NOT EXISTS lab_packages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    price FLOAT NOT NULL
);

-- Tạo bảng medicines
CREATE TABLE IF NOT EXISTS medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price FLOAT NOT NULL,
    quantity INT DEFAULT 0,
    category VARCHAR(255)
);

-- Tạo bảng appointments
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    doctor_id INT,
    doctor_name VARCHAR(255) NOT NULL,
    doctor_specialty VARCHAR(255) NOT NULL,
    doctor_location VARCHAR(255) NOT NULL,
    doctor_contact VARCHAR(255) NOT NULL,
    appointment_date VARCHAR(255) NOT NULL,
    appointment_time VARCHAR(255) NOT NULL,
    symptoms TEXT,
    fee FLOAT NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu
INSERT INTO users (username, email, password, full_name, phone) VALUES
    ('admin', 'admin@example.com', 'admin123', 'Admin User', '0901000000'),
    ('user1', 'user1@example.com', 'password1', 'Nguyễn Văn A', '0901234567'),
    ('user2', 'user2@example.com', 'password2', 'Trần Thị B', '0912345678');

INSERT INTO cart (user_id, product_name, price, quantity, category) VALUES
    (2, 'Gói 1: Khám sức khỏe toàn thân', 999.00, 1, 'lab'),
    (3, 'Gói 2: Đường huyết lúc đói', 299.00, 1, 'lab');

INSERT INTO orders (user_id, fullname, address, contact, pincode, order_date, order_time, amount, category) VALUES
    (2, 'Nguyễn Văn A', '123 Đường ABC, Hà Nội', '0901234567', '100000', '2026-04-15', '10:00', 999.00, 'lab'),
    (3, 'Trần Thị B', '456 Đường XYZ, TP Hồ Chí Minh', '0912345678', '700000', '2026-04-16', '14:30', 299.00, 'lab');

INSERT INTO doctors (specialty, name, location, experience, phone, price) VALUES
    ('Bác sĩ gia đình', 'Nguyễn Khắc Nhẫn', 'Hà Nội', '5 năm', '0335620803', '600000'),
    ('Bác sĩ gia đình', 'Vũ Ngọc Thức', 'Nghệ An', '3 năm', '0347779999', '789000'),
    ('Bác sĩ gia đình', 'Nguyễn Chí Đức Anh', 'Ninh Bình', '4 năm', '0335612345', '700000'),
    ('Bác sĩ gia đình', 'Nguyễn Tuấn Anh', 'Thanh Hóa', '1 năm', '0123456789', '400000'),
    ('Bác sĩ gia đình', 'Trần Đức Hải', 'Cao Bằng', '2 năm', '0334447777', '450000'),
    ('Bác sĩ gia đình', 'Lê Thị Mai', 'Hải Phòng', '6 năm', '0335987654', '650000'),
    ('Chuyên gia dinh dưỡng', 'Phạm Thanh Sơn', 'Hà Nội', '5 năm', '0335620804', '600000'),
    ('Chuyên gia dinh dưỡng', 'Đào Trung Hiếu', 'Nghệ An', '3 năm', '0347779998', '789000'),
    ('Nha sĩ', 'Chu Thị Lý', 'Hà Nội', '5 năm', '0335620803', '600000'),
    ('Nha sĩ', 'Lê Hồng Thái', 'Nghệ An', '3 năm', '0347779999', '789000');

INSERT INTO health_articles (title, summary, image) VALUES
    ('Đi bộ hàng ngày', 'Tăng cường tuần hoàn máu và giảm stress', 'health1'),
    ('Chăm sóc tại nhà cho bệnh nhân Covid-19', 'Hướng dẫn chế độ dinh dưỡng và theo dõi triệu chứng', 'health2'),
    ('Ngừng hút thuốc', 'Lợi ích cho phổi và tim mạch sau 1 tuần đến 1 tháng', 'health3'),
    ('Đau bụng kinh', 'Mẹo giảm đau và lựa chọn thực phẩm phù hợp', 'health4'),
    ('Đường ruột khỏe mạnh', 'Thực phẩm giàu probiotic giúp tiêu hóa tốt', 'health5'),
    ('Giấc ngủ chất lượng', 'Cách xây dựng thói quen ngủ sâu và đều đặn', 'health1'),
    ('Uống đủ nước', 'Tác dụng với làn da và hệ bài tiết', 'health2');

INSERT INTO lab_packages (name, details, price) VALUES
    ('Gói 1: Khám sức khỏe toàn thân', 'Xét nghiệm đường huyết lúc đói\nHbA1C\nXét nghiệm đánh giá tình trạng sắt\nKiểm tra chức năng thận\nLDH Lactate Dehydrogenase, Serum\nHồ sơ lipid\nKiểm tra chức năng gan', 999.00),
    ('Gói 2: Đường huyết lúc đói', 'Xét nghiệm đường huyết lúc đói', 299.00),
    ('Gói 3: Kháng thể Covid - IgG', 'Xét nghiệm kháng thể IgG Covid', 899.00),
    ('Gói 4: Kiểm tra tuyến giáp', 'Xét nghiệm FT3, FT4, TSH trong chẩn đoán bệnh lý tuyến giáp', 399.00),
    ('Gói 5: Kiểm tra hệ miễn dịch', 'Xét nghiệm công thức máu toàn phần\nXét nghiệm CRP (C-Reactive Protein)\nXét nghiệm đánh giá tình trạng sắt\nXét nghiệm chức năng thận\nXét nghiệm Vitamin D Total-25 Hydroxy\nBộ xét nghiệm mỡ máu\nXét nghiệm chức năng gan', 799.00),
    ('Gói 6: Kiểm tra chức năng gan', 'Xét nghiệm men gan AST\nXét nghiệm men gan ALT\nXét nghiệm bilirubin toàn phần\nXét nghiệm albumin\nProtein toàn phần', 499.00),
    ('Gói 7: Kiểm tra tim mạch cơ bản', 'Xét nghiệm điện tâm đồ (ECG)\nXét nghiệm cholesterol toàn phần\nXét nghiệm triglyceride\nXét nghiệm HDL, LDL\nXét nghiệm tạo máu cơ bản', 549.00);

INSERT INTO appointments (user_id, doctor_name, doctor_specialty, doctor_location, doctor_contact, appointment_date, appointment_time, symptoms, fee, status) VALUES
    (2, 'Nguyễn Khắc Nhẫn', 'Bác sĩ gia đình', 'Hà Nội', '0335620803', '2026-04-20', '09:00', 'Đau đầu, sốt', 600.00, 'confirmed'),
    (3, 'Chu Thị Lý', 'Nha sĩ', 'Hà Nội', '0335620803', '2026-04-21', '14:00', 'Đau răng', 600.00, 'confirmed');