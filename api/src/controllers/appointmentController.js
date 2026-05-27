const { sql, getPool } = require('../config/db');

async function getAppointments(req, res) {
  const userId = req.user.id;
  const { status } = req.query;
  try {
    const pool = await getPool();
    let query = `
      SELECT * FROM appointments
      WHERE user_id = ?
    `;
    const params = [userId];
    if (status) {
      query += ' AND status = ?';
      params.push(status);
    }
    query += ' ORDER BY appointment_date DESC, appointment_time DESC';

    const [rows] = await pool.execute(query, params);
    return res.json({ success: true, data: rows });
  } catch (err) {
    console.error('appointments.getAppointments error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function getAppointmentById(req, res) {
  const userId = req.user.id;
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    const [rows] = await pool.execute(
      'SELECT * FROM appointments WHERE id = ? AND user_id = ?',
      [id, userId]
    );

    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy lịch hẹn' });
    }
    return res.json({ success: true, data: rows[0] });
  } catch (err) {
    console.error('appointments.getAppointmentById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function bookAppointment(req, res) {
  const userId = req.user.id;
  const { doctor_id, fullname, address, contact, pincode, appointment_date, appointment_time, notes } = req.body;

  if (!doctor_id || !fullname || !address || !contact || !appointment_date || !appointment_time) {
    return res.status(400).json({ success: false, message: 'Thiếu thông tin bắt buộc' });
  }

  try {
    const pool = await getPool();

    // Get doctor info
    const [doctors] = await pool.execute(
      'SELECT id, name, specialty, location, phone, price FROM doctors WHERE id = ?',
      [doctor_id]
    );

    if (doctors.length === 0) {
      return res.status(404).json({ success: false, message: 'Bác sĩ không tồn tại' });
    }

    const doctor = doctors[0];
    const doctorName = doctor.name;
    const doctorSpecialty = doctor.specialty;
    const doctorLocation = doctor.location;
    const doctorContact = doctor.phone;
    const fee = parseFloat(doctor.price);

    // Check if slot is already taken
    const [slots] = await pool.execute(`
        SELECT id FROM appointments
        WHERE doctor_id = ?
          AND appointment_date = ?
          AND appointment_time = ?
          AND status NOT IN ('cancelled')
      `, [doctor_id, appointment_date, appointment_time]);

    if (slots.length > 0) {
      return res.status(409).json({ success: false, message: 'Khung giờ này đã được đặt, vui lòng chọn giờ khác' });
    }

    // Insert appointment
    console.log('🔔 Inserting appointment...');
    const [result] = await pool.execute(`
      INSERT INTO appointments (user_id, doctor_id, doctor_name, doctor_specialty, doctor_location, doctor_contact, fullname, address, contact, pincode, appointment_date, appointment_time, symptoms, fee, status)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, [userId, doctor_id, doctorName, doctorSpecialty, doctorLocation, doctorContact, fullname, address, contact, pincode || '', appointment_date, appointment_time, notes || '', fee, 'pending']);

    console.log('✅ Appointment inserted successfully');

    return res.status(201).json({
      success: true,
      message: 'Đặt lịch hẹn thành công',
      data: { appointment_id: result.insertId, fee },
    });
  } catch (err) {
    console.error('appointments.bookAppointment error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function cancelAppointment(req, res) {
  const userId = req.user.id;
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    const [rows] = await pool.execute(
      "SELECT status FROM appointments WHERE id = ? AND user_id = ?",
      [id, userId]
    );

    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy lịch hẹn' });
    }
    if (!['pending', 'confirmed'].includes(rows[0].status)) {
      return res.status(400).json({ success: false, message: 'Không thể hủy lịch hẹn ở trạng thái này' });
    }

    await pool.execute("UPDATE appointments SET status = 'cancelled' WHERE id = ?", [id]);

    return res.json({ success: true, message: 'Đã hủy lịch hẹn' });
  } catch (err) {
    console.error('appointments.cancelAppointment error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAppointments, getAppointmentById, bookAppointment, cancelAppointment };
