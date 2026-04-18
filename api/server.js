require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { getPool, closePool } = require('./src/config/db');

const app = express();

// ── Middleware ──────────────────────────────────────────────
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// ── Routes ──────────────────────────────────────────────────
app.use('/api/auth',         require('./src/routes/auth'));
app.use('/api/doctors',      require('./src/routes/doctors'));
app.use('/api/cart',         require('./src/routes/cart'));
app.use('/api/orders',       require('./src/routes/orders'));
app.use('/api/appointments', require('./src/routes/appointments'));
app.use('/api/articles',     require('./src/routes/articles'));
app.use('/api/lab-packages', require('./src/routes/lab'));
app.use('/api/medicines',    require('./src/routes/medicines'));

// ── Health check ────────────────────────────────────────────
app.get('/api/health', (req, res) => {
  res.json({ success: true, message: 'HealthCare API is running', timestamp: new Date() });
});

// ── 404 handler ─────────────────────────────────────────────
app.use((req, res) => {
  res.status(404).json({ success: false, message: `Không tìm thấy route: ${req.method} ${req.path}` });
});

// ── Global error handler ────────────────────────────────────
app.use((err, req, res, _next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ success: false, message: 'Lỗi máy chủ không xác định' });
});

// ── Start ───────────────────────────────────────────────────
const PORT = process.env.PORT || 3000;

async function start() {
  try {
    await getPool();
    app.listen(PORT, () => {
      console.log(`HealthCare API running on http://localhost:${PORT}`);
    });
  } catch (err) {
    console.error('Failed to start server:', err.message);
    process.exit(1);
  }
}

process.on('SIGINT',  async () => { await closePool(); process.exit(0); });
process.on('SIGTERM', async () => { await closePool(); process.exit(0); });

start();
