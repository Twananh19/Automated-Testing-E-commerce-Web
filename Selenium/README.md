# Báo cáo Bài tập: Thực hành kiểm thử tự động với Selenium

**Sinh viên thực hiện:** Vũ Tuấn Anh
**Mã sinh viên:** 22010203
**Repository:** [https://github.com/Twananh19/Selenium](https://github.com/Twananh19/Selenium)

## 1. Mục tiêu bài tập
- Tìm hiểu về công cụ kiểm thử tự động trên giao diện web (Automation Testing) - **Selenium WebDriver**.
- Biết cách sử dụng các hàm cơ bản để định vị phần tử (Locators) như ID, Class Name, XPath,...
- Thực hành viết mã kịch bản kiểm thử (Test Script) để tự động hóa các thao tác của người dùng trên trình duyệt.

## 2. Trang web kiểm thử
Trong bài thực hành này, em sử dụng trang web **Swag Labs** (`https://www.saucedemo.com/`) - Đây là một hệ thống website thương mại điện tử chuyên dùng để thực hành automation test do cộng đồng Selenium thế giới xây dựng.

## 3. Các kịch bản kiểm thử (Test Cases)
Ngôn ngữ sử dụng: **Python 3**
Thư viện kiểm thử: **Selenium WebDriver** & **unittest**

Em đã xây dựng 03 kịch bản kiểm thử (Test cases) tự động sau:

### 3.1. Test Case 1: Đăng nhập thành công (Login)
- **Mô tả thao tác:** 
  1. Truy cập trang web.
  2. Định vị ô Username (ID: `user-name`), nhập `standard_user`.
  3. Định vị ô Password (ID: `password`), nhập `secret_sauce`.
  4. Định vị và click nút Login (ID: `login-button`).
- **Xác nhận (Assertion):** Kiểm tra xem trang web đã chuyển hướng thành công và hiển thị tiêu đề "Products".
- **Ảnh minh họa quá trình chạy:**

  ![Test Login](thay_bang_link_anh_cua_ban_1.png)

### 3.2. Test Case 2: Thêm sản phẩm vào giỏ hàng (Add to Cart)
- **Mô tả thao tác:**
  1. Thực hiện Đăng nhập.
  2. Tại trang danh sách sản phẩm, định vị nút "Add to cart" của sản phẩm đầu tiên (ID: `add-to-cart-sauce-labs-backpack`) và click.
- **Xác nhận (Assertion):** Kiểm tra biểu tượng giỏ hàng ở góc phải trên cùng (`shopping_cart_badge`) có hiển thị đúng số `1`.
- **Ảnh minh họa quá trình chạy:**

  ![Test Cart](thay_bang_link_anh_cua_ban_2.png)

### 3.3. Test Case 3: Đăng xuất (Logout)
- **Mô tả thao tác:**
  1. Thực hiện Đăng nhập.
  2. Click vào nút Hamburger Menu ở góc trái trên (ID: `react-burger-menu-btn`).
  3. Chờ menu mở ra, định vị và click vào nút "Logout" (ID: `logout_sidebar_link`).
- **Xác nhận (Assertion):** Kiểm tra hệ thống đã đăng xuất, quay về màn hình ban đầu và hiển thị lại nút "Login".
- **Ảnh minh họa quá trình chạy:**

  ![Test Logout](thay_bang_link_anh_cua_ban_3.png)

## 4. Hướng dẫn chạy thử nghiệm mã nguồn
1. Cài đặt Python 3 trên máy tính.
2. Cài đặt thư viện Selenium bằng dòng lệnh trong Terminal/CMD: `pip install selenium`
3. Chạy file mã nguồn:
   ```bash
   python selenium_test.py
   ```
4. Kết quả mong đợi: Trình duyệt Chrome sẽ tự động bật lên, tự động click chuột cực kỳ nhanh chạy qua 3 kịch bản, và console báo `OK (ran 3 tests)`.

## 5. Kết luận
Qua quá trình tìm hiểu và viết code thực hành, em đã có thể tự động hóa các thao tác duyệt web thay cho con người. Tương lai có thể áp dụng Selenium vào các luồng nghiệp vụ phức tạp của dự án như thanh toán, quản lý đơn hàng để tiết kiệm thời gian test hồi quy (Regression Testing).
