# Hướng dẫn Demo Kiểm thử trước Giảng viên
## E-Commerce Web Application — Automated Testing Project

---

## 1. Chuẩn bị Môi trường

### 1.1 Yêu cầu hệ thống
| Thành phần | Yêu cầu |
|------------|---------|
| JDK | 17 trở lên (`java -version` kiểm tra) |
| Maven | 3.9+ (`mvn -version` kiểm tra) |
| Google Chrome | Phiên bản mới nhất (cho Selenium tests) |
| RAM | Tối thiểu 4GB |
| Thời gian demo | ~15-20 phút |

### 1.2 Kiểm tra trước demo
```bash
# Kiểm tra Java
java -version

# Kiểm tra Maven
mvn -version

# Kiểm tra Chrome
google-chrome --version

# Biên dịch project (đảm bảo không lỗi)
mvn compile -q
```

---

## 2. Kịch bản Demo

### Phần A: Demo Ứng dụng Web (~5 phút)

#### Bước 1: Khởi động ứng dụng
```bash
mvn spring-boot:run
```
- Mở browser: **http://localhost:8080**
- Đợi đến khi thấy trang login

#### Bước 2: Demo luồng người dùng chính
1. **Đăng nhập**: `standard_user` / `secret_sauce`
2. **Tìm kiếm sản phẩm**: Gõ "Laptop" → hiển thị kết quả lọc
3. **Thêm vào giỏ**: Click "Thêm vào giỏ" cho 1-2 sản phẩm → badge cart tăng
4. **Xem giỏ hàng**: Click icon giỏ hàng → xem danh sách, tổng tiền
5. **Checkout**: Click "Thanh toán" → nhập voucher `SAVE10` → thấy giảm giá 10%
6. **Đặt hàng**: Click "Đặt hàng" → trang xác nhận hiện order ID, status PENDING
7. **Đăng xuất**: Dropdown user → Đăng xuất

> **Điểm nhấn**: Hệ thống có Spring Security (CSRF, session), H2 in-memory DB, 3 tài khoản test, 4 mã voucher.

#### Bước 3: Tắt ứng dụng
- Nhấn `Ctrl+C` trong terminal

---

### Phần B: Demo Unit Tests + Property-Based Tests (~5 phút)

#### Bước 1: Chạy toàn bộ Unit Tests
```bash
mvn test -B
```

