# ĐỀ CƯƠNG ĐỒ ÁN CƠ SỞ (CẬP NHẬT)

**Họ và tên sinh viên:** Từ Văn Huy Hoàng  
**Lớp:** k24CSE  

---

## 1. TÊN ĐỀ TÀI
**Xây dựng chương trình chơi cờ Caro (Gomoku) có giao diện đồ họa, hỗ trợ hai chế độ chơi: Người vs Người và Người vs Máy (AI)**

---

## 2. MỤC TIÊU

### 2.1. Mục tiêu tổng quát
Xây dựng một ứng dụng trò chơi cờ Caro có giao diện đồ họa (GUI) bằng Java, cho phép người chơi thi đấu trực tiếp với nhau trên cùng một máy tính hoặc thi đấu với máy tính thông qua chế độ chơi với AI, đồng thời áp dụng các kiến thức lập trình hướng đối tượng và thuật toán cây trò chơi vào một sản phẩm hoàn chỉnh.

### 2.2. Mục tiêu cụ thể
- Xây dựng bàn cờ Caro kích thước tùy chỉnh (mặc định $15 \times 15$).
- Cài đặt luật chơi và kiểm tra điều kiện thắng/thua/hòa.
- Xây dựng giao diện đồ họa bằng Java Swing/JavaFX.
- Quản lý lượt chơi giữa 2 người chơi (Người vs Người).
- Xây dựng đối thủ máy tính (AI) với nhiều mức độ khó, sử dụng thuật toán Minimax kết hợp cắt tỉa Alpha-Beta và hàm đánh giá heuristic.
- Cho phép người chơi lựa chọn chế độ chơi: Người vs Người hoặc Người vs Máy.
- Lưu trữ lịch sử ván đấu, cho phép chơi lại (undo) và bắt đầu ván mới.
- Áp dụng lập trình hướng đối tượng (OOP) vào thiết kế hệ thống.

---

## 3. CHỨC NĂNG HỆ THỐNG

### (1) Khởi tạo bàn cờ
- Cho phép chọn kích thước bàn cờ (tối thiểu $10 \times 10$, tối đa $20 \times 20$, mặc định $15 \times 15$).
- Khởi tạo bàn cờ trống, sẵn sàng cho ván đấu mới.
- Hiển thị lưới bàn cờ rõ ràng, hỗ trợ quan sát trong quá trình kiểm thử.

### (2) Quản lý người chơi và chế độ chơi
- **Chế độ Người vs Người:** 2 người chơi luân phiên trên cùng một máy.
- **Chế độ Người vs Máy:** người chơi đấu với AI do máy tính điều khiển.
- Đặt tên người chơi, chọn quân cờ (X/O).
- Chọn mức độ khó của AI khi chơi chế độ Người vs Máy (Dễ / Trung bình / Khó).

### (3) Xử lý nước đi
- Nhận nước đi từ thao tác click chuột trên bàn cờ (đối với người chơi).
- Tự động tính toán và thực hiện nước đi của AI khi đến lượt máy.
- Kiểm tra tính hợp lệ của nước đi (ô trống, trong bàn cờ).
- Chuyển lượt chơi tự động sau mỗi nước đi.
- Tô sáng nước đi vừa thực hiện để người chơi dễ theo dõi.

### (4) Kiểm tra kết quả ván đấu
- Kiểm tra đủ 5 quân liên tiếp (ngang/dọc/chéo) để xác định thắng.
- Kiểm tra hòa khi bàn cờ đầy mà không có người thắng.
- Hiển thị thông báo kết thúc ván đấu, phân biệt rõ người thắng là người chơi hay AI.

#### 💡 Chi tiết thuật toán kiểm tra thắng (Giả mã / Pseudo-code)
- Với ô $(r, c)$ vừa đánh, chỉ kiểm tra **4 hướng** đi qua ô đó:
  - Ngang: $(0, 1)$
  - Dọc: $(1, 0)$
  - Chéo chính: $(1, 1)$
  - Chéo phụ: $(1, -1)$
- Với mỗi hướng $(dx, dy)$, đếm số quân cùng loại liên tiếp:
  - Về phía trước: $(r + dx, c + dy)$
  - Về phía sau: $(r - dx, c - dy)$
- **Tổng số quân liên tiếp theo hướng:**  
  $$\text{Tổng} = 1 \text{ (ô vừa đánh)} + \text{số quân phía trước} + \text{số quân phía sau}$$
- Nếu $\text{Tổng} \ge 5$ ở bất kỳ hướng nào $\rightarrow$ **Người vừa đánh thắng**.
- Nếu bàn cờ đầy mà không có hướng nào đủ 5 $\rightarrow$ **Hòa**.
- **Độ phức tạp:** $O(1)$ chỉ xét cục bộ quanh nước vừa đánh, tối ưu hơn rất nhiều so với việc quét lại toàn bộ bàn cờ $O(n^2)$ sau mỗi lượt.

