const { sql, getPool } = require('../config/db');

async function getAppointments(req, res) {
  const userId = req.user.id;
  const { status } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request().input('user_id', sql.Int, userId);

    let query = `
      SELECT * FROM appointments
      WHERE user_id = @user_id
    `;
    if (status) {
      request.input('status', sql.NVarChar, status);
      query += ' AND status = @status';
    }
    query += ' ORDER BY appointment_date DESC, appointment_time DESC';

    const result = await request.query(query);
    return res.json({ success: true, data: result.recordset });
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
    const result = await pool.request()
      .input('id', sql.Int, id)
      .input('user_id', sql.Int, userId)
      .query(`
        SELECT * FROM appointments
        WHERE id = @id AND user_id = @user_id
      `);

    if (result.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy lịch hẹn' });
    }
    return res.json({ success: true, data: result.recordset[0] });
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
    const doctorResult = await pool.request()
      .input('doctor_id', sql.Int, doctor_id)
      .query('SELECT id, name, specialty, location, phone, price FROM doctors WHERE id = @doctor_id');

    if (doctorResult.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Bác sĩ không tồn tại' });
    }

    const doctor = doctorResult.recordset[0];
    const doctorName = doctor.name;
    const doctorSpecialty = doctor.specialty;
    const doctorLocation = doctor.location;
    const doctorContact = doctor.phone;
    const fee = parseFloat(doctor.price);

    // Check if slot is already taken
    const slotCheck = await pool.request()
      .input('doctor_id', sql.Int, doctor_id)
      .input('appointment_date', sql.NVarChar, appointment_date)
      .input('appointment_time', sql.NVarChar, appointment_time)
      .query(`
        SELECT id FROM appointments
        WHERE doctor_id = @doctor_id
          AND appointment_date = @appointment_date
          AND appointment_time = @appointment_time
          AND status NOT IN ('cancelled')
      `);

    if (slotCheck.recordset.length > 0) {
      return res.status(409).json({ success: false, message: 'Khung giờ này đã được đặt, vui lòng chọn giờ khác' });
    }

    // Insert appointment
    const insertRequest = pool.request()
      .input('user_id', sql.Int, userId)
      .input('doctor_id', sql.Int, doctor_id)
      .input('fullname', sql.NVarChar, fullname)
      .input('address', sql.NVarChar, address)
      .input('contact', sql.NVarChar, contact)
      .input('pincode', sql.NVarChar, pincode || '')
      .input('appointment_date', sql.NVarChar, appointment_date)
      .input('appointment_time', sql.NVarChar, appointment_time)
      .input('doctor_name', sql.NVarChar, doctorName)
      .input('doctor_specialty', sql.NVarChar, doctorSpecialty)
      .input('doctor_location', sql.NVarChar, doctorLocation)
      .input('doctor_contact', sql.NVarChar, doctorContact)
      .input('fee', sql.Float, fee)
      .input('symptoms', sql.NVarChar, notes || '')
      .input('status', sql.NVarChar, 'pending');

    // For MySQL, we need to handle the insert differently
    console.log('🔔 Inserting appointment...');
    const insertResult = await insertRequest.query(`
      INSERT INTO appointments (user_id, doctor_id, doctor_name, doctor_specialty, doctor_location, doctor_contact, fullname, address, contact, pincode, appointment_date, appointment_time, symptoms, fee, status)
      VALUES (@user_id, @doctor_id, @doctor_name, @doctor_specialty, @doctor_location, @doctor_contact, @fullname, @address, @contact, @pincode, @appointment_date, @appointment_time, @symptoms, @fee, @status)
    `);
    console.log('✅ Appointment inserted successfully');

    // Get the last inserted ID
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const appointmentId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;

    return res.status(201).json({
      success: true,
      message: 'Đặt lịch hẹn thành công',
      data: { appointment_id: appointmentId, fee },
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
    const check = await pool.request()
      .input('id', sql.Int, id)
      .input('user_id', sql.Int, userId)
      .query("SELECT status FROM appointments WHERE id = @id AND user_id = @user_id");

    if (check.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy lịch hẹn' });
    }
    if (!['pending', 'confirmed'].includes(check.recordset[0].status)) {
      return res.status(400).json({ success: false, message: 'Không thể hủy lịch hẹn ở trạng thái này' });
    }

    await pool.request()
      .input('id', sql.Int, id)
      .query("UPDATE appointments SET status = 'cancelled' WHERE id = @id");

    return res.json({ success: true, message: 'Đã hủy lịch hẹn' });
  } catch (err) {
    console.error('appointments.cancelAppointment error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAppointments, getAppointmentById, bookAppointment, cancelAppointment };
