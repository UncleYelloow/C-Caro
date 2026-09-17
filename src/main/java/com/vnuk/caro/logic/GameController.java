package com.vnuk.caro.logic;

import com.vnuk.caro.model.AIDifficulty;
import com.vnuk.caro.model.AIPlayer;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.GameHistory;
import com.vnuk.caro.model.GameMode;
import com.vnuk.caro.model.HumanPlayer;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.Player;
import com.vnuk.caro.model.WinResult;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Bộ điều khiển trò chơi (Controller trong mô hình MVC).
 * Quản lý trạng thái ván cờ, luân chuyển lượt chơi, kết nối giữa View, Model và AI.
 */
public class GameController {
    public interface GameStateListener {
        void onMoveMade(Move move, WinResult result);
        void onGameReset();
        void onUndoMade();
        void onStatusMessage(String message);
    }

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentTurn;
    private GameMode mode;
    private AIDifficulty difficulty;
    private boolean isAiFirst;
    private final GameHistory history;
    private WinResult lastResult;
    private boolean isAiThinking;
    private final List<GameStateListener> listeners;

    private boolean ruleBlockTwoEnds;
    private String customPlayer1Name;
    private String customPlayer2Name;

    private int scoreX = 0;
    private int scoreO = 0;
    private int scoreDraw = 0;

    public GameController() {
        this.history = new GameHistory();
        this.listeners = new ArrayList<>();
        this.board = new Board(Board.DEFAULT_SIZE);
        this.mode = GameMode.PVE;
        this.difficulty = AIDifficulty.HARD;
        this.isAiFirst = false;
        this.ruleBlockTwoEnds = false;
        this.customPlayer1Name = "Người chơi 1";
        this.customPlayer2Name = "Người chơi 2";
        this.lastResult = WinResult.continuePlaying();
        this.isAiThinking = false;
        setupPlayers();
    }

