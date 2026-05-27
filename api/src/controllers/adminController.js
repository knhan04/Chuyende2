const { getPool } = require('../config/db');

// Lấy toàn bộ lịch hẹn của tất cả bệnh nhân (Chỉ Admin mới dùng)
async function getAllAppointments(req, res) {
    try {
        const pool = await getPool();
        const [rows] = await pool.execute(`
            SELECT a.*, u.username as patient_name, u.phone as patient_phone
            FROM appointments a
            JOIN users u ON a.user_id = u.id
            ORDER BY a.appointment_date DESC, a.id DESC
        `);
        res.json({ success: true, data: rows });
    } catch (err) {
        console.error('admin.getAllAppointments error:', err);
        res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
    }
}

// Cập nhật trạng thái lịch hẹn (Xác nhận/Hủy)
async function updateAppointmentStatus(req, res) {
    const { appointment_id, status } = req.body;
    try {
        const pool = await getPool();
        await pool.execute('UPDATE appointments SET status = ? WHERE id = ?', [status, appointment_id]);
        res.json({ success: true, message: 'Cập nhật trạng thái thành công' });
    } catch (err) {
        res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
    }
}

// Chia ca bác sĩ (morning/afternoon/full_day)
async function updateDoctorShift(req, res) {
    const { doctor_id, shift } = req.body;
    try {
        const pool = await getPool();
        await pool.execute('UPDATE doctors SET shift = ? WHERE id = ?', [shift, doctor_id]);
        res.json({ success: true, message: 'Đã cập nhật ca làm việc cho bác sĩ' });
    } catch (err) {
        res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
    }
}

module.exports = { getAllAppointments, updateAppointmentStatus, updateDoctorShift };
