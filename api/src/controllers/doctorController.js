const { getPool } = require('../config/db');

async function getAll(req, res) {
  const { specialty, name } = req.query;
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      let query = 'SELECT * FROM doctors';
      const params = [];
      const conditions = [];

      if (specialty) {
        conditions.push('specialty LIKE ?');
        params.push(`%${specialty}%`);
      }
      if (name) {
        conditions.push('name LIKE ?');
        params.push(`%${name}%`);
      }

      if (conditions.length > 0) {
        query += ' WHERE ' + conditions.join(' AND ');
      }
      query += ' ORDER BY name';

      const [rows] = await connection.execute(query, params);
      return res.json({ success: true, data: rows });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('doctors.getAll error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function getById(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      const [rows] = await connection.execute('SELECT * FROM doctors WHERE id = ?', [id]);
      if (rows.length === 0) {
        return res.status(404).json({ success: false, message: 'Không tìm thấy bác sĩ' });
      }
      return res.json({ success: true, data: rows[0] });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('doctors.getById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function create(req, res) {
  const { name, specialty, location, experience, phone, price, shift } = req.body;
  if (!name || !specialty) {
    return res.status(400).json({ success: false, message: 'name và specialty là bắt buộc' });
  }
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      const [result] = await connection.execute(
        `INSERT INTO doctors (name, specialty, location, experience, phone, price, shift)
         VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [name, specialty, location || '', experience || '', phone || '', price || '0', shift || '']
      );

      return res.status(201).json({
        success: true,
        message: 'Tạo bác sĩ thành công',
        data: { id: result.insertId, name, specialty, location, experience, phone, price, shift }
      });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('doctors.create error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function update(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  const { name, specialty, location, experience, phone, price, shift } = req.body;
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      const updates = [];
      const params = [];

      if (name !== undefined) { updates.push('name = ?'); params.push(name); }
      if (specialty !== undefined) { updates.push('specialty = ?'); params.push(specialty); }
      if (location !== undefined) { updates.push('location = ?'); params.push(location); }
      if (experience !== undefined) { updates.push('experience = ?'); params.push(experience); }
      if (phone !== undefined) { updates.push('phone = ?'); params.push(phone); }
      if (price !== undefined) { updates.push('price = ?'); params.push(price); }
      if (shift !== undefined) { updates.push('shift = ?'); params.push(shift); }

      if (updates.length === 0) {
        return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
      }

      params.push(id);
      const query = `UPDATE doctors SET ${updates.join(', ')} WHERE id = ?`;
      await connection.execute(query, params);

      return res.json({ success: true, message: 'Cập nhật thành công' });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('doctors.update error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function remove(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      await connection.execute('DELETE FROM doctors WHERE id = ?', [id]);
      return res.json({ success: true, message: 'Đã xóa bác sĩ' });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('doctors.remove error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update, remove };
