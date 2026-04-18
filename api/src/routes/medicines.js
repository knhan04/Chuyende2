const express = require('express');
const router = express.Router();
const { getAll, getById, create, update } = require('../controllers/medicineController');
const { authenticate, authorizeAdmin } = require('../middleware/auth');

router.get('/',      getAll);
router.get('/:id',   getById);
router.post('/',     authenticate, authorizeAdmin, create);
router.put('/:id',   authenticate, authorizeAdmin, update);

module.exports = router;
