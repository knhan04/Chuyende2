const { getPool } = require('../config/db');

async function getOrders(req, res) {
  const username = req.query.username;
  try {
    const pool = await getPool();

    // Lấy dữ liệu từ bảng orders (Mua thuốc/Xét nghiệm)
    const [orders] = await pool.execute(`
      SELECT o.fullname, o.address, o.contact, o.pincode, o.order_date, o.order_time, o.amount, o.category
      FROM orders o
      JOIN users u ON o.user_id = u.id
      WHERE u.username = ?
    `, [username]);

    // Lấy dữ liệu từ bảng appointments (Lịch hẹn bác sĩ)
    const [appointments] = await pool.execute(`
      SELECT doctor_name, doctor_location, doctor_contact, '' as pincode, appointment_date, appointment_time, fee, 'appointment' as category
      FROM appointments a
      JOIN users u ON a.user_id = u.id
      WHERE u.username = ?
    `, [username]);

    // Chuyển đổi sang định dạng chuỗi fullname$address$contact$pincode$date$time$amount$category
    const combinedData = [...orders, ...appointments].map(item => {
      const date = item.order_date || item.appointment_date;
      const time = item.order_time || item.appointment_time;
      const amount = item.amount || item.fee;
      const name = item.fullname || item.doctor_name;
      const address = item.address || item.doctor_location;
      const contact = item.contact || item.doctor_contact;

      return `${name}$${address}$${contact}$${item.pincode || ''}$${date}$${time}$${amount}$${item.category}`;
    });

    return res.json({ success: true, data: combinedData });
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

      // NẾU LÀ LỊCH HẸN BÁC SĨ -> LƯU VÀO BẢNG APPOINTMENTS CHO ADMIN THẤY
      if (actualCategory === 'appointment' || otype === 'appointment') {
          // Phân tách tên bác sĩ nếu fullname có dạng "Tiêu đề => Tên BS"
          let doctorName = fullname;
          let specialty = 'Bác sĩ';
          if (fullname.includes("=>")) {
              const parts = fullname.split("=>");
              specialty = parts[0].trim();
              doctorName = parts[1].trim();
          }

          console.log('📝 Đồng bộ lịch hẹn sang bảng Admin cho bác sĩ:', doctorName);

          await connection.execute(`
              INSERT INTO appointments (user_id, doctor_name, doctor_specialty, doctor_location, doctor_contact, appointment_date, appointment_time, fee, status)
              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
          `, [userId, doctorName, specialty, address, contact, actualDate, actualTime, amount, 'pending']);
      }

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

async function removeOrder(req, res) {
  const { fullname, otype, address } = req.query;
  try {
    const pool = await getPool();

    if (otype === 'appointment') {
        // Nếu là lịch hẹn, xóa ở cả 2 bảng
        let doctorName = fullname;
        if (fullname.includes("=>")) {
            doctorName = fullname.split("=>")[1].trim();
        }
        await pool.execute('DELETE FROM appointments WHERE doctor_name = ? AND doctor_location = ?', [doctorName, address]);
    }

    // Xóa trong bảng orders
    await pool.execute('DELETE FROM orders WHERE fullname = ? AND category = ? AND address = ?', [fullname, otype, address]);

    return res.json({ success: true, message: 'Đã xóa thành công' });
  } catch (err) {
    console.error('orders.removeOrder error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { getOrders, getOrderById, createOrder, cancelOrder, removeOrder };