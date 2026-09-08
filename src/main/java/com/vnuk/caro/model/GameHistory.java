package com.vnuk.caro.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Quản lý lịch sử các nước đi của ván cờ (hỗ trợ Undo, Redo, Replay, Save).
 */
public class GameHistory {
    private final Deque<Move> moveStack;

    public GameHistory() {
        this.moveStack = new ArrayDeque<>();
    }

    public void push(Move move) {
        if (move != null) {
            moveStack.push(move);
        }
    }

    public Move pop() {
        return moveStack.poll();
    }

    public Move peek() {
        return moveStack.peek();
    }

    public boolean isEmpty() {
        return moveStack.isEmpty();
    }

    public int size() {
        return moveStack.size();
    }

    public void clear() {
        moveStack.clear();
    }

    /**
     * Lấy toàn bộ danh sách các nước đi theo trình tự thời gian (từ nước đầu đến nước cuối).
     */
    public List<Move> getMovesChronological() {
        List<Move> list = new ArrayList<>(moveStack);
        java.util.Collections.reverse(list);
        return list;
    }
}
