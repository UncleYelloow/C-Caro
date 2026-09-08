package com.vnuk.caro.model;

import java.util.Objects;

/**
 * Đại diện cho một nước đi trên bàn cờ.
 */
public class Move {
    private final int row;
    private final int col;
    private final CellState symbol;
    private final long timestamp;

    public Move(int row, int col, CellState symbol) {
        this(row, col, symbol, System.currentTimeMillis());
    }

    public Move(int row, int col, CellState symbol, long timestamp) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
        this.timestamp = timestamp;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getSymbol() {
        return symbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return row == move.row && col == move.col && symbol == move.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, symbol);
    }

    @Override
    public String toString() {
        return String.format("%s tại (%d, %d)", symbol, row, col);
    }
}
