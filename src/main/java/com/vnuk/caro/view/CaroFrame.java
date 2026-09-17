package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Cửa sổ chính của ứng dụng Cờ Caro (Gomoku).
 * Sử dụng CardLayout để chuyển đổi mượt mà giữa Menu và màn hình Game
 * với kích thước cửa sổ ổn định, không bị co giật hay nhảy vị trí.
 */
public class CaroFrame extends JFrame implements GameController.GameStateListener {
    private static final String CARD_MENU = "MENU";
    private static final String CARD_GAME = "GAME";

    private static final int APP_WIDTH  = 1020;
    private static final int APP_HEIGHT = 740;

    private final GameController controller;
    private final BoardPanel boardPanel;
    private final ControlPanel controlPanel;
    private final MenuPanel menuPanel;
    private final CardLayout cardLayout;
    private final JPanel rootPanel;

    public CaroFrame() {
        super("Cờ Caro (Gomoku) - Đồ án cơ sở | k24CSE");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        this.controller = new GameController();
        this.boardPanel = new BoardPanel(controller);
        this.controlPanel = new ControlPanel(controller, this::showMenu);

        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);

        // Màn hình Menu
        this.menuPanel = new MenuPanel((boardSize, mode, diff, aiFirst, p1Name, p2Name, blockTwoEnds) -> {
            controller.startNewGame(boardSize, mode, diff, aiFirst, p1Name, p2Name, blockTwoEnds);
            cardLayout.show(rootPanel, CARD_GAME);
            boardPanel.updateDimensions();
            boardPanel.revalidate();
            boardPanel.repaint();
        });

        // Màn hình Game
        JPanel gamePanel = new JPanel(new java.awt.BorderLayout());
        JScrollPane boardScrollPane = new JScrollPane(boardPanel);
        boardScrollPane.setBorder(null);
        boardScrollPane.getViewport().setBackground(new java.awt.Color(11, 15, 25));
        gamePanel.add(boardScrollPane, java.awt.BorderLayout.CENTER);
        gamePanel.add(controlPanel, java.awt.BorderLayout.EAST);

        rootPanel.add(menuPanel, CARD_MENU);
        rootPanel.add(gamePanel, CARD_GAME);

        controller.addListener(this);

        // Đăng ký phím tắt tiện ích
        setupKeyboardShortcuts();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        cardLayout.show(rootPanel, CARD_MENU);

        setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
        setMinimumSize(new Dimension(860, 620));
        pack();
        setLocationRelativeTo(null);
    }

    private void setupKeyboardShortcuts() {
        // Ctrl + Z: Hoàn tác
        rootPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK), "undo");
        rootPanel.getActionMap().put("undo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.undoMove();
            }
        });

        // F2: Đấu lại ván hiện tại
        rootPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0), "rematch");
        rootPanel.getActionMap().put("rematch", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.rematch();
            }
        });

        // Ctrl + N hoặc Escape: Trở về Menu chính
        rootPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK), "newgame");
        rootPanel.getActionMap().put("newgame", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showMenu();
            }
        });

        rootPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "escape");
        rootPanel.getActionMap().put("escape", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showMenu();
            }
        });
    }

    public void showMenu() {
        cardLayout.show(rootPanel, CARD_MENU);
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    @Override
    public void onMoveMade(Move move, WinResult result) {
        boardPanel.setLastMove(move);

        if (result.hasWinner()) {
            boardPanel.setWinningPoints(result.getWinningLine());
            String winnerName = controller.getCurrentTurn().getName();
            boolean isX = result.getWinner() == CellState.X;
            controlPanel.updateTurnDisplay("THẮNG: " + winnerName, isX);

            String[] options = {"Đấu lại", "Menu chính"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "CHIẾN THẮNG!\n" + winnerName + " đã tạo thành chuỗi 5 quân liên tiếp!",
                "Kết thúc ván đấu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
            );
            if (choice == 0) {
                SwingUtilities.invokeLater(controller::rematch);
            } else {
                SwingUtilities.invokeLater(this::showMenu);
            }
        } else if (result.isDraw()) {
            controlPanel.updateTurnDisplay("HÒA CỜ", true);
            String[] options = {"Đấu lại", "Menu chính"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "HÒA CỜ!\nBàn cờ đã đầy mà không ai thắng.\nKết quả ván đấu: HÒA!",
                "Kết thúc ván đấu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
            );
            if (choice == 0) {
                SwingUtilities.invokeLater(controller::rematch);
            } else {
                SwingUtilities.invokeLater(this::showMenu);
            }
        } else {
            boolean isX = controller.getCurrentTurn().getSymbol() == CellState.X;
            controlPanel.updateTurnDisplay(controller.getCurrentTurn().getName(), isX);
        }
    }

    @Override
    public void onGameReset() {
        boardPanel.resetBoardState();
        boolean isX = controller.getCurrentTurn().getSymbol() == CellState.X;
        controlPanel.updateTurnDisplay(controller.getCurrentTurn().getName(), isX);
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    @Override
    public void onUndoMade() {
        Move topMove = controller.getHistory().peek();
        boardPanel.setLastMove(topMove);
        boardPanel.setWinningPoints(null);
        boolean isX = controller.getCurrentTurn().getSymbol() == CellState.X;
        controlPanel.updateTurnDisplay(controller.getCurrentTurn().getName(), isX);
        boardPanel.repaint();
    }

    @Override
    public void onStatusMessage(String message) {
        controlPanel.setStatusMessage(message);
    }
}
