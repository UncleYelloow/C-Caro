package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Thanh điều khiển và thông tin trạng thái ván cờ (Modern Sidebar Dashboard).
 * Tích hợp Bảng điểm (Scoreboard), Thẻ lượt đi trực quan (Turn Card),
 * Thống kê nước cờ (Move tracker) và các nút thao tác nhanh.
 */
public class ControlPanel extends JPanel {

    // Theme Slate Dark sang trọng
    private static final Color SIDEBAR_BG      = new Color(15, 23, 42);
    private static final Color SIDEBAR_BORDER  = new Color(30, 41, 59);
    private static final Color CARD_BG         = new Color(30, 41, 59, 180);
    private static final Color CARD_BORDER     = new Color(51, 65, 85);

    private static final Color ACCENT_RED      = new Color(244, 63, 94);
    private static final Color ACCENT_BLUE     = new Color(6, 182, 212);
    private static final Color ACCENT_GREEN    = new Color(16, 185, 129);
    private static final Color ACCENT_PURPLE   = new Color(139, 92, 246);

    private static final Color TEXT_PRIMARY    = new Color(248, 250, 252);
    private static final Color TEXT_MUTED      = new Color(148, 163, 184);

    private final GameController controller;
    private final Runnable onBackToMenu;

    // Các thành phần hiển thị động
    private JLabel lblTurnSymbol;
    private JLabel lblTurnName;
    private JLabel lblTurnStatus;
    private TurnCard turnCardPanel;

    private JLabel lblScoreX;
    private JLabel lblScoreDraw;
    private JLabel lblScoreO;

    private JLabel lblMoveCount;
    private JLabel lblLastMove;
    private JLabel lblStatusText;

    private JButton btnUndo;

    public ControlPanel(GameController controller, Runnable onBackToMenu) {
        this.controller = controller;
        this.onBackToMenu = onBackToMenu;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(290, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

        buildUI();
        updateStats();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền tối đồng nhất
        GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_BG, getWidth(), getHeight(), new Color(11, 15, 25));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Đường phân cách mỏng bên trái
        g2.setColor(new Color(255, 255, 255, 15));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(0, 0, 0, getHeight());

        g2.dispose();
    }

    private void buildUI() {
        // --- 1. TOP HEADER: MINI LOGO + MENU BUTTON ---
        JPanel topRow = new JPanel();
        topRow.setOpaque(false);
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLogo = new JLabel("<html><b style='color:#F43F5E'>X</b><b style='color:#06B6D4'>O</b> CARO</html>");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(TEXT_PRIMARY);

        JButton btnMenu = createIconButton("Menu", UiIcons.createHomeIcon(12), new Color(255, 255, 255, 12), e -> onBackToMenu.run());
        btnMenu.setPreferredSize(new Dimension(86, 30));

        topRow.add(lblLogo);
        topRow.add(Box.createHorizontalGlue());
        topRow.add(btnMenu);
        add(topRow);

        add(Box.createVerticalStrut(14));

        // --- 2. THẺ HIỂN THỊ LƯỢT ĐI HIỆN TẠI (TURN CARD) ---
        turnCardPanel = new TurnCard();
        turnCardPanel.setLayout(new BoxLayout(turnCardPanel, BoxLayout.Y_AXIS));
        turnCardPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        turnCardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnCardPanel.setMaximumSize(new Dimension(258, 92));
        turnCardPanel.setPreferredSize(new Dimension(258, 92));

        JPanel turnInnerRow = new JPanel();
        turnInnerRow.setOpaque(false);
        turnInnerRow.setLayout(new BoxLayout(turnInnerRow, BoxLayout.X_AXIS));

        lblTurnSymbol = new JLabel("X");
        lblTurnSymbol.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTurnSymbol.setForeground(ACCENT_RED);

        JPanel turnInfoBox = new JPanel();
        turnInfoBox.setOpaque(false);
        turnInfoBox.setLayout(new BoxLayout(turnInfoBox, BoxLayout.Y_AXIS));

        lblTurnName = new JLabel("Người chơi 1");
        lblTurnName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTurnName.setForeground(TEXT_PRIMARY);

        lblTurnStatus = new JLabel("Đang đến lượt đánh...");
        lblTurnStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTurnStatus.setForeground(TEXT_MUTED);

        turnInfoBox.add(lblTurnName);
        turnInfoBox.add(Box.createVerticalStrut(2));
        turnInfoBox.add(lblTurnStatus);

        turnInnerRow.add(lblTurnSymbol);
        turnInnerRow.add(Box.createHorizontalStrut(14));
        turnInnerRow.add(turnInfoBox);
        turnInnerRow.add(Box.createHorizontalGlue());

        turnCardPanel.add(turnInnerRow);
        add(turnCardPanel);

        add(Box.createVerticalStrut(12));

        // --- 3. BẢNG ĐIỂM SỐ (SCOREBOARD) ---
        JPanel scoreCard = createGlassCard();
        scoreCard.setMaximumSize(new Dimension(258, 70));
        scoreCard.setPreferredSize(new Dimension(258, 70));
        scoreCard.setLayout(new java.awt.GridLayout(1, 3, 6, 0));
        scoreCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        lblScoreX = createScorePill("QUÂN X", "0", ACCENT_RED);
        lblScoreDraw = createScorePill("HÒA", "0", TEXT_MUTED);
        lblScoreO = createScorePill("QUÂN O", "0", ACCENT_BLUE);

        scoreCard.add(lblScoreX);
        scoreCard.add(lblScoreDraw);
        scoreCard.add(lblScoreO);
        add(scoreCard);

        add(Box.createVerticalStrut(12));

        // --- 4. THỐNG KÊ VÁN ĐẤU (MATCH STATS) ---
        JPanel statsCard = createGlassCard();
        statsCard.setMaximumSize(new Dimension(258, 64));
        statsCard.setPreferredSize(new Dimension(258, 64));
        statsCard.setLayout(new java.awt.GridLayout(2, 1, 0, 4));
        statsCard.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        lblMoveCount = new JLabel("Tổng nước cờ: 0");
        lblMoveCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMoveCount.setForeground(TEXT_MUTED);

        lblLastMove = new JLabel("Nước cờ cuối: Chưa có");
        lblLastMove.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLastMove.setForeground(TEXT_MUTED);

        statsCard.add(lblMoveCount);
        statsCard.add(lblLastMove);
        add(statsCard);

        add(Box.createVerticalStrut(14));

        // --- 5. NÚT ĐIỀU KHIỂN THAO TÁC (ACTION BUTTONS) ---
        btnUndo = createActionButton("Hoàn tác (Undo)", UiIcons.createUndoIcon(13), new Color(51, 65, 85), e -> controller.undoMove());
        JButton btnRematch = createActionButton("Đấu lại ván này", UiIcons.createRefreshIcon(13), ACCENT_GREEN, e -> controller.rematch());
        JButton btnNewGame = createActionButton("Cài đặt ván mới", UiIcons.createSettingsIcon(13), ACCENT_PURPLE, e -> onBackToMenu.run());

        add(btnUndo);
        add(Box.createVerticalStrut(8));
        add(btnRematch);
        add(Box.createVerticalStrut(8));
        add(btnNewGame);

        add(Box.createVerticalStrut(14));

        // --- 6. HỘP THÔNG BÁO TRẠNG THÁI (STATUS TOAST) ---
        JPanel statusCard = createGlassCard();
        statusCard.setMaximumSize(new Dimension(258, 80));
        statusCard.setPreferredSize(new Dimension(258, 80));
        statusCard.setLayout(new java.awt.BorderLayout());
        statusCard.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblStatusText = new JLabel("<html>Sẵn sàng. Nhấp ô bất kỳ trên bàn cờ để đi quân.</html>");
        lblStatusText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatusText.setForeground(new Color(203, 213, 225));
        lblStatusText.setVerticalAlignment(SwingConstants.TOP);

        statusCard.add(lblStatusText, java.awt.BorderLayout.CENTER);
        add(statusCard);

        add(Box.createVerticalGlue());
    }

