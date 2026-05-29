const express = require('express');
const db = require('../config/database');
const { authMiddleware } = require('../middleware/authMiddleware');

const router = express.Router();

router.get('/products', authMiddleware, (req, res) => {
  const products = db.prepare('SELECT * FROM products').all();
  res.json(products);
});

module.exports = router;
