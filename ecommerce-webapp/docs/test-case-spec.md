# Test Case Specification — E-Commerce Web Application

## Quy ước
- **Technique**: BVA = Boundary Value Analysis, EP = Equivalence Partitioning, DT = Decision Table, ST = State Transition, PBT = Property-Based Testing, PW = Pairwise
- **Priority**: Critical / High / Medium / Low
- **Status**: Passed ✅ / Failed ❌ / Not Run ⏳

---

## 1. Unit Tests — ProductService

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

## 2. Unit Tests — OrderService

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| UT-009 | createOrder_sufficientStock_shouldCreate | Stock đủ | Tạo order với 2 items | Order tạo thành công, stock giảm | EP (valid) | Critical | ✅ |
| UT-010 | createOrder_insufficientStock_shouldThrow | Stock=5, yêu cầu 10 | Tạo order | Ném InsufficientStockException | BVA | Critical | ✅ |
| UT-011 | cancelOrder_pendingStatus_shouldCancel | Order status=PENDING | Gọi cancelOrder | Status → CANCELLED, stock hoàn | ST (PENDING→CANCELLED) | High | ✅ |
| UT-012 | cancelOrder_confirmedStatus_shouldThrow | Order status=CONFIRMED | Gọi cancelOrder | Ném InvalidOrderStateException | ST (invalid transition) | High | ✅ |
| UT-013 | calculateTotal_below10Items_noDiscount | 5 items | Gọi calculateOrderTotal | Tổng = subtotal × 1.0 | EP (<10 partition) | High | ✅ |
| UT-014 | calculateTotal_10to19_5percent | 15 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.95 | BVA (10 boundary) | High | ✅ |
| UT-015 | calculateTotal_20orMore_10percent | 25 items | Gọi calculateOrderTotal | Tổng = subtotal × 0.90 | BVA (20 boundary) | High | ✅ |
| UT-016 | applyVoucher_validCode_shouldDiscount | Order tồn tại, voucher SAVE10 | Gọi applyVoucher("SAVE10") | TotalAmount giảm 10% | DT (valid code) | Critical | ✅ |
| UT-017 | applyVoucher_invalidCode_shouldThrow | Order tồn tại | Gọi applyVoucher("INVALID") | Ném InvalidInputException | DT (invalid code) | High | ✅ |
| UT-018 | applyVoucher_expiredCode_shouldThrow | Order tồn tại | Gọi applyVoucher("EXPIRED2024") | Ném InvalidInputException "hết hạn" | DT (expired code) | High | ✅ |

## 3. Unit Tests — CartService

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| UT-019 | addToCart_inStock_shouldAdd | Product stock=10 | Gọi addToCart(userId, productId, 2) | Cart có 1 item, qty=2 | EP (valid) | Critical | ✅ |
| UT-020 | addToCart_outOfStock_shouldThrow | Product stock=0 | Gọi addToCart(userId, productId, 1) | Ném InsufficientStockException | BVA (stock=0) | Critical | ✅ |
| UT-021 | addToCart_exceedsStock_shouldThrow | Product stock=5 | Gọi addToCart(userId, productId, 10) | Ném InsufficientStockException | BVA (qty > stock) | High | ✅ |
| UT-022 | removeFromCart_existing_shouldRemove | Cart có item | Gọi removeFromCart(userId, productId) | Cart trống | EP | High | ✅ |
| UT-023 | removeFromCart_nonExistent_shouldThrow | Cart có item khác | Gọi removeFromCart(userId, 999) | Ném InvalidInputException | EP (invalid) | High | ✅ |
| UT-024 | clearCart_shouldRemoveAll | Cart có 2 items | Gọi clearCart(userId) | Cart trống | EP | Medium | ✅ |
| UT-025 | getCartTotal_multiple_shouldCalc | Cart: item1×2 + item2×3 | Gọi getCartTotal(userId) | Tổng đúng | EP | High | ✅ |

## 4. Integration Tests — Controller Layer (⏳ Chờ implement)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| IT-001 | home_authenticated_shouldShowProducts | User logged in | GET / with MockUser | 200 OK, products in model | EP | Critical | ⏳ |
| IT-002 | home_unauthenticated_shouldRedirectLogin | No auth | GET / | 302 Redirect → /login | EP | Critical | ⏳ |
| IT-003 | search_withKeyword_shouldFilter | User logged in | GET /?keyword=Laptop | Filtered products | EP | High | ⏳ |
| IT-004 | addToCart_withCsrf_shouldRedirect | User logged in | POST /add-to-cart/1 + csrf | 302 Redirect | EP | High | ⏳ |
| IT-005 | addToCart_noCsrf_shouldForbid | User logged in | POST /add-to-cart/1 (no csrf) | 403 Forbidden | EP (security) | High | ⏳ |
| IT-006 | viewCart_authenticated_shouldShowCart | User logged in | GET /cart | 200 OK, cart in model | EP | High | ⏳ |
| IT-007 | checkout_emptyCart_shouldShowWarning | Cart trống | GET /checkout | Warning message | EP | High | ⏳ |
| IT-008 | applyVoucher_valid_shouldRedirect | Cart có item | POST /checkout/apply-voucher (SAVE10) | Redirect + flash success | DT | High | ⏳ |
| IT-009 | applyVoucher_invalid_shouldShowError | Cart có item | POST /checkout/apply-voucher (INVALID) | Redirect + flash error | DT | High | ⏳ |
| IT-010 | placeOrder_shouldClearCart | Cart có item | POST /checkout/place-order + csrf | Redirect → confirmation | EP | Critical | ⏳ |

## 5. State Transition Tests (⏳ Chờ implement)

| TC-ID | Title | Precondition | Steps | Expected | Technique | Priority | Status |
|-------|-------|-------------|-------|----------|-----------|----------|--------|
| ST-001 | order_pending_to_confirmed | Order PENDING | confirmOrder(id) | Status = CONFIRMED | ST (valid) | High | ⏳ |
| ST-002 | order_confirmed_to_shipped | Order CONFIRMED | shipOrder(id) | Status = SHIPPED | ST (valid) | High | ⏳ |
| ST-003 | order_shipped_to_delivered | Order SHIPPED | deliverOrder(id) | Status = DELIVERED | ST (valid) | High | ⏳ |
| ST-004 | order_pending_to_cancelled | Order PENDING | cancelOrder(id) | Status = CANCELLED, stock restored | ST (valid) | High | ⏳ |
| ST-005 | order_confirmed_cancel_shouldThrow | Order CONFIRMED | cancelOrder(id) | InvalidOrderStateException | ST (invalid) | High | ⏳ |
| ST-006 | order_pending_ship_shouldThrow | Order PENDING | shipOrder(id) | InvalidOrderStateException | ST (invalid) | High | ⏳ |
| ST-007 | order_delivered_cancel_shouldThrow | Order DELIVERED | cancelOrder(id) | InvalidOrderStateException | ST (invalid) | Medium | ⏳ |

## 6. E2E Tests — Selenium (8 existing + 4-5 new)

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
| E2E-009 | login_logout_loginDifferentUser | 2 user accounts | Login user1→Logout→Login user2 | Session isolation | ST | High | ⏳ |
| E2E-010 | voucherExpired_showError | Cart có items | Nhập EXPIRED2024 | Hiển thị "hết hạn" | DT | High | ⏳ |
| E2E-011 | stockDepletion_outOfStock | SP có stock=1 | User mua hết → SP hiện "Hết hàng" | Button disabled | BVA | Medium | ⏳ |
