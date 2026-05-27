const { getPool } = require('../config/db');

async function getAll(req, res) {
  const { search } = req.query;
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      let query = 'SELECT * FROM medicines';
      const params = [];
      if (search) {
        query += ' WHERE name LIKE ?';
        params.push(`%${search}%`);
      }
      query += ' ORDER BY name';
      const [rows] = await connection.execute(query, params);
      return res.json({ success: true, data: rows });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('medicines.getAll error:', err);
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
      const [rows] = await connection.execute('SELECT * FROM medicines WHERE id = ?', [id]);
      if (rows.length === 0) {
        return res.status(404).json({ success: false, message: 'Không tìm thấy thuốc' });
      }
      return res.json({ success: true, data: rows[0] });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('medicines.getById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function create(req, res) {
  const { name, description, price, quantity, category } = req.body;
  if (!name || price === undefined) {
    return res.status(400).json({ success: false, message: 'name và price là bắt buộc' });
  }
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      const [result] = await connection.execute(
        `INSERT INTO medicines (name, description, price, quantity, category)
         VALUES (?, ?, ?, ?, ?)`,
        [name, description || '', price, quantity || 0, category || '']
      );

      return res.status(201).json({
        success: true,
        message: 'Tạo thuốc thành công',
        data: { id: result.insertId, name, description, price, quantity, category }
      });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('medicines.create error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function update(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });
  const { name, description, price, quantity, category } = req.body;
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();
    try {
      const updates = [];
      const params = [];

      if (name !== undefined) { updates.push('name = ?'); params.push(name); }
      if (description !== undefined) { updates.push('description = ?'); params.push(description); }
      if (price !== undefined) { updates.push('price = ?'); params.push(price); }
      if (quantity !== undefined) { updates.push('quantity = ?'); params.push(quantity); }
      if (category !== undefined) { updates.push('category = ?'); params.push(category); }

      if (updates.length === 0) {
        return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
      }

      params.push(id);
      const query = `UPDATE medicines SET ${updates.join(', ')} WHERE id = ?`;
      await connection.execute(query, params);
      return res.json({ success: true, message: 'Cập nhật thành công' });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('medicines.update error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update };
