package com.vnuk.caro.view;

import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.GameMode;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Màn hình Menu chính của Game Cờ Caro.
 * Thiết kế hiện đại với nền gradient đậm, logo nổi bật và lựa chọn cấu hình trực quan.
 */
public class MenuPanel extends JPanel {

    // Bảng màu đậm, sang trọng
    private static final Color DARK_BG_TOP    = new Color(10, 10, 25);
    private static final Color DARK_BG_BOTTOM = new Color(20, 30, 60);
    private static final Color ACCENT_RED     = new Color(239, 68, 68);
    private static final Color ACCENT_BLUE    = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN   = new Color(16, 185, 129);
    private static final Color CARD_BG        = new Color(255, 255, 255, 18);
    private static final Color CARD_BORDER    = new Color(255, 255, 255, 35);
    private static final Color TEXT_PRIMARY   = new Color(240, 248, 255);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    public interface MenuCallback {
        void onStartGame(int boardSize, GameMode mode, AIDifficulty difficulty, boolean aiFirst);
    }

    private final MenuCallback callback;

    private JComboBox<String> cmbMode;
    private JComboBox<AIDifficulty> cmbDifficulty;
    private JComboBox<String> cmbFirstTurn;
    private JComboBox<Integer> cmbSize;

    public MenuPanel(MenuCallback callback) {
        this.callback = callback;
        setLayout(new BorderLayout());
        setOpaque(true);
        buildUI();
    }

    private void buildUI() {
        // --- PHẦN TRÊN: Logo + Tiêu đề ---
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BG_TOP, 0, getHeight(), DARK_BG_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                drawDecorativeGrid(g2);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 280));
        headerPanel.setBorder(new EmptyBorder(40, 30, 30, 30));

        JPanel logoBox = new JPanel();
        logoBox.setOpaque(false);
        logoBox.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));

        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblX.setForeground(ACCENT_RED);
        logoBox.add(lblX);

        JLabel lblO = new JLabel("O");
        lblO.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblO.setForeground(ACCENT_BLUE);
        logoBox.add(lblO);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        textBox.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("CARO GAME", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Gomoku - Đồ án cơ sở  |  k24CSE", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(TEXT_SECONDARY);

        JPanel innerHeader = new JPanel(new java.awt.GridBagLayout());
        innerHeader.setOpaque(false);
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new java.awt.Insets(0,0,6,0);
        innerHeader.add(logoBox, gbc);
        gbc.gridy = 1;
        innerHeader.add(lblTitle, gbc);
        gbc.gridy = 2;
        innerHeader.add(lblSub, gbc);

        headerPanel.add(innerHeader, BorderLayout.CENTER);

        // --- PHẦN DƯỚI: Cấu hình + Nút bắt đầu ---
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, DARK_BG_BOTTOM, 0, getHeight(), new Color(5, 10, 30));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(30, 50, 40, 50));

        // Card cấu hình
        JPanel configCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        configCard.setOpaque(false);
        configCard.setBorder(new EmptyBorder(28, 32, 28, 32));
        configCard.setLayout(new GridLayout(4, 2, 16, 14));

        // Row 1: Chế độ chơi
        configCard.add(createConfigLabel("⚔  Chế độ chơi"));
        String[] modes = {"Người vs Máy (AI)", "Người vs Người (PvP)"};
        cmbMode = new JComboBox<>(modes);
        styleComboBox(cmbMode);
        configCard.add(cmbMode);

        // Row 2: Độ khó AI
        configCard.add(createConfigLabel("🤖  Độ khó AI"));
        cmbDifficulty = new JComboBox<>(AIDifficulty.values());
        cmbDifficulty.setSelectedItem(AIDifficulty.HARD);
        styleComboBox(cmbDifficulty);
        configCard.add(cmbDifficulty);

        // Row 3: Lượt đi trước
        configCard.add(createConfigLabel("🎯  Đi trước"));
        String[] firstTurnOpts = {"Người chơi đi trước (X)", "Máy AI đi trước (X)"};
        cmbFirstTurn = new JComboBox<>(firstTurnOpts);
        styleComboBox(cmbFirstTurn);
        configCard.add(cmbFirstTurn);

        // Row 4: Kích thước bàn cờ
        configCard.add(createConfigLabel("📐  Kích thước bàn"));
        Integer[] sizes = {10, 12, 15, 18, 20};
        cmbSize = new JComboBox<>(sizes);
        cmbSize.setSelectedItem(Board.DEFAULT_SIZE);
        styleComboBox(cmbSize);
        configCard.add(cmbSize);

        // Listener: chuyển chế độ PvP
        cmbMode.addActionListener(e -> {
            boolean isPve = cmbMode.getSelectedIndex() == 0;
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

        // Nút bắt đầu
        JButton btnStart = createMenuButton("BẮT ĐẦU VÁN CỜ", ACCENT_GREEN);
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnStart.setPreferredSize(new Dimension(340, 54));
        btnStart.addActionListener(e -> startGame());

        JButton btnQuit = createMenuButton("Thoát", new Color(71, 85, 105));
        btnQuit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnQuit.setPreferredSize(new Dimension(160, 40));
        btnQuit.addActionListener(e -> System.exit(0));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 14));
        btnPanel.add(btnStart);

        JPanel quitPanel = new JPanel();
        quitPanel.setOpaque(false);
        quitPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        quitPanel.add(btnQuit);

        JPanel bottomBox = new JPanel(new BorderLayout());
        bottomBox.setOpaque(false);
        bottomBox.add(btnPanel, BorderLayout.NORTH);
        bottomBox.add(quitPanel, BorderLayout.CENTER);

        contentPanel.add(configCard, BorderLayout.CENTER);
        contentPanel.add(bottomBox, BorderLayout.SOUTH);

        // Wrapper chính
        JPanel wrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, DARK_BG_TOP, 0, getHeight(), new Color(5, 10, 30));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setLayout(new BorderLayout());
        wrapper.add(headerPanel, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private void startGame() {
        int boardSize = (Integer) cmbSize.getSelectedItem();
        GameMode mode = cmbMode.getSelectedIndex() == 0 ? GameMode.PVE : GameMode.PVP;
        AIDifficulty diff = (AIDifficulty) cmbDifficulty.getSelectedItem();
        boolean aiFirst = cmbFirstTurn.getSelectedIndex() == 1;
        callback.onStartGame(boardSize, mode, diff, aiFirst);
    }

    private JLabel createConfigLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private void styleComboBox(JComboBox<?> cmb) {
        cmb.setBackground(new Color(30, 41, 80));
        cmb.setForeground(TEXT_PRIMARY);
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setPreferredSize(new Dimension(240, 36));
        cmb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cmb.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(71, 85, 120), 1));
        cmb.setFocusable(false);
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private static void drawDecorativeGrid(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 8));
        g2.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i < 600; i += 32) {
            g2.drawLine(i, 0, i, 300);
            g2.drawLine(0, i, 600, i);
        }
    }
}
