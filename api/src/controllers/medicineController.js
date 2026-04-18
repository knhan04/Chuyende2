const { sql, getPool } = require('../config/db');

async function getAll(req, res) {
  const { search } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request();
    let query = 'SELECT * FROM medicines';
    if (search) {
      request.input('search', sql.NVarChar, `%${search}%`);
      query += ' WHERE name LIKE @search';
    }
    query += ' ORDER BY name';
    const result = await request.query(query);
    return res.json({ success: true, data: result.recordset });
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
    const result = await pool.request()
      .input('id', sql.Int, id)
      .query('SELECT * FROM medicines WHERE id = @id');

    if (result.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy thuốc' });
    }
    return res.json({ success: true, data: result.recordset[0] });
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
    await pool.request()
      .input('name',        sql.NVarChar, name)
      .input('description', sql.NVarChar, description || '')
      .input('price',       sql.Float,    price)
      .input('quantity',    sql.Int,      quantity || 0)
      .input('category',    sql.NVarChar, category || '')
      .query(`
        INSERT INTO medicines (name, description, price, quantity, category)
        VALUES (@name, @description, @price, @quantity, @category)
      `);
    
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const medicineId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;
    
    return res.status(201).json({ 
      success: true, 
      message: 'Tạo thuốc thành công',
      data: { id: medicineId, name, description, price, quantity, category } 
    });
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
    
    const updates = [];
    const request = pool.request().input('id', sql.Int, id);
    
    if (name !== undefined) {
      request.input('name', sql.NVarChar, name);
      updates.push('name = @name');
    }
    if (description !== undefined) {
      request.input('description', sql.NVarChar, description);
      updates.push('description = @description');
    }
    if (price !== undefined) {
      request.input('price', sql.Float, price);
      updates.push('price = @price');
    }
    if (quantity !== undefined) {
      request.input('quantity', sql.Int, quantity);
      updates.push('quantity = @quantity');
    }
    if (category !== undefined) {
      request.input('category', sql.NVarChar, category);
      updates.push('category = @category');
    }

    if (updates.length === 0) {
      return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
    }

    const query = `UPDATE medicines SET ${updates.join(', ')} WHERE id = @id`;
    await request.query(query);
    return res.json({ success: true, message: 'Cập nhật thành công' });
  } catch (err) {
    console.error('medicines.update error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update };
