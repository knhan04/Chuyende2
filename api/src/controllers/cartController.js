const { sql, getPool } = require('../config/db');

async function getCart(req, res) {
  const userId = req.user.id;
  const { category } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request().input('user_id', sql.Int, userId);
    let query = 'SELECT * FROM cart WHERE user_id = @user_id';
    if (category) {
      request.input('category', sql.NVarChar, category);
      query += ' AND category = @category';
    }
    query += ' ORDER BY added_at DESC';

    const result = await request.query(query);
    return res.json({ success: true, data: result.recordset });
  } catch (err) {
    console.error('cart.getCart error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function addToCart(req, res) {
  const userId = req.user.id;
  const { product_name, price, quantity, category } = req.body;

  if (!product_name || price === undefined) {
    return res.status(400).json({ success: false, message: 'product_name và price là bắt buộc' });
  }

  try {
    const pool = await getPool();

    const existing = await pool.request()
      .input('user_id',      sql.Int,     userId)
      .input('product_name', sql.NVarChar, product_name)
      .input('category',     sql.NVarChar, category || 'medicine')
      .query(`
        SELECT id, quantity FROM cart
        WHERE user_id = @user_id AND product_name = @product_name AND category = @category
      `);

    if (existing.recordset.length > 0) {
      const cartId = existing.recordset[0].id;
      const newQty = existing.recordset[0].quantity + (quantity || 1);
      await pool.request()
        .input('id',       sql.Int, cartId)
        .input('quantity', sql.Int, newQty)
        .query('UPDATE cart SET quantity = @quantity WHERE id = @id');

      return res.json({ success: true, message: 'Đã cập nhật số lượng', data: { id: cartId, quantity: newQty } });
    }

    await pool.request()
      .input('user_id',      sql.Int,     userId)
      .input('product_name', sql.NVarChar, product_name)
      .input('price',        sql.Float,   price)
      .input('quantity',     sql.Int,     quantity || 1)
      .input('category',     sql.NVarChar, category || 'medicine')
      .query(`
        INSERT INTO cart (user_id, product_name, price, quantity, category)
        VALUES (@user_id, @product_name, @price, @quantity, @category)
      `);

    // Get the inserted item
    const idResult = await pool.request().query('SELECT LAST_INSERT_ID() as id');
    const cartId = idResult.recordset[0]['LAST_INSERT_ID()'] || idResult.recordset[0].id;

    return res.status(201).json({ 
      success: true, 
      message: 'Đã thêm vào giỏ hàng', 
      data: { id: cartId, product_name, price, quantity: quantity || 1, category: category || 'medicine' } 
    });
  } catch (err) {
    console.error('cart.addToCart error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function removeFromCart(req, res) {
  const userId = req.user.id;
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    
    // Check if item exists
    const checkResult = await pool.request()
      .input('id',      sql.Int, id)
      .input('user_id', sql.Int, userId)
      .query('SELECT id FROM cart WHERE id = @id AND user_id = @user_id');

    if (checkResult.recordset.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy sản phẩm trong giỏ hàng' });
    }

    await pool.request()
      .input('id',      sql.Int, id)
      .input('user_id', sql.Int, userId)
      .query('DELETE FROM cart WHERE id = @id AND user_id = @user_id');

    return res.json({ success: true, message: 'Đã xóa khỏi giỏ hàng' });
  } catch (err) {
    console.error('cart.removeFromCart error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function clearCart(req, res) {
  const userId = req.user.id;
  const { category } = req.query;
  try {
    const pool = await getPool();
    const request = pool.request().input('user_id', sql.Int, userId);
    let query = 'DELETE FROM cart WHERE user_id = @user_id';
    if (category) {
      request.input('category', sql.NVarChar, category);
      query += ' AND category = @category';
    }
    await request.query(query);
    return res.json({ success: true, message: 'Đã xóa giỏ hàng' });
  } catch (err) {
    console.error('cart.clearCart error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getCart, addToCart, removeFromCart, clearCart };
