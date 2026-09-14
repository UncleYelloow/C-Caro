package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.GameMode;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Thanh điều khiển và thông tin trạng thái ván cờ (Control Panel).
 */
public class ControlPanel extends JPanel {
    private final GameController controller;

    private final JComboBox<GameMode> cmbMode;
    private final JComboBox<AIDifficulty> cmbDifficulty;
    private final JComboBox<Integer> cmbSize;
    private final JButton btnNewGame;
    private final JButton btnUndo;
    private final JLabel lblStatus;
    private final JLabel lblTurnBadge;

    public ControlPanel(GameController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(280, 0));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("CARO GAME", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblTitle);

        JLabel lblSub = new JLabel("Đồ án cơ sở - k24CSE", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblSub);

        add(Box.createVerticalStrut(20));

        // 2. Thẻ hiển thị lượt chơi (Turn badge)
        lblTurnBadge = new JLabel("LƯỢT ĐI: NGƯỜI CHƠI 1 (X)", SwingConstants.CENTER);
        lblTurnBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTurnBadge.setOpaque(true);
        lblTurnBadge.setBackground(new Color(254, 226, 226));
        lblTurnBadge.setForeground(new Color(220, 38, 38));
        lblTurnBadge.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        lblTurnBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTurnBadge.setMaximumSize(new Dimension(240, 40));
        add(lblTurnBadge);

        add(Box.createVerticalStrut(20));

        // 3. Cấu hình chế độ chơi
        add(createSectionLabel("Chế độ chơi:"));
        cmbMode = new JComboBox<>(GameMode.values());
        cmbMode.setSelectedItem(controller.getMode());
        cmbMode.setMaximumSize(new Dimension(240, 36));
        cmbMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(cmbMode);

        add(Box.createVerticalStrut(12));

        // 4. Cấu hình độ khó AI
        add(createSectionLabel("Độ khó AI:"));
        cmbDifficulty = new JComboBox<>(AIDifficulty.values());
        cmbDifficulty.setSelectedItem(controller.getDifficulty());
        cmbDifficulty.setMaximumSize(new Dimension(240, 36));
        cmbDifficulty.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(cmbDifficulty);

        cmbMode.addActionListener(e -> {
            boolean isPve = cmbMode.getSelectedItem() == GameMode.PVE;
            cmbDifficulty.setEnabled(isPve);
        });

        cmbDifficulty.addActionListener(e -> {
            AIDifficulty diff = (AIDifficulty) cmbDifficulty.getSelectedItem();
            if (diff != null) {
                controller.setDifficulty(diff);
            }
        });

        add(Box.createVerticalStrut(12));

        // 5. Cấu hình kích thước bàn cờ
        add(createSectionLabel("Kích thước bàn cờ:"));
        Integer[] sizes = {10, 12, 15, 18, 20};
        cmbSize = new JComboBox<>(sizes);
        cmbSize.setSelectedItem(Board.DEFAULT_SIZE);
        cmbSize.setMaximumSize(new Dimension(240, 36));
        cmbSize.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(cmbSize);

        add(Box.createVerticalStrut(24));

        // 6. Nút chức năng
        btnNewGame = createStyledButton("Ván mới (New Game)", new Color(37, 99, 235), Color.WHITE);
        btnNewGame.addActionListener(e -> {
            int size = (Integer) cmbSize.getSelectedItem();
            GameMode mode = (GameMode) cmbMode.getSelectedItem();
            AIDifficulty diff = (AIDifficulty) cmbDifficulty.getSelectedItem();
            controller.startNewGame(size, mode, diff);
        });
        add(btnNewGame);

        add(Box.createVerticalStrut(10));

        btnUndo = createStyledButton("Hoàn tác nước cờ (Undo)", new Color(241, 245, 249), new Color(51, 65, 85));
        btnUndo.addActionListener(e -> controller.undoMove());
        add(btnUndo);

        add(Box.createVerticalStrut(20));

        // 7. Hộp tin nhắn trạng thái
        add(createSectionLabel("Thông báo:"));
        lblStatus = new JLabel("<html>Sẵn sàng. Nhấp ô bất kỳ để đi cờ.</html>");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(71, 85, 105));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setPreferredSize(new Dimension(240, 60));
        add(lblStatus);

        add(Box.createVerticalGlue());
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 42));
        return btn;
    }

    public void updateTurnDisplay(String turnName, boolean isX) {
        lblTurnBadge.setText("LƯỢT ĐI: " + turnName.toUpperCase());
        if (isX) {
            lblTurnBadge.setBackground(new Color(254, 226, 226));
            lblTurnBadge.setForeground(new Color(220, 38, 38));
        } else {
            lblTurnBadge.setBackground(new Color(219, 234, 254));
            lblTurnBadge.setForeground(new Color(29, 78, 216));
        }
    }

    public void setStatusMessage(String message) {
        lblStatus.setText("<html>" + message + "</html>");
    }
}
