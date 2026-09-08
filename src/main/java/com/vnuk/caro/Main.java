package com.vnuk.caro;

import com.vnuk.caro.view.CaroFrame;

import javax.swing.SwingUtilities;

/**
 * Điểm khởi chạy (Entry Point) của ứng dụng Game Cờ Caro.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CaroFrame frame = new CaroFrame();
            frame.setVisible(true);
        });
    }
}
