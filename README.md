# ĐỒ ÁN CƠ SỞ: GAME CỜ CARO (GOMOKU) GUI & AI

- **Sinh viên thực hiện:** Từ Văn Huy Hoàng
- **Lớp:** k24CSE
- **Giai đoạn hiện tại:** **Tuần 3 - 4 (Xây dựng bàn cờ, Xử lý nước đi, Kiểm tra Thắng/Thua/Hòa PvP & Cải tiến hệ thống - HOÀN THÀNH)**

---

## 1. Tổng quan dự án

Dự án phát triển ứng dụng chơi cờ Caro (Gomoku) bằng ngôn ngữ **Java**, áp dụng kiến trúc **Model-View-Controller (MVC)** và các nguyên lý lập trình hướng đối tượng (OOP: Encapsulation, Inheritance, Polymorphism, Abstraction).

Hệ thống hỗ trợ 2 chế độ chơi và 2 bộ luật:
1. **Người vs Người (PvP):** 2 người chơi luân phiên trên cùng một máy, tùy chỉnh tên người chơi, chọn lượt đi trước, hỗ trợ Hoàn tác (Undo) và Đấu lại (Rematch).
2. **Người vs Máy (PvE):** Người chơi thi đấu với máy tính điều khiển bởi thuật toán AI có 3 cấp độ (Dễ, Trung bình, Khó) sử dụng **Minimax** kết hợp **cắt tỉa Alpha-Beta**, hàm đánh giá **Heuristic**, và kỹ thuật lọc bán kính tìm kiếm lân cận $R=2$.
3. **Bộ luật chiến thắng:**
   - **Gomoku tự do (Freestyle):** Đủ 5 quân liên tiếp (kể cả bị chặn 2 đầu) là chiến thắng ngay.
   - **Luật Caro Việt Nam:** 5 quân liên tiếp bị chặn cả 2 đầu bởi quân đối phương (`O X X X X X O`) thì chưa thắng (phải mở ít nhất 1 đầu).

---

## 2. Cấu trúc thư mục

```
project caro/
├── De_cuong_Do_an_Co_so_Caro-v2.md         # Đề cương đồ án chi tiết
├── README.md                               # Hướng dẫn và tổng quan tiến độ
├── build.bat                               # Script tự động biên dịch toàn bộ dự án
├── run.bat                                 # Script khởi chạy ứng dụng GUI
├── run-tests.bat                           # Script chạy bộ 3 kiểm thử tự động
├── docs/
│   ├── Bao_cao_Thiet_ke_He_thong_Tuan_1_2.md  # Tài liệu SRS & Sơ đồ UML hoàn chỉnh (Tuần 1-2)
│   └── Bao_cao_Tien_do_Tuan_3_4.md           # Báo cáo kết quả nghiệm thu & Cải tiến Tuần 3-4
└── src/
    ├── main/java/com/vnuk/caro/
    │   ├── Main.java                       # Điểm khởi chạy chương trình (Entry Point)
    │   ├── model/                          # [MODEL] Dữ liệu trạng thái ván cờ
    │   │   ├── CellState.java              # Enum trạng thái ô: EMPTY, X, O
    │   │   ├── GameMode.java               # Enum chế độ chơi: PVP, PVE
    │   │   ├── AIDifficulty.java           # Enum độ khó AI: EASY, MEDIUM, HARD
    │   │   ├── Move.java                   # Thông tin nước đi (tọa độ, quân cờ, thời gian)
    │   │   ├── Board.java                  # Quản lý ma trận bàn cờ tùy chỉnh 10x10 -> 20x20
    │   │   ├── Player.java                 # Abstract class đại diện người chơi (OOP)
    │   │   ├── HumanPlayer.java            # Kế thừa Player cho người chơi con người
    │   │   ├── AIPlayer.java               # Kế thừa Player tích hợp AI Solver
    │   │   ├── WinResult.java              # Kết quả kiểm tra ván cờ (thắng, hòa, đường thắng)
    │   │   └── GameHistory.java            # Ngăn xếp lưu lịch sử phục vụ Undo / Replay
    │   ├── logic/                          # [CONTROLLER & BUSINESS LOGIC]
    │   │   ├── WinChecker.java             # Thuật toán kiểm tra thắng thua cục bộ O(1) & luật Caro VN
    │   │   ├── GameController.java         # Bộ điều khiển trung tâm theo mô hình MVC
    │   │   └── ai/                         # [AI ENGINE]
    │   │       ├── HeuristicEvaluator.java # Đánh giá thế cờ (Tứ mở, Tam mở, Thế đôi)
    │   │       └── MinimaxSolver.java      # Thuật toán Minimax + Alpha-Beta Pruning
    │   └── view/                           # [VIEW] Giao diện đồ họa Swing
    │       ├── CaroFrame.java              # Cửa sổ chính JFrame tích hợp Phím tắt
    │       ├── BoardPanel.java             # Vẽ bàn cờ Graphics2D Dark Slate, tọa độ, tia thắng
    │       ├── ControlPanel.java           # Thanh điều khiển (Dashboard, Turn Card, Scoreboard)
    │       ├── MenuPanel.java              # Màn hình Menu chính Glassmorphism, chọn tên & luật
    │       └── UiIcons.java                # Bộ icon vector thuần Graphics2D siêu nét
    └── test/java/com/vnuk/caro/
        ├── WinCheckerVerification.java     # Bộ test kiểm chứng thuật toán O(1) 4 hướng
        ├── PvPModeTest.java                # Bộ test luồng Người vs Người, Undo, Score & Luật VN
        ├── AIAlgorithmTest.java            # Bộ test thuật toán AI chặn nước & Alpha-Beta
        └── GUIRenderTest.java              # Test tự động kiểm chứng render màn hình GUI
```

