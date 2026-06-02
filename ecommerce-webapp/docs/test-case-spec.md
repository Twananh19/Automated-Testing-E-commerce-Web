# Test Case Specification — E-Commerce Web Application

## Quy ước
- **Technique**: BVA = Boundary Value Analysis, EP = Equivalence Partitioning, DT = Decision Table, ST = State Transition, PBT = Property-Based Testing, PW = Pairwise
- **Priority**: Critical / High / Medium / Low
- **Status**: Passed ✅ / Failed ❌ / Not Run ⏳

---

## 1. Unit Tests — ProductService (15 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| UT-001 | addProduct_withValidData_shouldSave | Repository available | Gọi addProduct(name="Laptop", price=25M, stock=10, cat="Electronics") | SP được lưu thành công | EP (valid class) | High | ✅ |
| UT-002 | addProduct_withEmptyName_shouldThrow | Repository available | Gọi addProduct(name="", ...) | Ném InvalidInputException | EP (invalid class) | High | ✅ |
| UT-003 | addProduct_withNegativePrice_shouldThrow | Repository available | Gọi addProduct(price=-1, ...) | Ném InvalidInputException | BVA (price < 0) | High | ✅ |
| UT-004 | updateStock_validQuantity_shouldUpdate | Product tồn tại, stock=10 | Gọi updateStock(id, +5) | Stock = 15 | EP (valid) | High | ✅ |
| UT-005 | updateStock_negativeResult_shouldThrow | Product tồn tại, stock=5 | Gọi updateStock(id, -10) | Ném InsufficientStockException | BVA (stock < 0) | Critical | ✅ |
| UT-006 | searchByCategory_caseInsensitive | Products seeded | Gọi searchByCategory("electronics") | Trả về products Electronics | EP | Medium | ✅ |
| UT-007 | calculateDiscount_validPercent | Product price=100 | Gọi calculateDiscountedPrice(product, 20) | Trả về 80.0 | EP (valid 0-100) | High | ✅ |
| UT-008 | calculateDiscount_above100_shouldThrow | Product price=100 | Gọi calculateDiscountedPrice(product, 101) | Ném InvalidInputException | BVA (> 100) | High | ✅ |
| UT-008b | addProduct_withZeroPrice_shouldThrow | Repository available | Gọi addProduct(price=0, ...) | Ném InvalidInputException | BVA (price = 0, boundary) | High | ✅ |
| UT-008c | addProduct_withNegativeStock_shouldThrow | Repository available | Gọi addProduct(stock=-1, ...) | Ném InvalidInputException | BVA (stock < 0) | High | ✅ |
| UT-008d | calculateDiscount_zeroPercent_shouldReturnOriginal | Product price=1M | Gọi calculateDiscountedPrice(product, 0) | Trả về 1000000 (giá gốc) | BVA (0%, boundary dưới) | High | ✅ |
| UT-008e | calculateDiscount_100percent_shouldReturnZero | Product price=1M | Gọi calculateDiscountedPrice(product, 100) | Trả về 0 | BVA (100%, boundary trên) | High | ✅ |
| UT-008f | calculateDiscount_negativePercent_shouldThrow | Product price=1M | Gọi calculateDiscountedPrice(product, -1) | Ném InvalidInputException | BVA (< 0, boundary invalid) | High | ✅ |
| UT-008g | searchByName_emptyKeyword_shouldReturnAll | Products seeded | Gọi searchByName("") | Trả về tất cả SP | EP (empty input) | Medium | ✅ |
| UT-008h | addProduct_withNullName_shouldThrow | Repository available | Gọi addProduct(name=null, ...) | Ném InvalidInputException | EP (null input) | High | ✅ |

