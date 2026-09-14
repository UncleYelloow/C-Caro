package com.vnuk.caro.logic.ai;

import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;

/**
 * Đánh giá heuristic thế trận bàn cờ Caro và giá trị chiến thuật của các nước đi.
 * Tích hợp nhận diện mẫu hình (Pattern Recognition), thế đôi (Fork 3-3, 4-3, 4-4)
 * và cân bằng điểm Tấn công - Phòng thủ.
 */
public class HeuristicEvaluator {
    // Thang điểm chiến thuật chuẩn xác trong cờ Caro / Gomoku
    public static final int WIN_SCORE = 10_000_000;       // Thắng tuyệt đối (5 quân)
    public static final int OPEN_FOUR = 1_000_000;        // Tứ mở (2 đầu trống) - chắc chắn thắng lượt kế
    public static final int FORK_FOUR_FOUR = 800_000;     // Thế đôi Tứ - Tứ
    public static final int FORK_FOUR_THREE = 500_000;    // Thế đôi Tứ - Tam (4-3)
    public static final int FORK_THREE_THREE = 200_000;   // Thế đôi Tam - Tam (3-3 mở)
    public static final int BLOCKED_FOUR = 150_000;       // Tứ đóng / Tứ nhảy - đe dọa thắng ngay
    public static final int OPEN_THREE = 15_000;          // Tam mở (chuỗi 3 hoặc 3 nhảy hở 2 đầu)
    public static final int BLOCKED_THREE = 1_500;        // Tam đóng (bị chặn 1 đầu)
    public static final int OPEN_TWO = 200;               // Nhị mở
    public static final int BLOCKED_TWO = 20;             // Nhị đóng

    public static final int[][] DIRECTIONS = {
        {0, 1},   // Ngang
        {1, 0},   // Dọc
        {1, 1},   // Chéo chính
        {1, -1}   // Chéo phụ
    };

    /**
     * Đánh giá tổng điểm thế trận toàn cục của bàn cờ cho AI.
     */
    public static int evaluateBoard(Board board, CellState aiSymbol) {
        CellState humanSymbol = aiSymbol.opposite();
        int aiScore = evaluateBoardForPlayer(board, aiSymbol);
        int humanScore = evaluateBoardForPlayer(board, humanSymbol);
        return (int) (aiScore - 1.4 * humanScore);
    }

    /**
     * Tính điểm chiến thuật tổng hợp của một ô cờ (r, c) nếu AI lựa chọn đánh vào đây.
     * Kết hợp điểm Tấn công (AI) và điểm Phòng thủ (chặn Đối thủ) có trọng số.
     */
    public static int evaluateMoveTactics(Board board, int r, int c, CellState aiSymbol, double defenseWeight) {
        int attack = evaluatePointPatterns(board, r, c, aiSymbol);
        int defense = evaluatePointPatterns(board, r, c, aiSymbol.opposite());

        // Điểm ưu tiên vị trí gần trung tâm (tạo lợi thế khai cuộc)
        int center = board.getSize() / 2;
        int distFromCenter = Math.abs(r - center) + Math.abs(c - center);
        int positionalBonus = Math.max(0, 20 - distFromCenter);

        return (int) (attack + defenseWeight * defense) + positionalBonus;
    }