    public void updateTurnDisplay(String turnName, boolean isX) {
        lblTurnSymbol.setText(isX ? "X" : "O");
        lblTurnSymbol.setForeground(isX ? ACCENT_RED : ACCENT_BLUE);
        lblTurnName.setText(turnName);

        if (controller.isAiThinking()) {
            lblTurnStatus.setText("Bot đang tính toán...");
        } else {
            lblTurnStatus.setText("Đang chờ đi cờ...");
        }

        turnCardPanel.setTurn(isX);
        updateStats();
    }

    public void setStatusMessage(String message) {
        lblStatusText.setText("<html>" + message + "</html>");
        updateStats();
    }

    public void updateStats() {
        // Cập nhật bảng điểm
        lblScoreX.setText("<html><center><small style='color:#94A3B8'>QUÂN X</small><br><b>" + controller.getScoreX() + "</b></center></html>");
        lblScoreDraw.setText("<html><center><small style='color:#94A3B8'>HÒA</small><br><b>" + controller.getScoreDraw() + "</b></center></html>");
        lblScoreO.setText("<html><center><small style='color:#94A3B8'>QUÂN O</small><br><b>" + controller.getScoreO() + "</b></center></html>");

        // Cập nhật số lượng nước đi
        int totalMoves = controller.getHistory().size();
        lblMoveCount.setText("Tổng nước cờ: " + totalMoves);

        Move last = controller.getHistory().peek();
        if (last != null) {
            char colChar = (char) ('A' + last.getCol());
            int rowNum = last.getRow() + 1;
            lblLastMove.setText("Nước cuối: " + last.getSymbol() + " tại " + colChar + rowNum);
        } else {
            lblLastMove.setText("Nước cuối: Chưa có");
        }

        // Bật / tắt nút Undo
        btnUndo.setEnabled(totalMoves > 0 && !controller.isAiThinking() && !controller.getLastResult().isOver());
    }

    private JPanel createGlassCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private JLabel createScorePill(String title, String score, Color accent) {
        JLabel lbl = new JLabel("<html><center><small style='color:#94A3B8'>" + title + "</small><br><b>" + score + "</b></center></html>", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(accent);
        lbl.setOpaque(false);
        return lbl;
    }

    private JButton createActionButton(String text, javax.swing.Icon icon, Color bg, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = isEnabled() ? getBackground() : new Color(255, 255, 255, 6);
                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                if (isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setIconTextGap(8);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(258, 36));
        btn.setPreferredSize(new Dimension(258, 36));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(listener);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg);
            }
        });

        return btn;
    }

    private JButton createIconButton(String text, javax.swing.Icon icon, Color bg, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setIconTextGap(6);
        btn.setBackground(bg);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * Thẻ lượt đi có viền phát sáng theo màu quân X (đỏ) hoặc O (lam)
     */
    private static class TurnCard extends JPanel {
        private boolean isX = true;

        public void setTurn(boolean isX) {
            this.isX = isX;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color glowColor = isX ? new Color(244, 63, 94, 25) : new Color(6, 182, 212, 25);
            Color borderColor = isX ? new Color(244, 63, 94, 160) : new Color(6, 182, 212, 160);

            // Nền thẻ kèm quầng sáng nhẹ
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(glowColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            // Viền phát sáng chủ động
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
