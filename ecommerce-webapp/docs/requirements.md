# Đặc tả Yêu cầu — E-Commerce Web Application

## 1. Quản lý Người dùng

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-001 | Đăng nhập | Người dùng nhập username/password để truy cập hệ thống |
| REQ-002 | Đăng xuất | Người dùng đăng xuất, xóa session, chuyển về trang login |
| REQ-003 | Phân quyền | Hệ thống phân biệt role USER và ADMIN |
| REQ-004 | Bảo mật CSRF | Mọi form POST phải có CSRF token hợp lệ |

## 2. Quản lý Sản phẩm

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-005 | Hiển thị danh sách SP | Trang chủ hiển thị tất cả sản phẩm từ DB |
| REQ-006 | Tìm kiếm sản phẩm | Tìm kiếm theo từ khóa trong tên SP (không phân biệt hoa/thường) |
| REQ-007 | Tìm kiếm theo danh mục | Lọc SP theo category (không phân biệt hoa/thường) |
| REQ-008 | Thêm sản phẩm | Admin thêm SP mới: tên không rỗng, giá > 0, stock ≥ 0 |
| REQ-009 | Cập nhật tồn kho | Tăng/giảm stock, không cho phép stock âm |
| REQ-010 | Tính giá giảm | Tính giá sau discount (0-100%), reject ngoài range |

## 3. Giỏ hàng

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-011 | Thêm vào giỏ | Thêm SP vào giỏ nếu đủ tồn kho, mỗi user có giỏ riêng |
| REQ-012 | Xóa khỏi giỏ | Xóa 1 SP khỏi giỏ, báo lỗi nếu SP không có trong giỏ |
| REQ-013 | Xóa toàn bộ giỏ | Xóa tất cả SP khỏi giỏ hàng |
| REQ-014 | Tính tổng giỏ | Tổng = Σ(giá × số lượng) cho tất cả items |
| REQ-015 | Kiểm tra tồn kho | Không cho phép thêm vượt quá stock hiện có |

## 4. Đặt hàng & Thanh toán

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-016 | Tạo đơn hàng | Tạo order từ cart items, giảm stock, tính tổng |
| REQ-017 | Giảm giá theo SL | < 10 items: 0%, 10-19: 5%, ≥ 20: 10% |
| REQ-018 | Áp dụng voucher | Nhập mã voucher hợp lệ → giảm % tương ứng |
| REQ-019 | Voucher không hợp lệ | Mã sai → báo lỗi rõ ràng |
| REQ-020 | Voucher hết hạn | Mã expired → báo lỗi "đã hết hạn" |
| REQ-021 | Giỏ trống không checkout | Hiển thị cảnh báo khi checkout với giỏ trống |

## 5. Quản lý Trạng thái Đơn hàng (State Transition)

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-022 | Trạng thái ban đầu | Đơn mới tạo có status = PENDING |
| REQ-023 | Xác nhận đơn | PENDING → CONFIRMED (chỉ từ PENDING) |
| REQ-024 | Giao hàng | CONFIRMED → SHIPPED (chỉ từ CONFIRMED) |
| REQ-025 | Hoàn tất | SHIPPED → DELIVERED (chỉ từ SHIPPED) |
| REQ-026 | Hủy đơn | PENDING → CANCELLED (chỉ từ PENDING), hoàn lại stock |
| REQ-027 | Transition không hợp lệ | Chuyển trạng thái sai → ném exception |

## 6. Xem sản phẩm & Giao diện

| ID | Yêu cầu | Mô tả |
|----|---------|-------|
| REQ-028 | Hiển thị giá format | Giá hiển thị dạng VND có dấu phân cách nghìn |
| REQ-029 | Nút hết hàng | SP stock = 0 → nút "Hết hàng" disabled |
| REQ-030 | Kết quả tìm kiếm trống | Không tìm thấy → hiển thị "Không tìm thấy sản phẩm nào" |
