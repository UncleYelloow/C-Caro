package com.vnuk.caro.model;

/**
 * Cấp độ khó của AI đối thủ.
 */
public enum AIDifficulty {
    EASY("Dễ (Heuristic cơ bản)", 1),
    MEDIUM("Trung bình (Minimax Depth 2)", 2),
    HARD("Khó (Alpha-Beta Pruning Depth 3-4)", 3);

    private final String displayName;
    private final int searchDepth;

    AIDifficulty(String displayName, int searchDepth) {
        this.displayName = displayName;
        this.searchDepth = searchDepth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
