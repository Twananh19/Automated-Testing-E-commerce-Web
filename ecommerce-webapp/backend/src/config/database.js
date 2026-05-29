const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, '..', '..', 'data', 'shop.db');
const db = new Database(dbPath);

// Bật WAL mode cho hiệu suất tốt hơn
db.pragma('journal_mode = WAL');

// Tạo bảng
db.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'user'
  );

  CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    image_url TEXT
  );

  CREATE TABLE IF NOT EXISTS cart_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
  );

  CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    zip_code TEXT NOT NULL,
    total REAL NOT NULL,
    status TEXT DEFAULT 'completed',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
  );
`);

// Seed data — chỉ chèn nếu bảng trống
const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get();
if (userCount.count === 0) {
  const insertUser = db.prepare('INSERT INTO users (username, password, role) VALUES (?, ?, ?)');
  insertUser.run('standard_user', 'secret_sauce', 'user');
  insertUser.run('locked_out_user', 'secret_sauce', 'locked');
  insertUser.run('performance_glitch_user', 'secret_sauce', 'user');

  const insertProduct = db.prepare('INSERT INTO products (name, description, price, image_url) VALUES (?, ?, ?, ?)');
  insertProduct.run('Sauce Labs Backpack', 'Sly Pack that melds function with fashion.', 29.99, '/images/backpack.jpg');
  insertProduct.run('Sauce Labs Bike Light', 'Water-resistant bike light for safe night riding.', 9.99, '/images/bike-light.jpg');
  insertProduct.run('Sauce Labs Bolt T-Shirt', 'American Apparel bolt t-shirt.', 15.99, '/images/bolt-shirt.jpg');
  insertProduct.run('Sauce Labs Fleece Jacket', 'Midweight fleece jacket.', 49.99, '/images/fleece-jacket.jpg');
  insertProduct.run('Sauce Labs Onesie', 'Rib snap infant onesie.', 7.99, '/images/onesie.jpg');
  insertProduct.run('Test.allTheThings() T-Shirt', 'Super soft t-shirt for developers.', 15.99, '/images/tshirt.jpg');

  console.log('✅ Database seeded with users and products');
}

module.exports = db;
