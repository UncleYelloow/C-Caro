package com.vnuk.caro.model;

/**
 * Lớp trừu tượng định nghĩa người chơi trong hệ thống Caro.
 * Thể hiện nguyên lý Tính trừu tượng (Abstraction) và Đa hình (Polymorphism) trong OOP.
 */
public abstract class Player {
    protected final String name;
    protected final CellState symbol;

    public Player(String name, CellState symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public CellState getSymbol() {
        return symbol;
    }

    /**
     * Phương thức trừu tượng tạo nước đi trên bàn cờ.
     * Đối với HumanPlayer, nước đi nhận từ sự kiện giao diện.
     * Đối với AIPlayer, nước đi tính toán từ thuật toán.
     */
    public abstract Move makeMove(Board board);

    @Override
    public String toString() {
        return String.format("%s (%s)", name, symbol);
    }
}
