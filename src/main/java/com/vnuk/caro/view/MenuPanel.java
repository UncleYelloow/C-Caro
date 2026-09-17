package com.vnuk.caro.view;

import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.GameMode;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Menu chính của Game Cờ Caro.
 * Thiết kế Modern Dark Slate Glassmorphism với bộ điều khiển dạng Pill / Card Selectors
 * thay thế toàn bộ dropdown cũ, trực quan, mượt mà và không bị lỗi tràn khung hình.
 */
public class MenuPanel extends JPanel {

    // Palette màu hiện đại
    private static final Color BG_TOP       = new Color(11, 15, 25);
    private static final Color BG_BOTTOM    = new Color(15, 23, 42);
    private static final Color CARD_BG      = new Color(30, 41, 59, 210);
    private static final Color CARD_BORDER  = new Color(255, 255, 255, 25);

    private static final Color ACCENT_RED   = new Color(244, 63, 94);
    private static final Color ACCENT_BLUE  = new Color(6, 182, 212);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);

    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_MUTED   = new Color(148, 163, 184);

    public interface MenuCallback {
        void onStartGame(int boardSize, GameMode mode, AIDifficulty difficulty, boolean aiFirst,
                         String p1Name, String p2Name, boolean blockTwoEnds);
    }

    private final MenuCallback callback;

    // Trạng thái cấu hình hiện tại
    private GameMode selectedMode = GameMode.PVE;
    private AIDifficulty selectedDiff = AIDifficulty.HARD;
    private boolean selectedAiFirst = false; // false = Người chơi 1/Bạn đi trước (X); true = Máy/Người chơi 2 đi trước (X)
    private int selectedSize = Board.DEFAULT_SIZE;
    private boolean selectedBlockTwoEnds = false;

    // Các nút nhóm điều khiển
    private final List<PillButton> modeButtons = new ArrayList<>();
    private final List<PillButton> diffButtons = new ArrayList<>();
    private final List<PillButton> turnButtons = new ArrayList<>();
    private final List<PillButton> ruleButtons = new ArrayList<>();
    private final List<PillButton> sizeButtons = new ArrayList<>();
    private JPanel diffRowPanel;

    private JTextField txtPlayer1;
    private JTextField txtPlayer2;
    private JLabel lblPlayer1Title;
    private JLabel lblPlayer2Title;

    public MenuPanel(MenuCallback callback) {
        this.callback = callback;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        buildUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền Gradient chuyển tiếp sâu lắng
        GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Lưới trang trí mờ công nghệ cao
        g2.setColor(new Color(255, 255, 255, 5));
        g2.setStroke(new BasicStroke(0.8f));
        int spacing = 36;
        for (int x = 0; x < getWidth(); x += spacing) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += spacing) {
            g2.drawLine(0, y, getWidth(), y);
        }

        // Quầng sáng Ambient nhẹ nhàng ở góc
        g2.setColor(new Color(6, 182, 212, 12));
        g2.fillOval(getWidth() - 250, -100, 350, 350);
        g2.setColor(new Color(244, 63, 94, 10));
        g2.fillOval(-100, getHeight() - 250, 350, 350);
    }

    private void buildUI() {
        add(Box.createVerticalGlue());

        // --- 1. HEADER (LOGO & TITLE) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo X & O
        JPanel logoRow = new JPanel();
        logoRow.setOpaque(false);
        logoRow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 0));

        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblX.setForeground(ACCENT_RED);

        JLabel lblO = new JLabel("O");
        lblO.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblO.setForeground(ACCENT_BLUE);

        logoRow.add(lblX);
        logoRow.add(lblO);
        logoRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoRow);

        JLabel lblTitle = new JLabel("CARO GOMOKU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Đồ án cơ sở k24CSE  •  Trí tuệ nhân tạo Minimax & Alpha-Beta", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblSub);

        add(headerPanel);
        add(Box.createVerticalStrut(20));

        // --- 2. CONFIGURATION CARD (GLASSMORPHISM) ---
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền card
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Viền bóng tinh tế
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));
        cardPanel.setMaximumSize(new Dimension(610, 420));
        cardPanel.setPreferredSize(new Dimension(610, 420));
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Row 1: Chế độ chơi
        cardPanel.add(createSectionLabel("CHẾ ĐỘ CHƠI"));
        cardPanel.add(Box.createVerticalStrut(4));
        JPanel modeRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        modeRow.setOpaque(false);

        PillButton btnPve = new PillButton("Đấu với Máy (AI)", UiIcons.createRobotIcon(13), true);
        PillButton btnPvp = new PillButton("2 Người chơi (PvP)", UiIcons.createUsersIcon(13), false);
        modeButtons.add(btnPve);
        modeButtons.add(btnPvp);

        btnPve.addActionListener(e -> setGameMode(GameMode.PVE));
        btnPvp.addActionListener(e -> setGameMode(GameMode.PVP));
        modeRow.add(btnPve);
        modeRow.add(btnPvp);
        cardPanel.add(modeRow);

        cardPanel.add(Box.createVerticalStrut(10));

        // Row 2: Tên người chơi
        cardPanel.add(createSectionLabel("TÊN NGƯỜI CHƠI"));
        cardPanel.add(Box.createVerticalStrut(4));
        JPanel namesRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        namesRow.setOpaque(false);

        lblPlayer1Title = new JLabel("Tên bạn:");
        lblPlayer1Title.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPlayer1Title.setForeground(TEXT_MUTED);

        txtPlayer1 = createTextField("Người chơi");

        lblPlayer2Title = new JLabel("Người 2:");
        lblPlayer2Title.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPlayer2Title.setForeground(TEXT_MUTED);
        lblPlayer2Title.setVisible(false);

        txtPlayer2 = createTextField("Người chơi 2");
        txtPlayer2.setVisible(false);

        namesRow.add(lblPlayer1Title);
        namesRow.add(txtPlayer1);
        namesRow.add(Box.createHorizontalStrut(10));
        namesRow.add(lblPlayer2Title);
        namesRow.add(txtPlayer2);
        cardPanel.add(namesRow);

        cardPanel.add(Box.createVerticalStrut(10));

        // Row 3: Độ khó AI
        diffRowPanel = new JPanel();
        diffRowPanel.setOpaque(false);
        diffRowPanel.setLayout(new BoxLayout(diffRowPanel, BoxLayout.Y_AXIS));

        diffRowPanel.add(createSectionLabel("ĐỘ KHÓ AI"));
        diffRowPanel.add(Box.createVerticalStrut(4));
        JPanel diffRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        diffRow.setOpaque(false);

        PillButton btnEasy = new PillButton("Dễ", UiIcons.createDotIcon(ACCENT_GREEN, 8), false);
        PillButton btnMed  = new PillButton("Vừa", UiIcons.createDotIcon(ACCENT_AMBER, 8), false);
        PillButton btnHard = new PillButton("Khó", UiIcons.createDotIcon(ACCENT_RED, 8), true);

        diffButtons.add(btnEasy);
        diffButtons.add(btnMed);
        diffButtons.add(btnHard);

        btnEasy.addActionListener(e -> setDifficulty(AIDifficulty.EASY));
        btnMed.addActionListener(e  -> setDifficulty(AIDifficulty.MEDIUM));
        btnHard.addActionListener(e -> setDifficulty(AIDifficulty.HARD));

        diffRow.add(btnEasy);
        diffRow.add(btnMed);
        diffRow.add(btnHard);
        diffRowPanel.add(diffRow);
        cardPanel.add(diffRowPanel);

        cardPanel.add(Box.createVerticalStrut(10));

        // Row 4: Lượt đi trước
        cardPanel.add(createSectionLabel("LƯỢT ĐI TRƯỚC"));
        cardPanel.add(Box.createVerticalStrut(4));
        JPanel turnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        turnRow.setOpaque(false);

        PillButton btnTurn1 = new PillButton("Bạn đi trước (X)", UiIcons.createSwordsIcon(13), true);
        PillButton btnTurn2 = new PillButton("Máy đi trước (X)", UiIcons.createRobotIcon(13), false);
        turnButtons.add(btnTurn1);
        turnButtons.add(btnTurn2);

        btnTurn1.addActionListener(e -> setFirstTurn(false));
        btnTurn2.addActionListener(e -> setFirstTurn(true));

        turnRow.add(btnTurn1);
        turnRow.add(btnTurn2);
        cardPanel.add(turnRow);

        cardPanel.add(Box.createVerticalStrut(10));

        // Row 5: Luật chiến thắng
        cardPanel.add(createSectionLabel("LUẬT CHIẾN THẮNG"));
        cardPanel.add(Box.createVerticalStrut(4));
        JPanel ruleRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        ruleRow.setOpaque(false);

        PillButton btnRuleFree = new PillButton("Gomoku tự do (≥5 thắng)", true);
        PillButton btnRuleBlock = new PillButton("Chặn 2 đầu (Luật Caro VN)", false);
        ruleButtons.add(btnRuleFree);
        ruleButtons.add(btnRuleBlock);

        btnRuleFree.addActionListener(e -> setRuleBlockTwoEnds(false));
        btnRuleBlock.addActionListener(e -> setRuleBlockTwoEnds(true));

        ruleRow.add(btnRuleFree);
        ruleRow.add(btnRuleBlock);
        cardPanel.add(ruleRow);

        cardPanel.add(Box.createVerticalStrut(10));

        // Row 6: Kích thước bàn cờ
        cardPanel.add(createSectionLabel("KÍCH THƯỚC BÀN CỜ"));
        cardPanel.add(Box.createVerticalStrut(4));
        JPanel sizeRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        sizeRow.setOpaque(false);

        int[] sizes = {12, 15, 18, 20};
        for (int s : sizes) {
            String txt = s == 15 ? "15 × 15 (Chuẩn)" : (s + " × " + s);
            PillButton btnS = new PillButton(txt, s == selectedSize);
            sizeButtons.add(btnS);
            final int thisSize = s;
            btnS.addActionListener(e -> setBoardSize(thisSize));
            sizeRow.add(btnS);
        }
        cardPanel.add(sizeRow);

        add(cardPanel);
        add(Box.createVerticalStrut(18));

        // --- 3. ACTION BUTTONS ---
        JButton btnStart = new JButton("BẮT ĐẦU VÁN CỜ", UiIcons.createPlayIcon(12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, getBackground(), getWidth(), 0, getBackground().darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Viền sáng tinh tế
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnStart.setHorizontalTextPosition(SwingConstants.LEFT);
        btnStart.setIconTextGap(10);
        btnStart.setBackground(ACCENT_GREEN);
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStart.setBorderPainted(false);
        btnStart.setContentAreaFilled(false);
        btnStart.setFocusPainted(false);
        btnStart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnStart.setMaximumSize(new Dimension(300, 46));
        btnStart.setPreferredSize(new Dimension(300, 46));
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.addActionListener(e -> {
            String p1 = txtPlayer1.getText().trim();
            String p2 = txtPlayer2.getText().trim();
            if (p1.isEmpty()) p1 = (selectedMode == GameMode.PVE) ? "Người chơi" : "Người chơi 1";
            if (p2.isEmpty()) p2 = (selectedMode == GameMode.PVE) ? "Máy AI" : "Người chơi 2";
            callback.onStartGame(selectedSize, selectedMode, selectedDiff, selectedAiFirst, p1, p2, selectedBlockTwoEnds);
        });

        btnStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnStart.setBackground(new Color(20, 200, 140)); }
            @Override
            public void mouseExited(MouseEvent e)  { btnStart.setBackground(ACCENT_GREEN); }
        });

        JButton btnQuit = new JButton("Thoát trò chơi") {
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
        btnQuit.setBackground(new Color(255, 255, 255, 12));
        btnQuit.setForeground(TEXT_MUTED);
        btnQuit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnQuit.setBorderPainted(false);
        btnQuit.setContentAreaFilled(false);
        btnQuit.setFocusPainted(false);
        btnQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuit.setMaximumSize(new Dimension(140, 32));
        btnQuit.setPreferredSize(new Dimension(140, 32));
        btnQuit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuit.addActionListener(e -> System.exit(0));

        btnQuit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnQuit.setBackground(new Color(255, 255, 255, 25));
                btnQuit.setForeground(TEXT_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnQuit.setBackground(new Color(255, 255, 255, 12));
                btnQuit.setForeground(TEXT_MUTED);
            }
        });

        add(btnStart);
        add(Box.createVerticalStrut(8));
        add(btnQuit);
        add(Box.createVerticalGlue());
    }

    private JTextField createTextField(String defaultValue) {
        JTextField tf = new JTextField(defaultValue);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(new Color(15, 23, 42));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        tf.setPreferredSize(new Dimension(140, 28));
        return tf;
    }

    private void setGameMode(GameMode mode) {
        this.selectedMode = mode;
        modeButtons.get(0).setActive(mode == GameMode.PVE);
        modeButtons.get(1).setActive(mode == GameMode.PVP);

        boolean isPve = mode == GameMode.PVE;
        diffRowPanel.setVisible(isPve);
        for (PillButton b : diffButtons) {
            b.setEnabled(isPve);
        }

        if (isPve) {
            lblPlayer1Title.setText("Tên bạn:");
            lblPlayer2Title.setVisible(false);
            txtPlayer2.setVisible(false);
            txtPlayer1.setText("Người chơi");
            turnButtons.get(0).setText("Bạn đi trước (X)");
            turnButtons.get(0).setIcon(UiIcons.createSwordsIcon(13));
            turnButtons.get(1).setText("Máy đi trước (X)");
            turnButtons.get(1).setIcon(UiIcons.createRobotIcon(13));
        } else {
            lblPlayer1Title.setText("Người 1 (X/O):");
            lblPlayer2Title.setVisible(true);
            txtPlayer2.setVisible(true);
            txtPlayer1.setText("Người chơi 1");
            txtPlayer2.setText("Người chơi 2");
            turnButtons.get(0).setText("Người chơi 1 đi trước (X)");
            turnButtons.get(0).setIcon(UiIcons.createUserIcon(13));
            turnButtons.get(1).setText("Người chơi 2 đi trước (X)");
            turnButtons.get(1).setIcon(UiIcons.createUserIcon(13));
        }
        revalidate();
        repaint();
    }

    private void setRuleBlockTwoEnds(boolean blockTwoEnds) {
        this.selectedBlockTwoEnds = blockTwoEnds;
        ruleButtons.get(0).setActive(!blockTwoEnds);
        ruleButtons.get(1).setActive(blockTwoEnds);
    }

    private void setDifficulty(AIDifficulty diff) {
        this.selectedDiff = diff;
        diffButtons.get(0).setActive(diff == AIDifficulty.EASY);
        diffButtons.get(1).setActive(diff == AIDifficulty.MEDIUM);
        diffButtons.get(2).setActive(diff == AIDifficulty.HARD);
    }

    private void setFirstTurn(boolean aiOrPlayer2First) {
        this.selectedAiFirst = aiOrPlayer2First;
        turnButtons.get(0).setActive(!aiOrPlayer2First);
        turnButtons.get(1).setActive(aiOrPlayer2First);
    }

    private void setBoardSize(int size) {
        this.selectedSize = size;
        int[] sizes = {12, 15, 18, 20};
        for (int i = 0; i < sizes.length; i++) {
            sizeButtons.get(i).setActive(sizes[i] == size);
        }
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Nút bấm dạng Pill / Segmented Tab mượt mà.
     */
    private static class PillButton extends JButton {
        private boolean active;
        private static final Color ACTIVE_BG   = new Color(37, 99, 235);
        private static final Color INACTIVE_BG = new Color(255, 255, 255, 10);
        private static final Color HOVER_BG    = new Color(255, 255, 255, 20);

        public PillButton(String text, boolean active) {
            this(text, null, active);
        }

        public PillButton(String text, javax.swing.Icon icon, boolean active) {
            super(text, icon);
            this.active = active;
            setIconTextGap(7);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            updateStyle();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled() && !PillButton.this.active) {
                        setBackground(HOVER_BG);
                        repaint();
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        updateStyle();
                        repaint();
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            updateStyle();
            repaint();
        }

        private void updateStyle() {
            if (!isEnabled()) {
                setBackground(new Color(255, 255, 255, 4));
                setForeground(new Color(71, 85, 105));
                setFont(getFont().deriveFont(Font.PLAIN));
            } else if (active) {
                setBackground(ACTIVE_BG);
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setBackground(INACTIVE_BG);
                setForeground(new Color(203, 213, 225));
                setFont(getFont().deriveFont(Font.PLAIN));
            }
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            updateStyle();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            if (active && isEnabled()) {
                g2.setColor(new Color(96, 165, 250, 180));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            } else if (isEnabled()) {
                g2.setColor(new Color(255, 255, 255, 18));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 20, 32);
        }
    }
}