## 2. Unit Tests — OrderService (25 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| UT-009 | createOrder_sufficientStock_shouldCreate | Stock đủ | Tạo order với 2 items | Order tạo thành công, stock giảm | EP (valid) | Critical | ✅ |
| UT-010 | createOrder_insufficientStock_shouldThrow | Stock=5, yêu cầu 10 | Tạo order | Ném InsufficientStockException | BVA | Critical | ✅ |
| UT-010b | createOrder_emptyCart_shouldThrow | Giỏ hàng rỗng | Gọi createOrder(userId, emptyList) | Ném InvalidInputException | EP (empty) | Critical | ✅ |
| UT-010c | createOrder_nullCart_shouldThrow | Cart items = null | Gọi createOrder(userId, null) | Ném InvalidInputException | EP (null) | Critical | ✅ |
| UT-011 | cancelOrder_pendingStatus_shouldCancel | Order status=PENDING | Gọi cancelOrder | Status → CANCELLED, stock hoàn | ST (PENDING→CANCELLED) | High | ✅ |
| UT-012 | cancelOrder_confirmedStatus_shouldThrow | Order status=CONFIRMED | Gọi cancelOrder | Ném InvalidOrderStateException | ST (invalid transition) | High | ✅ |
| UT-013 | calculateTotal_below10Items_noDiscount | 5 items | Gọi calculateOrderTotal | Tổng = subtotal × 1.0 | EP (<10 partition) | High | ✅ |
| UT-014 | calculateTotal_10to19_5percent | 12 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.95 | EP (10-19 partition) | High | ✅ |
| UT-015 | calculateTotal_20orMore_10percent | 22 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.90 | EP (≥20 partition) | High | ✅ |
| UT-015b | calculateTotal_exactly9Items_noDiscount | 9 items | Gọi calculateOrderTotal | Tổng = subtotal × 1.0 (no discount) | BVA (9, boundary dưới) | High | ✅ |
| UT-015c | calculateTotal_exactly10Items_5percent | 10 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.95 | BVA (10, boundary chính xác) | High | ✅ |
| UT-015d | calculateTotal_exactly19Items_5percent | 19 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.95 | BVA (19, boundary trên bracket 5%) | High | ✅ |
| UT-015e | calculateTotal_exactly20Items_10percent | 20 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.90 | BVA (20, boundary chính xác) | High | ✅ |
| UT-015f | calculateTotal_nullItems_shouldReturnZero | null items | Gọi calculateOrderTotal(null) | Trả về 0.0 | EP (null) | Medium | ✅ |
| UT-016 | applyVoucher_validCode_SAVE10_shouldDiscount | Order tồn tại | Gọi applyVoucher("SAVE10") | TotalAmount giảm 10% | DT (valid code) | Critical | ✅ |
| UT-016b | applyVoucher_validCode_SAVE20_shouldDiscount | Order tồn tại | Gọi applyVoucher("SAVE20") | TotalAmount giảm 20% | DT (valid code) | High | ✅ |
| UT-016c | applyVoucher_validCode_SAVE50_shouldDiscount | Order tồn tại | Gọi applyVoucher("SAVE50") | TotalAmount giảm 50% | DT (valid code) | High | ✅ |
| UT-017 | applyVoucher_invalidCode_shouldThrow | Order tồn tại | Gọi applyVoucher("INVALID") | Ném InvalidInputException | DT (invalid code) | High | ✅ |
| UT-018 | applyVoucher_expiredCode_shouldThrow | Order tồn tại | Gọi applyVoucher("EXPIRED2024") | Ném InvalidInputException "hết hạn" | DT (expired code) | High | ✅ |
| ST-001 | confirmOrder_pendingStatus_shouldConfirm | Order PENDING | confirmOrder(id) | Status = CONFIRMED | ST (PENDING→CONFIRMED) | High | ✅ |
| ST-002 | confirmOrder_nonPending_shouldThrow | Order CONFIRMED | confirmOrder(id) | InvalidOrderStateException | ST (invalid) | High | ✅ |
| ST-003 | shipOrder_confirmedStatus_shouldShip | Order CONFIRMED | shipOrder(id) | Status = SHIPPED | ST (CONFIRMED→SHIPPED) | High | ✅ |
| ST-004 | shipOrder_nonConfirmed_shouldThrow | Order PENDING | shipOrder(id) | InvalidOrderStateException | ST (invalid) | High | ✅ |
| ST-005 | deliverOrder_shippedStatus_shouldDeliver | Order SHIPPED | deliverOrder(id) | Status = DELIVERED | ST (SHIPPED→DELIVERED) | High | ✅ |
| ST-006 | deliverOrder_nonShipped_shouldThrow | Order CONFIRMED | deliverOrder(id) | InvalidOrderStateException | ST (invalid) | High | ✅ |

