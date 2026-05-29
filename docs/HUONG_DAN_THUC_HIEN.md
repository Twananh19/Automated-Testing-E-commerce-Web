# HƯỚNG DẪN THỰC HIỆN BÀI TẬP LỚN
## Đề tài: Tự động hóa Web - Kiểm thử luồng mua hàng trên trang E-commerce (saucedemo.com)
## Học phần: Đánh giá và Kiểm định Chất lượng Phần mềm

---

## MỤC LỤC

1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Cấu trúc thư mục](#2-cấu-trúc-thư-mục)
3. [Kiến trúc Framework](#3-kiến-trúc-framework)
4. [Kỹ thuật kiểm thử áp dụng](#4-kỹ-thuật-kiểm-thử-áp-dụng)
5. [Ma trận Test Cases](#5-ma-trận-test-cases)
6. [Phân công công việc](#6-phân-công-công-việc)
7. [Hướng dẫn chạy test](#7-hướng-dẫn-chạy-test)
8. [Checklist hoàn thành](#8-checklist-hoàn-thành)

---

## 1. TỔNG QUAN DỰ ÁN

### 1.1 Mục tiêu
Xây dựng bộ kiểm thử tự động (Automated Testing) cho trang web thương mại điện tử **saucedemo.com**, tập trung vào luồng mua hàng chính (Purchase Flow).

### 1.2 Công nghệ sử dụng
| Thành phần | Công nghệ | Phiên bản |
|-----------|-----------|-----------|
| Ngôn ngữ | Java | 17 |
| Automation Tool | Selenium WebDriver | 4.19.1 |
| Test Framework | JUnit 5 (Jupiter) | 5.10.2 |
| Build Tool | Apache Maven | 3.x |
| Design Pattern | Page Object Model (POM) | - |

### 1.3 Phạm vi kiểm thử (Scope)
- **Chức năng Đăng nhập (Login)**: 8 test cases
- **Luồng Mua hàng (Checkout Flow)**: 6 test cases
- **Tổng cộng**: 14 test cases tự động

### 1.4 Ngoài phạm vi (Out of Scope)
- Kiểm thử hiệu năng (Performance Testing)
- Kiểm thử bảo mật (Security Testing)
- Kiểm thử trên nhiều trình duyệt (Cross-browser Testing)

---

## 2. CẤU TRÚC THƯ MỤC

```
saucedemo-automation/
├── pom.xml                                          ← File cấu hình Maven (dependencies)
├── docs/
│   ├── HUONG_DAN_THUC_HIEN.md                      ← File hướng dẫn này
│   ├── screenshots/                                 ← Ảnh chụp khi test fail (tự động)
│   └── TestPlan.md                                  ← Test Plan (tùy chọn mở rộng)
│
├── src/main/java/com/saucedemo/pages/              ← PAGE OBJECTS (Mô hình hóa các trang web)
│   ├── BasePage.java                                ← Lớp cơ sở - chứa hàm tiện ích dùng chung
│   ├── LoginPage.java                               ← Trang Đăng nhập
│   ├── InventoryPage.java                           ← Trang Danh sách sản phẩm
│   ├── CartPage.java                                ← Trang Giỏ hàng
│   ├── CheckoutInfoPage.java                        ← Trang Nhập thông tin giao hàng
│   ├── CheckoutOverviewPage.java                    ← Trang Tổng quan đơn hàng
│   └── CheckoutCompletePage.java                    ← Trang Đặt hàng thành công
│
├── src/test/java/com/saucedemo/
│   ├── base/
│   │   └── BaseTest.java                            ← Lớp cơ sở test - setup/teardown WebDriver
│   └── tests/
│       ├── LoginTest.java                           ← Bộ test Đăng nhập (8 test cases)
│       └── CheckoutTest.java                        ← Bộ test Mua hàng (6 test cases)
│
└── target/                                          ← Thư mục build (tự động tạo bởi Maven)
    └── surefire-reports/                            ← Báo cáo kết quả test (tự động)
```

---

## 3. KIẾN TRÚC FRAMEWORK

### 3.1 Page Object Model (POM)

```
┌─────────────────────────────────────────────────────┐
│                    TEST LAYER                        │
│  LoginTest.java       CheckoutTest.java             │
│  (Chứa logic kiểm thử, assertions)                 │
│         ↓ kế thừa BaseTest                          │
├─────────────────────────────────────────────────────┤
│                  BASE TEST LAYER                     │
│  BaseTest.java                                       │
│  (Setup/Teardown WebDriver, screenshot)              │
├─────────────────────────────────────────────────────┤
│                   PAGE LAYER                         │
│  LoginPage  InventoryPage  CartPage  Checkout...     │
│  (Locators + Action methods cho từng trang)          │
│         ↓ kế thừa BasePage                          │
├─────────────────────────────────────────────────────┤
│                  BASE PAGE LAYER                     │
│  BasePage.java                                       │
│  (Hàm tiện ích: click, enterText, getText + Wait)    │
├─────────────────────────────────────────────────────┤
│              SELENIUM WEBDRIVER                       │
│  (Tương tác trực tiếp với trình duyệt)              │
└─────────────────────────────────────────────────────┘
```

### 3.2 Nguyên tắc thiết kế
1. **Tính đóng gói (Encapsulation)**: Tất cả locators là `private final` — không ai ngoài Page class được truy cập trực tiếp.
2. **Explicit Wait**: Không dùng `Thread.sleep()` hay `implicitlyWait()`. Mọi thao tác đều qua `WebDriverWait` + `ExpectedConditions`.
3. **DRY (Don't Repeat Yourself)**: Hàm dùng chung nằm trong `BasePage`, logic setup nằm trong `BaseTest`.
4. **Arrange-Act-Assert**: Mỗi test case tuân theo pattern AAA rõ ràng.

---

## 4. KỸ THUẬT KIỂM THỬ ÁP DỤNG

### 4.1 Equivalence Partitioning (Phân vùng tương đương) — Khóa 2

Áp dụng cho **chức năng Login**:

| Phân vùng | Mô tả | Đại diện | Kết quả mong đợi |
|-----------|--------|----------|------------------|
| Hợp lệ | Username + Password đúng | standard_user / secret_sauce | Đăng nhập thành công |
| Không hợp lệ - Sai password | Username đúng, password sai | standard_user / wrong_password | Hiển thị lỗi |
| Không hợp lệ - User không tồn tại | Username sai | invalid_user / secret_sauce | Hiển thị lỗi |
| Không hợp lệ - User bị khóa | Tài khoản locked | locked_out_user / secret_sauce | Hiển thị lỗi bị khóa |

### 4.2 Boundary Value Analysis (Phân tích giá trị biên) — Khóa 2

| Giá trị biên | Input | Kết quả mong đợi |
|-------------|-------|------------------|
| Rỗng (min) | Username = "" | "Username is required" |
| Rỗng (min) | Password = "" | "Password is required" |
| Cả 2 rỗng | Username = "", Password = "" | "Username is required" |

### 4.3 Decision Table Testing (Bảng quyết định) — Khóa 2

Áp dụng cho **chức năng Checkout Info**:

| Rule | First Name | Last Name | Zip Code | Kết quả |
|------|-----------|----------|----------|---------|
| R1 | ✓ | ✓ | ✓ | Chuyển sang Overview |
| R2 | ✗ (rỗng) | ✓ | ✓ | "First Name is required" |
| R3 | ✓ | ✗ (rỗng) | ✓ | "Last Name is required" |
| R4 | ✓ | ✓ | ✗ (rỗng) | "Postal Code is required" |

---

## 5. MA TRẬN TEST CASES

### 5.1 Login Test Cases

| ID | Tên Test Case | Loại | Kỹ thuật | Thành viên |
|----|--------------|------|---------|------------|
| TC_LOGIN_01 | Đăng nhập thành công với tài khoản hợp lệ | Positive | EP | A |
| TC_LOGIN_02 | Đăng nhập với tài khoản bị khóa | Negative | EP | A |
| TC_LOGIN_03 | Đăng nhập với mật khẩu sai | Negative | EP | A |
| TC_LOGIN_04 | Đăng nhập với username rỗng | Negative | BVA | A |
| TC_LOGIN_05 | Đăng nhập với password rỗng | Negative | BVA | A |
| TC_LOGIN_06 | Đăng nhập với cả 2 trường rỗng | Negative | BVA | A |
| TC_LOGIN_07 | Đăng nhập với username không tồn tại | Negative | EP | A |
| TC_LOGIN_08 | Đăng nhập với performance_glitch_user | Positive | EP | A |

### 5.2 Checkout Test Cases

| ID | Tên Test Case | Loại | Kỹ thuật | Thành viên |
|----|--------------|------|---------|------------|
| TC_CHECKOUT_01 | Luồng mua hàng thành công end-to-end | Positive | E2E | B |
| TC_CHECKOUT_02 | Checkout không nhập First Name | Negative | Decision Table | B |
| TC_CHECKOUT_03 | Checkout không nhập Last Name | Negative | Decision Table | B |
| TC_CHECKOUT_04 | Checkout không nhập Zip Code | Negative | Decision Table | B |
| TC_CHECKOUT_05 | Xóa sản phẩm khỏi giỏ hàng | Negative | Functional | B |
| TC_CHECKOUT_06 | Verify tên sản phẩm trên trang Overview | Positive | Functional | B |

---

## 6. PHÂN CÔNG CÔNG VIỆC

### Thành viên A — Login + Framework Core

| Phase | Task | Mô tả | Trạng thái |
|-------|------|-------|-----------|
| 1 | Hiểu kiến trúc | Đọc hiểu BasePage.java, BaseTest.java, cách POM hoạt động | ☐ |
| 2 | Review LoginPage.java | Đọc code, hiểu locators và action methods | ☐ |
| 3 | Review LoginTest.java | Hiểu 8 test cases, biết cách thêm test mới | ☐ |
| 4 | Chạy thử test | `mvn test -Dtest=LoginTest` — verify tất cả PASS | ☐ |
| 5 | Viết báo cáo phần Login | Mô tả kỹ thuật EP, BVA đã áp dụng | ☐ |
| 6 | Viết phần Test Plan | Mục đích, scope, strategy, môi trường | ☐ |

### Thành viên B — Checkout Flow + Reporting

| Phase | Task | Mô tả | Trạng thái |
|-------|------|-------|-----------|
| 1 | Hiểu kiến trúc | Đọc hiểu các Page classes cho checkout flow | ☐ |
| 2 | Review CheckoutTest.java | Hiểu 6 test cases, luồng E2E | ☐ |
| 3 | Chạy thử test | `mvn test -Dtest=CheckoutTest` — verify tất cả PASS | ☐ |
| 4 | Chạy toàn bộ test | `mvn test` — verify 14 test cases PASS | ☐ |
| 5 | Tạo test report | `mvn surefire-report:report` → mở target/site/surefire-report.html | ☐ |
| 6 | Viết báo cáo phần Checkout | Mô tả kỹ thuật Decision Table, E2E đã áp dụng | ☐ |

### Công việc chung

| Task | Mô tả | Trạng thái |
|------|-------|-----------|
| Viết báo cáo BTL | Ghép 2 phần lại, thêm mở đầu/kết luận | ☐ |
| Chuẩn bị slide thuyết trình | Tóm tắt kiến trúc + demo | ☐ |
| Demo trước nhóm | Chạy `mvn test` live | ☐ |

---

## 7. HƯỚNG DẪN CHẠY TEST

### 7.1 Yêu cầu hệ thống
- **Java JDK 17** trở lên
- **Apache Maven 3.x**
- **Google Chrome** (phiên bản mới nhất)
- **IDE**: IntelliJ IDEA hoặc VS Code

### 7.2 Cài đặt
```bash
# Kiểm tra Java
java -version

# Kiểm tra Maven
mvn -version

# Tải dependencies
mvn clean compile
```

### 7.3 Chạy test

```bash
# Chạy TẤT CẢ test cases
mvn test

# Chạy CHỈ LoginTest
mvn test -Dtest=com.saucedemo.tests.LoginTest

# Chạy CHỈ CheckoutTest
mvn test -Dtest=com.saucedemo.tests.CheckoutTest

# Chạy 1 test case cụ thể
mvn test -Dtest=com.saucedemo.tests.LoginTest#testLoginSuccessfully
```

### 7.4 Xem báo cáo kết quả
```bash
# Tạo báo cáo HTML
mvn surefire-report:report

# Mở file báo cáo (Linux)
xdg-open target/site/surefire-report.html
```

---

## 8. CHECKLIST HOÀN THÀNH

Trước khi nộp bài, đảm bảo đã hoàn thành tất cả:

- [ ] Tất cả 14 test cases chạy PASS (`mvn test`)
- [ ] Code có comment tiếng Việt đầy đủ
- [ ] Báo cáo BTL có đủ:
  - [ ] Test Plan (mục đích, scope, strategy)
  - [ ] Mô tả kiến trúc POM
  - [ ] Ma trận Test Cases
  - [ ] Mô tả kỹ thuật kiểm thử (EP, BVA, Decision Table)
  - [ ] Kết quả chạy test (screenshot hoặc report)
  - [ ] Kết luận
- [ ] Code được push lên Git repository
- [ ] Slide thuyết trình đã chuẩn bị
- [ ] Demo chạy được trên máy thật

---

**Lưu ý quan trọng**: Nếu test fail do Chrome chạy headless trên máy cá nhân, hãy bỏ dòng `options.addArguments("--headless")` trong `BaseTest.java` để xem trình duyệt chạy trực tiếp và debug dễ hơn.
