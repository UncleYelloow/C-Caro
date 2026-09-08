package com.vnuk.caro.logic.ai;

import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;

/**
 * Đánh giá heuristic thế trận bàn cờ Caro.
 * Phục vụ thuật toán Minimax và Alpha-Beta Pruning.
 */
public class HeuristicEvaluator {
    public static final int WIN_SCORE = 1_000_000;
    public static final int OPEN_FOUR = 50_000;
    public static final int BLOCKED_FOUR = 5_000;
    public static final int OPEN_THREE = 3_000;
    public static final int BLOCKED_THREE = 300;
    public static final int OPEN_TWO = 100;
    public static final int BLOCKED_TWO = 10;

    private static final int[][] DIRECTIONS = {
        {0, 1}, {1, 0}, {1, 1}, {1, -1}
    };

    /**
     * Đánh giá tổng điểm thế cờ cho AI so với đối thủ.
     */
    public static int evaluateBoard(Board board, CellState aiSymbol) {
        CellState humanSymbol = aiSymbol.opposite();

        int aiScore = evaluateForPlayer(board, aiSymbol);
        int humanScore = evaluateForPlayer(board, humanSymbol);

        // Nhân hệ số 1.2 cho điểm đối thủ để AI ưu tiên chặn hiểm
        return (int) (aiScore - 1.2 * humanScore);
    }

    private static int evaluateForPlayer(Board board, CellState player) {
        int totalScore = 0;
        int size = board.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board.getCell(r, c) == player) {
                    for (int[] dir : DIRECTIONS) {
                        int dr = dir[0];
                        int dc = dir[1];
                        // Chỉ xét nếu ô trước đó không cùng quân cờ (tránh tính trùng lặp)
                        int prevR = r - dr;
                        int prevC = c - dc;
                        if (board.isValid(prevR, prevC) && board.getCell(prevR, prevC) == player) {
                            continue;
                        }
                        totalScore += evaluateLineFrom(board, r, c, dr, dc, player);
                    }
                }
            }
        }
        return totalScore;
    }

    private static int evaluateLineFrom(Board board, int r, int c, int dr, int dc, CellState player) {
        int count = 0;
        int cr = r;
        int cc = c;

        while (board.isValid(cr, cc) && board.getCell(cr, cc) == player) {
            count++;
            cr += dr;
            cc += dc;
        }

        // Kiểm tra 2 đầu có bị chặn hay còn trống
        int openEnds = 0;
        if (board.isValid(cr, cc) && board.getCell(cr, cc) == CellState.EMPTY) {
            openEnds++;
        }

        int startPrevR = r - dr;
        int startPrevC = c - dc;
        if (board.isValid(startPrevR, startPrevC) && board.getCell(startPrevR, startPrevC) == CellState.EMPTY) {
            openEnds++;
        }

        if (count >= 5) {
            return WIN_SCORE;
        }

        if (count == 4) {
            if (openEnds == 2) return OPEN_FOUR;
            if (openEnds == 1) return BLOCKED_FOUR;
        } else if (count == 3) {
            if (openEnds == 2) return OPEN_THREE;
            if (openEnds == 1) return BLOCKED_THREE;
        } else if (count == 2) {
            if (openEnds == 2) return OPEN_TWO;
            if (openEnds == 1) return BLOCKED_TWO;
        }

        return 0;
    }
}
