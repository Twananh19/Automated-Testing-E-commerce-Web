# Ma trận Truy xuất (Traceability Matrix)
## Requirement ↔ Test Case Mapping

Mỗi yêu cầu (REQ) được ánh xạ tới các test case (TC) cover nó.

| REQ-ID | Mô tả | Test Cases | Covered? |
|--------|-------|------------|----------|
| REQ-001 | Đăng nhập | IT-002, E2E-009 | ✅ |
| REQ-002 | Đăng xuất | E2E-009 | ✅ |
| REQ-003 | Phân quyền | IT-001, IT-002 | ✅ |
| REQ-004 | Bảo mật CSRF | IT-004, IT-005 | ✅ |
| REQ-005 | Hiển thị danh sách SP | IT-001, E2E-001 | ✅ |
| REQ-006 | Tìm kiếm SP theo tên | UT-006, IT-003, E2E-001, E2E-002 | ✅ |
| REQ-007 | Tìm kiếm theo danh mục | UT-006 | ✅ |
| REQ-008 | Thêm sản phẩm | UT-001, UT-002, UT-003 | ✅ |
| REQ-009 | Cập nhật tồn kho | UT-004, UT-005 | ✅ |
| REQ-010 | Tính giá giảm | UT-007, UT-008 | ✅ |
| REQ-011 | Thêm vào giỏ | UT-019, IT-004, E2E-003 | ✅ |
| REQ-012 | Xóa khỏi giỏ | UT-022, UT-023, E2E-004 | ✅ |
| REQ-013 | Xóa toàn bộ giỏ | UT-024 | ✅ |
| REQ-014 | Tính tổng giỏ | UT-025 | ✅ |
| REQ-015 | Kiểm tra tồn kho | UT-020, UT-021 | ✅ |
| REQ-016 | Tạo đơn hàng | UT-009, UT-010, IT-010, E2E-005 | ✅ |
| REQ-017 | Giảm giá theo SL | UT-013, UT-014, UT-015 | ✅ |
| REQ-018 | Áp dụng voucher | UT-016, IT-008, E2E-005, E2E-006 | ✅ |
| REQ-019 | Voucher không hợp lệ | UT-017, IT-009, E2E-007 | ✅ |
| REQ-020 | Voucher hết hạn | UT-018, E2E-010 | ✅ |
| REQ-021 | Giỏ trống không checkout | IT-007, E2E-008 | ✅ |
| REQ-022 | Trạng thái ban đầu PENDING | UT-009, ST-001 | ✅ |
| REQ-023 | Xác nhận đơn (PENDING→CONFIRMED) | ST-001 | ✅ |
| REQ-024 | Giao hàng (CONFIRMED→SHIPPED) | ST-002 | ✅ |
| REQ-025 | Hoàn tất (SHIPPED→DELIVERED) | ST-003 | ✅ |
| REQ-026 | Hủy đơn (PENDING→CANCELLED) | UT-011, ST-004 | ✅ |
| REQ-027 | Transition không hợp lệ | UT-012, ST-005, ST-006, ST-007 | ✅ |
| REQ-028 | Hiển thị giá format VND | E2E-001, E2E-003 | ✅ |
| REQ-029 | Nút hết hàng disabled | E2E-011 | ✅ |
| REQ-030 | Kết quả tìm kiếm trống | E2E-002 | ✅ |

---

## Tóm tắt Coverage

| Hạng mục | Số lượng | % Covered |
|----------|----------|-----------|
| Tổng số REQ | 30 | 100% |
| REQ có ≥ 1 TC | 30 | 100% |
| REQ có ≥ 2 TC | 25 | 83% |
| REQ chỉ có 1 TC | 5 | 17% |

> **Kết luận**: Tất cả 30 yêu cầu đều được cover bởi ít nhất 1 test case. 83% yêu cầu được cover bởi 2+ test cases từ các tầng kiểm thử khác nhau.
