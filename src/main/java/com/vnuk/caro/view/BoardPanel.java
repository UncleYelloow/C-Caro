package com.vnuk.caro.view;

import com.vnuk.caro.logic.GameController;
import com.vnuk.caro.model.Board;
import com.vnuk.caro.model.CellState;
import com.vnuk.caro.model.Move;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
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
 * Bàn cờ Caro - Dark Theme hiện đại.
 * Lưới gỗ nâu-tối, quân X đỏ tươi, quân O xanh lam.
 * Hiệu ứng: hover, last-move highlight, winning line glow.
 */
public class BoardPanel extends JPanel {
    private final GameController controller;
    private int cellSize = 38;
    private final int margin = 32;

    private Point hoverCell = null;
    private Move lastMove = null;
    private List<Point> winningPoints = Collections.emptyList();

    // Dark board palette
    private static final Color BG_OUTER      = new Color(15, 23, 42);
    private static final Color BOARD_BG      = new Color(31, 43, 28);   // Nền gỗ xanh đậm
    private static final Color BOARD_BG2     = new Color(24, 36, 20);
    private static final Color GRID_COLOR    = new Color(80, 110, 65, 200);
    private static final Color BORDER_COLOR  = new Color(100, 140, 80);
    private static final Color DOT_COLOR     = new Color(120, 160, 100, 180);
    private static final Color HOVER_COLOR   = new Color(255, 255, 255, 28);
    private static final Color LAST_MOVE_BG  = new Color(250, 204, 21, 55);
    private static final Color LAST_MOVE_BR  = new Color(250, 204, 21, 200);
    private static final Color WIN_BG        = new Color(34, 197, 94, 60);
    private static final Color WIN_BORDER    = new Color(34, 197, 94, 220);
    private static final Color X_COLOR       = new Color(248, 80, 80);
    private static final Color X_SHADOW      = new Color(239, 68, 68, 60);
    private static final Color O_COLOR       = new Color(74, 158, 255);
    private static final Color O_SHADOW      = new Color(59, 130, 246, 60);

