const mysql = require('mysql2/promise');
require('dotenv').config();

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT) || 3306,
  database: process.env.DB_NAME || 'healthcare_db',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  charset: process.env.DB_CHARSET || 'utf8mb4',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
};

let pool = null;

const getPool = async () => {
  if (pool) return pool;

  try {
    pool = mysql.createPool(dbConfig);
    const connection = await pool.getConnection();
    console.log('✅ Connected to MySQL Database:', dbConfig.database);
    connection.release();
    return pool;
  } catch (err) {
    console.error('❌ MySQL Connection Error:', err.message);
    pool = null;
    throw err;
  }
};

module.exports = {
  mysql,
  getPool
};
