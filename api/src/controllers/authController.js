const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { mysql, getPool } = require('../config/db');

async function register(req, res) {
  const { username, email, password, full_name, phone } = req.body;

  if (!username || !email || !password) {
    return res.status(400).json({ success: false, message: 'username, email và password là bắt buộc' });
  }

  try {
    const pool = await getPool();
    const connection = await pool.getConnection();

    try {
      const [rows] = await connection.execute(
        'SELECT id FROM users WHERE username = ? OR email = ?',
        [username, email]
      );

      if (rows.length > 0) {
        return res.status(409).json({ success: false, message: 'Username hoặc email đã được sử dụng' });
      }

      const hash = await bcrypt.hash(password, 10);

      const [result] = await connection.execute(
        `INSERT INTO users (username, email, password, full_name, phone)
         VALUES (?, ?, ?, ?, ?)`,
        [username, email, hash, full_name || null, phone || null]
      );

      const [user] = await connection.execute(
        'SELECT id, username, email, full_name, phone, role, created_at FROM users WHERE id = ?',
        [result.insertId]
      );

      const token = jwt.sign(
        { id: user[0].id, username: user[0].username, role: user[0].role },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
      );

      return res.status(201).json({ 
        success: true, 
        message: 'Đăng ký thành công', 
        data: { user: user[0], token } 
      });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('register error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function login(req, res) {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({ success: false, message: 'username và password là bắt buộc' });
  }

  try {
    const pool = await getPool();
    const connection = await pool.getConnection();

    try {
      const [rows] = await connection.execute(
        'SELECT id, username, email, password, full_name, phone, role FROM users WHERE username = ?',
        [username]
      );

      if (rows.length === 0) {
        return res.status(401).json({ success: false, message: 'Tên đăng nhập hoặc mật khẩu không đúng' });
      }

      const user = rows[0];
      const match = await bcrypt.compare(password, user.password);

      if (!match) {
        return res.status(401).json({ success: false, message: 'Tên đăng nhập hoặc mật khẩu không đúng' });
      }

      const token = jwt.sign(
        { id: user.id, username: user.username, role: user.role },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
      );

      const { password: _pw, ...safeUser } = user;
      return res.json({ success: true, message: 'Đăng nhập thành công', data: { user: safeUser, token } });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('login error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function getProfile(req, res) {
  try {
    const pool = await getPool();
    const connection = await pool.getConnection();

    try {
      const [rows] = await connection.execute(
        'SELECT id, username, email, full_name, phone, role, created_at FROM users WHERE id = ?',
        [req.user.id]
      );

      if (rows.length === 0) {
        return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' });
      }

      return res.json({ success: true, data: rows[0] });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('getProfile error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

async function updateProfile(req, res) {
  const { full_name, phone } = req.body;
  const userId = req.user.id;

  try {
    const pool = await getPool();
    const connection = await pool.getConnection();

    try {
      await connection.execute(
        'UPDATE users SET full_name = ?, phone = ? WHERE id = ?',
        [full_name || null, phone || null, userId]
      );

      return res.json({ success: true, message: 'Cập nhật hồ sơ thành công' });
    } finally {
      connection.release();
    }
  } catch (err) {
    console.error('updateProfile error:', err);
    return res.status(500).json({ success: false, message: 'Lỗi máy chủ' });
  }
}

module.exports = { register, login, getProfile, updateProfile };
