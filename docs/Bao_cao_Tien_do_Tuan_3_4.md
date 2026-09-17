# BÁO CÁO NGHIỆM THU TIẾN ĐỘ TUẦN 3 - 4
## DỰ ÁN: GAME CỜ CARO (GOMOKU) GUI & TRÍ TUỆ NHÂN TẠO (AI)

- **Học phần:** Đồ án cơ sở  
- **Sinh viên thực hiện:** Từ Văn Huy Hoàng  
- **Mã sinh viên / Lớp:** k24CSE  
- **Giai đoạn:** **Tuần 3 - 4**  
- **Nội dung công việc:** Xây dựng bàn cờ, xử lý nước đi, kiểm tra thắng/thua/hòa cho chế độ Người vs Người và hoàn thiện các cải tiến chất lượng hệ thống.

---

# MỤC LỤC
1. [MỤC TIÊU VÀ YÊU CẦU ĐẶT RA (TUẦN 3 - 4)](#1-mục-tiêu-và-yêu-cầu-đặt-ra-tuần-3---4)
2. [KẾT QUẢ THỰC HIỆN CHI TIẾT](#2-kết-quả-thực-hiện-chi-tiết)
   - [2.1. Khởi tạo và Quản lý Bàn cờ (Board & BoardPanel)](#21-khởi-tạo-và-quản-lý-bàn-cờ-board--boardpanel)
   - [2.2. Xử lý Nước đi Chế độ Người vs Người (PvP Flow)](#22-xử-lý-nước-đi-chế-độ-người-vs-người-pvp-flow)
   - [2.3. Thuật toán Kiểm tra Thắng/Thua/Hòa Cục bộ O(1)](#23-thuật-toán-kiểm-tra-thắngthuahòa-cục-bộ-o1)
   - [2.4. Cài đặt Luật Caro Việt Nam (Chặn 2 đầu)](#24-cài-đặt-luật-caro-việt-nam-chặn-2-đầu)
   - [2.5. Xử lý Hoàn tác (Undo) và Bảo toàn Điểm số](#25-xử-lý-hoàn-tác-undo-và-bảo-toàn-điểm-số)
3. [CÁC CẢI TIẾN VÀ NÂNG CẤP HỆ THỐNG](#3-các-cải-tiến-và-nâng-cấp-hệ-thống)
4. [KẾT QUẢ KIỂM THỬ TỰ ĐỘNG (AUTOMATED TESTING)](#4-kết-quả-kiểm-thử-tự-động-automated-testing)
5. [ĐÁNH GIÁ TIẾN ĐỘ & KẾ HOẠCH TUẦN TIẾP THEO](#5-đánh-giá-tiến-độ--kế-hoạch-tuần-tiếp-theo)

---

## 1. MỤC TIÊU VÀ YÊU CẦU ĐẶT RA (TUẦN 3 - 4)

Căn cứ theo bản Đề cương Đồ án cơ sở:
- Xây dựng cấu trúc dữ liệu bàn cờ tùy biến kích thước từ $10\times10$ đến $20\times20$ (mặc định $15\times15$).
- Xây dựng giao diện bàn cờ rõ nét, hiển thị lưới ô cờ, hệ tọa độ chuẩn để người chơi dễ quan sát.
- Nhận diện tương tác click chuột từ người chơi, kiểm tra tính hợp lệ của ô cờ (nằm trong bàn cờ, ô còn trống), đặt quân và tự động đảo lượt chơi luân phiên giữa 2 người (PvP).
- Cài đặt thuật toán kiểm tra điều kiện kết thúc ván cờ: Thắng khi đủ 5 quân liên tiếp và Hòa khi bàn cờ đầy.
- Hỗ trợ các chức năng tiện ích: Hoàn tác nước cờ (Undo), Đấu lại ván hiện tại (Rematch), Bắt đầu ván mới (New Game), bảng điểm (Scoreboard).

---

## 2. KẾT QUẢ THỰC HIỆN CHI TIẾT

### 2.1. Khởi tạo và Quản lý Bàn cờ (Board & BoardPanel)
- **Lớp `Board.java` (Model):**
  - Quản lý ma trận hai chiều `CellState[size][size]` với các giá trị enum `EMPTY`, `X`, `O`.
  - Hỗ trợ kiểm tra giới hạn ô hợp lệ `isValid(r, c)`.
  - Cung cấp phương thức `setCell`, `clearCell`, `isFull()` và `getOccupiedCells()`.
  - Có Copy Constructor phục vụ sao chép bàn cờ trạng thái độc lập.
- **Lớp `BoardPanel.java` (View):**
  - Kế thừa `JPanel`, vẽ lại bàn cờ bằng `Graphics2D` với độ phân giải cao và khử răng cưa `ANTIALIAS_ON`.
  - Tự động căn giữa bàn cờ, vẽ hệ trục tọa độ cột (A, B, C...) và hàng (1, 2, 3...).
  - Đánh dấu các điểm sao chuẩn (Hoshi points) tại vị trí quy chuẩn của bàn cờ $15\times15$ và $19\times19$.
  - Hiệu ứng "Ghost piece": Khi người chơi rê chuột qua ô trống, một quân cờ mờ nhẹ sẽ xuất hiện giúp người chơi nhắm tọa độ chuẩn xác trước khi click.
  - Vòng tròn màu hổ phách (Amber Ring) đánh dấu nước cờ vừa được đánh gần nhất.

### 2.2. Xử lý Nước đi Chế độ Người vs Người (PvP Flow)
- Lớp `HumanPlayer.java` kế thừa lớp trừu tượng `Player.java`.
- Khi người chơi click chuột trên `BoardPanel`, phương thức `getCellFromCoordinates(px, py)` chuyển đổi tọa độ pixel màn hình sang tọa độ ma trận ô cờ $(r, c)$.
- `GameController.java` tiếp nhận và kiểm tra:
  1. Ván cờ chưa kết thúc (`!lastResult.isOver()`).
  2. Không có tiến trình AI đang suy nghĩ.
  3. Lượt hiện tại thuộc về `HumanPlayer`.
  4. Ô $(r, c)$ nằm trong bàn cờ và đang ở trạng thái `CellState.EMPTY`.
- Sau khi hợp lệ, nước cờ được ghi vào `Board`, đóng gói thành `Move(r, c, symbol)` và lưu trữ vào ngăn xếp `GameHistory` (Stack) phục vụ Undo.
- Lượt chơi tự động được luân chuyển (`switchTurn()`), cập nhật giao diện `TurnCard` và thông báo trạng thái.

### 2.3. Thuật toán Kiểm tra Thắng/Thua/Hòa Cục bộ O(1)
Thay vì duyệt toàn bộ bàn cờ có độ phức tạp $O(N^2)$ sau mỗi lượt đi, hệ thống áp dụng kỹ thuật kiểm tra cục bộ quanh ô vừa đánh $(r, c)$ với độ phức tạp thời gian tối ưu **$O(1)$** tại `WinChecker.java`:
- Chỉ quét theo **4 hướng** đi qua ô $(r, c)$:
  1. Hướng Ngang: $(dr=0, dc=1)$
  2. Hướng Dọc: $(dr=1, dc=0)$
  3. Hướng Chéo chính: $(dr=1, dc=1)$
  4. Hướng Chéo phụ: $(dr=1, dc=-1)$
- Trên mỗi hướng, đếm số quân cùng loại liên tiếp về chiều dương (phía trước) và chiều âm (phía sau).
- Nếu tổng số quân liên tiếp $\ge 5$, xác định người chơi vừa đánh là người **Chiến thắng**, trả về danh sách tọa độ các ô thắng cuộc để `BoardPanel` vẽ dải chùm tia ánh sáng (Winning Beam) màu xanh ngọc nối liền 5 ô.
- Nếu không có chuỗi 5 quân và bàn cờ đã kín (`board.isFull()`), trả về kết quả **Hòa cờ (Draw)**.

### 2.4. Cài đặt Luật Caro Việt Nam (Chặn 2 đầu)
Để đáp ứng cả chuẩn quốc tế và luật thi đấu cờ Caro truyền thống tại Việt Nam, `WinChecker.java` đã được bổ sung tham số tùy chọn:
$$\text{checkWin}(board, r, c, blockTwoEnds)$$
- **Quy tắc:** Khi người chơi tạo thành chuỗi 5 quân liên tiếp, thuật toán kiểm tra 2 ô chặn ở 2 đầu chuỗi:
  - Đầu phía trước: $(frontR, frontC)$
  - Đầu phía sau: $(backR, backC)$
- Nếu **cả hai đầu đều bị quân của đối phương chặn** (`frontBlocked && backBlocked`), chuỗi 5 quân này **chưa được tính là chiến thắng** theo luật Caro Việt Nam và ván cờ vẫn tiếp tục.
- Nếu còn ít nhất 1 đầu mở (hoặc chỉ bị chặn 1 đầu), người chơi lập tức giành chiến thắng.

### 2.5. Xử lý Hoàn tác (Undo) và Bảo toàn Điểm số
- Chức năng Undo cho phép thu hồi nước cờ đã đi thông qua cấu trúc dữ liệu ngăn xếp `GameHistory`:
  - Ở chế độ PvP: `history.pop()` lấy nước đi gần nhất, xóa ô cờ tương ứng và đảo lại lượt chơi cho người vừa đánh.
- **Khắc phục triệt để lỗi cộng dồn điểm số:**
  - Nếu người chơi Undo một nước cờ sau khi ván cờ đã kết thúc (thắng hoặc hòa), hệ thống sẽ nhận diện `wasGameOver = true` và **tự động hoàn trừ điểm số tương ứng** (`scoreX--`, `scoreO--` hoặc `scoreDraw--`).
  - Lượt chơi được giữ nguyên cho người vừa thắng để họ thử lại nước đi khác mà không bị đảo lượt sai lầm.

---

## 3. CÁC CẢI TIẾN VÀ NÂNG CẤP HỆ THỐNG

| Hạng mục | Hiện trạng trước cải tiến | Sau khi cải tiến (Tuần 3 - 4) |
| :--- | :--- | :--- |
| **Tùy chỉnh tên người chơi** | Tên mặc định cố định `"Người chơi 1"`, `"Người chơi 2"` | Bổ sung ô nhập tên người chơi trực tiếp tại Menu, hiển thị cá nhân hóa trên Turn Card và thông báo thắng cuộc. |
| **Tùy chọn luật chơi** | Chỉ hỗ trợ Gomoku tự do | Tích hợp 2 bộ luật: **Gomoku tự do (≥5 thắng)** và **Luật Caro Việt Nam (Chặn 2 đầu không thắng)**. |
| **Nhãn lượt đi trong Menu PvP** | Hiển thị `"Người chơi 2 (O)"` gây hiểu nhầm | Đổi nhãn thành chuẩn xác: `"Người chơi 1 đi trước (X)"` và `"Người chơi 2 đi trước (X)"`. |
| **Xử lý điểm khi Undo nước thắng** | Điểm số không bị trừ, đánh lại bị cộng dồn trùng | Điểm số được bảo toàn chính xác tuyệt đối; hoàn trừ ngay khi undo nước thắng/hòa. |
| **Phím tắt điều khiển (Shortcuts)** | Chưa hỗ trợ | Bổ sung `Ctrl+Z` (Undo), `F2` (Rematch), `Ctrl+N` (Menu), `Esc` (Thoát ra Menu). |

---

## 4. KẾT QUẢ KIỂM THỬ TỰ ĐỘNG (AUTOMATED TESTING)

Dự án đã xây dựng bộ 3 chương trình kiểm thử tự động, tích hợp qua tập lệnh `run-tests.bat`:

### 1. `WinCheckerVerification.java` (Kiểm thử Logic Bàn cờ & Thuật toán O(1))
- [✓ PASS] 1. Thắng theo hàng ngang (5 ô liên tiếp).
- [✓ PASS] 2. Thắng theo hàng dọc (5 ô liên tiếp).
- [✓ PASS] 3. Thắng theo đường chéo chính (5 ô liên tiếp).
- [✓ PASS] 4. Thắng theo đường chéo phụ (5 ô liên tiếp).
- [✓ PASS] 5. Thế cờ 4 quân chưa đủ thắng, ván cờ tiếp tục.
- [✓ PASS] 6. Lọc không gian tìm kiếm bán kính $R=2$ (24 ô lân cận).
👉 **Kết quả: 6/6 test cases PASS.**

### 2. `PvPModeTest.java` (Kiểm thử Chế độ Người vs Người & Luật Caro VN)
- [✓ PASS] 1. Luân chuyển lượt chơi luân phiên chính xác (X -> O -> X).
- [✓ PASS] 2. Ngăn chặn nước đi vào ô đã có quân cờ.
- [✓ PASS] 3. Hoàn tác (Undo) nước đi trong PvP và trả lại lượt trước.
- [✓ PASS] 4. Sửa triệt để bug hoàn trừ điểm số khi Undo nước cờ thắng.
- [✓ PASS] 5. Kiểm tra kết quả chiến thắng đường chéo trong PvP.
- [✓ PASS] 6. Kiểm thử chuẩn xác Luật Caro Việt Nam (Chuỗi `O X X X X X O` bị chặn 2 đầu không thắng, mở 1 đầu thì thắng).
- [✓ PASS] 7. Kiểm tra trạng thái Hòa cờ khi bàn cờ đầy nước.
👉 **Kết quả: 7/7 test cases PASS.**

### 3. `AIAlgorithmTest.java` (Kiểm thử Module AI & Cắt tỉa Alpha-Beta)
- [✓ PASS] 1. AI chặn đứng 4 quân X thẳng hàng hở 1 đầu.
- [✓ PASS] 2. AI chặn đứng thế 4 quân nhảy cách ở giữa `(X X . X X)`.
- [✓ PASS] 3. AI chặn đứng thế 4 quân nhảy lệch `(X X X . X)`.
- [✓ PASS] 4. AI chặn Tam mở `(. X X X .)`.
- [✓ PASS] 5. AI ưu tiên đánh nước kết liễu 5 quân thắng ngay thay vì chỉ phòng thủ.
- [✓ PASS] 6. Tốc độ tính toán cấp Khó cực nhanh (0ms, dưới ngưỡng 200ms).
- [✓ PASS] 7. Cấu hình Bot đi trước thành công, tự động đánh tâm bàn cờ.
- [✓ PASS] 8. Đấu lại (Rematch) làm mới ván cờ, Bot tự động đi trước ván mới.
👉 **Kết quả: 8/8 test cases PASS.**

> **TỔNG KẾT:** **21/21 (100%) bài test tự động đều vượt qua xuất sắc.**

---

## 5. ĐÁNH GIÁ TIẾN ĐỘ & KẾ HOẠCH TUẦN TIẾP THEO

### 5.1. Đánh giá chung
- Toàn bộ khối lượng công việc đặt ra cho **Tuần 3 - 4** đã được nghiệm thu hoàn thành 100%.
- Kiến trúc phần mềm tuân thủ nghiêm ngặt mô hình MVC, mã nguồn sạch sẽ, chú thích rõ ràng, tương thích hoàn toàn từ Java 8 đến Java 21+.
- Dự án đã làm vượt tiến độ: đã hoàn thiện sớm toàn bộ phần AI Minimax + Alpha-Beta (Tuần 5-7) và Giao diện đồ họa Swing Glassmorphism (Tuần 8-9).

### 5.2. Kế hoạch tiếp theo (Chuẩn bị cho Tuần 10 - 12)
1. **Tuần 10:** Xây dựng chức năng lưu và đọc lịch sử ván đấu ra file JSON / Text cục bộ (`GameSaveManager`), cho phép xem lại từng nước đi (Replay).
2. **Tuần 11:** Kiểm thử toàn diện hiệu năng AI trên các kích thước bàn cờ lớn ($19\times19$, $20\times20$), tinh chỉnh độ nhạy heuristic thế đôi.
3. **Tuần 12:** Soạn thảo báo cáo đồ án hoàn chỉnh (40 - 60 trang) theo mẫu quy định của nhà trường và quay video demo thực nghiệm.
