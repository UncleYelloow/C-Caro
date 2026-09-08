package com.vnuk.caro.model;

/**
 * Lớp đại diện cho người chơi con người (Human Player).
 * Kế thừa từ lớp Player.
 */
public class HumanPlayer extends Player {
    private Move pendingMove;

    public HumanPlayer(String name, CellState symbol) {
        super(name, symbol);
    }

    public void setPendingMove(int r, int c) {
        this.pendingMove = new Move(r, c, this.symbol);
    }

    @Override
    public Move makeMove(Board board) {
        Move m = this.pendingMove;
        this.pendingMove = null;
        return m;
    }
}
