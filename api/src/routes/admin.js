const express = require('express');
const router = express.Router();
const { getAllAppointments, updateAppointmentStatus, updateDoctorShift } = require('../controllers/adminController');
const { authenticate, authorizeAdmin } = require('../middleware/auth');

// Chỉ những người có role là 'admin' mới được truy cập các đường dẫn này
router.get('/appointments', authenticate, authorizeAdmin, getAllAppointments);
router.put('/appointment-status', authenticate, authorizeAdmin, updateAppointmentStatus);
router.put('/doctor-shift', authenticate, authorizeAdmin, updateDoctorShift);

module.exports = router;
