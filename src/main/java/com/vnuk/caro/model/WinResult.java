package com.vnuk.caro.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kết quả kiểm tra trạng thái ván cờ (thắng, thua, hòa, tiếp tục).
 */
public class WinResult {
    private final boolean over;
    private final boolean draw;
    private final CellState winner;
    private final List<Point> winningLine;

    private WinResult(boolean over, boolean draw, CellState winner, List<Point> winningLine) {
        this.over = over;
        this.draw = draw;
        this.winner = winner;
        this.winningLine = winningLine != null ? Collections.unmodifiableList(new ArrayList<>(winningLine)) : Collections.emptyList();
    }

    public static WinResult continuePlaying() {
        return new WinResult(false, false, CellState.EMPTY, null);
    }

    public static WinResult draw() {
        return new WinResult(true, true, CellState.EMPTY, null);
    }

    public static WinResult win(CellState winner, List<Point> winningLine) {
        return new WinResult(true, false, winner, winningLine);
    }

    public boolean isOver() {
        return over;
    }

    public boolean isDraw() {
        return draw;
    }

    public boolean hasWinner() {
        return over && !draw && winner != CellState.EMPTY;
    }

    public CellState getWinner() {
        return winner;
    }

    public List<Point> getWinningLine() {
        return winningLine;
    }

    @Override
    public String toString() {
        if (!over) return "Game in progress";
        if (draw) return "Hòa cờ";
        return "Người thắng: " + winner + " với " + winningLine.size() + " quân liên tiếp";
    }
}
