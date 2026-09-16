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
import java.awt.FontMetrics;
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
 * Bàn cờ Caro - Thiết kế Dark Cyber Slate hiện đại.
 * Căn giữa linh hoạt, hiển thị tọa độ chuẩn, hiệu ứng Ghost piece khi rê chuột,
 * quân cờ neon rực rỡ và hiệu ứng kết nối chuỗi chiến thắng.
 */
public class BoardPanel extends JPanel {
    private final GameController controller;
    private int cellSize = 38;
    private final int margin = 40;

    private Point hoverCell = null;
    private Move lastMove = null;
    private List<Point> winningPoints = Collections.emptyList();

    // Bảng màu hiện đại (Modern Slate Palette)
    private static final Color BG_CANVAS      = new Color(11, 15, 25);
    private static final Color BOARD_BG_TOP   = new Color(30, 41, 59);
    private static final Color BOARD_BG_BOT   = new Color(15, 23, 42);
    private static final Color BOARD_BORDER   = new Color(51, 65, 85);
    private static final Color BOARD_SHADOW   = new Color(0, 0, 0, 80);

    private static final Color GRID_LINE      = new Color(71, 85, 105, 210);
    private static final Color GRID_BORDER    = new Color(100, 116, 139, 240);
    private static final Color COORD_TEXT     = new Color(148, 163, 184);
    private static final Color STAR_DOT       = new Color(148, 163, 184, 220);

    private static final Color HOVER_SQUARE   = new Color(255, 255, 255, 18);
    private static final Color LAST_MOVE_RING = new Color(245, 158, 11);
    private static final Color LAST_MOVE_BG   = new Color(245, 158, 11, 40);

    private static final Color WIN_BEAM       = new Color(16, 185, 129);
    private static final Color WIN_BG         = new Color(16, 185, 129, 65);

    private static final Color X_COLOR        = new Color(244, 63, 94);
    private static final Color X_GLOW         = new Color(244, 63, 94, 65);
    private static final Color X_HIGHLIGHT    = new Color(254, 205, 211);

    private static final Color O_COLOR        = new Color(6, 182, 212);
    private static final Color O_GLOW         = new Color(6, 182, 212, 65);
    private static final Color O_HIGHLIGHT    = new Color(224, 242, 254);

    // Điểm sao Hoshi
    private static final int[][] STAR_OFFSETS_15 = {{3,3},{3,11},{11,3},{11,11},{7,7}};
    private static final int[][] STAR_OFFSETS_19 = {{3,3},{3,9},{3,15},{9,3},{9,9},{9,15},{15,3},{15,9},{15,15}};

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setBackground(BG_CANVAS);
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
        if (size <= 12) cellSize = 42;
        else if (size <= 15) cellSize = 38;
        else if (size <= 18) cellSize = 34;
        else cellSize = 30;

        int boardPixel = margin * 2 + (size - 1) * cellSize;
        setPreferredSize(new Dimension(boardPixel + 40, boardPixel + 40));
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

    private int getStartX(int boardW) {
        return Math.max(margin, (getWidth() - boardW) / 2);
    }

    private int getStartY(int boardH) {
        return Math.max(margin, (getHeight() - boardH) / 2);
    }

