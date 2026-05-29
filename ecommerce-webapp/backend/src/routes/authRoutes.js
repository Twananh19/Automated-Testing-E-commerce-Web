const express = require('express');
const jwt = require('jsonwebtoken');
const db = require('../config/database');
const { SECRET } = require('../middleware/authMiddleware');

const router = express.Router();

router.post('/login', (req, res) => {
  const { username, password } = req.body;

  if (!username) return res.status(400).json({ error: 'Username is required' });
  if (!password) return res.status(400).json({ error: 'Password is required' });

  const user = db.prepare('SELECT * FROM users WHERE username = ? AND password = ?').get(username, password);
  if (!user) return res.status(401).json({ error: 'Username and password do not match any user in this service' });
  if (user.role === 'locked') return res.status(403).json({ error: 'Sorry, this user has been locked out.' });

  const token = jwt.sign({ id: user.id, username: user.username }, SECRET, { expiresIn: '1h' });
  res.json({ token, username: user.username });
});

module.exports = router;
