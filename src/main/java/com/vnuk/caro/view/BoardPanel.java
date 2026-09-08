package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;
import com.vnuk.caro.model.WinResult;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.List;

/**
 * Giao diện hiển thị bàn cờ Caro.
 * Sử dụng Java Graphics2D với hiệu ứng vẽ hiện đại:
 * - Khử răng cưa (Anti-aliasing)
 * - Hiệu ứng hover ô cờ
 * - Tô sáng nước đi gần nhất (Highlight last move)
 * - Tô sáng đường chiến thắng (Winning line highlight)
 */
public class BoardPanel extends JPanel {
    private final GameController controller;
    private int cellSize = 38;
    private final int margin = 28;

    private Point hoverCell = null;
    private Move lastMove = null;
    private List<Point> winningPoints = Collections.emptyList();

    // Bảng màu hiện đại (Modern Palette)
    private static final Color BG_COLOR = new Color(248, 249, 252);
    private static final Color GRID_COLOR = new Color(203, 213, 225);
    private static final Color BORDER_COLOR = new Color(148, 163, 184);
    private static final Color HOVER_COLOR = new Color(224, 231, 255, 120);
    private static final Color LAST_MOVE_COLOR = new Color(254, 240, 138, 140);
    private static final Color WIN_LINE_COLOR = new Color(34, 197, 94, 180);
    private static final Color X_COLOR = new Color(239, 68, 68);       // Đỏ tươi hiện đại
    private static final Color O_COLOR = new Color(37, 99, 235);       // Xanh dương hiện đại

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setBackground(BG_COLOR);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point cell = getCellFromCoordinates(e.getX(), e.getY());
                if (cell != null && !cell.equals(hoverCell)) {
                    hoverCell = cell;
                    repaint();
                } else if (cell == null && hoverCell != null) {
                    hoverCell = null;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverCell = null;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point cell = getCellFromCoordinates(e.getX(), e.getY());
                if (cell != null) {
                    controller.handleHumanMove(cell.x, cell.y);
                }
            }
        });

        updateDimensions();
    }

    public void updateDimensions() {
        Board b = controller.getBoard();
        int boardPixelSize = margin * 2 + b.getSize() * cellSize;
        setPreferredSize(new Dimension(boardPixelSize, boardPixelSize));
        revalidate();
        repaint();
    }

    public void setLastMove(Move move) {
        this.lastMove = move;
        repaint();
    }

    public void setWinningPoints(List<Point> points) {
        this.winningPoints = points != null ? points : Collections.emptyList();
        repaint();
    }

    public void resetBoardState() {
        this.lastMove = null;
        this.winningPoints = Collections.emptyList();
        this.hoverCell = null;
        updateDimensions();
    }

    private Point getCellFromCoordinates(int px, int py) {
        Board b = controller.getBoard();
        int size = b.getSize();

        int c = (px - margin) / cellSize;
        int r = (py - margin) / cellSize;

        if (r >= 0 && r < size && c >= 0 && c < size) {
            return new Point(r, c);
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Board board = controller.getBoard();
        int size = board.getSize();

        // 1. Vẽ nền và khung lưới
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(margin - 6, margin - 6, size * cellSize + 12, size * cellSize + 12, 16, 16);
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(margin - 6, margin - 6, size * cellSize + 12, size * cellSize + 12, 16, 16);

        // 2. Vẽ lưới ô cờ
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(GRID_COLOR);
        for (int i = 0; i <= size; i++) {
            int pos = margin + i * cellSize;
            g2.drawLine(pos, margin, pos, margin + size * cellSize);
            g2.drawLine(margin, pos, margin + size * cellSize, pos);
        }

        // 3. Tô sáng các ô đặc biệt (Hover, Last move, Win line)
        if (hoverCell != null && board.getCell(hoverCell.x, hoverCell.y) == CellState.EMPTY) {
            int hx = margin + hoverCell.y * cellSize;
            int hy = margin + hoverCell.x * cellSize;
            g2.setColor(HOVER_COLOR);
            g2.fillRect(hx + 1, hy + 1, cellSize - 1, cellSize - 1);
        }

        if (lastMove != null) {
            int lx = margin + lastMove.getCol() * cellSize;
            int ly = margin + lastMove.getRow() * cellSize;
            g2.setColor(LAST_MOVE_COLOR);
            g2.fillRect(lx + 1, ly + 1, cellSize - 1, cellSize - 1);
            g2.setColor(new Color(234, 179, 8));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(lx + 1, ly + 1, cellSize - 2, cellSize - 2);
        }

        for (Point wp : winningPoints) {
            int wx = margin + wp.y * cellSize;
            int wy = margin + wp.x * cellSize;
            g2.setColor(WIN_LINE_COLOR);
            g2.fillRect(wx + 1, wy + 1, cellSize - 1, cellSize - 1);
        }

        // 4. Vẽ các quân cờ X và O
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                CellState state = board.getCell(r, c);
                if (state != CellState.EMPTY) {
                    drawPiece(g2, r, c, state);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2, int r, int c, CellState state) {
        int x = margin + c * cellSize;
        int y = margin + r * cellSize;
        int padding = 9;

        if (state == CellState.X) {
            g2.setColor(X_COLOR);
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + padding, y + padding, x + cellSize - padding, y + cellSize - padding);
            g2.drawLine(x + cellSize - padding, y + padding, x + padding, y + cellSize - padding);
        } else if (state == CellState.O) {
            g2.setColor(O_COLOR);
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + padding, y + padding, cellSize - padding * 2, cellSize - padding * 2);
        }
    }
}
