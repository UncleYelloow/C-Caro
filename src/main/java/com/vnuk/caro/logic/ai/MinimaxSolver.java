package com.vnuk.caro.logic.ai;

import com.vnuk.caro.logic.WinChecker;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Bộ giải thuật toán AI: Minimax kết hợp Cắt tỉa Alpha-Beta và Lọc bán kính lân cận (Radius = 2).
 */
public class MinimaxSolver {
    private static final int SEARCH_RADIUS = 2;
    private final Random random = new Random();

    /**
     * Tìm nước đi tối ưu nhất cho AI dựa trên cấp độ khó.
     */
    public Move findBestMove(Board board, CellState aiSymbol, AIDifficulty difficulty) {
        List<Point> candidates = getCandidateMoves(board);
        if (candidates.isEmpty()) {
            int center = board.getSize() / 2;
            return new Move(center, center, aiSymbol);
        }

        // Nếu cấp độ Dễ: đánh giá heuristic 1 lượt và có độ ngẫu nhiên
        if (difficulty == AIDifficulty.EASY) {
            return findMoveEasy(board, aiSymbol, candidates);
        }

        int depth = difficulty.getSearchDepth();
        Point bestPoint = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Ưu tiên kiểm tra nước thắng ngay lập tức
        for (Point p : candidates) {
            board.setCell(p.x, p.y, aiSymbol);
            WinResult wr = WinChecker.checkWin(board, p.x, p.y);
            board.clearCell(p.x, p.y);
            if (wr.hasWinner()) {
                return new Move(p.x, p.y, aiSymbol);
            }
        }

        // Ưu tiên chặn nước đối thủ thắng ngay lập tức
        CellState opponent = aiSymbol.opposite();
        for (Point p : candidates) {
            board.setCell(p.x, p.y, opponent);
            WinResult wr = WinChecker.checkWin(board, p.x, p.y);
            board.clearCell(p.x, p.y);
            if (wr.hasWinner()) {
                return new Move(p.x, p.y, aiSymbol);
            }
        }

        // Chạy thuật toán Alpha-Beta
        for (Point p : candidates) {
            board.setCell(p.x, p.y, aiSymbol);
            int score = alphaBeta(board, depth - 1, alpha, beta, false, aiSymbol, p.x, p.y);
            board.clearCell(p.x, p.y);

            if (score > bestScore) {
                bestScore = score;
                bestPoint = p;
            }
            alpha = Math.max(alpha, bestScore);
            if (beta <= alpha) {
                break;
            }
        }

        if (bestPoint == null) {
            bestPoint = candidates.get(0);
        }

        return new Move(bestPoint.x, bestPoint.y, aiSymbol);
    }

    private Move findMoveEasy(Board board, CellState aiSymbol, List<Point> candidates) {
        Point bestPoint = candidates.get(0);
        int maxScore = Integer.MIN_VALUE;

        for (Point p : candidates) {
            board.setCell(p.x, p.y, aiSymbol);
            int score = HeuristicEvaluator.evaluateBoard(board, aiSymbol) + random.nextInt(50);
            board.clearCell(p.x, p.y);

            if (score > maxScore) {
                maxScore = score;
                bestPoint = p;
            }
        }
        return new Move(bestPoint.x, bestPoint.y, aiSymbol);
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isMax, CellState aiSymbol, int lastR, int lastC) {
        WinResult wr = WinChecker.checkWin(board, lastR, lastC);
        if (wr.hasWinner()) {
            return isMax ? -HeuristicEvaluator.WIN_SCORE : HeuristicEvaluator.WIN_SCORE;
        }
        if (wr.isDraw() || depth == 0) {
            return HeuristicEvaluator.evaluateBoard(board, aiSymbol);
        }

        List<Point> candidates = getCandidateMoves(board);
        if (candidates.isEmpty()) {
            return 0;
        }

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Point p : candidates) {
                board.setCell(p.x, p.y, aiSymbol);
                int eval = alphaBeta(board, depth - 1, alpha, beta, false, aiSymbol, p.x, p.y);
                board.clearCell(p.x, p.y);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Cắt tỉa Beta
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            CellState humanSymbol = aiSymbol.opposite();
            for (Point p : candidates) {
                board.setCell(p.x, p.y, humanSymbol);
                int eval = alphaBeta(board, depth - 1, alpha, beta, true, aiSymbol, p.x, p.y);
                board.clearCell(p.x, p.y);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Cắt tỉa Alpha
                }
            }
            return minEval;
        }
    }

    /**
     * Giới hạn không gian tìm kiếm: chỉ xét các ô trống trong bán kính SEARCH_RADIUS = 2
     * tính từ các ô đã có quân cờ.
     */
    public List<Point> getCandidateMoves(Board board) {
        List<Point> occupied = board.getOccupiedCells();
        if (occupied.isEmpty()) {
            int center = board.getSize() / 2;
            List<Point> centerList = new ArrayList<>();
            centerList.add(new Point(center, center));
            return centerList;
        }

        Set<Point> candidateSet = new HashSet<>();
        int size = board.getSize();

        for (Point p : occupied) {
            for (int dr = -SEARCH_RADIUS; dr <= SEARCH_RADIUS; dr++) {
                for (int dc = -SEARCH_RADIUS; dc <= SEARCH_RADIUS; dc++) {
                    int nr = p.x + dr;
                    int nc = p.y + dc;
                    if (board.isValid(nr, nc) && board.getCell(nr, nc) == CellState.EMPTY) {
                        candidateSet.add(new Point(nr, nc));
                    }
                }
            }
        }

        return new ArrayList<>(candidateSet);
    }
}
