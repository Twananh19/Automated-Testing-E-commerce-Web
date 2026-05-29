const express = require('express');
const db = require('../config/database');
const { authMiddleware } = require('../middleware/authMiddleware');

const router = express.Router();

router.post('/orders', authMiddleware, (req, res) => {
  const { firstName, lastName, zipCode } = req.body;

  if (!firstName) return res.status(400).json({ error: 'First Name is required' });
  if (!lastName) return res.status(400).json({ error: 'Last Name is required' });
  if (!zipCode) return res.status(400).json({ error: 'Postal Code is required' });

  // Tính tổng từ giỏ hàng
  const items = db.prepare(`
    SELECT ci.quantity, p.price FROM cart_items ci
    JOIN products p ON ci.product_id = p.id WHERE ci.user_id = ?
  `).all(req.user.id);

  if (items.length === 0) return res.status(400).json({ error: 'Cart is empty' });

  const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  db.prepare('INSERT INTO orders (user_id, first_name, last_name, zip_code, total) VALUES (?, ?, ?, ?, ?)')
    .run(req.user.id, firstName, lastName, zipCode, total);

  // Xóa giỏ hàng sau khi đặt hàng
  db.prepare('DELETE FROM cart_items WHERE user_id = ?').run(req.user.id);

  res.json({ success: true, message: 'Thank you for your order!', total });
});

module.exports = router;
