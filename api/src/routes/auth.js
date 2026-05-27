const express = require('express');
const router = express.Router();
const { register, login, getProfile, updateProfile, getAllUsers } = require('../controllers/authController');
const { authenticate } = require('../middleware/auth');

router.post('/register', register);
router.post('/login',    login);
router.get('/profile',   authenticate, getProfile);
router.put('/profile',   authenticate, updateProfile);
router.get('/users',     authenticate, getAllUsers);

module.exports = router;