    private Point getCellFromCoordinates(int px, int py) {
        Board b = controller.getBoard();
        int size = b.getSize();
        int boardW = (size - 1) * cellSize;
        int boardH = (size - 1) * cellSize;
        int startX = getStartX(boardW);
        int startY = getStartY(boardH);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int cx = startX + c * cellSize;
                int cy = startY + r * cellSize;
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

        // 1. Toàn bộ nền canvas
        g2.setColor(BG_CANVAS);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Board board = controller.getBoard();
        int size = board.getSize();
        int boardW = (size - 1) * cellSize;
        int boardH = (size - 1) * cellSize;

        int startX = getStartX(boardW);
        int startY = getStartY(boardH);
        int pad = 24;

        // 2. Bóng đổ bàn cờ (Soft Outer Shadow)
        g2.setColor(BOARD_SHADOW);
        g2.fillRoundRect(startX - pad + 4, startY - pad + 6, boardW + pad * 2, boardH + pad * 2, 20, 20);

        // 3. Khối bàn cờ Slate vát bo tròn sang trọng
        GradientPaint boardGrad = new GradientPaint(
            startX, startY, BOARD_BG_TOP,
            startX + boardW, startY + boardH, BOARD_BG_BOT
        );
        g2.setPaint(boardGrad);
        g2.fillRoundRect(startX - pad, startY - pad, boardW + pad * 2, boardH + pad * 2, 18, 18);

        // Viền bàn cờ tinh tế
        g2.setColor(BOARD_BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(startX - pad, startY - pad, boardW + pad * 2, boardH + pad * 2, 18, 18);

        // 4. Tọa độ chữ cái (A, B, C...) và số (1, 2, 3...)
        drawCoordinates(g2, size, startX, startY);

        // 5. Lưới giao điểm Caro
        g2.setColor(GRID_LINE);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i < size; i++) {
            int posC = startX + i * cellSize;
            int posR = startY + i * cellSize;
            g2.drawLine(startX, posR, startX + boardW, posR);
            g2.drawLine(posC, startY, posC, startY + boardH);
        }

        // Khung viền ngoài lưới dày hơn
        g2.setColor(GRID_BORDER);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(startX, startY, boardW, boardH);

        // 6. Điểm sao Hoshi
        drawStarPoints(g2, size, startX, startY);

        // 7. Hiệu ứng Hover & Ghost Piece (Quân cờ mờ xem trước)
        if (hoverCell != null && board.getCell(hoverCell.x, hoverCell.y) == CellState.EMPTY && !controller.isAiThinking() && !controller.getLastResult().isOver()) {
            int hx = startX + hoverCell.y * cellSize;
            int hy = startY + hoverCell.x * cellSize;
            int half = cellSize / 2;

            // Ô sáng nhẹ
            g2.setColor(HOVER_SQUARE);
            g2.fillRoundRect(hx - half + 2, hy - half + 2, cellSize - 4, cellSize - 4, 8, 8);

            // Bóng quân cờ mờ
            CellState previewSymbol = controller.getCurrentTurn().getSymbol();
            drawGhostPiece(g2, hx, hy, previewSymbol);
        }

        // 8. Đánh dấu nước đi gần nhất (Last Move)
        if (lastMove != null) {
            int lx = startX + lastMove.getCol() * cellSize;
            int ly = startY + lastMove.getRow() * cellSize;
            int rad = (int) (cellSize * 0.44);

            g2.setColor(LAST_MOVE_BG);
            g2.fillOval(lx - rad, ly - rad, rad * 2, rad * 2);

            g2.setColor(LAST_MOVE_RING);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(lx - rad, ly - rad, rad * 2, rad * 2);
        }

        // 9. Nối chùm tia chiến thắng (Winning Beam & Stones)
        if (!winningPoints.isEmpty()) {
            drawWinningLine(g2, startX, startY);
        }

        // 10. Vẽ toàn bộ quân cờ đã đánh trên bàn
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                CellState state = board.getCell(r, c);
                if (state != CellState.EMPTY) {
                    drawPiece(g2, startX + c * cellSize, startY + r * cellSize, state);
                }
            }
        }
    }

    private void drawCoordinates(Graphics2D g2, int size, int startX, int startY) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(COORD_TEXT);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < size; i++) {
            // Chữ cái cột (A, B, C...)
            String colLabel = String.valueOf((char) ('A' + i));
            int colX = startX + i * cellSize - fm.stringWidth(colLabel) / 2;
            g2.drawString(colLabel, colX, startY - 8);

            // Số dòng (1, 2, 3...)
            String rowLabel = String.valueOf(i + 1);
            int rowY = startY + i * cellSize + fm.getAscent() / 2 - 1;
            g2.drawString(rowLabel, startX - fm.stringWidth(rowLabel) - 10, rowY);
        }
    }

    private void drawStarPoints(Graphics2D g2, int size, int startX, int startY) {
        g2.setColor(STAR_DOT);
        int[][] pts = size >= 18 ? STAR_OFFSETS_19 : (size >= 15 ? STAR_OFFSETS_15 : null);
        if (pts == null) return;
        for (int[] p : pts) {
            if (p[0] < size && p[1] < size) {
                int cx = startX + p[1] * cellSize;
                int cy = startY + p[0] * cellSize;
                g2.fillOval(cx - 3, cy - 3, 6, 6);
            }
        }
    }

    private void drawPiece(Graphics2D g2, int cx, int cy, CellState state) {
        int radius = (int) (cellSize * 0.33);

        if (state == CellState.X) {
            // Hiệu ứng phát sáng X
            g2.setColor(X_GLOW);
            g2.setStroke(new BasicStroke(6.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - radius, cy - radius, cx + radius, cy + radius);
            g2.drawLine(cx + radius, cy - radius, cx - radius, cy + radius);

            // Nét chính X
            g2.setColor(X_COLOR);
            g2.setStroke(new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - radius, cy - radius, cx + radius, cy + radius);
            g2.drawLine(cx + radius, cy - radius, cx - radius, cy + radius);

            // Điểm sáng nhẹ trung tâm
            g2.setColor(X_HIGHLIGHT);
            g2.fillOval(cx - 2, cy - 2, 4, 4);
        } else if (state == CellState.O) {
            // Hiệu ứng phát sáng O
            g2.setColor(O_GLOW);
            g2.setStroke(new BasicStroke(6.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Nét chính O
            g2.setColor(O_COLOR);
            g2.setStroke(new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Điểm bóng sáng phía trên
            g2.setColor(O_HIGHLIGHT);
            g2.fillOval(cx - radius / 2, cy - radius + 1, 3, 3);
        }
    }

    private void drawGhostPiece(Graphics2D g2, int cx, int cy, CellState state) {
        int radius = (int) (cellSize * 0.30);
        if (state == CellState.X) {
            g2.setColor(new Color(244, 63, 94, 85));
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - radius, cy - radius, cx + radius, cy + radius);
            g2.drawLine(cx + radius, cy - radius, cx - radius, cy + radius);
        } else {
            g2.setColor(new Color(6, 182, 212, 85));
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }

    private void drawWinningLine(Graphics2D g2, int startX, int startY) {
        // Vẽ chùm tia kết nối từ điểm đầu đến điểm cuối
        if (winningPoints.size() >= 2) {
            Point first = winningPoints.get(0);
            Point last = winningPoints.get(winningPoints.size() - 1);
            int x1 = startX + first.y * cellSize;
            int y1 = startY + first.x * cellSize;
            int x2 = startX + last.y * cellSize;
            int y2 = startY + last.x * cellSize;

            // Glow ngoài
            g2.setColor(new Color(16, 185, 129, 90));
            g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);

            // Tia chính
            g2.setColor(WIN_BEAM);
            g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);
        }

        // Tô sáng từng ô chiến thắng
        for (Point wp : winningPoints) {
            int wx = startX + wp.y * cellSize;
            int wy = startY + wp.x * cellSize;
            int rad = (int) (cellSize * 0.44);

            g2.setColor(WIN_BG);
            g2.fillOval(wx - rad, wy - rad, rad * 2, rad * 2);

            g2.setColor(WIN_BEAM);
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawOval(wx - rad, wy - rad, rad * 2, rad * 2);
        }
    }
}
