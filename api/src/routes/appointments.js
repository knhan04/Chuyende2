const express = require('express');
const router = express.Router();
const { getAppointments, getAppointmentById, bookAppointment, cancelAppointment } = require('../controllers/appointmentController');
const { authenticate } = require('../middleware/auth');

router.use(authenticate);

router.get('/',       getAppointments);
router.get('/:id',    getAppointmentById);
router.post('/',      bookAppointment);
router.patch('/:id/cancel', cancelAppointment);

module.exports = router;
