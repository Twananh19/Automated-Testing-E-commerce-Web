# E-Commerce Web Application - Automated Testing Project

Dự án kiểm thử tự động cho ứng dụng web thương mại điện tử sử dụng Spring Boot, JUnit 5, Mockito và Selenium WebDriver.

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.2.5 |
| Template Engine | Thymeleaf | (via starter) |
| Database | H2 (in-memory) | 2.x |
| ORM | Spring Data JPA | (via starter) |
| Unit Testing | JUnit 5 + Mockito | 5.10.x |
| E2E Testing | Selenium WebDriver | 4.27.0 |
| Driver Management | WebDriverManager | 5.9.2 |
| Build Tool | Maven | 3.9+ |
| Java | JDK | 17+ |

## Prerequisites

- **JDK**: 17 trở lên (khuyến nghị OpenJDK 17 hoặc 21)
- **Maven**: 3.9+
- **Google Chrome**: phiên bản mới nhất (cho Selenium tests)
- **RAM**: tối thiểu 4GB

## Cấu trúc dự án

```
spring-boot-app/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── EcommerceApplication.java         (Main class)
│   │   │   ├── config/DataSeeder.java            (Seed data)
│   │   │   ├── exception/                        (Custom exceptions)
│   │   │   ├── model/                            (Entity + POJO)
│   │   │   ├── repository/                       (JPA Repositories)
│   │   │   ├── service/                          (Business logic)
│   │   │   └── controller/                       (Web controllers)
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/                        (Thymeleaf HTML)
│   └── test/
│       ├── java/com/ecommerce/
│       │   ├── unit/                             (25 Unit tests)
│       │   └── selenium/                         (8 E2E tests)
│       └── resources/
│           └── application-test.properties
```

## Setup (Step-by-step)

```bash
# 1. Di chuyển vào thư mục project
cd spring-boot-app

# 2. Biên dịch project
mvn compile

# 3. Chạy ứng dụng (xem web tại http://localhost:8080)
mvn spring-boot:run

# 4. Chạy unit tests
mvn test

# 5. Chạy Selenium tests (cần Chrome)
mvn verify

# 6. Chạy tất cả tests + report
mvn verify
```

## Chạy từng loại test

```bash
# Chỉ Unit Tests (25 test cases)
mvn test -Dtest="com.ecommerce.unit.*"

# Chỉ Selenium E2E Tests (8 test cases)
mvn verify -Dit.test="com.ecommerce.selenium.*"

# Một class test cụ thể
mvn test -Dtest="com.ecommerce.unit.ProductServiceTest"
```

## Xem kết quả

