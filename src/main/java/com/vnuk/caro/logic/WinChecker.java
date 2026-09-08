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
     * Kiểm tra trạng thái ván cờ sau nước đi tại ô (r, c).
     * Độ phức tạp: O(1) do chỉ xét tối đa 4 hướng x 8 ô = 32 ô lân cận.
     */
    public static WinResult checkWin(Board board, int r, int c) {
        CellState symbol = board.getCell(r, c);
        if (symbol == CellState.EMPTY) {
            return WinResult.continuePlaying();
        }

        for (int[] dir : DIRECTIONS) {
            int dr = dir[0];
            int dc = dir[1];

            List<Point> line = new ArrayList<>();
            line.add(new Point(r, c));

            // Đếm về phía trước (chiều dương)
            int step = 1;
            while (true) {
                int nr = r + step * dr;
                int nc = c + step * dc;
                if (!board.isValid(nr, nc) || board.getCell(nr, nc) != symbol) {
                    break;
                }
                line.add(new Point(nr, nc));
                step++;
            }

            // Đếm về phía sau (chiều âm)
            step = 1;
            while (true) {
                int nr = r - step * dr;
                int nc = c - step * dc;
                if (!board.isValid(nr, nc) || board.getCell(nr, nc) != symbol) {
                    break;
                }
                line.add(new Point(nr, nc));
                step++;
            }

            // Nếu số quân liên tiếp >= 5 thì thắng
            if (line.size() >= WIN_COUNT) {
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
