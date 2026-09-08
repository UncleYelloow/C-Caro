package com.vnuk.caro.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho Bàn cờ Caro kích thước tùy chỉnh (10x10 đến 20x20, mặc định 15x15).
 * Đảm bảo tính đóng gói và an toàn dữ liệu trạng thái ô cờ.
 */
public class Board {
    public static final int DEFAULT_SIZE = 15;
    public static final int MIN_SIZE = 10;
    public static final int MAX_SIZE = 20;

    private final int size;
    private final CellState[][] grid;
    private int moveCount;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException(
                String.format("Kích thước bàn cờ phải từ %d đến %d (nhận được: %d)", MIN_SIZE, MAX_SIZE, size)
            );
        }
        this.size = size;
        this.grid = new CellState[size][size];
        this.moveCount = 0;
        reset();
    }

    /**
     * Khởi tạo bản sao phục vụ tính toán cây Minimax không làm ảnh hưởng bàn cờ chính.
     */
    public Board(Board other) {
        this.size = other.size;
        this.grid = new CellState[size][size];
        this.moveCount = other.moveCount;
        for (int r = 0; r < size; r++) {
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, size);
        }
    }

    public void reset() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = CellState.EMPTY;
            }
        }
        this.moveCount = 0;
    }

    public int getSize() {
        return size;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    public CellState getCell(int r, int c) {
        if (!isValid(r, c)) {
            return CellState.EMPTY;
        }
        return grid[r][c];
    }

    public boolean setCell(int r, int c, CellState state) {
        if (!isValid(r, c) || grid[r][c] != CellState.EMPTY || state == CellState.EMPTY) {
            return false;
        }
        grid[r][c] = state;
        moveCount++;
        return true;
    }

    public void clearCell(int r, int c) {
        if (isValid(r, c) && grid[r][c] != CellState.EMPTY) {
            grid[r][c] = CellState.EMPTY;
            moveCount--;
        }
    }

    public boolean isFull() {
        return moveCount >= size * size;
    }

    /**
     * Lấy danh sách các ô đã có quân trên bàn cờ.
     */
    public List<Point> getOccupiedCells() {
        List<Point> occupied = new ArrayList<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] != CellState.EMPTY) {
                    occupied.add(new Point(r, c));
                }
            }
        }
        return occupied;
    }
}