## 3. Unit Tests — CartService (13 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| UT-019 | addToCart_inStock_shouldAdd | Product stock=10 | Gọi addToCart(userId, productId, 2) | Cart có 1 item, qty=2 | EP (valid) | Critical | ✅ |
| UT-020 | addToCart_outOfStock_shouldThrow | Product stock=0 | Gọi addToCart(userId, productId, 1) | Ném InsufficientStockException | BVA (stock=0) | Critical | ✅ |
| UT-021 | addToCart_exceedsStock_shouldThrow | Product stock=5 | Gọi addToCart(userId, productId, 10) | Ném InsufficientStockException | BVA (qty > stock) | High | ✅ |
| UT-022 | removeFromCart_existing_shouldRemove | Cart có item | Gọi removeFromCart(userId, productId) | Cart trống | EP | High | ✅ |
| UT-023 | removeFromCart_nonExistent_shouldThrow | Cart có item khác | Gọi removeFromCart(userId, 999) | Ném InvalidInputException | EP (invalid) | High | ✅ |
| UT-024 | clearCart_shouldRemoveAll | Cart có 2 items | Gọi clearCart(userId) | Cart trống | EP | Medium | ✅ |
| UT-025 | getCartTotal_multiple_shouldCalc | Cart: item1×2 + item2×3 | Gọi getCartTotal(userId) | Tổng đúng | EP | High | ✅ |
| UT-025b | addToCart_zeroQuantity_shouldThrow | Product tồn tại | Gọi addToCart(userId, productId, 0) | Ném InvalidInputException | BVA (qty=0, boundary) | High | ✅ |
| UT-025c | addToCart_negativeQuantity_shouldThrow | Product tồn tại | Gọi addToCart(userId, productId, -5) | Ném InvalidInputException | BVA (qty<0) | High | ✅ |
| UT-025d | addToCart_productNotFound_shouldThrow | Product không tồn tại | Gọi addToCart(userId, 999, 1) | Ném InvalidInputException | EP (not found) | High | ✅ |
| UT-025e | removeFromCart_nonExistentUser_shouldThrow | User chưa có cart | Gọi removeFromCart(newUser, 1) | Ném InvalidInputException | EP (invalid user) | High | ✅ |
| UT-025f | getCartTotal_emptyCart_shouldReturnZero | Cart trống | Gọi getCartTotal(newUser) | Trả về 0.0 | EP (empty) | Medium | ✅ |
| UT-025g | addToCart_sameProductTwice_shouldAccumulate | Product stock=10 | Gọi addToCart 2 lần | Cart: 1 item, qty cộng dồn | EP (accumulation) | High | ✅ |

## 4. Property-Based Tests — jqwik (6 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| PBT-001 | orderTotal_shouldAlwaysBeNonNegative | Random OrderItems | Gọi calculateOrderTotal với random items | Total luôn ≥ 0 | PBT (invariant) | High | ✅ |
| PBT-002 | orderTotal_shouldNotExceedSubtotal | Random OrderItems | So sánh total vs subtotal | Total ≤ subtotal | PBT (invariant) | High | ✅ |
| PBT-003 | orderTotal_shouldBeMonotonic | Random items + extra | Thêm item vào list | Subtotal tổng tăng | PBT (monotonic) | Medium | ✅ |
| PBT-004 | discountRate_shouldBeOneOfThreeValues | Random OrderItems | Tính ratio total/subtotal | Ratio ∈ {1.0, 0.95, 0.90} | PBT (exhaustive) | High | ✅ |
| PBT-005 | discountedPrice_shouldBeInValidRange | Random price & discount | Gọi calculateDiscountedPrice | 0 ≤ result ≤ price | PBT (range) | High | ✅ |
| PBT-006 | higherDiscount_shouldResultInLowerPrice | Random price & 2 discounts | So sánh 2 giá | Discount cao → giá thấp | PBT (monotonic) | Medium | ✅ |

