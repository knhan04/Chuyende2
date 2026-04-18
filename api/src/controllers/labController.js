const { sql, getPool } = require('../config/db');

async function getAll(req, res) {
  try {
    const pool = await getPool();
    const result = await pool.request()
      .query('SELECT * FROM lab_packages ORDER BY price');
    return res.json({ success: true, data: result.recordset });
  } catch (err) {
    console.error('lab.getAll error:', err);
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
      .query('SELECT * FROM lab_packages WHERE id = @id');

    if (result.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy gói xét nghiệm' });
    }
    return res.json({ success: true, data: result.recordset[0] });
  } catch (err) {
    console.error('lab.getById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function create(req, res) {
  const { name, details, price } = req.body;
  if (!name || price === undefined) {
    return res.status(400).json({ success: false, message: 'name và price là bắt buộc' });
  }
  try {
    const pool = await getPool();
    await pool.request()
      .input('name',    sql.NVarChar, name)
      .input('details', sql.NVarChar, details || '')
      .input('price',   sql.Float,    price)
      .query(`
        INSERT INTO lab_packages (name, details, price)
        VALUES (@name, @details, @price)
      `);
    
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const labId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;
    
    return res.status(201).json({ 
      success: true, 
      message: 'Tạo gói xét nghiệm thành công',
      data: { id: labId, name, details, price } 
    });
  } catch (err) {
    console.error('lab.create error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function update(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });
  const { name, details, price } = req.body;
  try {
    const pool = await getPool();
    
    const updates = [];
    const request = pool.request().input('id', sql.Int, id);
    
    if (name !== undefined) {
      request.input('name', sql.NVarChar, name);
      updates.push('name = @name');
    }
    if (details !== undefined) {
      request.input('details', sql.NVarChar, details);
      updates.push('details = @details');
    }
    if (price !== undefined) {
      request.input('price', sql.Float, price);
      updates.push('price = @price');
    }

    if (updates.length === 0) {
      return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
    }

    const query = `UPDATE lab_packages SET ${updates.join(', ')} WHERE id = @id`;
    await request.query(query);
    return res.json({ success: true, message: 'Cập nhật thành công' });
  } catch (err) {
    console.error('lab.update error:', err);
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
      .query('DELETE FROM lab_packages WHERE id = @id');
    return res.json({ success: true, message: 'Đã ẩn gói xét nghiệm' });
  } catch (err) {
    console.error('lab.remove error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update, remove };
