package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;

/**
 * Cửa sổ chính của ứng dụng Cờ Caro.
 * Đóng vai trò là View tổng thể kết nối BoardPanel và ControlPanel với GameController.
 */
public class CaroFrame extends JFrame implements GameController.GameStateListener {
    private final GameController controller;
    private final BoardPanel boardPanel;
    private final ControlPanel controlPanel;

    public CaroFrame() {
        super("Game Cờ Caro (Gomoku) - Đồ án cơ sở | k24CSE");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        this.controller = new GameController();
        this.boardPanel = new BoardPanel(controller);
        this.controlPanel = new ControlPanel(controller);

        controller.addListener(this);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane boardScrollPane = new JScrollPane(boardPanel);
        boardScrollPane.setBorder(null);

        add(boardScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
    }

    @Override
    public void onMoveMade(Move move, WinResult result) {
        boardPanel.setLastMove(move);

        if (result.hasWinner()) {
            boardPanel.setWinningPoints(result.getWinningLine());
            String winnerName = controller.getCurrentTurn().getName();
            boolean isX = result.getWinner() == CellState.X;
            controlPanel.updateTurnDisplay("THẮNG: " + winnerName, isX);
            int choice = JOptionPane.showConfirmDialog(
                this,
                "🎉 CHIẾN THẮNG!\n" + winnerName + " đã tạo thành chuỗi 5 quân liên tiếp!\n\nBạn có muốn ĐẤU LẠI ngay không?",
                "Kết thúc ván đấu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                controller.rematch();
            }
        } else if (result.isDraw()) {
            controlPanel.updateTurnDisplay("HÒA CỜ", true);
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Bàn cờ đã đầy mà không ai thắng.\nKết quả ván đấu: HÒA!\n\nBạn có muốn ĐẤU LẠI ngay không?",
                "Kết thúc ván đấu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                controller.rematch();
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
