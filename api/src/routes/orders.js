const express = require('express');
const router = express.Router();
const { getOrders, getOrderById, createOrder, cancelOrder, removeOrder } = require('../controllers/orderController');
const { authenticate } = require('../middleware/auth');

router.use(authenticate);

router.get('/',       getOrders);
router.get('/:id',    getOrderById);
router.post('/',      createOrder);
router.post('/remove', removeOrder);
router.patch('/:id/cancel', cancelOrder);

module.exports = router;