## 5. Integration Tests — Controller Layer (15 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| IT-001 | home_authenticated_shouldShowProducts | User logged in | GET / with Principal | 200 OK, products in model | EP | Critical | ✅ |
| IT-003 | search_withKeyword_shouldFilter | User logged in | GET /?keyword=Laptop | Filtered products, keyword in model | EP | High | ✅ |
| IT-003b | search_emptyKeyword_shouldReturnAll | User logged in | GET /?keyword= | All products | EP | Medium | ✅ |
| IT-004 | addToCart_shouldRedirect | User logged in | POST /add-to-cart/1 | 302 Redirect → / | EP | High | ✅ |
| IT-006 | viewCart_withItems_shouldShowCart | Cart có items | GET /cart | 200 OK, cart in model, cartEmpty=false | EP | High | ✅ |
| IT-006b | viewCart_emptyCart_shouldShowEmpty | Cart trống | GET /cart | 200 OK, cartEmpty=true | EP | High | ✅ |
| IT-006c | removeFromCart_shouldRedirect | Cart có items | POST /cart/remove/1 | 302 Redirect → /cart | EP | High | ✅ |
| IT-006d | clearCart_shouldRedirect | Cart có items | POST /cart/clear | 302 Redirect → /cart | EP | Medium | ✅ |
| IT-007 | checkout_emptyCart_shouldShowWarning | Cart trống | GET /checkout | Warning message, orderTotal=0 | EP | High | ✅ |
| IT-007b | checkout_withItems_shouldShowTotal | Cart có items | GET /checkout | orderTotal hiển thị đúng | EP | High | ✅ |
| IT-008 | applyVoucher_valid_shouldRedirect | Cart có item | POST apply-voucher (SAVE10) | Redirect + flash success | DT | High | ✅ |
| IT-009 | applyVoucher_invalid_shouldShowError | Cart có item | POST apply-voucher (INVALID) | Redirect + flash error | DT | High | ✅ |
| IT-010 | placeOrder_shouldClearCart | Cart có item | POST place-order | Redirect → confirmation | EP | Critical | ✅ |
| IT-010b | placeOrder_emptyCart_shouldWarn | Cart trống | POST place-order | Redirect → checkout + warning | EP | High | ✅ |
| IT-010c | orderConfirmation_shouldShowDetails | Order tồn tại | GET /order-confirmation/1 | 200 OK, order in model | EP | High | ✅ |

## 6. E2E Tests — Selenium (8 cases)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| E2E-001 | searchProduct_byKeyword | Logged in | Tìm "Laptop" | Hiển thị SP có Laptop | EP | High | ✅ |
| E2E-002 | searchProduct_noResults | Logged in | Tìm "XYZ_NONEXISTENT" | Hiển thị "Không tìm thấy" | EP | High | ✅ |
| E2E-003 | addMultipleProducts_updateCount | Logged in | Thêm 2 SP | Cart count tăng | EP | High | ✅ |
| E2E-004 | removeProduct_recalculateTotal | Cart có items | Xóa 1 SP | Tổng tính lại | EP | High | ✅ |
| E2E-005 | fullPurchaseFlow_validVoucher | Logged in | Login→Add→Cart→Voucher→Order | Order xác nhận | EP (happy path) | Critical | ✅ |
| E2E-006 | applyValidVoucher_showDiscount | Cart có items | Nhập SAVE10 | Tổng giảm | DT | High | ✅ |
| E2E-007 | applyInvalidVoucher_showError | Cart có items | Nhập INVALID | Hiển thị lỗi | DT | High | ✅ |
| E2E-008 | checkoutEmptyCart_showWarning | Cart trống | Vào /checkout | Hiển thị cảnh báo | EP | High | ✅ |

---

## Tổng kết

| Tầng kiểm thử | Số lượng TC | Passed | Failed |
|----------------|-------------|--------|--------|
| Unit Tests (ProductService) | 15 | 15 | 0 |
| Unit Tests (OrderService) | 25 | 25 | 0 |
| Unit Tests (CartService) | 13 | 13 | 0 |
| Property-Based Tests (jqwik) | 6 | 6 | 0 |
| Integration Tests (MockMvc) | 15 | 15 | 0 |
| E2E Tests (Selenium) | 8 | 8 | 0 |
| **TỔNG** | **82** | **82** | **0** |
