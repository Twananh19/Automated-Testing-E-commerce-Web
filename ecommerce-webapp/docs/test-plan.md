# Kế hoạch Kiểm thử — E-Commerce Web Application
## (IEEE 829 Standard)

---

## 1. Giới thiệu

### 1.1 Mục đích
Tài liệu này mô tả kế hoạch kiểm thử cho ứng dụng web thương mại điện tử xây dựng bằng Spring Boot. Kế hoạch bao gồm phạm vi, phương pháp, tài nguyên, lịch trình và tiêu chí kiểm thử.

### 1.2 Phạm vi
- **Trong phạm vi**: Unit Test, Integration Test, E2E Test, Property-Based Test, Mutation Testing
- **Ngoài phạm vi**: Performance Test, Security Penetration Test, Usability Test

### 1.3 Tài liệu tham chiếu
- `docs/requirements.md` — Đặc tả yêu cầu (REQ-001 → REQ-030)
- `docs/test-case-spec.md` — Bảng đặc tả test case
- `docs/traceability-matrix.md` — Ma trận truy xuất
- `docs/demo-guide.md` — Hướng dẫn demo trước giảng viên

---

## 2. Chiến lược Kiểm thử

### 2.1 Mức độ kiểm thử (Testing Levels)

| Mức | Công cụ | Mục tiêu | Số lượng |
|-----|---------|----------|----------|
| Unit Test | JUnit 5 + Mockito | Service layer logic | 53 cases |
| Property-Based Test | jqwik 1.8.4 | Bất biến logic nghiệp vụ | 6 cases |
| Integration Test | MockMvc + standaloneSetup | Controller + Service integration | 15 cases |
| E2E Test | Selenium WebDriver | Full user flow qua browser | 8 cases |

### 2.2 Kỹ thuật Thiết kế Test Case

| Kỹ thuật | Áp dụng cho | Ví dụ |
|----------|-------------|-------|
| Boundary Value Analysis (BVA) | Giá trị biên: stock=0, qty=0, discount=0%/100%, quantity=9/10/19/20 | `calculateTotal_exactly10Items_BVA()` |
| Equivalence Partitioning (EP) | Phân lớp: giá hợp lệ/không hợp lệ, voucher valid/invalid/expired | `addProduct_withNegativePrice_EP()` |
| Decision Table | Voucher × Cart state combinations | `applyVoucher_decisionTable()` |
| State Transition | OrderStatus lifecycle: PENDING→CONFIRMED→SHIPPED→DELIVERED | `confirmOrder_fromPending_stateTransition()` |
| Property-Based | Tính chất bất biến của calculateTotal(), discountedPrice | `total_alwaysNonNegative_property()` |

### 2.3 Công cụ Đo chất lượng

| Công cụ | Mục đích | Ngưỡng mục tiêu |
|---------|----------|-----------------|
| JaCoCo | Code Coverage (Line & Branch) | Line ≥ 80%, Branch ≥ 70% |
| PIT | Mutation Testing Score | ≥ 70% cho service layer |
| GitHub Actions | CI — chạy test tự động mỗi push | Badge xanh |

---

## 3. Tài nguyên

### 3.1 Môi trường kiểm thử
- **Ngôn ngữ**: Java 17+
- **Framework**: Spring Boot 3.2.5
- **Database**: H2 In-Memory (tự động reset mỗi lần test)
- **Browser**: Chrome headless (cho Selenium)
- **Build**: Maven 3.9+

### 3.2 Dữ liệu test
- DataSeeder tự động seed 6 sản phẩm
- 3 user accounts qua InMemoryUserDetailsManager
- 3 voucher hợp lệ + 1 voucher hết hạn

---

## 4. Tiêu chí

### 4.1 Entry Criteria (Tiêu chí bắt đầu)
- [x] Source code biên dịch thành công (`mvn compile`)
- [x] Môi trường H2 database khởi tạo đúng
- [x] DataSeeder tạo đủ dữ liệu mẫu
- [x] Chrome browser có sẵn trên máy test

### 4.2 Exit Criteria (Tiêu chí kết thúc)
- [x] Tất cả test cases PASS (0 failures)
- [x] JaCoCo Line Coverage ≥ 80% cho service layer
- [x] JaCoCo Branch Coverage ≥ 70% cho service layer
- [x] PIT Mutation Score ≥ 70% cho service layer
- [x] Traceability Matrix: mọi REQ có ≥ 1 TC cover
- [x] CI pipeline (GitHub Actions) xanh

### 4.3 Suspension Criteria
- Hệ thống không thể khởi động
- Database không khởi tạo được
- Chrome driver không tương thích

---

## 5. Rủi ro

| Rủi ro | Khả năng | Tác động | Giải pháp |
|--------|----------|----------|-----------|
| Selenium flaky test (timing issue) | Trung bình | Test fail không ổn định | Dùng WebDriverWait explicit, JavaScript click |
| JaCoCo version không tương thích JDK | Thấp | Coverage report lỗi | Cập nhật JaCoCo lên phiên bản mới nhất |
| PIT chạy quá chậm | Trung bình | Mất thời gian | Giới hạn targetClasses, dùng threads=4 |
| Spring Security CSRF làm vỡ form test | Cao | Selenium test fail 403 | Dùng th:action, đảm bảo token trong form |
| ByteBuddy không tương thích JDK mới | Trung bình | Mockito test fail | Thêm `-Dnet.bytebuddy.experimental=true` |

---

## 6. Lịch trình

| Phase | Hoạt động | Trạng thái |
|-------|-----------|-----------|
| Phase 0 | Setup tooling (JaCoCo, PIT, CI, Spring Security) | ✅ Hoàn thành |
| Phase 1 | Tài liệu kiểm thử (Test Plan, Test Case Spec) | ✅ Hoàn thành |
| Phase 2 | Hoàn thiện tính năng (Login, fix hardcode user) | ✅ Hoàn thành |
| Phase 3 | Refactor Page Object + data-testid | ✅ Hoàn thành |
| Phase 4 | Mở rộng test suite (Integration + BVA + PBT) | ✅ Hoàn thành |
| Phase 5 | Đo chất lượng & phân tích (JaCoCo, PIT) | ✅ Hoàn thành |
| Phase 6 | Viết tài liệu demo & hoàn thiện docs | ✅ Hoàn thành |