- **Surefire Reports (Unit)**: `target/surefire-reports/`
- **Failsafe Reports (E2E)**: `target/failsafe-reports/`
- **Screenshots (khi test fail)**: `target/screenshots/`
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:ecommercedb`)

## Danh sách Test Cases

### Unit Tests (25 cases)

| # | Class | Test Case | Mô tả |
|---|-------|-----------|--------|
| 1 | ProductServiceTest | test_addProduct_withValidData_shouldSaveSuccessfully | Thêm SP hợp lệ |
| 2 | ProductServiceTest | test_addProduct_withEmptyName_shouldThrowException | Tên rỗng → lỗi |
| 3 | ProductServiceTest | test_addProduct_withNegativePrice_shouldThrowException | Giá âm → lỗi |
| 4 | ProductServiceTest | test_updateStock_validQuantity_shouldUpdateCorrectly | Cập nhật tồn kho |
| 5 | ProductServiceTest | test_updateStock_negativeResult_shouldThrowInsufficientStockException | Tồn kho âm → lỗi |
| 6 | ProductServiceTest | test_searchByCategory_caseInsensitive_shouldReturnResults | Tìm theo category |
| 7 | ProductServiceTest | test_calculateDiscount_validPercent_shouldReturnCorrectPrice | Giảm giá hợp lệ |
| 8 | ProductServiceTest | test_calculateDiscount_above100Percent_shouldThrowException | Discount > 100% |
| 9 | OrderServiceTest | test_createOrder_sufficientStock_shouldCreateSuccessfully | Tạo đơn hàng |
| 10 | OrderServiceTest | test_createOrder_insufficientStock_shouldThrowException | Hết hàng → lỗi |
| 11 | OrderServiceTest | test_cancelOrder_pendingStatus_shouldCancelSuccessfully | Hủy đơn PENDING |
| 12 | OrderServiceTest | test_cancelOrder_confirmedStatus_shouldThrowException | Hủy đơn CONFIRMED |
| 13 | OrderServiceTest | test_calculateTotal_below10Items_noDiscount | <10 items, no discount |
| 14 | OrderServiceTest | test_calculateTotal_10to19Items_apply5PercentDiscount | 10-19 items, -5% |
| 15 | OrderServiceTest | test_calculateTotal_20orMoreItems_apply10PercentDiscount | >=20 items, -10% |
| 16 | OrderServiceTest | test_applyVoucher_validCode_shouldApplyDiscount | Voucher hợp lệ |
| 17 | OrderServiceTest | test_applyVoucher_invalidCode_shouldThrowException | Voucher sai |
| 18 | OrderServiceTest | test_applyVoucher_expiredCode_shouldThrowException | Voucher hết hạn |
| 19 | CartServiceTest | test_addToCart_productInStock_shouldAddSuccessfully | Thêm vào giỏ |
| 20 | CartServiceTest | test_addToCart_outOfStock_shouldThrowException | Hết hàng → lỗi |
| 21 | CartServiceTest | test_addToCart_exceedsStock_shouldThrowException | Vượt tồn kho |
| 22 | CartServiceTest | test_removeFromCart_existingItem_shouldRemoveSuccessfully | Xóa khỏi giỏ |
| 23 | CartServiceTest | test_removeFromCart_nonExistentItem_shouldThrowException | SP không có |
| 24 | CartServiceTest | test_clearCart_shouldRemoveAllItems | Xóa toàn bộ giỏ |
| 25 | CartServiceTest | test_getCartTotal_multipleItems_shouldCalculateCorrectly | Tính tổng giỏ |

### Selenium E2E Tests (8 cases)

| # | Class | Test Case | Mô tả |
|---|-------|-----------|--------|
| 1 | HomePageTest | test_searchProduct_byKeyword_shouldShowRelevantResults | Tìm kiếm SP |
| 2 | HomePageTest | test_searchProduct_noResults_shouldShowEmptyMessage | Tìm không có kết quả |
| 3 | CartTest | test_addMultipleProducts_shouldUpdateCartCount | Thêm nhiều SP |
| 4 | CartTest | test_removeProductFromCart_shouldRecalculateTotal | Xóa SP tính lại tổng |
| 5 | CheckoutFlowTest | test_fullPurchaseFlow_validVoucher_shouldCompleteOrder | Happy path E2E |
| 6 | CheckoutFlowTest | test_applyValidVoucher_shouldShowDiscountedTotal | Voucher hợp lệ |
| 7 | CheckoutFlowTest | test_applyInvalidVoucher_shouldShowErrorMessage | Voucher không hợp lệ |
| 8 | CheckoutFlowTest | test_checkoutWithEmptyCart_shouldShowWarning | Giỏ trống → cảnh báo |

## Kịch bản Demo (10 phút)

### Phút 1-2: Giới thiệu
- Mở project, giải thích cấu trúc thư mục
- Giới thiệu tech stack

### Phút 3-4: Chạy ứng dụng
```bash
mvn spring-boot:run
```
- Mở browser → http://localhost:8080
- Demo: tìm kiếm sản phẩm, thêm vào giỏ, checkout

### Phút 5-6: Unit Tests
```bash
mvn test
```
- Xem output: 25 tests passed
- Mở file test, giải thích cấu trúc Arrange-Act-Assert
- Highlight: @Mock, @InjectMocks, verify(), assertThrows()

### Phút 7-8: Selenium E2E Tests
```bash
mvn verify
```
- Xem output: 8 E2E tests passed
- Mở Page Object files, giải thích pattern
- Highlight: WebDriverWait, headless mode, screenshot on failure

### Phút 9-10: Kết luận
- Mở H2 Console → xem data đã seed
- Tổng kết: 33 test cases (25 Unit + 8 E2E)
- Testing pyramid: 75% Unit, 25% E2E
- Giải thích Surefire vs Failsafe plugin

## Mã voucher test

| Code | Discount | Trạng thái |
|------|----------|-----------|
| SAVE10 | 10% | Hợp lệ |
| SAVE20 | 20% | Hợp lệ |
| SAVE50 | 50% | Hợp lệ |
| EXPIRED2024 | - | Hết hạn |

## Tác giả

Bài tập lớn môn Kiểm thử Phần mềm
