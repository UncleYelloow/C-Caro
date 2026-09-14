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
 * Bộ giải thuật toán AI Cờ Caro:
 * - Tầng kiểm tra chiến thuật tức thời (Pre-search Tactical Check).
 * - Sắp xếp nước đi (Move Ordering) theo điểm chiến thuật (Tấn công + Phòng thủ).
 * - Cắt tỉa Alpha-Beta kết hợp giới hạn không gian lân cận (Radius = 2).
 */
public class MinimaxSolver {
    private static final int SEARCH_RADIUS = 2;
    private final Random random = new Random();

    public static class ScoredPoint implements Comparable<ScoredPoint> {
        public final Point point;
        public final int score;

        public ScoredPoint(Point point, int score) {
            this.point = point;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredPoint o) {
            return Integer.compare(o.score, this.score); // Sắp xếp giảm dần (điểm cao nhất trước)
        }
    }

    /**
     * Tìm nước đi tối ưu nhất cho AI theo cấp độ khó đã chọn.
     */
    public Move findBestMove(Board board, CellState aiSymbol, AIDifficulty difficulty) {
        List<Point> candidates = getCandidateMoves(board);
        if (candidates.isEmpty()) {
            int center = board.getSize() / 2;
            return new Move(center, center, aiSymbol);
        }

        CellState opponent = aiSymbol.opposite();
        double defenseWeight = (difficulty == AIDifficulty.HARD) ? 1.4 :
                               (difficulty == AIDifficulty.MEDIUM) ? 1.2 : 1.0;

        // 1. TẦNG KIỂM TRA CHIẾN THUẬT ƯU TIÊN (TACTICAL PRE-CHECK)
        // 1.1. AI có nước thắng ngay lập tức (5 quân liên tiếp hoặc lấp khe hở 5) -> Đánh ngay
        for (Point p : candidates) {
            board.setCell(p.x, p.y, aiSymbol);
            WinResult wr = WinChecker.checkWin(board, p.x, p.y);
            board.clearCell(p.x, p.y);
            if (wr.hasWinner()) {
                return new Move(p.x, p.y, aiSymbol);
            }
        }

        // 1.2. Đối thủ có nước thắng ngay lập tức (5 quân) -> Bắt buộc chặn ngay (mọi cấp độ)
        for (Point p : candidates) {
            board.setCell(p.x, p.y, opponent);
            WinResult wr = WinChecker.checkWin(board, p.x, p.y);
            board.clearCell(p.x, p.y);
            if (wr.hasWinner()) {
                return new Move(p.x, p.y, aiSymbol);
            }
        }

        // 2. TÍNH ĐIỂM CHIẾN THUẬT VÀ SẮP XẾP NƯỚC ĐI (MOVE ORDERING)
        List<ScoredPoint> scoredCandidates = new ArrayList<>(candidates.size());
        for (Point p : candidates) {
            int score = HeuristicEvaluator.evaluateMoveTactics(board, p.x, p.y, aiSymbol, defenseWeight);
            scoredCandidates.add(new ScoredPoint(p, score));
        }
        Collections.sort(scoredCandidates);

        // 1.3. Nước kết liễu không thể cản phá: AI tạo Tứ mở hoặc Thế đôi hiểm hóc
        if (difficulty != AIDifficulty.EASY && !scoredCandidates.isEmpty()) {
            ScoredPoint top = scoredCandidates.get(0);
            if (top.score >= HeuristicEvaluator.OPEN_FOUR) {
                return new Move(top.point.x, top.point.y, aiSymbol);
            }
        }

        // Chế độ DỄ: chọn trong top các nước hợp lý nhưng có yếu tố ngẫu nhiên
        if (difficulty == AIDifficulty.EASY) {
            // Nếu có nước cực kỳ nguy hiểm (đối thủ chuẩn bị tạo tứ mở), vẫn ưu tiên chặn
            ScoredPoint top = scoredCandidates.get(0);
            if (top.score >= HeuristicEvaluator.BLOCKED_FOUR) {
                return new Move(top.point.x, top.point.y, aiSymbol);
            }
            int poolSize = Math.min(3, scoredCandidates.size());
            Point chosen = scoredCandidates.get(random.nextInt(poolSize)).point;
            return new Move(chosen.x, chosen.y, aiSymbol);
        }

        // 3. THUẬT TOÁN ALPHA-BETA VỚI MOVE ORDERING VÀ ĐỘ SÂU THEO CẤP ĐỘ
        int searchLimit = (difficulty == AIDifficulty.HARD) ? 14 : 10;
        int maxCandidates = Math.min(scoredCandidates.size(), searchLimit);
        List<Point> filteredCandidates = new ArrayList<>(maxCandidates);
        for (int i = 0; i < maxCandidates; i++) {
            filteredCandidates.add(scoredCandidates.get(i).point);
        }

        int depth = difficulty.getSearchDepth();
        Point bestPoint = filteredCandidates.get(0);
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Point p : filteredCandidates) {
            board.setCell(p.x, p.y, aiSymbol);
            int score = alphaBeta(board, depth - 1, alpha, beta, false, aiSymbol, p.x, p.y, defenseWeight);
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

        return new Move(bestPoint.x, bestPoint.y, aiSymbol);
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isMax,
                          CellState aiSymbol, int lastR, int lastC, double defenseWeight) {
        WinResult wr = WinChecker.checkWin(board, lastR, lastC);
        if (wr.hasWinner()) {
            // isMax = true: đối thủ vừa đánh và thắng -> điểm âm cực lớn cho AI
            // isMax = false: AI vừa đánh và thắng -> điểm dương cực lớn cho AI
            return isMax ? (-HeuristicEvaluator.WIN_SCORE - depth) : (HeuristicEvaluator.WIN_SCORE + depth);
        }
        if (wr.isDraw() || depth == 0) {
            return HeuristicEvaluator.evaluateBoard(board, aiSymbol);
        }

        List<Point> rawCandidates = getCandidateMoves(board);
        if (rawCandidates.isEmpty()) {
            return 0;
        }

        CellState currentSymbol = isMax ? aiSymbol : aiSymbol.opposite();
        List<ScoredPoint> scoredList = new ArrayList<>(rawCandidates.size());
        for (Point p : rawCandidates) {
            int score = HeuristicEvaluator.evaluateMoveTactics(board, p.x, p.y, currentSymbol, defenseWeight);
            scoredList.add(new ScoredPoint(p, score));
        }
        Collections.sort(scoredList);

        // Thu hẹp nhánh ở các tầng sâu hơn để duy trì phản hồi dưới 100ms
        int branchLimit = (depth >= 3) ? 8 : (depth == 2 ? 6 : 4);
        int limit = Math.min(scoredList.size(), branchLimit);

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < limit; i++) {
                Point p = scoredList.get(i).point;
                board.setCell(p.x, p.y, aiSymbol);
                int eval = alphaBeta(board, depth - 1, alpha, beta, false, aiSymbol, p.x, p.y, defenseWeight);
                board.clearCell(p.x, p.y);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            CellState opponentSymbol = aiSymbol.opposite();
            for (int i = 0; i < limit; i++) {
                Point p = scoredList.get(i).point;
                board.setCell(p.x, p.y, opponentSymbol);
                int eval = alphaBeta(board, depth - 1, alpha, beta, true, aiSymbol, p.x, p.y, defenseWeight);
                board.clearCell(p.x, p.y);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
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
