# System Design — Custom E-commerce WebApp Mini

> Mục đích: Xây dựng một WebApp E-commerce mini để thay thế saucedemo.com làm mục tiêu kiểm thử tự động.

---

## 1. Stack công nghệ

| Layer | Công nghệ | Lý do |
|-------|-----------|-------|
| **Backend** | Node.js + Express.js | Nhẹ, không cần cài server phức tạp, npm install là chạy |
| **Frontend** | React (Vite) | Build nhanh, component-based, dễ thêm `data-testid` |
| **Database** | SQLite (via better-sqlite3) | Không cần cài DB server, 1 file `.db` là đủ |
| **State Mgmt** | React Context API | Đơn giản cho cart state, không cần Redux |

---

## 2. Kiến trúc hệ thống

```
┌──────────────────────────┐     REST API     ┌──────────────────────┐
│  React Frontend          │ ───────────────► │  Express Backend     │
│  (Port 5173)             │                  │  (Port 3000)         │
│                          │ ◄─────────────── │                      │
└──────────────────────────┘     JSON         └──────────┬───────────┘
         ▲                                               │
         │ HTTP                                          │ SQL
         │                                               ▼
┌──────────────────────────┐                  ┌──────────────────────┐
│  Selenium Tests          │                  │  SQLite Database     │
│  (Java + JUnit 5)        │                  │  (shop.db)           │
└──────────────────────────┘                  └──────────────────────┘
```

**Luồng mua hàng** (mirror saucedemo.com):
```
Login → Products → Cart → CheckoutInfo → CheckoutOverview → CheckoutComplete
```

---

## 3. Cấu trúc cây thư mục

```
ecommerce-webapp/
├── package.json                    ← Root workspace config
│
├── backend/                        ← Express.js API Server
│   ├── package.json
│   ├── src/
│   │   ├── app.js                  ← Express app entry point
│   │   ├── config/
│   │   │   └── database.js         ← SQLite connection + seed data
│   │   ├── routes/
│   │   │   ├── authRoutes.js       ← POST /api/login
│   │   │   ├── productRoutes.js    ← GET /api/products
│   │   │   ├── cartRoutes.js       ← GET/POST/DELETE /api/cart
│   │   │   └── orderRoutes.js      ← POST /api/orders
│   │   ├── controllers/
│   │   │   ├── authController.js
│   │   │   ├── productController.js
│   │   │   ├── cartController.js
│   │   │   └── orderController.js
│   │   ├── models/
│   │   │   ├── User.js
│   │   │   ├── Product.js
│   │   │   ├── Cart.js
│   │   │   └── Order.js
│   │   └── middleware/
│   │       └── authMiddleware.js   ← JWT verify
│   └── data/
│       └── shop.db                 ← SQLite database file
│
├── frontend/                       ← React + Vite
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.jsx                ← React entry point
│       ├── App.jsx                 ← Router setup
│       ├── context/
│       │   ├── AuthContext.jsx      ← Login state
│       │   └── CartContext.jsx      ← Cart state
│       ├── pages/
│       │   ├── LoginPage.jsx
│       │   ├── ProductsPage.jsx
│       │   ├── CartPage.jsx
│       │   ├── CheckoutInfoPage.jsx
│       │   ├── CheckoutOverviewPage.jsx
│       │   └── CheckoutCompletePage.jsx
│       ├── components/
│       │   ├── ProductCard.jsx
│       │   ├── CartItem.jsx
│       │   ├── Header.jsx
│       │   └── ProtectedRoute.jsx
│       ├── services/
│       │   └── api.js              ← Axios/fetch wrapper
│       └── styles/
│           └── App.css
│
└── README.md
```

---

## 4. Design Patterns áp dụng

| Pattern | Nơi áp dụng | Mô tả |
|---------|-------------|-------|
| **MVC** | Backend (Express) | Routes → Controllers → Models, tách biệt rõ ràng |
| **Repository Pattern** | `models/` | Mỗi model đóng gói truy vấn DB, không SQL rải rác |
| **Middleware Pattern** | `authMiddleware.js` | Xác thực JWT trước khi vào controller |
| **Context/Provider** | React Context API | `AuthContext` và `CartContext` quản lý state global |
| **Protected Route** | `ProtectedRoute.jsx` | HOC pattern chặn truy cập khi chưa login |
| **Service Layer** | `services/api.js` | Tập trung mọi API call vào 1 nơi |

---

## 5. Database Schema (SQLite)

### USERS
| Column | Type | Constraint |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| username | TEXT | UNIQUE, NOT NULL |
| password | TEXT | NOT NULL |
| role | TEXT | DEFAULT 'user' |

### PRODUCTS
| Column | Type | Constraint |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| name | TEXT | NOT NULL |
| description | TEXT | |
| price | REAL | NOT NULL |
| image_url | TEXT | |

### CART_ITEMS
| Column | Type | Constraint |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| user_id | INTEGER | FK → USERS(id) |
| product_id | INTEGER | FK → PRODUCTS(id) |
| quantity | INTEGER | DEFAULT 1 |

### ORDERS
| Column | Type | Constraint |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| user_id | INTEGER | FK → USERS(id) |
| first_name | TEXT | NOT NULL |
| last_name | TEXT | NOT NULL |
| zip_code | TEXT | NOT NULL |
| total | REAL | NOT NULL |
| status | TEXT | DEFAULT 'completed' |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP |

---

## 6. API Endpoints

| Method | Endpoint | Mô tả | Auth |
|--------|---------|-------|------|
| POST | `/api/login` | Đăng nhập, trả JWT | ✗ |
| GET | `/api/products` | Lấy danh sách sản phẩm | ✓ |
| POST | `/api/cart` | Thêm sản phẩm vào giỏ | ✓ |
| GET | `/api/cart` | Xem giỏ hàng | ✓ |
| DELETE | `/api/cart/:id` | Xóa sản phẩm khỏi giỏ | ✓ |
| POST | `/api/orders` | Tạo đơn hàng | ✓ |

---

## 7. Chiến lược data-testid cho Selenium

Mỗi element tương tác đều có `data-testid` rõ ràng, map 1:1 với Page Object hiện tại:

| Trang | Element | data-testid |
|-------|---------|------------|
| Login | Username input | `data-testid="username"` |
| Login | Password input | `data-testid="password"` |
| Login | Login button | `data-testid="login-button"` |
| Login | Error message | `data-testid="error-message"` |
| Products | Add to cart btn | `data-testid="add-to-cart-{product-id}"` |
| Header | Cart icon | `data-testid="cart-link"` |
| Header | Cart badge | `data-testid="cart-badge"` |
| Cart | Checkout btn | `data-testid="checkout-button"` |
| Cart | Remove btn | `data-testid="remove-{product-id}"` |
| Checkout | First name | `data-testid="first-name"` |
| Checkout | Last name | `data-testid="last-name"` |
| Checkout | Zip code | `data-testid="postal-code"` |
| Checkout | Continue btn | `data-testid="continue-button"` |
| Overview | Finish btn | `data-testid="finish-button"` |
| Overview | Total price | `data-testid="total-price"` |
| Complete | Success header | `data-testid="success-header"` |
