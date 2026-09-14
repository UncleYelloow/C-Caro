package com.vnuk.caro;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.logic.ai.MinimaxSolver;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.GameMode;
import com.vnuk.caro.model.HumanPlayer;
import com.vnuk.caro.model.Move;

/**
 * Kiểm thử toàn diện thuật toán AI sau khi nâng cấp:
 * 1. Chặn nước 4 thẳng đứng hở 1 đầu (khắc phục lỗi người dùng phản ánh).
 * 2. Chặn nước 4 nhảy cách (Broken 4: X X . X X).
 * 3. Chặn nước 4 nhảy cách (Broken 4: X X X . X).
 * 4. Chặn Tam mở (Open 3: . X X X .).
 * 5. Tận dụng nước thắng ngay lập tức thay vì phòng thủ.
 * 6. Đảm bảo tốc độ phản hồi < 200ms ở cấp độ Khó (Depth 4).
 */
public class AIAlgorithmTest {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("KIỂM THỬ THUẬT TOÁN AI CARO (CHẾ ĐỘ KHÓ & TOÀN DIỆN)");
        System.out.println("=================================================");

        testBlockVerticalFour();
        testBlockBrokenFourCenter();
        testBlockBrokenFourOffset();
        testBlockOpenThree();
        testTakeImmediateWin();
        testPerformanceHardMode();
        testBotFirstAndRematch();

