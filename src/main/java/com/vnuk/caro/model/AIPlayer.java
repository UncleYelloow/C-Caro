package com.vnuk.caro.model;

import com.vnuk.caro.logic.ai.MinimaxSolver;

/**
 * Lớp đại diện cho đối thủ máy (AI Player).
 * Kế thừa từ lớp Player và tích hợp bộ giải thuật toán Minimax / Alpha-Beta.
 */
public class AIPlayer extends Player {
    private AIDifficulty difficulty;
    private final MinimaxSolver solver;

    public AIPlayer(String name, CellState symbol, AIDifficulty difficulty) {
        super(name, symbol);
        this.difficulty = difficulty;
        this.solver = new MinimaxSolver();
    }

    public AIDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(AIDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public Move makeMove(Board board) {
        return solver.findBestMove(board, this.symbol, this.difficulty);
    }
}
