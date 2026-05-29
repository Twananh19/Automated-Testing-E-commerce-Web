# BÁO CÁO BÀI TẬP KIỂM THỬ PHẦN MỀM (LAB 4 & LAB 5)

## Lab 4: Phân tích lý thuyết

### 1. Control Flow Graph (CFG) của phương thức `classify`
Để xây dựng Control Flow Graph, ta gán nhãn cho từng khối lệnh trong phương thức `classify` của lớp `Triangle`:

1. Khởi đầu phương thức và điều kiện 1: `if (a <= 0 || b <= 0 || c <= 0)`
2. Trả về Invalid: `return "Invalid";`
3. Điều kiện 2: `if (a + b <= c || a + c <= b || b + c <= a)`
4. Trả về Not a triangle: `return "Not a triangle";`
5. Điều kiện 3: `if (a == b && b == c)`
6. Trả về Equilateral: `return "Equilateral";`
7. Điều kiện 4: `else if (a == b || b == c || a == c)`
8. Trả về Isosceles: `return "Isosceles";`
9. Trả về Scalene: `return "Scalene";`
10. End (Điểm kết thúc).

**Mô tả các luồng cơ bản (Edges):**
- $1 \rightarrow 2$ (Nếu điều kiện 1 đúng)
- $2 \rightarrow 10$ (Kết thúc)
- $1 \rightarrow 3$ (Nếu điều kiện 1 sai)
- $3 \rightarrow 4$ (Nếu điều kiện 2 đúng)
- $4 \rightarrow 10$ (Kết thúc)
- $3 \rightarrow 5$ (Nếu điều kiện 2 sai)
- $5 \rightarrow 6$ (Nếu điều kiện 3 đúng)
- $6 \rightarrow 10$ (Kết thúc)
- $5 \rightarrow 7$ (Nếu điều kiện 3 sai)
- $7 \rightarrow 8$ (Nếu điều kiện 4 đúng)
- $8 \rightarrow 10$ (Kết thúc)
- $7 \rightarrow 9$ (Nếu điều kiện 4 sai)
- $9 \rightarrow 10$ (Kết thúc)

### 2. Cyclomatic Complexity (CC)
Cyclomatic Complexity đo lường số lượng đường đi độc lập đi qua một đoạn mã. Nó cũng tương đương với số lượng quyết định cộng thêm 1.
Trong Java, đối với các toán tử logic đoản mạch (`||` và `&&`), mỗi toán hạng có thể xem là một nhánh con riêng biệt ở tầng bytecode. Tuy nhiên, tính theo công thức số điều kiện đơn cao cấp (Statement-level Conditions):
- Điều kiện 1: `a <= 0`, `b <= 0`, `c <= 0`
- Điều kiện 2: `a + b <= c`, `a + c <= b`, `b + c <= a`
- Điều kiện 3: `a == b`, `b == c`
- Điều kiện 4: `a == b`, `b == c`, `a == c`

Xét đơn giản trên các nhánh if-else lớn: $CC = 4 \text{ (quyết định if)} + 1 = 5$.
Nếu tính chi tiết từng toán hạng logic nhỏ do short-circuit: Có thêm 6 phép toán `||` và 1 phép toán `&&` phân nhánh. Tính tổng thể ở cấp độ bytecode: $CC = 1 \text{ (mặc định)} + 9 \text{ (số điều kiện rẽ nhánh)} = 10$.

### 3. Independent Paths (Các đường đi độc lập cơ bản)
Nếu chỉ xét các nhánh rẽ điều kiện ở cấp độ dòng lệnh lớn (bỏ qua mô phỏng short-circuit bên trong):
- Đường 1: $1 \rightarrow 2 \rightarrow 10$ (Output: Invalid)
- Đường 2: $1 \rightarrow 3 \rightarrow 4 \rightarrow 10$ (Output: Not a triangle)
- Đường 3: $1 \rightarrow 3 \rightarrow 5 \rightarrow 6 \rightarrow 10$ (Output: Equilateral)
- Đường 4: $1 \rightarrow 3 \rightarrow 5 \rightarrow 7 \rightarrow 8 \rightarrow 10$ (Output: Isosceles)
- Đường 5: $1 \rightarrow 3 \rightarrow 5 \rightarrow 7 \rightarrow 9 \rightarrow 10$ (Output: Scalene)

