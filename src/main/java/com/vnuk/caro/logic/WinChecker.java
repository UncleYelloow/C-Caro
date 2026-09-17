package com.vnuk.caro.logic;

import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.WinResult;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Thuật toán kiểm tra kết quả ván cờ (Thắng / Hòa / Tiếp tục).
 * Áp dụng kỹ thuật kiểm tra cục bộ O(1) theo 4 hướng đi qua ô vừa đánh:
 * - Ngang: (0, 1)
 * - Dọc: (1, 0)
 * - Chéo chính: (1, 1)
 * - Chéo phụ: (1, -1)
 */
public class WinChecker {
    public static final int WIN_COUNT = 5;

    // 4 cặp vector chỉ hướng: {dr, dc}
    private static final int[][] DIRECTIONS = {
        {0, 1},   // Ngang
        {1, 0},   // Dọc
        {1, 1},   // Chéo chính
        {1, -1}   // Chéo phụ
    };

    /**
     * Kiểm tra trạng thái ván cờ sau nước đi tại ô (r, c) theo luật Gomoku tự do (mặc định).
     */
    public static WinResult checkWin(Board board, int r, int c) {
        return checkWin(board, r, c, false);
    }

    /**
     * Kiểm tra trạng thái ván cờ sau nước đi tại ô (r, c).
     * @param board Bàn cờ hiện tại
     * @param r Hàng vừa đánh
     * @param c Cột vừa đánh
     * @param blockTwoEnds Nếu true, áp dụng luật Caro Việt Nam (5 quân bị chặn cả 2 đầu bởi quân đối phương không tính thắng)
     */
    public static WinResult checkWin(Board board, int r, int c, boolean blockTwoEnds) {
        CellState symbol = board.getCell(r, c);
        if (symbol == CellState.EMPTY) {
            return WinResult.continuePlaying();
        }

        CellState opponentSymbol = (symbol == CellState.X) ? CellState.O : CellState.X;

        for (int[] dir : DIRECTIONS) {
            int dr = dir[0];
            int dc = dir[1];

            List<Point> line = new ArrayList<>();
            line.add(new Point(r, c));

            // Đếm về phía trước (chiều dương)
            int step = 1;
            int frontR, frontC;
            while (true) {
                frontR = r + step * dr;
                frontC = c + step * dc;
                if (!board.isValid(frontR, frontC) || board.getCell(frontR, frontC) != symbol) {
                    break;
                }
                line.add(new Point(frontR, frontC));
                step++;
            }

            // Đếm về phía sau (chiều âm)
            step = 1;
            int backR, backC;
            while (true) {
                backR = r - step * dr;
                backC = c - step * dc;
                if (!board.isValid(backR, backC) || board.getCell(backR, backC) != symbol) {
                    break;
                }
                line.add(new Point(backR, backC));
                step++;
            }

            // Nếu số quân liên tiếp >= 5
            if (line.size() >= WIN_COUNT) {
                if (blockTwoEnds) {
                    boolean frontBlocked = board.isValid(frontR, frontC) && board.getCell(frontR, frontC) == opponentSymbol;
                    boolean backBlocked = board.isValid(backR, backC) && board.getCell(backR, backC) == opponentSymbol;
                    // Bị chặn cả 2 đầu bởi quân đối phương -> Chưa thắng theo luật Caro VN
                    if (frontBlocked && backBlocked) {
                        continue;
                    }
                }
                return WinResult.win(symbol, line);
            }
        }

        // Nếu bàn cờ đã đầy mà không ai thắng -> Hòa cờ
        if (board.isFull()) {
            return WinResult.draw();
        }

        return WinResult.continuePlaying();
    }
}
