const express = require('express');
const db = require('../config/database');
const { authMiddleware } = require('../middleware/authMiddleware');

const router = express.Router();

router.get('/cart', authMiddleware, (req, res) => {
  const items = db.prepare(`
    SELECT ci.id, ci.quantity, p.id as product_id, p.name, p.price, p.image_url
    FROM cart_items ci JOIN products p ON ci.product_id = p.id
    WHERE ci.user_id = ?
  `).all(req.user.id);
  res.json(items);
});

router.post('/cart', authMiddleware, (req, res) => {
  const { productId } = req.body;
  const existing = db.prepare('SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?').get(req.user.id, productId);
  if (existing) {
    db.prepare('UPDATE cart_items SET quantity = quantity + 1 WHERE id = ?').run(existing.id);
  } else {
    db.prepare('INSERT INTO cart_items (user_id, product_id) VALUES (?, ?)').run(req.user.id, productId);
  }
  res.json({ success: true });
});

router.delete('/cart/:id', authMiddleware, (req, res) => {
  db.prepare('DELETE FROM cart_items WHERE id = ? AND user_id = ?').run(req.params.id, req.user.id);
  res.json({ success: true });
});

module.exports = router;
