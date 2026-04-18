const { getPool } = require('../config/db');

async function getOrders(req, res) {
  const userId = req.user.id;
  const { status, category } = req.query;
  try {
    const pool = await getPool();
    let query = `
      SELECT o.*,
        (SELECT SUM(oi.price * oi.quantity) FROM order_items oi WHERE oi.order_id = o.id) AS total_amount
      FROM orders o
      WHERE o.user_id = ?
    `;
    const params = [userId];
    if (status) {
      query += ' AND o.status = ?';
      params.push(status);
    }
    if (category) {
      query += ' AND o.category = ?';
      params.push(category);
    }
    query += ' ORDER BY o.created_at DESC';

    const [rows] = await pool.execute(query, params);
    return res.json({ success: true, data: rows });
  } catch (err) {
    console.error('orders.getOrders error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function getOrderById(req, res) {
  const userId = req.user.id;
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    const [orders] = await pool.execute('SELECT * FROM orders WHERE id = ? AND user_id = ?', [id, userId]);

    if (orders.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy đơn hàng' });
    }

    const [items] = await pool.execute('SELECT * FROM order_items WHERE order_id = ?', [id]);

    return res.json({
      success: true,
      data: { ...orders[0], items },
    });
  } catch (err) {
    console.error('orders.getOrderById error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function createOrder(req, res) {
  const userId = req.user.id;
  const { fullname, address, contact, pincode, order_date, order_time, category, items, date, time, otype } = req.body;
  const actualDate = order_date || date;
  const actualTime = order_time || time;
  const actualCategory = category || otype || 'medicine';
  const orderItems = Array.isArray(items) ? items : [];

  if (!fullname || !address || !contact || !actualDate || !actualTime) {
    return res.status(400).json({ success: false, message: 'Thiếu thông tin bắt buộc' });
  }

  const amount = orderItems.length > 0
    ? orderItems.reduce((sum, i) => sum + (i.price || 0) * (i.quantity || 1), 0)
    : parseFloat(req.body.amount || 0);

  try {
    const pool = await getPool();
    const connection = await pool.getConnection();

    try {
      await connection.beginTransaction();

      // Insert order
      const [orderResult] = await connection.execute(`
          INSERT INTO orders (user_id, fullname, address, contact, pincode, order_date, order_time, amount, category, status)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        `, [userId, fullname, address, contact, pincode || '', actualDate, actualTime, amount, actualCategory, 'pending']);

      const orderId = orderResult.insertId;

      // Insert order items
      if (orderItems.length > 0) {
        for (const item of orderItems) {
          await connection.execute(`
              INSERT INTO order_items (order_id, product_name, price, quantity)
              VALUES (?, ?, ?, ?)
            `, [orderId, item.product_name, item.price || 0, item.quantity || 1]);
        }
      }

      // Clear user's cart
      await connection.execute('DELETE FROM cart WHERE user_id = ? AND category = ?', [userId, actualCategory]);

      await connection.commit();
      return res.status(201).json({ success: true, message: 'Đặt hàng thành công', data: { order_id: orderId, amount } });
    } catch (innerErr) {
      await connection.rollback();
      throw innerErr;
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('orders.createOrder error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function cancelOrder(req, res) {
  const userId = req.user.id;
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ success: false, message: 'ID không hợp lệ' });

  try {
    const pool = await getPool();
    const [check] = await pool.execute("SELECT status FROM orders WHERE id = ? AND user_id = ?", [id, userId]);

    if (check.length === 0) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy đơn hàng' });
    }
    if (check[0].status !== 'pending') {
      return res.status(400).json({ success: false, message: 'Chỉ có thể hủy đơn hàng đang chờ xử lý' });
    }

    await pool.execute("UPDATE orders SET status = 'cancelled' WHERE id = ?", [id]);

    return res.json({ success: true, message: 'Đã hủy đơn hàng' });
  } catch (err) {
    console.error('orders.cancelOrder error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getOrders, getOrderById, createOrder, cancelOrder };