---

## Lab 5 - Phân tích Test Coverage

### 1. Kết quả độ bao phủ (Coverage) 
Sau khi chạy các bài Unit Test với JaCoCo plugin thông qua lệnh `mvn clean test jacoco:report`, dự án ghi nhận:
- **Statement Coverage:** Đạt 100%
- **Branch Coverage:** Đạt 100%

### 2. Phân tích bài toán Short-circuit Evaluation (Đoản mạch)
Nếu chúng ta **chỉ sử dụng 5 test case** đi theo 5 đường đi độc lập phía trên, chúng ta sẽ dễ dàng đạt **100% Statement Coverage** vì mọi dòng mã (các câu lệnh `return` ở mỗi nhánh) đều được thực thi ít nhất một lần.

Tuy nhiên, như thế **chưa đạt 100% Branch Coverage** vì cơ chế **Short-circuit (Đoản mạch)** trong Java:
- Toán tử `||` (OR): Nếu toán hạng bên trái ĐÚNG, Java sẽ không đánh giá toán hạng bên phải. (Ví dụ `a <= 0 || b <= 0`, nếu `a <= 0` đúng, máy ảo Java bỏ qua việc kiểm tra `b <= 0`).
- Toán tử `&&` (AND): Nếu toán hạng bên trái SAI, Java sẽ không đánh giá toán hạng bên phải.

Do đó, ở mức mã máy (bytecode) mà công cụ coverge như JaCoCo đo lường, mỗi toán tử boolean đa thành phần đều tương đương với các khối lệnh rẽ nhánh con (Branch). Việc chỉ đáp ứng các điều kiện đầu tiên bằng một test case cơ bản đồng nghĩa với việc các điều kiện phía sau (và các nhánh sinh ra từ đó) sẽ bị "bỏ rơi" chưa được chạy qua.

### 3. Giải thích tác dụng của 7 Test Cases bổ sung
Để giải quyết vấn đề đoản mạch và đưa Branch Coverage lên 100%, các test case bổ sung được thiết kế để bắt buộc Java phải thực thi kiểm tra đến tận các biến logic cuối cùng của cụm IF:

- **Cho Điều kiện 1 `(a <= 0 || b <= 0 || c <= 0)`:**
  - Cần bẻ false vế đầu để chạy tiếp: `(5, 0, 5)` -> `a > 0` đúng logic (chặn đoản mạch dòng đầu), buộc Java phải kiểm tra tiếp `b <= 0`.
  - `(5, 5, -1)` -> buộc phải đánh giá đến phần tử `c <= 0` cuối cùng.

- **Cho Điều kiện 2 `(a + b <= c || a + c <= b || b + c <= a)`:**
  - Tương tự, gài cạnh vi phạm ở vị trí thứ 2 và 3: `(2, 5, 2)` buộc đánh giá `a + c <= b`; `(5, 2, 2)` buộc đánh giá `b + c <= a`.

- **Cho Điều kiện 3 và 4 (Cạnh bằng nhau):**
  - Chống đoản mạch cho `a == b && b == c`: `(4, 4, 5)` giúp `a == b` được chạy vào nhưng bị ngắt ở `b == c` (tạo ra trạng thái False tại nhánh AND kiểm tra).
  - Chống đoản mạch cho OR của Isosceles: `(5, 4, 4)` và `(4, 5, 4)` bảo đảm mã nguồn phải rẽ vào các nhánh kiểm tra cạnh thứ 2 và thứ 3 xem có bằng nhau hay không. Nhờ thế toàn bộ các nhánh ẩn sinh ra bởi Operator đoản mạch đã được bao phủ hoàn toàn.