    public void addListener(GameStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void startNewGame(int boardSize, GameMode mode, AIDifficulty diff) {
        startNewGame(boardSize, mode, diff, false, null, null, false);
    }

    public void startNewGame(int boardSize, GameMode mode, AIDifficulty diff, boolean aiFirst) {
        startNewGame(boardSize, mode, diff, aiFirst, null, null, false);
    }

    public void startNewGame(int boardSize, GameMode mode, AIDifficulty diff, boolean aiFirst,
                             String p1Name, String p2Name, boolean blockTwoEnds) {
        this.board = new Board(boardSize);
        this.mode = mode;
        this.difficulty = diff;
        this.isAiFirst = aiFirst;
        this.customPlayer1Name = p1Name;
        this.customPlayer2Name = p2Name;
        this.ruleBlockTwoEnds = blockTwoEnds;
        this.history.clear();
        this.lastResult = WinResult.continuePlaying();
        this.isAiThinking = false;
        setupPlayers();

        for (GameStateListener l : listeners) {
            l.onGameReset();
            l.onStatusMessage("Ván mới bắt đầu! Lượt của: " + currentTurn.getName());
        }

        if (mode == GameMode.PVE && currentTurn instanceof AIPlayer) {
            triggerAIMove();
        }
    }

    public void rematch() {
        startNewGame(board.getSize(), mode, difficulty, isAiFirst, customPlayer1Name, customPlayer2Name, ruleBlockTwoEnds);
    }

    private void setupPlayers() {
        String p1 = (customPlayer1Name != null && !customPlayer1Name.trim().isEmpty())
                ? customPlayer1Name.trim() : "Người chơi 1";
        String p2 = (customPlayer2Name != null && !customPlayer2Name.trim().isEmpty())
                ? customPlayer2Name.trim() : "Người chơi 2";

        if (mode == GameMode.PVP) {
            if (isAiFirst) {
                // Người chơi 2 đi trước (X)
                this.player1 = new HumanPlayer(p2 + " (X)", CellState.X);
                this.player2 = new HumanPlayer(p1 + " (O)", CellState.O);
            } else {
                this.player1 = new HumanPlayer(p1 + " (X)", CellState.X);
                this.player2 = new HumanPlayer(p2 + " (O)", CellState.O);
            }
        } else {
            String humanName = (customPlayer1Name != null && !customPlayer1Name.trim().isEmpty())
                    ? customPlayer1Name.trim() : "Người chơi";
            if (isAiFirst) {
                this.player1 = new AIPlayer("Máy AI (X)", CellState.X, difficulty);
                this.player2 = new HumanPlayer(humanName + " (O)", CellState.O);
            } else {
                this.player1 = new HumanPlayer(humanName + " (X)", CellState.X);
                this.player2 = new AIPlayer("Máy AI (O)", CellState.O, difficulty);
            }
        }
        this.currentTurn = player1;
    }

    public boolean handleHumanMove(int r, int c) {
        if (isAiThinking || lastResult.isOver()) {
            return false;
        }

        if (!(currentTurn instanceof HumanPlayer)) {
            return false;
        }

        if (!board.isValid(r, c) || board.getCell(r, c) != CellState.EMPTY) {
            return false;
        }

        executeMove(r, c, currentTurn.getSymbol());

        // Nếu chơi với AI và ván cờ chưa kết thúc, kích hoạt lượt của AI
        if (!lastResult.isOver() && mode == GameMode.PVE && currentTurn instanceof AIPlayer) {
            triggerAIMove();
        }

        return true;
    }

    private void executeMove(int r, int c, CellState symbol) {
        board.setCell(r, c, symbol);
        Move move = new Move(r, c, symbol);
        history.push(move);

        lastResult = WinChecker.checkWin(board, r, c, ruleBlockTwoEnds);

        if (lastResult.hasWinner()) {
            if (lastResult.getWinner() == CellState.X) {
                scoreX++;
            } else {
                scoreO++;
            }
        } else if (lastResult.isDraw()) {
            scoreDraw++;
        }

        for (GameStateListener l : listeners) {
            l.onMoveMade(move, lastResult);
        }

        if (lastResult.hasWinner()) {
            notifyStatus("Chúc mừng! " + currentTurn.getName() + " đã chiến thắng!");
        } else if (lastResult.isDraw()) {
            notifyStatus("Bàn cờ đã đầy! Ván cờ Hòa!");
        } else {
            switchTurn();
            notifyStatus("Lượt chơi tiếp theo: " + currentTurn.getName());
        }
    }

    private void triggerAIMove() {
        isAiThinking = true;
        notifyStatus("Máy AI (" + difficulty.getDisplayName() + ") đang suy nghĩ...");

        // Chạy tính toán AI trên luồng riêng để không làm đơ giao diện Swing
        new Thread(() -> {
            try {
                Thread.sleep(300); // Tạo độ trễ nhẹ tự nhiên
                Move aiMove = currentTurn.makeMove(board);
                SwingUtilities.invokeLater(() -> {
                    isAiThinking = false;
                    if (aiMove != null && !lastResult.isOver()) {
                        executeMove(aiMove.getRow(), aiMove.getCol(), currentTurn.getSymbol());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isAiThinking = false;
            }
        }).start();
    }

    public boolean undoMove() {
        if (isAiThinking || history.isEmpty()) {
            return false;
        }

        boolean wasGameOver = lastResult.isOver();
        CellState winner = lastResult.hasWinner() ? lastResult.getWinner() : null;

        // Nếu ván cờ đã kết thúc (thắng hoặc hòa), việc undo sẽ thu hồi kết quả và trừ lại điểm số đã cộng
        if (lastResult.hasWinner()) {
            if (winner == CellState.X) {
                scoreX = Math.max(0, scoreX - 1);
            } else {
                scoreO = Math.max(0, scoreO - 1);
            }
        } else if (lastResult.isDraw()) {
            scoreDraw = Math.max(0, scoreDraw - 1);
        }

        if (mode == GameMode.PVP) {
            Move lastMove = history.pop();
            if (lastMove != null) {
                board.clearCell(lastMove.getRow(), lastMove.getCol());
                // Nếu ván cờ chưa kết thúc, executeMove đã gọi switchTurn(), do đó undo cần đảo lại lượt.
                // Nếu ván cờ đã kết thúc do chiến thắng, executeMove chưa switchTurn(),
                // nên currentTurn hiện tại chính là người vừa thắng, giữ nguyên để người đó đánh lại.
                if (!wasGameOver) {
                    switchTurn();
                }
            }
        } else {
            // Chế độ PvE: hoàn tác để trả lại lượt đi cho người chơi
            if (isAiFirst) {
                // AI đi trước (X), Người chơi là player2 (O)
                if (wasGameOver && winner == CellState.O) {
                    // Người chơi thắng ở lượt của mình -> chỉ cần thu hồi 1 nước thắng của người chơi
                    Move humanMove = history.pop();
                    if (humanMove != null) board.clearCell(humanMove.getRow(), humanMove.getCol());
                    currentTurn = player2;
                } else if (history.size() >= 2) {
                    Move aiMove = history.pop();
                    board.clearCell(aiMove.getRow(), aiMove.getCol());
                    Move humanMove = history.pop();
                    board.clearCell(humanMove.getRow(), humanMove.getCol());
                    currentTurn = player2;
                }
            } else {
                // Người đi trước (X), AI là player2 (O)
                if (wasGameOver && winner == CellState.X) {
                    // Người chơi thắng ở lượt của mình -> chỉ cần thu hồi 1 nước thắng của người chơi
                    Move humanMove = history.pop();
                    if (humanMove != null) board.clearCell(humanMove.getRow(), humanMove.getCol());
                    currentTurn = player1;
                } else if (history.size() >= 2) {
                    Move aiMove = history.pop();
                    board.clearCell(aiMove.getRow(), aiMove.getCol());
                    Move humanMove = history.pop();
                    board.clearCell(humanMove.getRow(), humanMove.getCol());
                    currentTurn = player1;
                } else if (history.size() == 1) {
                    Move onlyMove = history.pop();
                    board.clearCell(onlyMove.getRow(), onlyMove.getCol());
                    currentTurn = player1;
                }
            }
        }

        lastResult = WinResult.continuePlaying();

        for (GameStateListener l : listeners) {
            l.onUndoMade();
            l.onStatusMessage("Đã hoàn tác nước cờ. Lượt của: " + currentTurn.getName());
        }
        return true;
    }

    private void switchTurn() {
        currentTurn = (currentTurn == player1) ? player2 : player1;
    }

    private void notifyStatus(String msg) {
        for (GameStateListener l : listeners) {
            l.onStatusMessage(msg);
        }
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public GameMode getMode() {
        return mode;
    }

    public AIDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean isAiFirst() {
        return isAiFirst;
    }

    public void setDifficulty(AIDifficulty difficulty) {
        if (difficulty == null) return;
        this.difficulty = difficulty;
        if (player1 instanceof AIPlayer) {
            ((AIPlayer) player1).setDifficulty(difficulty);
        }
        if (player2 instanceof AIPlayer) {
            ((AIPlayer) player2).setDifficulty(difficulty);
        }
        notifyStatus("Đã đổi độ khó AI thành: " + difficulty.getDisplayName());
    }

    public GameHistory getHistory() {
        return history;
    }

    public WinResult getLastResult() {
        return lastResult;
    }

    public boolean isAiThinking() {
        return isAiThinking;
    }

    public int getScoreX() {
        return scoreX;
    }

    public int getScoreO() {
        return scoreO;
    }

    public int getScoreDraw() {
        return scoreDraw;
    }

    public void resetScores() {
        this.scoreX = 0;
        this.scoreO = 0;
        this.scoreDraw = 0;
    }

    public boolean isRuleBlockTwoEnds() {
        return ruleBlockTwoEnds;
    }

    public void setRuleBlockTwoEnds(boolean ruleBlockTwoEnds) {
        this.ruleBlockTwoEnds = ruleBlockTwoEnds;
    }

    public String getCustomPlayer1Name() {
        return customPlayer1Name;
    }

    public String getCustomPlayer2Name() {
        return customPlayer2Name;
    }
}
