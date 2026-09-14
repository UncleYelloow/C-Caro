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
import javax.swing.JSeparator;
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
 * Thanh điều khiển và thông tin trạng thái ván cờ - thiết kế dark theme hiện đại.
 */
public class ControlPanel extends JPanel {
    private static final Color PANEL_BG    = new Color(15, 23, 42);
    private static final Color PANEL_BG2   = new Color(22, 33, 62);
    private static final Color CARD_BG     = new Color(30, 41, 80);
    private static final Color CARD_BORDER = new Color(55, 65, 110);
    private static final Color TEXT_WHITE  = new Color(240, 248, 255);
    private static final Color TEXT_GRAY   = new Color(148, 163, 184);
    private static final Color ACCENT_RED  = new Color(239, 68, 68);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color BTN_BLUE    = new Color(37, 99, 235);
    private static final Color BTN_GREEN   = new Color(16, 185, 129);
    private static final Color BTN_ORANGE  = new Color(234, 88, 12);
    private static final Color BTN_SLATE   = new Color(51, 65, 100);

    private final GameController controller;
    private final Runnable onBackToMenu;

    private JComboBox<GameMode> cmbMode;
    private JComboBox<AIDifficulty> cmbDifficulty;
    private JComboBox<String> cmbFirstTurn;
    private JComboBox<Integer> cmbSize;
    private JLabel lblStatus;
    private JLabel lblTurnBadge;

    public ControlPanel(GameController controller, Runnable onBackToMenu) {
        this.controller = controller;
        this.onBackToMenu = onBackToMenu;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(270, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        buildUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, PANEL_BG, 0, getHeight(), PANEL_BG2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Đường kẻ trái nhẹ
        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(0, 0, 0, getHeight());
        g2.dispose();
    }

    private void buildUI() {
        add(Box.createVerticalStrut(8));

        // ===  LOGO TIÊU ĐỀ ===
        JPanel logoRow = new JPanel();
        logoRow.setOpaque(false);
        logoRow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0));

        JLabel lx = new JLabel("X");
        lx.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lx.setForeground(ACCENT_RED);
        JLabel lo = new JLabel("O");
        lo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lo.setForeground(ACCENT_BLUE);
        JLabel lt = new JLabel("CARO");
        lt.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lt.setForeground(TEXT_WHITE);

        logoRow.add(lx); logoRow.add(lo); logoRow.add(lt);
        logoRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(logoRow);

        JLabel lblSub = new JLabel("k24CSE  •  Gomoku", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblSub);

        add(Box.createVerticalStrut(14));
        add(makeSeparator());
        add(Box.createVerticalStrut(12));