---

## 3. Hướng dẫn biên dịch và khởi chạy

Dự án tương thích với tất cả các phiên bản Java từ **Java 8 trở lên đến Java 17, 21+**.

### Cách 1: Sử dụng các file script có sẵn (Khuyên dùng)
- **Biên dịch:** Nhấp đúp vào file `build.bat`
- **Khởi chạy game:** Nhấp đúp vào file `run.bat`
- **Chạy toàn bộ 3 bộ test tự động:** Nhấp đúp vào file `run-tests.bat`

### Cách 2: Sử dụng Command Line / PowerShell
```powershell
# 1. Biên dịch toàn bộ
javac --release 8 -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# 2. Khởi chạy ứng dụng
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.Main

# 3. Chạy trọn bộ 3 bài test kiểm thử tự động
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.WinCheckerVerification
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.PvPModeTest
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.AIAlgorithmTest
```

---

## 4. Phím tắt tiện ích trong trò chơi

| Phím tắt | Chức năng |
| :---: | :--- |
| **Ctrl + Z** | Hoàn tác nước cờ vừa đánh (Undo) |
| **F2** | Đấu lại ván hiện tại ngay lập tức (Rematch) |
| **Ctrl + N** | Cài đặt ván mới (Mở Menu chính) |
| **Esc** | Trở về Menu cấu hình chính |

---

## 5. Báo cáo tiến độ Tuần 3 - 4

- [x] **Khởi tạo và hiển thị bàn cờ (Board & BoardPanel):**
  - Hỗ trợ chọn kích thước bàn cờ linh hoạt ($10\times10$ đến $20\times20$, mặc định $15\times15$).
  - Vẽ lưới giao điểm, hiển thị tọa độ số/chữ (A-O, 1-15), điểm sao Hoshi.
- [x] **Xử lý nước đi chế độ Người vs Người (PvP):**
  - Chuyển đổi click chuột chính xác vào tọa độ giao điểm.
  - Ngăn chặn đánh vào ô đã có quân, tự động đổi lượt luân phiên giữa X và O.
  - Hiệu ứng quân cờ mờ khi rê chuột (Ghost piece), vòng tròn hổ phách tô sáng nước đi gần nhất.
- [x] **Kiểm tra Thắng / Thua / Hòa (WinChecker):**
  - Thuật toán cục bộ độ phức tạp $O(1)$ quét 4 hướng từ nước vừa đánh.
  - Tô sáng chùm tia chiến thắng (Winning beam) nối 5 ô thắng cuộc.
  - Kiểm tra hòa khi đầy bàn cờ (`board.isFull()`).
- [x] **Cải tiến & Nâng cấp toàn diện trong Tuần 3-4:**
  - **Sửa lỗi tính điểm khi Undo:** Tự động hoàn trừ điểm số khi thu hồi nước cờ kết thúc ván, ngăn chặn triệt để lỗi cộng dồn điểm trùng lặp.
  - **Nhập tên người chơi tùy chỉnh:** Thêm ô nhập tên người chơi trực quan trên Menu.
  - **Hỗ trợ Luật Caro Việt Nam:** Tùy chọn bật/tắt luật "Chặn 2 đầu không tính thắng".
  - **Sửa nhãn lượt đi:** Hiển thị rõ ràng `"Người chơi 1 đi trước (X)"` và `"Người chơi 2 đi trước (X)"`.
  - **Bổ sung bộ test tự động [PvPModeTest.java](file:///src/test/java/com/vnuk/caro/PvPModeTest.java):** Vượt qua 100% 7/7 ca kiểm thử.
  - **Tài liệu nghiệm thu:** File [docs/Bao_cao_Tien_do_Tuan_3_4.md](file:///docs/Bao_cao_Tien_do_Tuan_3_4.md).