### (5) Trí tuệ nhân tạo (AI) - Chế độ Người vs Máy
- Xây dựng module AI độc lập, tách biệt với logic bàn cờ và giao diện để dễ nâng cấp thuật toán.
- **Mức Dễ:** AI chọn nước đi dựa trên đánh giá heuristic đơn giản, có yếu tố ngẫu nhiên để giảm độ khó.
- **Mức Trung bình:** AI sử dụng thuật toán Minimax với độ sâu tìm kiếm giới hạn, kết hợp hàm đánh giá dựa trên số quân liên tiếp.
- **Mức Khó:** AI sử dụng Minimax kết hợp cắt tỉa Alpha-Beta để tìm kiếm sâu hơn, ưu tiên chặn nước thắng của đối thủ và tạo thế đôi (*double threat*).
- Giới hạn không gian tìm kiếm bằng cách chỉ xét các ô trống gần quân cờ đã đánh để giảm độ phức tạp tính toán.
- Đảm bảo thời gian phản hồi của AI dưới 1-2 giây mỗi nước đi để không làm gián đoạn trải nghiệm chơi.

#### 💡 Chi tiết thuật toán Minimax (Giả mã)
- Hàm `minimax(bàn_cờ, độ_sâu, lượt_chơi)` nhận trạng thái bàn cờ hiện tại, độ sâu còn lại và bên đang cần đánh.
- Nếu `độ_sâu = 0` hoặc ván đấu đã kết thúc (thắng/thua/hòa) $\rightarrow$ Trả về giá trị heuristic của bàn cờ hiện tại.
- **Lượt Max (AI):**
  - Khởi tạo `best = -∞`.
  - Với mỗi nước đi hợp lệ: Thử đánh $\rightarrow$ Gọi đệ quy `minimax(bàn_cờ, độ_sâu - 1, Min)` $\rightarrow$ Cập nhật `best = max(best, kết_quả)` $\rightarrow$ Hoàn tác nước đi (Undo).
  - Trả về `best`.
- **Lượt Min (Đối thủ):**
  - Thực hiện tương tự nhưng khởi tạo `best = +∞` và lấy `best = min(best, kết_quả)`.
- Tại nút gốc của cây, AI chọn nước đi ứng với giá trị Minimax cao nhất trong các nước đi con.

#### 💡 Chi tiết cắt tỉa Alpha-Beta (Giả mã)
- Bổ sung hai tham số $\alpha$ (giá trị tốt nhất Max đã đảm bảo được) và $\beta$ (giá trị tốt nhất Min đã đảm bảo được) vào hàm Minimax.
- **Lượt Max:**
  - `best = max(best, alphaBeta(..., độ_sâu - 1, alpha, beta, Min))`
  - Cập nhật `alpha = max(alpha, best)`
  - Nếu $\beta \le \alpha \rightarrow$ **Dừng xét các nước đi còn lại (Cắt tỉa Beta)**.
- **Lượt Min:**
  - `best = min(best, alphaBeta(..., độ_sâu - 1, alpha, beta, Max))`
  - Cập nhật `beta = min(beta, best)`
  - Nếu $\beta \le \alpha \rightarrow$ **Dừng xét các nước đi còn lại (Cắt tỉa Alpha)**.
- **Sắp xếp nước đi (Move Ordering):** Ưu tiên các ô gần quân đã đánh hoặc ô tạo thế mạnh trước khi duyệt giúp tăng khả năng cắt tỉa sớm, giảm thời gian tính toán so với Minimax thuần mà kết quả không đổi.

#### 💡 Chi tiết hàm đánh giá Heuristic (Bảng điểm)
Quét 4 hướng quanh mỗi dãy quân liên tiếp, tính riêng cho quân AI và quân đối thủ.  
$$\text{Điểm Heuristic Tổng} = \sum \text{Điểm AI} - \sum \text{Điểm Đối thủ}$$

| Trạng thái dãy quân | Mô tả | Điểm Heuristic |
| :--- | :--- | :--- |
| **5 quân liên tiếp** | Thắng ngay lập tức | $1.000.000$ |
| **4 quân liên tiếp (Tứ mở)** | Còn trống cả 2 đầu | $10.000$ |
| **4 quân liên tiếp (Tứ đóng)** | Trống 1 đầu / Bị chặn 1 đầu | $1.000$ |
| **3 quân liên tiếp (Tam mở)** | Còn trống cả 2 đầu | $100$ |
| **3 quân liên tiếp (Tam đóng)** | Trống 1 đầu / Bị chặn 1 đầu | $10$ |
| **2 quân liên tiếp** | Còn trống cả 2 đầu | $5$ |
| **Dãy quân bị chặn** | Bị chặn cả 2 đầu | $0$ |
| **Thế đôi (Double Threat)** | Hai dãy tứ mở hoặc tam mở cùng lúc | Cộng điểm thưởng lớn (đối thủ không thể chặn cả hai) |

