package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.GameMode;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Cửa sổ chính của ứng dụng Cờ Caro.
 * Sử dụng CardLayout để chuyển đổi giữa màn hình Menu chính và màn hình Game.
 */
public class CaroFrame extends JFrame implements GameController.GameStateListener {
    private static final String CARD_MENU = "MENU";
    private static final String CARD_GAME = "GAME";

    private final GameController controller;
    private final BoardPanel boardPanel;
    private final ControlPanel controlPanel;
    private final MenuPanel menuPanel;
    private final CardLayout cardLayout;
    private final JPanel rootPanel;

    public CaroFrame() {
        super("Game Cờ Caro (Gomoku) - Đồ án cơ sở | k24CSE");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        this.controller = new GameController();
        this.boardPanel = new BoardPanel(controller);
        this.controlPanel = new ControlPanel(controller, this::showMenu);

        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);

        // Màn hình Menu
        this.menuPanel = new MenuPanel((boardSize, mode, diff, aiFirst) -> {
            controller.startNewGame(boardSize, mode, diff, aiFirst);
            cardLayout.show(rootPanel, CARD_GAME);
            setMinimumSize(new Dimension(800, 640));
            pack();
            setLocationRelativeTo(null);
        });

        // Màn hình Game
        JPanel gamePanel = new JPanel(new java.awt.BorderLayout());
        JScrollPane boardScrollPane = new JScrollPane(boardPanel);
        boardScrollPane.setBorder(null);
        boardScrollPane.getViewport().setBackground(new java.awt.Color(15, 23, 42));
        gamePanel.add(boardScrollPane, java.awt.BorderLayout.CENTER);
        gamePanel.add(controlPanel, java.awt.BorderLayout.EAST);

        rootPanel.add(menuPanel, CARD_MENU);
        rootPanel.add(gamePanel, CARD_GAME);

        controller.addListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        cardLayout.show(rootPanel, CARD_MENU);

        setMinimumSize(new Dimension(620, 580));
        pack();
        setLocationRelativeTo(null);
    }

    public void showMenu() {
        cardLayout.show(rootPanel, CARD_MENU);
        setMinimumSize(new Dimension(620, 580));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onMoveMade(Move move, WinResult result) {
        boardPanel.setLastMove(move);

        if (result.hasWinner()) {
            boardPanel.setWinningPoints(result.getWinningLine());
            String winnerName = controller.getCurrentTurn().getName();
            boolean isX = result.getWinner() == CellState.X;
            controlPanel.updateTurnDisplay("THẮNG: " + winnerName, isX);

            String[] options = {"🔄 Đấu lại", "🏠 Menu chính"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "🎉 CHIẾN THẮNG!\n" + winnerName + " đã tạo thành chuỗi 5 quân liên tiếp!",
                "Kết thúc ván đấu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
            );
            if (choice == 0) {
                controller.rematch();
            } else {
                showMenu();
            }
        } else if (result.isDraw()) {
            controlPanel.updateTurnDisplay("HÒA CỜ", true);
            String[] options = {"🔄 Đấu lại", "🏠 Menu chính"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Bàn cờ đã đầy mà không ai thắng.\nKết quả ván đấu: HÒA!",
                "Kết thúc ván đấu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
            );
            if (choice == 0) {
                controller.rematch();
            } else {
                showMenu();
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
        pack();
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