    /**
     * Đánh giá mẫu hình chiến thuật hình thành khi người chơi (player) đánh vào ô (r, c).
     * Phát hiện: 5, Tứ mở, Tứ đóng/nhảy, Tam mở, Tam đóng, Nhị và các Thế đôi (Forks).
     */
    public static int evaluatePointPatterns(Board board, int r, int c, CellState player) {
        if (!board.isValid(r, c) || board.getCell(r, c) != CellState.EMPTY) {
            return 0;
        }

        board.setCell(r, c, player);

        int countFive = 0;
        int countOpenFour = 0;
        int countBlockedFour = 0;
        int countOpenThree = 0;
        int countBlockedThree = 0;
        int countOpenTwo = 0;
        int countBlockedTwo = 0;

        for (int[] dir : DIRECTIONS) {
            int dr = dir[0];
            int dc = dir[1];

            // 1. Đếm chuỗi quân liên tiếp đi qua (r, c)
            int pos = 0;
            while (board.isValid(r + (pos + 1) * dr, c + (pos + 1) * dc)
                    && board.getCell(r + (pos + 1) * dr, c + (pos + 1) * dc) == player) {
                pos++;
            }
            int neg = 0;
            while (board.isValid(r - (neg + 1) * dr, c - (neg + 1) * dc)
                    && board.getCell(r - (neg + 1) * dr, c - (neg + 1) * dc) == player) {
                neg++;
            }
            int consecutive = 1 + pos + neg;

            boolean posOpen = board.isValid(r + (pos + 1) * dr, c + (pos + 1) * dc)
                    && board.getCell(r + (pos + 1) * dr, c + (pos + 1) * dc) == CellState.EMPTY;
            boolean negOpen = board.isValid(r - (neg + 1) * dr, c - (neg + 1) * dc)
                    && board.getCell(r - (neg + 1) * dr, c - (neg + 1) * dc) == CellState.EMPTY;
            int openEnds = (posOpen ? 1 : 0) + (negOpen ? 1 : 0);

            int patternRank = 0; // Thang đo: 5 (Five), 4 (Open4), 3 (Blocked4), 2 (Open3), 1 (Blocked3)
            if (consecutive >= 5) {
                patternRank = 5;
            } else if (consecutive == 4) {
                patternRank = (openEnds == 2) ? 4 : (openEnds == 1 ? 3 : 0);
            } else if (consecutive == 3) {
                patternRank = (openEnds == 2) ? 2 : (openEnds == 1 ? 1 : 0);
            }

            // 2. Quét các cửa sổ độ dài 5 ô đi qua (r, c) để nhận diện thế cờ nhảy cách (Broken patterns)
            for (int offset = -4; offset <= 0; offset++) {
                int pCount = 0;
                boolean validWindow = true;

                for (int k = 0; k < 5; k++) {
                    int cr = r + (offset + k) * dr;
                    int cc = c + (offset + k) * dc;
                    if (!board.isValid(cr, cc)) {
                        validWindow = false;
                        break;
                    }
                    CellState state = board.getCell(cr, cc);
                    if (state == player.opposite()) {
                        validWindow = false;
                        break;
                    }
                    if (state == player) {
                        pCount++;
                    }
                }

                if (!validWindow) continue;

                // Kiểm tra 2 đầu ngoài của cửa sổ 5 ô
                int outLeftR = r + (offset - 1) * dr;
                int outLeftC = c + (offset - 1) * dc;
                boolean leftOpen = board.isValid(outLeftR, outLeftC) && board.getCell(outLeftR, outLeftC) == CellState.EMPTY;

                int outRightR = r + (offset + 5) * dr;
                int outRightC = c + (offset + 5) * dc;
                boolean rightOpen = board.isValid(outRightR, outRightC) && board.getCell(outRightR, outRightC) == CellState.EMPTY;
                int windowOpenEnds = (leftOpen ? 1 : 0) + (rightOpen ? 1 : 0);

                if (pCount == 5) {
                    patternRank = Math.max(patternRank, 5);
                } else if (pCount == 4) {
                    if (windowOpenEnds == 2) {
                        patternRank = Math.max(patternRank, 4);
                    } else if (windowOpenEnds >= 1) {
                        patternRank = Math.max(patternRank, 3);
                    }
                } else if (pCount == 3) {
                    if (windowOpenEnds == 2) {
                        patternRank = Math.max(patternRank, 2);
                    } else if (windowOpenEnds == 1) {
                        patternRank = Math.max(patternRank, 1);
                    }
                }
            }

            // Phân loại kết quả mẫu hình hướng này
            if (patternRank == 5) countFive++;
            else if (patternRank == 4) countOpenFour++;
            else if (patternRank == 3) countBlockedFour++;
            else if (patternRank == 2) countOpenThree++;
            else if (patternRank == 1) countBlockedThree++;
            else if (consecutive == 2) {
                if (openEnds == 2) countOpenTwo++;
                else if (openEnds == 1) countBlockedTwo++;
            }
        }

        board.clearCell(r, c);

        // Tổng hợp điểm và nhận diện Thế đôi (Forks)
        if (countFive > 0) return WIN_SCORE;
        if (countOpenFour > 0 || countBlockedFour >= 2) return OPEN_FOUR;
        if (countBlockedFour > 0 && countOpenThree > 0) return FORK_FOUR_THREE;
        if (countOpenThree >= 2) return FORK_THREE_THREE;
        if (countBlockedFour > 0) return BLOCKED_FOUR;
        if (countOpenThree > 0 && countBlockedThree > 0) return OPEN_THREE + BLOCKED_THREE;
        if (countOpenThree > 0) return OPEN_THREE;
        if (countBlockedThree >= 2) return BLOCKED_THREE * 2;
        if (countBlockedThree > 0) return BLOCKED_THREE;
        if (countOpenTwo >= 2) return OPEN_TWO * 3;
        if (countOpenTwo > 0) return OPEN_TWO;
        return countBlockedTwo * BLOCKED_TWO;
    }

    private static int evaluateBoardForPlayer(Board board, CellState player) {
        int totalScore = 0;
        int size = board.getSize();

        int openThreeCount = 0;
        int blockedFourCount = 0;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board.getCell(r, c) == player) {
                    for (int[] dir : DIRECTIONS) {
                        int dr = dir[0];
                        int dc = dir[1];
                        int prevR = r - dr;
                        int prevC = c - dc;
                        // Chỉ xét nếu ô trước đó không cùng quân (tránh lặp)
                        if (board.isValid(prevR, prevC) && board.getCell(prevR, prevC) == player) {
                            continue;
                        }

                        int count = 0;
                        int cr = r;
                        int cc = c;
                        while (board.isValid(cr, cc) && board.getCell(cr, cc) == player) {
                            count++;
                            cr += dr;
                            cc += dc;
                        }

                        int openEnds = 0;
                        if (board.isValid(cr, cc) && board.getCell(cr, cc) == CellState.EMPTY) {
                            openEnds++;
                        }
                        if (board.isValid(prevR, prevC) && board.getCell(prevR, prevC) == CellState.EMPTY) {
                            openEnds++;
                        }

                        if (count >= 5) {
                            totalScore += WIN_SCORE;
                        } else if (count == 4) {
                            if (openEnds == 2) {
                                totalScore += OPEN_FOUR;
                            } else if (openEnds == 1) {
                                totalScore += BLOCKED_FOUR;
                                blockedFourCount++;
                            }
                        } else if (count == 3) {
                            if (openEnds == 2) {
                                totalScore += OPEN_THREE;
                                openThreeCount++;
                            } else if (openEnds == 1) {
                                totalScore += BLOCKED_THREE;
                            }
                        } else if (count == 2) {
                            if (openEnds == 2) totalScore += OPEN_TWO;
                            else if (openEnds == 1) totalScore += BLOCKED_TWO;
                        }
                    }
                }
            }
        }

        // Điểm thưởng thế đôi toàn cục
        if (blockedFourCount >= 2) totalScore += FORK_FOUR_FOUR;
        else if (blockedFourCount >= 1 && openThreeCount >= 1) totalScore += FORK_FOUR_THREE;
        else if (openThreeCount >= 2) totalScore += FORK_THREE_THREE;

        return totalScore;
    }
}