#### 💡 Chi tiết giới hạn không gian tìm kiếm
Thay vì xét toàn bộ ô trống trên bàn cờ (tối đa 225 ô với bàn $15 \times 15$), AI chỉ xét các ô trống nằm trong **bán kính 2 ô** tính từ các quân đã đánh. Điều này giúp giảm đáng kể hệ số phân nhánh (*branching factor*) ở mỗi nút của cây Minimax / Alpha-Beta.

### (6) Tiện ích khác
- **Chức năng undo:** Đi lại nước cờ trước đó; khi chơi với AI, undo sẽ hoàn tác cả nước đi của người chơi và nước phản hồi của AI.
- Chức năng chơi lại ván mới.
- Lưu và xem lại lịch sử ván đấu, có ghi chú chế độ chơi và mức độ khó của AI (nếu có).

---

## 4. SẢN PHẨM ĐẦU RA

### 4.1. Phần mềm
Ứng dụng Java có giao diện đồ họa (Java Swing hoặc JavaFX) bao gồm các chức năng:
- Khởi tạo và hiển thị bàn cờ.
- Xử lý nước đi và lượt chơi (Người vs Người và Người vs Máy).
- Module AI với 3 mức độ khó.
- Kiểm tra thắng/thua/hòa.
- Undo và chơi lại ván mới.
- Lưu và xem lại lịch sử ván đấu.

### 4.2. Báo cáo
**Báo cáo từ 40-60 trang bao gồm:**
- **Cơ sở lý thuyết:** Thuật toán kiểm tra thắng, thuật toán Minimax và cắt tỉa Alpha-Beta, thiết kế hàm đánh giá heuristic cho cờ Caro, thiết kế OOP.
- **Thiết kế hệ thống:** Sơ đồ lớp, sơ đồ use-case, sơ đồ hoạt động, sơ đồ tuần tự xử lý nước đi của AI.
- **Kết quả thực nghiệm:** Giao diện chương trình, so sánh thời gian phản hồi và tỷ lệ thắng giữa các mức độ AI.

### 4.3. Demo
Video demo (5-10 phút) minh họa đầy đủ các chức năng của chương trình, bao gồm cả ván đấu Người vs Người và Người vs Máy ở nhiều mức độ khó khác nhau.

---

## 5. CÔNG NGHỆ SỬ DỤNG

| Hạng mục | Công nghệ / Công cụ |
| :--- | :--- |
| **Ngôn ngữ lập trình** | Java (JDK 11 trở lên) |
| **Giao diện đồ họa** | Java Swing hoặc JavaFX |
| **Thuật toán AI** | Minimax, cắt tỉa Alpha-Beta, hàm đánh giá heuristic |
| **Lưu trữ dữ liệu** | File văn bản / JSON cục bộ |
| **Công cụ hỗ trợ** | IntelliJ IDEA / Eclipse, Git (quản lý phiên bản mã nguồn) |

---

## 6. KẾ HOẠCH THỰC HIỆN

| Thời gian | Nội dung công việc |
| :--- | :--- |
| **Tuần 1-2** | Tìm hiểu đề tài, phân tích yêu cầu, thiết kế sơ đồ hệ thống (use-case, lớp, hoạt động). |
| **Tuần 3-4** | Xây dựng bàn cờ, xử lý nước đi, kiểm tra thắng/thua/hòa cho chế độ Người vs Người. |
| **Tuần 5-7** | Nghiên cứu và cài đặt module AI (heuristic, Minimax, cắt tỉa Alpha-Beta), thử nghiệm và cân chỉnh 3 mức độ khó. |
| **Tuần 8-9** | Hoàn thiện giao diện đồ họa, tích hợp chế độ Người vs Máy, chức năng undo, chơi lại. |
| **Tuần 10** | Xây dựng chức năng lưu và xem lại lịch sử ván đấu. |
| **Tuần 11** | Kiểm thử toàn diện (kiểm thử AI và logic thắng/thua), sửa lỗi, tối ưu hiệu năng AI. |
| **Tuần 12** | Hoàn thiện báo cáo và quay video demo. |

---

## 7. RỦI RO VÀ GIẢI PHÁP

1. **Rủi ro:** Thuật toán Minimax chạy chậm trên bàn cờ lớn ($15 \times 15$) do không gian trạng thái lớn.  
   👉 **Giải pháp:** Giới hạn độ sâu tìm kiếm, chỉ xét các ô lân cận quân đã đánh, áp dụng cắt tỉa Alpha-Beta.

2. **Rủi ro:** AI ở mức Khó quá mạnh khiến người chơi khó thắng.  
   👉 **Giải pháp:** Giới hạn độ sâu tìm kiếm theo từng mức độ, bổ sung yếu tố ngẫu nhiên ở mức Dễ và Trung bình.

3. **Rủi ro:** Trễ tiến độ do thời gian nghiên cứu thuật toán AI kéo dài.  
   👉 **Giải pháp:** Ưu tiên hoàn thành chế độ Người vs Người trước, phát triển AI song song và tích hợp dần theo từng mức độ.
