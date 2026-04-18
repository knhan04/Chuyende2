const { sql, getPool } = require('../config/db');

async function getAll(req, res) {
  const { search } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request();
    let query = 'SELECT id, title, summary, image FROM health_articles';
    if (search) {
      request.input('search', sql.NVarChar, `%${search}%`);
      query += ' WHERE title LIKE @search OR summary LIKE @search';
    }
    query += ' ORDER BY id DESC';
    const result = await request.query(query);
    return res.json({ success: true, data: result.recordset });
  } catch (err) {
    console.error('articles.getAll error:', err);
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
      .query('SELECT * FROM health_articles WHERE id = @id');

    if (result.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy bài viết' });
    }
    return res.json({ success: true, data: result.recordset[0] });
  } catch (err) {
    console.error('articles.getById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function create(req, res) {
  const { title, summary, image } = req.body;
  if (!title || !summary) return res.status(400).json({ success: false, message: 'title và summary là bắt buộc' });
  try {
    const pool = await getPool();
    await pool.request()
      .input('title',   sql.NVarChar, title)
      .input('summary', sql.NVarChar, summary)
      .input('image',   sql.NVarChar, image || null)
      .query(`
        INSERT INTO health_articles (title, summary, image)
        VALUES (@title, @summary, @image)
      `);
    
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const articleId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;
    
    return res.status(201).json({ 
      success: true, 
      message: 'Tạo bài viết thành công',
      data: { id: articleId, title, summary, image } 
    });
  } catch (err) {
    console.error('articles.create error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function update(req, res) {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });
  const { title, summary, image } = req.body;
  try {
    const pool = await getPool();
    
    const updates = [];
    const request = pool.request().input('id', sql.Int, id);
    
    if (title !== undefined) {
      request.input('title', sql.NVarChar, title);
      updates.push('title = @title');
    }
    if (summary !== undefined) {
      request.input('summary', sql.NVarChar, summary);
      updates.push('summary = @summary');
    }
    if (image !== undefined) {
      request.input('image', sql.NVarChar, image);
      updates.push('image = @image');
    }

    if (updates.length === 0) {
      return res.status(400).json({ success: false, message: 'Không có trường nào để cập nhật' });
    }

    const query = `UPDATE health_articles SET ${updates.join(', ')} WHERE id = @id`;
    await request.query(query);
    return res.json({ success: true, message: 'Cập nhật thành công' });
  } catch (err) {
    console.error('articles.update error:', err);
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
      .query('DELETE FROM health_articles WHERE id = @id');
    return res.json({ success: true, message: 'Đã xóa bài viết' });
  } catch (err) {
    console.error('articles.remove error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getAll, getById, create, update, remove };
