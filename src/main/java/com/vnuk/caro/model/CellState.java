package com.vnuk.caro.model;

/**
 * Đại diện cho trạng thái của một ô cờ trên bàn cờ Caro.
 */
public enum CellState {
    EMPTY,
    X,
    O;

    /**
     * Lấy quân cờ đối thủ tương ứng.
     */
    public CellState opposite() {
        if (this == X) return O;
        if (this == O) return X;
        return EMPTY;
    }

    @Override
    public String toString() {
        switch (this) {
            case X: return "X";
            case O: return "O";
            default: return " ";
        }
    }
}
