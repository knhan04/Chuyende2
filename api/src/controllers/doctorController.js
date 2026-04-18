const { sql, getPool } = require('../config/db');

async function getAll(req, res) {
  const { specialty, name } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request();

    let query = 'SELECT * FROM doctors';
    const hasFilters = specialty || name;
    if (hasFilters) {
      const conditions = [];
      if (specialty) {
        request.input('specialty', sql.NVarChar, `%${specialty}%`);
        conditions.push('specialty LIKE @specialty');
      }
      if (name) {
        request.input('name', sql.NVarChar, `%${name}%`);
        conditions.push('name LIKE @name');
      }
      query += ' WHERE ' + conditions.join(' AND ');
    }
    query += ' ORDER BY name';

    const result = await request.query(query);
    return res.json({ success: true, data: result.recordset });
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
    const result = await pool.request()
      .input('id', sql.Int, id)
      .query('SELECT * FROM doctors WHERE id = @id');

    if (result.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy bác sĩ' });
    }
    return res.json({ success: true, data: result.recordset[0] });
  } catch (err) {
    console.error('doctors.getById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function create(req, res) {
  const { name, specialty, location, experience, phone, price } = req.body;
  if (!name || !specialty) {
    return res.status(400).json({ success: false, message: 'name và specialty là bắt buộc' });
  }
  try {
    const pool = await getPool();
    await pool.request()
      .input('name',       sql.NVarChar,    name)
      .input('specialty',  sql.NVarChar,    specialty)
      .input('location',   sql.NVarChar,    location || '')
      .input('experience', sql.NVarChar,    experience || '')
      .input('phone',      sql.NVarChar,    phone || '')
      .input('price',      sql.NVarChar,    price || '0')
      .query(`
        INSERT INTO doctors (name, specialty, location, experience, phone, price)
        VALUES (@name, @specialty, @location, @experience, @phone, @price)
      `);

    // Get the last inserted ID
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const doctorId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;

    return res.status(201).json({ 
      success: true, 
      message: 'Tạo bác sĩ thành công',
      data: { id: doctorId, name, specialty, location, experience, phone, price } 
    });
  } catch (err) {
    console.error('doctors.create error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function update(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  const { name, specialty, location, experience, phone, price } = req.body;
  try {
    const pool = await getPool();
    
    // Build dynamic update query
    const updates = [];
    const request = pool.request().input('id', sql.Int, id);
    
    if (name !== undefined) {
      request.input('name', sql.NVarChar, name);
      updates.push('name = @name');
    }
    if (specialty !== undefined) {
      request.input('specialty', sql.NVarChar, specialty);
      updates.push('specialty = @specialty');
    }
    if (location !== undefined) {
      request.input('location', sql.NVarChar, location);
      updates.push('location = @location');
    }
    if (experience !== undefined) {
      request.input('experience', sql.NVarChar, experience);
      updates.push('experience = @experience');
    }
    if (phone !== undefined) {
      request.input('phone', sql.NVarChar, phone);
      updates.push('phone = @phone');
    }
    if (price !== undefined) {
      request.input('price', sql.NVarChar, price);
      updates.push('price = @price');
    }

    if (updates.length === 0) {
      return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
    }

    const query = `UPDATE doctors SET ${updates.join(', ')} WHERE id = @id`;
    await request.query(query);

    return res.json({ success: true, message: 'Cập nhật thành công' });
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
    await pool.request()
      .input('id', sql.Int, id)
      .query('DELETE FROM doctors WHERE id = @id');

    return res.json({ success: true, message: 'Đã xóa bác sĩ' });
  } catch (err) {
    console.error('doctors.remove error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update, remove };