        System.out.println("\n=================================================");
        System.out.println("TẤT CẢ 8/8 BÀI TEST AI & TÍNH NĂNG ĐÃ VƯỢT QUA! (PASS)");
        System.out.println("=================================================");
    }

    private static void testBlockVerticalFour() {
        Board board = new Board(18);
        // X có 4 quân dọc: (5, 7), (6, 7), (7, 7), (8, 7)
        // Ô dưới (9, 7) bị O chặn. Ô trên (4, 7) đang trống.
        for (int r = 5; r <= 8; r++) {
            board.setCell(r, 7, CellState.X);
        }
        board.setCell(9, 7, CellState.O);

        MinimaxSolver solver = new MinimaxSolver();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);

        assertCondition(aiMove.getRow() == 4 && aiMove.getCol() == 7,
            "1. Chặn đứng 4 quân X thẳng hàng hở 1 đầu tại (4, 7) [Thực tế AI chọn: (" + aiMove.getRow() + ", " + aiMove.getCol() + ")]");
    }

    private static void testBlockBrokenFourCenter() {
        Board board = new Board(18);
        // X có thế: X X . X X tại hàng 7: cột 4, 5 và cột 7, 8
        board.setCell(7, 4, CellState.X);
        board.setCell(7, 5, CellState.X);
        // Cột 6 trống
        board.setCell(7, 7, CellState.X);
        board.setCell(7, 8, CellState.X);

        MinimaxSolver solver = new MinimaxSolver();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);

        assertCondition(aiMove.getRow() == 7 && aiMove.getCol() == 6,
            "2. Chặn đứng thế 4 nhảy cách ở giữa (X X . X X) tại (7, 6) [Thực tế AI chọn: (" + aiMove.getRow() + ", " + aiMove.getCol() + ")]");
    }

    private static void testBlockBrokenFourOffset() {
        Board board = new Board(18);
        // X có thế: X X X . X tại hàng 8: cột 3, 4, 5 và cột 7
        board.setCell(8, 3, CellState.X);
        board.setCell(8, 4, CellState.X);
        board.setCell(8, 5, CellState.X);
        // Cột 6 trống
        board.setCell(8, 7, CellState.X);

        MinimaxSolver solver = new MinimaxSolver();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);

        assertCondition(aiMove.getRow() == 8 && aiMove.getCol() == 6,
            "3. Chặn đứng thế 4 nhảy lệch (X X X . X) tại (8, 6) [Thực tế AI chọn: (" + aiMove.getRow() + ", " + aiMove.getCol() + ")]");
    }

    private static void testBlockOpenThree() {
        Board board = new Board(18);
        // X có Tam mở: . X X X . tại hàng 9: cột 6, 7, 8 (cột 5 và 9 trống)
        board.setCell(9, 6, CellState.X);
        board.setCell(9, 7, CellState.X);
        board.setCell(9, 8, CellState.X);

        MinimaxSolver solver = new MinimaxSolver();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);

        boolean blockedOneSide = (aiMove.getRow() == 9 && (aiMove.getCol() == 5 || aiMove.getCol() == 9));
        assertCondition(blockedOneSide,
            "4. Chặn Tam mở (. X X X .) ở 1 trong 2 đầu (9, 5) hoặc (9, 9) [Thực tế AI chọn: (" + aiMove.getRow() + ", " + aiMove.getCol() + ")]");
    }

    private static void testTakeImmediateWin() {
        Board board = new Board(18);
        // AI (O) có 4 quân: (10, 4), (10, 5), (10, 6), (10, 7)
        for (int c = 4; c <= 7; c++) {
            board.setCell(10, c, CellState.O);
        }

        // Đối thủ (X) cũng có 4 quân đe dọa: (12, 4), (12, 5), (12, 6), (12, 7)
        for (int c = 4; c <= 7; c++) {
            board.setCell(12, c, CellState.X);
        }

        MinimaxSolver solver = new MinimaxSolver();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);

        boolean isWinMove = (aiMove.getRow() == 10 && (aiMove.getCol() == 3 || aiMove.getCol() == 8));
        assertCondition(isWinMove,
            "5. AI ưu tiên đánh nước thắng ngay (kết liễu 5 quân) thay vì chỉ chặn đối thủ [Thực tế AI chọn: (" + aiMove.getRow() + ", " + aiMove.getCol() + ")]");
    }

    private static void testPerformanceHardMode() {
        Board board = new Board(18);
        // Thiết lập thế trận phức tạp gồm 12 nước đi đan xen
        int[][] xMoves = {{7, 7}, {7, 8}, {8, 6}, {9, 5}, {6, 8}, {8, 7}};
        int[][] oMoves = {{8, 8}, {6, 7}, {7, 6}, {8, 5}, {9, 6}, {7, 9}};
        for (int[] p : xMoves) board.setCell(p[0], p[1], CellState.X);
        for (int[] p : oMoves) board.setCell(p[0], p[1], CellState.O);

        MinimaxSolver solver = new MinimaxSolver();
        long startTime = System.currentTimeMillis();
        Move aiMove = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);
        long elapsed = System.currentTimeMillis() - startTime;

        assertCondition(aiMove != null && elapsed < 200,
            "6. Tốc độ tính toán cấp Khó cực nhanh: " + elapsed + "ms (< 200ms tiêu chuẩn)");
    }

    private static void testBotFirstAndRematch() {
        GameController controller = new GameController();
        controller.startNewGame(15, GameMode.PVE, AIDifficulty.HARD, true);

        // Chờ AI đi nước đầu tiên (chạy trên thread riêng)
        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {}

        assertCondition(controller.isAiFirst(), "7. Bật cấu hình Bot đi trước thành công (isAiFirst = true)");
        assertCondition(controller.getBoard().getCell(7, 7) == CellState.X, "   Bot đã tự động đánh nước mở màn X tại tâm bàn cờ (7, 7)");
        assertCondition(controller.getCurrentTurn() instanceof HumanPlayer, "   Lượt chơi chuyển sang Người chơi (O)");

        // Người chơi đi nước cờ O tại (7, 8)
        boolean moved = controller.handleHumanMove(7, 8);
        assertCondition(moved && controller.getBoard().getCell(7, 8) == CellState.O, "   Người chơi đi nước cờ O tại (7, 8)");

        // Chờ AI phản hồi nước tiếp theo
        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {}

        // Thử nghiệm tính năng Hoàn tác (Undo) khi Bot đi trước
        boolean undid = controller.undoMove();
        assertCondition(undid && controller.getCurrentTurn() instanceof HumanPlayer, "   Hoàn tác (Undo) khi Bot đi trước: trả lượt về Người chơi (O)");

        // Kiểm thử tính năng Đấu lại (Rematch)
        controller.rematch();
        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {}

        assertCondition(controller.getHistory().size() == 1, "8. Đấu lại (Rematch) làm mới ván cờ, Bot tự động đi trước ván mới");
    }

    private static void assertCondition(boolean condition, String testName) {
        if (condition) {
            System.out.println("[✓ THÀNH CÔNG] " + testName);
        } else {
            System.err.println("[✗ THẤT BẠI] " + testName);
            throw new AssertionError("Kiểm thử thất bại tại: " + testName);
        }
    }
}
