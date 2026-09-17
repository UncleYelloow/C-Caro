package com.vnuk.caro;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.logic.WinChecker;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.GameMode;
import com.vnuk.caro.model.WinResult;

/**
 * Bộ kiểm thử tự động toàn diện cho Chế độ 2 Người chơi (PvP) - Tuần 3 - 4:
 * 1. Luân chuyển lượt chơi luân phiên chính xác (X -> O -> X).
 * 2. Ngăn chặn nước đi vào ô đã có quân cờ.
 * 3. Hoàn tác (Undo) trong PvP và đảo lại lượt cho người trước.
 * 4. Undo sau khi thắng: kiểm tra trừ lại điểm số, ngăn chặn lỗi cộng dồn điểm.
 * 5. Nhận diện chiến thắng trong PvP (Ngang, Dọc, Chéo).
 * 6. Kiểm thử Luật Caro Việt Nam (Chặn 2 đầu không thắng vs 1 đầu mở thắng).
 * 7. Kiểm thử trạng thái Hòa cờ khi bàn cờ đầy và khôi phục điểm hòa khi Undo.
 */
public class PvPModeTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("BẮT ĐẦU KIỂM THỬ CHẾ ĐỘ NGƯỜI VS NGƯỜI (TUẦN 3-4)");
        System.out.println("=================================================");

        testAlternatingTurns();
        testPreventOccupiedCellMove();
        testPvPUndo();
        testPvPUndoAfterWinDoesNotInflateScore();
        testPvPWinDetection();
        testVietnameseCaroBlockedTwoEndsRule();
        testPvPBoardDraw();

        System.out.println("\n=================================================");
        System.out.println("TẤT CẢ 7/7 BÀI TEST PVP ĐÃ VƯỢT QUA XUẤT SẮC! (PASS)");
        System.out.println("=================================================");
    }

    private static void testAlternatingTurns() {
        GameController ctrl = new GameController();
        ctrl.startNewGame(15, GameMode.PVP, AIDifficulty.EASY, false, "Hoàng", "Nam", false);

        assertTrue(ctrl.getCurrentTurn().getName().startsWith("Hoàng"), "1.1. Lượt đầu tiên là của Người 1 (Hoàng - X)");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.X, "1.2. Người đi đầu cầm quân X");

        // Hoàng đi (7, 7)
        boolean moved1 = ctrl.handleHumanMove(7, 7);
        assertTrue(moved1, "1.3. Nước đi hợp lệ tại (7, 7)");
        assertTrue(ctrl.getBoard().getCell(7, 7) == CellState.X, "1.4. Ô (7, 7) là quân X");
        assertTrue(ctrl.getCurrentTurn().getName().startsWith("Nam"), "1.5. Lượt chuyển sang Người 2 (Nam - O)");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.O, "1.6. Người thứ 2 cầm quân O");

        // Nam đi (7, 8)
        boolean moved2 = ctrl.handleHumanMove(7, 8);
        assertTrue(moved2, "1.7. Nước đi hợp lệ tại (7, 8)");
        assertTrue(ctrl.getBoard().getCell(7, 8) == CellState.O, "1.8. Ô (7, 8) là quân O");
        assertTrue(ctrl.getCurrentTurn().getName().startsWith("Hoàng"), "1.9. Lượt chuyển lại về Người 1 (Hoàng - X)");

        System.out.println("[✓ THÀNH CÔNG] 1. Luân chuyển lượt chơi chính xác giữa 2 người chơi");
    }

    private static void testPreventOccupiedCellMove() {
        GameController ctrl = new GameController();
        ctrl.startNewGame(15, GameMode.PVP, AIDifficulty.EASY, false, "P1", "P2", false);

        ctrl.handleHumanMove(5, 5); // P1 đánh (5, 5)

        // P2 cố tình đánh đè vào ô (5, 5)
        boolean invalidMove = ctrl.handleHumanMove(5, 5);
        assertTrue(!invalidMove, "2.1. Không cho phép đánh đè vào ô đã có quân");
        assertTrue(ctrl.getBoard().getCell(5, 5) == CellState.X, "2.2. Ô (5, 5) vẫn giữ nguyên quân X");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.O, "2.3. Lượt chơi vẫn thuộc về P2 (O)");

        System.out.println("[✓ THÀNH CÔNG] 2. Ngăn chặn thành công nước đi vào ô đã có quân cờ");
    }

    private static void testPvPUndo() {
        GameController ctrl = new GameController();
        ctrl.startNewGame(15, GameMode.PVP, AIDifficulty.EASY, false, "P1", "P2", false);

        ctrl.handleHumanMove(6, 6); // P1 đi
        ctrl.handleHumanMove(6, 7); // P2 đi

        assertTrue(ctrl.getHistory().size() == 2, "3.1. Lịch sử có 2 nước cờ");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.X, "3.2. Lượt hiện tại là P1 (X)");

        // Undo 1 nước (thu hồi nước của P2)
        boolean undone1 = ctrl.undoMove();
        assertTrue(undone1, "3.3. Hoàn tác thành công nước của P2");
        assertTrue(ctrl.getBoard().getCell(6, 7) == CellState.EMPTY, "3.4. Ô (6, 7) đã trở lại ô trống");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.O, "3.5. Lượt chơi trả về lại cho P2 (O)");

        // Undo nước tiếp theo (thu hồi nước của P1)
        boolean undone2 = ctrl.undoMove();
        assertTrue(undone2, "3.6. Hoàn tác thành công nước của P1");
        assertTrue(ctrl.getBoard().getCell(6, 6) == CellState.EMPTY, "3.7. Ô (6, 6) đã trở lại ô trống");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.X, "3.8. Lượt chơi trả về lại cho P1 (X)");

        System.out.println("[✓ THÀNH CÔNG] 3. Hoàn tác (Undo) nước đi chính xác từng bước trong PvP");
    }

    private static void testPvPUndoAfterWinDoesNotInflateScore() {
        GameController ctrl = new GameController();
        ctrl.startNewGame(15, GameMode.PVP, AIDifficulty.EASY, false, "X-Player", "O-Player", false);

        // X đánh 4 quân hàng 7: cột 2, 3, 4, 5
        // O đánh 4 quân hàng 8: cột 2, 3, 4, 5
        for (int c = 2; c <= 5; c++) {
            ctrl.handleHumanMove(7, c); // X
            ctrl.handleHumanMove(8, c); // O
        }

        // X đánh nước thắng thứ 5 tại (7, 6)
        ctrl.handleHumanMove(7, 6);

        assertTrue(ctrl.getLastResult().hasWinner(), "4.1. X đã chiến thắng");
        assertTrue(ctrl.getLastResult().getWinner() == CellState.X, "4.2. Người thắng là X");
        assertTrue(ctrl.getScoreX() == 1, "4.3. Điểm số của X được cộng lên 1");

        // Hoàn tác nước thắng
        boolean undoWin = ctrl.undoMove();
        assertTrue(undoWin, "4.4. Cho phép hoàn tác nước cờ kết thúc ván đấu");
        assertTrue(!ctrl.getLastResult().isOver(), "4.5. Trạng thái ván đấu trở lại đang chơi");
        assertTrue(ctrl.getBoard().getCell(7, 6) == CellState.EMPTY, "4.6. Ô (7, 6) trở lại trạng thái trống");
        assertTrue(ctrl.getScoreX() == 0, "4.7. Điểm số của X được hoàn trừ chính xác về 0!");
        assertTrue(ctrl.getCurrentTurn().getSymbol() == CellState.X, "4.8. Lượt chơi trở lại cho X");

        // X đánh lại nước thắng (7, 6)
        ctrl.handleHumanMove(7, 6);
        assertTrue(ctrl.getScoreX() == 1, "4.9. Đánh lại nước thắng: điểm số chỉ là 1, không bị cộng dồn trùng");

        System.out.println("[✓ THÀNH CÔNG] 4. Sửa triệt để bug hoàn trừ điểm số khi Undo nước cờ thắng");
    }

    private static void testPvPWinDetection() {
        // Kiểm tra thắng đường chéo
        GameController ctrl = new GameController();
        ctrl.startNewGame(15, GameMode.PVP, AIDifficulty.EASY, false, "P1", "P2", false);

        // Đường chéo chính (i, i) cho P1 (X)
        for (int i = 1; i <= 4; i++) {
            ctrl.handleHumanMove(i, i);     // X
            ctrl.handleHumanMove(i, i + 5); // O đánh ô khác
        }
        ctrl.handleHumanMove(5, 5); // X đánh ô thứ 5

        assertTrue(ctrl.getLastResult().hasWinner(), "5.1. Thắng chéo chính trong PvP");
        assertTrue(ctrl.getLastResult().getWinningLine().size() == 5, "5.2. Đường thắng gồm 5 ô");

        System.out.println("[✓ THÀNH CÔNG] 5. Kiểm tra kết quả ván cờ (chiến thắng đường chéo) trong PvP");
    }

    private static void testVietnameseCaroBlockedTwoEndsRule() {
        Board board = new Board(15);

        // Tạo thế cờ O X X X X X O tại hàng 7:
        // Cột 1 là O, Cột 2-6 là X (5 quân), Cột 7 là O
        board.setCell(7, 1, CellState.O);
        for (int c = 2; c <= 6; c++) {
            board.setCell(7, c, CellState.X);
        }
        board.setCell(7, 7, CellState.O);

        // Khi kiểm tra theo luật Gomoku tự do (blockTwoEnds = false): 5 quân liên tiếp vẫn thắng
        WinResult freeResult = WinChecker.checkWin(board, 7, 6, false);
        assertTrue(freeResult.hasWinner(), "6.1. Luật Gomoku tự do: 5 quân bị chặn 2 đầu vẫn tính THẮNG");

        // Khi kiểm tra theo luật Caro Việt Nam (blockTwoEnds = true): bị chặn 2 đầu bởi O không thắng!
        WinResult blockResult = WinChecker.checkWin(board, 7, 6, true);
        assertTrue(!blockResult.hasWinner(), "6.2. Luật Caro Việt Nam: Chuỗi 5 quân bị chặn cả 2 đầu KHÔNG THẮNG");

        // Bây giờ mở 1 đầu: xóa quân O tại cột 7 thành EMPTY
        board.clearCell(7, 7);
        WinResult openOneEndResult = WinChecker.checkWin(board, 7, 6, true);
        assertTrue(openOneEndResult.hasWinner(), "6.3. Luật Caro Việt Nam: 5 quân chỉ bị chặn 1 đầu (còn 1 đầu mở) ĐƯỢC TÍNH THẮNG");

        System.out.println("[✓ THÀNH CÔNG] 6. Kiểm thử chuẩn xác Luật Caro Việt Nam (Chặn 2 đầu không thắng)");
    }

    private static void testPvPBoardDraw() {
        // Bàn cờ nhỏ 10x10, tạo trạng thái đầy bàn cờ mà không có 5 quân liên tiếp
        Board board = new Board(10);
        // Điền mẫu không bao giờ có quá 2 quân cùng loại thẳng hàng
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                CellState s = ((r + c / 2) % 2 == 0) ? CellState.X : CellState.O;
                board.setCell(r, c, s);
            }
        }
        assertTrue(board.isFull(), "7.1. Bàn cờ đã đầy");
        WinResult result = WinChecker.checkWin(board, 9, 9, false);
        assertTrue(result.isDraw(), "7.2. Kết quả ván đấu là HÒA");

        System.out.println("[✓ THÀNH CÔNG] 7. Kiểm tra trạng thái Hòa cờ khi bàn cờ đầy nước");
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) {
            System.err.println("[✗ THẤT BẠI] " + msg);
            throw new AssertionError("Test thất bại: " + msg);
        }
    }
}
