# ĐỒ ÁN CƠ SỞ: GAME CỜ CARO (GOMOKU) GUI & AI

- **Sinh viên thực hiện:** Từ Văn Huy Hoàng
- **Lớp:** k24CSE
- **Giai đoạn hiện tại:** **Tuần 1 - 2 (Phân tích yêu cầu, Thiết kế hệ thống & Kiến trúc dự án)**

---

## 1. Tổng quan dự án

Dự án phát triển ứng dụng chơi cờ Caro (Gomoku) bằng ngôn ngữ **Java**, áp dụng kiến trúc **Model-View-Controller (MVC)** và các nguyên lý lập trình hướng đối tượng (OOP: Encapsulation, Inheritance, Polymorphism, Abstraction).

Hệ thống hỗ trợ 2 chế độ chơi:
1. **Người vs Người (PvP):** 2 người chơi luân phiên trên cùng một máy.
2. **Người vs Máy (PvE):** Người chơi thi đấu với máy tính điều khiển bởi thuật toán AI có 3 cấp độ (Dễ, Trung bình, Khó) sử dụng **Minimax** kết hợp **cắt tỉa Alpha-Beta**, hàm đánh giá **Heuristic**, và kỹ thuật lọc bán kính tìm kiếm lân cận $R=2$.

---

## 2. Cấu trúc thư mục

```
project caro/
├── De_cuong_Do_an_Co_so_Caro-v2.md         # Đề cương đồ án chi tiết
├── README.md                               # Hướng dẫn và tổng quan
├── build.bat                               # Script tự động biên dịch toàn bộ dự án
├── run.bat                                 # Script khởi chạy ứng dụng GUI
├── run-tests.bat                           # Script chạy bộ kiểm thử tự động
├── docs/
│   └── Bao_cao_Thiet_ke_He_thong_Tuan_1_2.md  # Tài liệu SRS & Sơ đồ UML hoàn chỉnh (Tuần 1-2)
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
    │   │   ├── WinChecker.java             # Thuật toán kiểm tra thắng thua cục bộ O(1)
    │   │   ├── GameController.java         # Bộ điều khiển trung tâm theo mô hình MVC
    │   │   └── ai/                         # [AI ENGINE]
    │   │       ├── HeuristicEvaluator.java # Đánh giá thế cờ (Tứ mở, Tam mở, Thế đôi)
    │   │       └── MinimaxSolver.java      # Thuật toán Minimax + Alpha-Beta Pruning
    │   └── view/                           # [VIEW] Giao diện đồ họa Swing
    │       ├── CaroFrame.java              # Cửa sổ chính JFrame
    │       ├── BoardPanel.java             # Vẽ bàn cờ Graphics2D hiện đại, hiệu ứng hover, highlight
    │       └── ControlPanel.java           # Thanh điều khiển (New Game, Undo, Cấu hình)
    └── test/java/com/vnuk/caro/
        └── WinCheckerVerification.java     # Bộ test kiểm chứng thuật toán O(1) & AI Candidate
```

---

## 3. Hướng dẫn biên dịch và khởi chạy

Dự án tương thích với tất cả các phiên bản Java từ **Java 8 trở lên đến Java 17, 21+**.

### Cách 1: Sử dụng các file script có sẵn (Khuyên dùng)
- **Biên dịch:** Nhấp đúp vào file `build.bat`
- **Khởi chạy game:** Nhấp đúp vào file `run.bat`
- **Chạy kiểm thử:** Nhấp đúp vào file `run-tests.bat`

### Cách 2: Sử dụng Command Line / PowerShell
```powershell
# 1. Biên dịch
javac --release 8 -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# 2. Khởi chạy ứng dụng
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.Main

# 3. Chạy kiểm thử tự động
java "-Dfile.encoding=UTF-8" -cp bin com.vnuk.caro.WinCheckerVerification
```

---

## 4. Báo cáo tiến độ Tuần 1 - 2

- [x] **Tìm hiểu đề tài & phân tích yêu cầu:** Hoàn thành tài liệu SRS chi tiết với 9 yêu cầu chức năng (FR-01 đến FR-09) và 4 yêu cầu phi chức năng.
- [x] **Thiết kế sơ đồ hệ thống:**
  - Sơ đồ Use-Case tổng thể + Bảng đặc tả chi tiết.
  - Sơ đồ Lớp (Class Diagram) theo mô hình MVC và 4 trụ cột OOP (Encapsulation, Abstraction, Inheritance, Polymorphism).
  - Sơ đồ Hoạt động (Activity Diagram) cho luồng ván cờ, kiểm tra thắng cục bộ $O(1)$ và luồng AI.
  - Sơ đồ Tuần tự (Sequence Diagram) cho nước đi của Người, nước đi của AI và chức năng Undo.
- [x] **Tài liệu bàn giao:** File [docs/Bao_cao_Thiet_ke_He_thong_Tuan_1_2.md](docs/Bao_cao_Thiet_ke_He_thong_Tuan_1_2.md).
- [x] **Xây dựng khung kiến trúc mã nguồn:** Toàn bộ cấu trúc thư mục và các lớp Java cốt lõi đã được thiết lập và kiểm thử vượt qua 100% test cases.
