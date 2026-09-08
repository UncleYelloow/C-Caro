package com.vnuk.caro;

import com.vnuk.caro.logic.WinChecker;
import com.vnuk.caro.logic.ai.MinimaxSolver;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import java.awt.Point;
import java.util.List;

/**
 * Chương trình kiểm thử tự động (Verification Test) cho logic Tuần 1-2:
 * 1. Kiểm tra thuật toán thắng O(1) theo 4 hướng: Ngang, Dọc, Chéo chính, Chéo phụ.
 * 2. Kiểm tra thế cờ 4 quân chưa đủ thắng.
 * 3. Kiểm tra tính năng lọc ô ứng viên bán kính 2 của AI.
 * 4. Kiểm tra AI giải nước cờ thành công.
 */
public class WinCheckerVerification {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("BẮT ĐẦU KIỂM THỬ THUẬT TOÁN VÀ LOGIC (TUẦN 1-2)");
        System.out.println("=================================================");

        testHorizontalWin();
        testVerticalWin();
        testMainDiagonalWin();
        testAntiDiagonalWin();
        testFourInARowNotWinning();
        testAICandidatesAndSolver();

        System.out.println("\n=================================================");
        System.out.println("TẤT CẢ 6/6 BÀI KIỂM THỬ ĐÃ VƯỢT QUA XUẤT SẮC! (PASS)");
        System.out.println("=================================================");
    }

    private static void testHorizontalWin() {
        Board board = new Board(15);
        for (int c = 2; c <= 5; c++) {
            board.setCell(7, c, CellState.X);
        }
        board.setCell(7, 6, CellState.X);
        WinResult result = WinChecker.checkWin(board, 7, 6);

        assertCondition(result.hasWinner() && result.getWinner() == CellState.X, "1. Thắng theo hàng ngang");
        assertCondition(result.getWinningLine().size() == 5, "   Số ô chiến thắng đúng bằng 5");
    }

    private static void testVerticalWin() {
        Board board = new Board(15);
        for (int r = 3; r <= 6; r++) {
            board.setCell(r, 4, CellState.O);
        }
        board.setCell(7, 4, CellState.O);
        WinResult result = WinChecker.checkWin(board, 7, 4);

        assertCondition(result.hasWinner() && result.getWinner() == CellState.O, "2. Thắng theo hàng dọc");
        assertCondition(result.getWinningLine().size() == 5, "   Số ô chiến thắng đúng bằng 5");
    }

    private static void testMainDiagonalWin() {
        Board board = new Board(15);
        for (int i = 0; i < 4; i++) {
            board.setCell(3 + i, 3 + i, CellState.X);
        }
        board.setCell(7, 7, CellState.X);
        WinResult result = WinChecker.checkWin(board, 7, 7);

        assertCondition(result.hasWinner() && result.getWinner() == CellState.X, "3. Thắng theo đường chéo chính");
        assertCondition(result.getWinningLine().size() == 5, "   Số ô chiến thắng đúng bằng 5");
    }

    private static void testAntiDiagonalWin() {
        Board board = new Board(15);
        for (int i = 0; i < 4; i++) {
            board.setCell(4 + i, 10 - i, CellState.O);
        }
        board.setCell(8, 6, CellState.O);
        WinResult result = WinChecker.checkWin(board, 8, 6);

        assertCondition(result.hasWinner() && result.getWinner() == CellState.O, "4. Thắng theo đường chéo phụ");
        assertCondition(result.getWinningLine().size() == 5, "   Số ô chiến thắng đúng bằng 5");
    }

    private static void testFourInARowNotWinning() {
        Board board = new Board(15);
        for (int c = 1; c <= 4; c++) {
            board.setCell(5, c, CellState.X);
        }
        WinResult result = WinChecker.checkWin(board, 5, 4);

        assertCondition(!result.hasWinner() && !result.isOver(), "5. 4 quân liên tiếp chưa đủ thắng (Game vẫn tiếp tục)");
    }

    private static void testAICandidatesAndSolver() {
        Board board = new Board(15);
        board.setCell(7, 7, CellState.X);

        MinimaxSolver solver = new MinimaxSolver();
        List<Point> candidates = solver.getCandidateMoves(board);

        // Bán kính 2 quanh (7, 7) gồm (2*2+1)^2 - 1 = 5^2 - 1 = 24 ô
        assertCondition(candidates.size() == 24, "6. Lọc không gian tìm kiếm AI bán kính 2 (chính xác 24 ô)");

        Move bestMove = solver.findBestMove(board, CellState.O, AIDifficulty.EASY);
        assertCondition(bestMove != null && board.isValid(bestMove.getRow(), bestMove.getCol()), "   AI cấp Dễ tìm ra nước đi hợp lệ: " + bestMove);

        Move bestMoveHard = solver.findBestMove(board, CellState.O, AIDifficulty.HARD);
        assertCondition(bestMoveHard != null && board.isValid(bestMoveHard.getRow(), bestMoveHard.getCol()), "   AI cấp Khó tìm ra nước đi hợp lệ: " + bestMoveHard);
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