**Kết quả mong đợi:**
```
Tests run: 76, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

#### Bước 2: Giải thích các kỹ thuật kiểm thử

**a) Equivalence Partitioning (EP):**
- Mở file `src/test/java/com/ecommerce/unit/ProductServiceTest.java`
- Chỉ ra test `addProduct_withValidData` (valid partition) vs `addProduct_withEmptyName` (invalid partition)
- Giải thích: "Chia miền input thành các lớp tương đương, chỉ cần 1 test mỗi lớp"

**b) Boundary Value Analysis (BVA):**
- Mở file `src/test/java/com/ecommerce/unit/OrderServiceTest.java`
- Chỉ ra 4 tests BVA:
  - `calculateTotal_exactly9Items` (9 items → 0% discount)
  - `calculateTotal_exactly10Items` (10 items → 5% discount)
  - `calculateTotal_exactly19Items` (19 items → 5% discount)
  - `calculateTotal_exactly20Items` (20 items → 10% discount)
- Giải thích: "Test tại chính xác boundary (9, 10, 19, 20) để phát hiện off-by-one errors"

**c) Decision Table:**
- Chỉ ra 3 tests voucher: `applyVoucher_SAVE10`, `applyVoucher_INVALID`, `applyVoucher_EXPIRED2024`
- Giải thích: "Decision table cho voucher: valid → giảm giá, invalid → lỗi, expired → lỗi hết hạn"

**d) State Transition:**
- Chỉ ra tests: `confirmOrder_pendingStatus`, `shipOrder_nonConfirmedStatus`
- Giải thích: "Kiểm thử chuyển trạng thái OrderStatus: PENDING → CONFIRMED → SHIPPED → DELIVERED"

**e) Property-Based Testing (jqwik):**
- Mở file `src/test/java/com/ecommerce/unit/PropertyBasedTest.java`
- Giải thích: "Tự động generate 200 random inputs, kiểm tra tính chất bất biến:
  - Total luôn ≥ 0
  - Total luôn ≤ subtotal
  - Discount rate chỉ có 3 giá trị: 0%, 5%, 10%"

---

### Phần C: Demo Integration Tests (~3 phút)

#### Bước 1: Giải thích Integration Tests
- Mở file `src/test/java/com/ecommerce/integration/CheckoutControllerTest.java`
- Giải thích: "Dùng MockMvc để test controller layer mà không cần khởi động server"
- Chỉ ra:
  - Mock services bằng `@Mock`
  - Test HTTP request/response: status code, redirect, model attributes, flash attributes
  - Test GET `/checkout` với giỏ trống → warning message
  - Test POST `/checkout/apply-voucher` → redirect + flash success/error

#### Bước 2: Chạy Integration Tests riêng (nếu cần)
```bash
mvn test -B -Dtest="com.ecommerce.integration.**"
```

---

### Phần D: Demo E2E Tests — Selenium (~3 phút)

#### Bước 1: Giải thích cấu trúc
- **Page Object Model**: Mỗi trang web có 1 page object class
  - `HomePage.java`, `CartPage.java`, `CheckoutPage.java`, `OrderConfirmationPage.java`
- **BaseSeleniumTest**: Setup Chrome headless, login tự động, screenshot

#### Bước 2: Chạy E2E Tests
```bash
# Cần ứng dụng KHÔNG đang chạy (Selenium tự khởi động bằng @SpringBootTest)
mvn verify -B
```

#### Bước 3: Xem screenshots
```bash
ls target/screenshots/
# Mỗi test tự động chụp screenshot
```

---

### Phần E: Demo Code Coverage & Mutation Testing (~3 phút)

#### Bước 1: JaCoCo Code Coverage
```bash
mvn test jacoco:report -B
```
- Mở file: `target/site/jacoco/index.html`
- Chỉ ra:
  - Line Coverage ≥ 80% cho service layer
  - Branch Coverage ≥ 70% cho service layer
  - Highlight những class được cover tốt (màu xanh)

#### Bước 2: PIT Mutation Testing
```bash
mvn org.pitest:pitest-maven:mutationCoverage -B
```
- Mở file: `target/pit-reports/[timestamp]/index.html`
- Giải thích:
  - "PIT thay đổi (mutate) source code, kiểm tra xem tests có bắt được mutation không"
  - "Mutation Score ≥ 70% nghĩa là tests phát hiện ≥ 70% mutations"
  - Chỉ ra ví dụ: đổi `>` thành `>=` → test nào bắt được?

---

## 3. Tổng kết khi trình bày

### Bảng tổng kết kỹ thuật kiểm thử

| Kỹ thuật | Mô tả | Ví dụ trong project |
|----------|-------|---------------------|
| BVA | Kiểm tra giá trị biên | qty=9/10/19/20, price=0, discount=0%/100% |
| EP | Phân lớp tương đương | Valid/invalid name, valid/invalid voucher |
| Decision Table | Bảng quyết định | Voucher: valid/invalid/expired |
| State Transition | Chuyển trạng thái | PENDING→CONFIRMED→SHIPPED→DELIVERED |
| Property-Based | Tính chất bất biến | Total ≥ 0, ratio ∈ {1.0, 0.95, 0.90} |
| White-box Coverage | Bao phủ code | JaCoCo Line ≥ 80%, Branch ≥ 70% |
| Mutation Testing | Kiểm thử đột biến | PIT Mutation Score ≥ 70% |

### Kiến trúc Testing Pyramid

```
          /\
         /E2E\          8 cases — Selenium (User flow qua browser)
        /──────\
       / Integ  \       15 cases — MockMvc (Controller logic)
      /──────────\
     / Unit+PBT   \    59 cases — JUnit + Mockito + jqwik
    /──────────────\

    Tổng: 82 test cases, 30 yêu cầu (100% covered)
```

### Công cụ đã sử dụng

| Công cụ | Mục đích |
|---------|----------|
| JUnit 5 | Framework chạy tests |
| Mockito | Mock dependencies |
| jqwik | Property-based testing |
| MockMvc | Test controller (no server) |
| Selenium | Browser automation (E2E) |
| JaCoCo | Đo code coverage |
| PIT | Mutation testing |
| GitHub Actions | CI/CD pipeline |

---

## 4. FAQ & Xử lý Sự cố

### Q: Tests chạy lâu quá?
**A**: Chạy riêng unit tests: `mvn test -Dtest="com.ecommerce.unit.**"` (~3 giây)

### Q: Selenium test bị fail?
**A**: Kiểm tra Chrome version: `google-chrome --version`. Đảm bảo không có instance app đang chạy.

### Q: PIT chạy quá chậm?
**A**: PIT cần ~2-5 phút. Đã config `threads=4` và giới hạn `targetClasses=com.ecommerce.service.*`.

### Q: JaCoCo report lỗi?
**A**: Chạy `mvn clean test jacoco:report` (clean trước).

### Q: Muốn xem chi tiết 1 test?
**A**: `mvn test -Dtest="OrderServiceTest#test_calculateTotal_exactly10Items_5percentDiscount_BVA"` — chạy 1 test cụ thể.

---

## 5. Checklist trước Demo

- [ ] `java -version` → JDK 17+
- [ ] `mvn -version` → Maven 3.9+
- [ ] `google-chrome --version` → Chrome mới nhất
- [ ] `mvn compile` → BUILD SUCCESS
- [ ] `mvn test` → 76 tests, 0 failures
- [ ] Đã mở sẵn các file test trong IDE
- [ ] Đã biết cách navigate đến JaCoCo/PIT report
- [ ] Đã chuẩn bị slide/notes cho phần giải thích
