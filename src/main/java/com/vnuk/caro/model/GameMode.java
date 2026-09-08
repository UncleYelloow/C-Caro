package com.vnuk.caro.model;

/**
 * Chế độ chơi của ván cờ.
 */
public enum GameMode {
    PVP("Người vs Người"),
    PVE("Người vs Máy (AI)");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