    // Các điểm sao trên bàn cờ (star points)
    private static final int[][] STAR_OFFSETS_15 = {{3,3},{3,11},{11,3},{11,11},{7,7}};
    private static final int[][] STAR_OFFSETS_19 = {{3,3},{3,9},{3,15},{9,3},{9,9},{9,15},{15,3},{15,9},{15,15}};

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setBackground(BG_OUTER);
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
        int size = b.getSize();
        // Điều chỉnh cellSize theo kích thước bàn cờ
        if (size <= 12) cellSize = 44;
        else if (size <= 15) cellSize = 40;
        else if (size <= 18) cellSize = 36;
        else cellSize = 32;
        int boardPixelSize = margin * 2 + (size - 1) * cellSize;
        setPreferredSize(new Dimension(boardPixelSize + margin, boardPixelSize + margin));
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

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int cx = margin + c * cellSize;
                int cy = margin + r * cellSize;
                if (Math.abs(px - cx) <= cellSize / 2 && Math.abs(py - cy) <= cellSize / 2) {
                    return new Point(r, c);
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Board board = controller.getBoard();
        int size = board.getSize();
        int boardW = (size - 1) * cellSize;
        int boardH = (size - 1) * cellSize;

        // 1. Nền ngoài (outer background)
        g2.setColor(BG_OUTER);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 2. Bàn cờ gradient gỗ xanh đậm
        GradientPaint woodGrad = new GradientPaint(
            margin, margin, BOARD_BG,
            margin + boardW, margin + boardH, BOARD_BG2
        );
        g2.setPaint(woodGrad);
        g2.fillRoundRect(margin - 14, margin - 14, boardW + 28, boardH + 28, 14, 14);

        // 3. Viền bàn cờ
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(margin - 14, margin - 14, boardW + 28, boardH + 28, 14, 14);

        // 4. Lưới giao điểm (Vẽ LINE thay vì ô)
        g2.setStroke(new BasicStroke(0.9f));
        g2.setColor(GRID_COLOR);
        for (int i = 0; i < size; i++) {
            int pos = margin + i * cellSize;
            g2.drawLine(margin, pos, margin + boardW, pos);
            g2.drawLine(pos, margin, pos, margin + boardH);
        }

        // 5. Viền ngoài bàn cờ dày hơn
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(BORDER_COLOR);
        g2.drawRect(margin, margin, boardW, boardH);

        // 6. Điểm sao (Star points)
        drawStarPoints(g2, size);

        // 7. Tô sáng: Hover
        if (hoverCell != null && board.getCell(hoverCell.x, hoverCell.y) == CellState.EMPTY) {
            int hx = margin + hoverCell.y * cellSize;
            int hy = margin + hoverCell.x * cellSize;
            int half = cellSize / 2;
            g2.setColor(HOVER_COLOR);
            g2.fillRoundRect(hx - half + 1, hy - half + 1, cellSize - 2, cellSize - 2, 6, 6);
        }

        // 8. Tô sáng: Nước đi cuối
        if (lastMove != null) {
            int lx = margin + lastMove.getCol() * cellSize;
            int ly = margin + lastMove.getRow() * cellSize;
            int half = cellSize / 2;
            g2.setColor(LAST_MOVE_BG);
            g2.fillRoundRect(lx - half + 1, ly - half + 1, cellSize - 2, cellSize - 2, 6, 6);
            g2.setColor(LAST_MOVE_BR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(lx - half + 1, ly - half + 1, cellSize - 2, cellSize - 2, 6, 6);
        }

        // 9. Tô sáng: Đường chiến thắng
        for (Point wp : winningPoints) {
            int wx = margin + wp.y * cellSize;
            int wy = margin + wp.x * cellSize;
            int half = cellSize / 2;
            g2.setColor(WIN_BG);
            g2.fillRoundRect(wx - half + 1, wy - half + 1, cellSize - 2, cellSize - 2, 6, 6);
            g2.setColor(WIN_BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(wx - half + 1, wy - half + 1, cellSize - 2, cellSize - 2, 6, 6);
        }

        // 10. Vẽ quân cờ X / O
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                CellState state = board.getCell(r, c);
                if (state != CellState.EMPTY) {
                    drawPiece(g2, r, c, state);
                }
            }
        }

        // 11. Số thứ tự cột/hàng (nhỏ, mờ) - chỉ hiển thị bàn ≤ 15
        if (size <= 15) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(150, 180, 130, 130));
            for (int i = 0; i < size; i++) {
                String label = Integer.toString(i + 1);
                int px = margin + i * cellSize - 3;
                g2.drawString(label, px, margin - 5);
                g2.drawString(label, margin - 18, margin + i * cellSize + 4);
            }
        }
    }

    private void drawStarPoints(Graphics2D g2, int size) {
        g2.setColor(DOT_COLOR);
        int[][] pts = size >= 18 ? STAR_OFFSETS_19 :
                      size >= 15 ? STAR_OFFSETS_15 : null;
        if (pts == null) return;
        for (int[] p : pts) {
            if (p[0] < size && p[1] < size) {
                int cx = margin + p[1] * cellSize;
                int cy = margin + p[0] * cellSize;
                g2.fillOval(cx - 4, cy - 4, 8, 8);
            }
        }
    }

    private void drawPiece(Graphics2D g2, int r, int c, CellState state) {
        int cx = margin + c * cellSize;
        int cy = margin + r * cellSize;
        int radius = (int) (cellSize * 0.33);

        if (state == CellState.X) {
            int pad = (int)(cellSize * 0.26);
            // Bóng đổ mềm
            g2.setColor(X_SHADOW);
            g2.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - radius + 1, cy - radius + 1, cx + radius + 1, cy + radius + 1);
            g2.drawLine(cx + radius + 1, cy - radius + 1, cx - radius + 1, cy + radius + 1);
            // Nét chính
            g2.setColor(X_COLOR);
            g2.setStroke(new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - radius, cy - radius, cx + radius, cy + radius);
            g2.drawLine(cx + radius, cy - radius, cx - radius, cy + radius);
        } else {
            // Bóng đổ mềm
            g2.setColor(O_SHADOW);
            g2.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - radius + 1, cy - radius + 1, radius * 2, radius * 2);
            // Vòng tròn chính
            g2.setColor(O_COLOR);
            g2.setStroke(new BasicStroke(3.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }
}