        // === TURN BADGE ===
        lblTurnBadge = new JLabel("LƯỢT ĐI: NGƯỜI CHƠI 1 (X)", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTurnBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTurnBadge.setOpaque(false);
        lblTurnBadge.setBackground(new Color(69, 10, 10));
        lblTurnBadge.setForeground(new Color(252, 165, 165));
        lblTurnBadge.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        lblTurnBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTurnBadge.setMaximumSize(new Dimension(242, 38));
        add(lblTurnBadge);

        add(Box.createVerticalStrut(14));
        add(makeSeparator());
        add(Box.createVerticalStrut(12));

        // === CẤU HÌNH ===
        add(makeSectionLabel("⚔  Chế độ chơi"));
        cmbMode = new JComboBox<>(GameMode.values());
        cmbMode.setSelectedItem(controller.getMode());
        styleCombo(cmbMode);
        add(cmbMode);

        add(Box.createVerticalStrut(8));

        add(makeSectionLabel("🤖  Độ khó AI"));
        cmbDifficulty = new JComboBox<>(AIDifficulty.values());
        cmbDifficulty.setSelectedItem(controller.getDifficulty());
        styleCombo(cmbDifficulty);
        add(cmbDifficulty);

        cmbDifficulty.addActionListener(e -> {
            AIDifficulty diff = (AIDifficulty) cmbDifficulty.getSelectedItem();
            if (diff != null) controller.setDifficulty(diff);
        });

        add(Box.createVerticalStrut(8));

        add(makeSectionLabel("🎯  Lượt đi trước"));
        cmbFirstTurn = new JComboBox<>(new String[]{"Người chơi đi trước (X)", "Máy AI đi trước (X)"});
        cmbFirstTurn.setSelectedIndex(controller.isAiFirst() ? 1 : 0);
        styleCombo(cmbFirstTurn);
        add(cmbFirstTurn);

        cmbMode.addActionListener(e -> {
            boolean isPve = cmbMode.getSelectedItem() == GameMode.PVE;
            cmbDifficulty.setEnabled(isPve);
            cmbFirstTurn.removeAllItems();
            if (isPve) {
                cmbFirstTurn.addItem("Người chơi đi trước (X)");
                cmbFirstTurn.addItem("Máy AI đi trước (X)");
            } else {
                cmbFirstTurn.addItem("Người chơi 1 đi trước (X)");
                cmbFirstTurn.addItem("Người chơi 2 đi trước (X)");
            }
        });

        add(Box.createVerticalStrut(8));

        add(makeSectionLabel("📐  Kích thước bàn cờ"));
        cmbSize = new JComboBox<>(new Integer[]{10, 12, 15, 18, 20});
        cmbSize.setSelectedItem(Board.DEFAULT_SIZE);
        styleCombo(cmbSize);
        add(cmbSize);

        add(Box.createVerticalStrut(16));
        add(makeSeparator());
        add(Box.createVerticalStrut(12));

        // === NÚT CHỨC NĂNG ===
        add(makeDarkButton("▶  Ván mới", BTN_BLUE, e -> {
            int size = (Integer) cmbSize.getSelectedItem();
            GameMode mode = (GameMode) cmbMode.getSelectedItem();
            AIDifficulty diff = (AIDifficulty) cmbDifficulty.getSelectedItem();
            boolean aiFirst = cmbFirstTurn.getSelectedIndex() == 1;
            controller.startNewGame(size, mode, diff, aiFirst);
        }));

        add(Box.createVerticalStrut(7));

        add(makeDarkButton("🔄  Đấu lại", BTN_GREEN, e -> controller.rematch()));

        add(Box.createVerticalStrut(7));

        add(makeDarkButton("↩  Hoàn tác", BTN_SLATE, e -> controller.undoMove()));

        add(Box.createVerticalStrut(7));

        add(makeDarkButton("🏠  Menu chính", new Color(88, 28, 135), e -> onBackToMenu.run()));

        add(Box.createVerticalStrut(16));
        add(makeSeparator());
        add(Box.createVerticalStrut(10));

        // === THÔNG BÁO ===
        add(makeSectionLabel("💬  Thông báo"));

        lblStatus = new JLabel("<html>Sẵn sàng. Nhấp ô bất kỳ để đi cờ.</html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_GRAY);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setMaximumSize(new Dimension(242, 72));
        lblStatus.setPreferredSize(new Dimension(242, 72));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblStatus.setOpaque(false);
        add(lblStatus);

        add(Box.createVerticalGlue());
    }

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel makeSeparator() {
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 18));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
    }

    private void styleCombo(JComboBox<?> cmb) {
        cmb.setBackground(CARD_BG);
        cmb.setForeground(TEXT_WHITE);
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmb.setMaximumSize(new Dimension(242, 32));
        cmb.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cmb.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        cmb.setFocusable(false);
    }

    private JPanel makeDarkButton(String text, Color bg, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setText(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.addActionListener(listener);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });

        JPanel wrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(242, 38));
        wrapper.add(btn);
        return wrapper;
    }

    public void updateTurnDisplay(String turnName, boolean isX) {
        lblTurnBadge.setText("LƯỢT ĐI: " + turnName.toUpperCase());
        if (isX) {
            lblTurnBadge.setBackground(new Color(69, 10, 10));
            lblTurnBadge.setForeground(new Color(252, 165, 165));
        } else {
            lblTurnBadge.setBackground(new Color(8, 28, 75));
            lblTurnBadge.setForeground(new Color(147, 197, 253));
        }
    }

    public void setStatusMessage(String message) {
        lblStatus.setText("<html>" + message + "</html>");
    }
}
