package com.vnuk.caro.view;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Bộ Icon Vector nhẹ, mượt mà được vẽ trực tiếp bằng Graphics2D.
 * Giải quyết triệt để vấn đề mất glyph (ô vuông trống) của emoji trên Java Swing/Windows.
 */
public final class UiIcons {

    private UiIcons() {}

    private static Color resolveColor(Component c, Color overrideColor) {
        if (overrideColor != null) {
            return overrideColor;
        }
        if (c != null && !c.isEnabled()) {
            return new Color(100, 116, 139); // Slate-500 muted
        }
        return (c != null) ? c.getForeground() : Color.WHITE;
    }

    /**
     * Chấm tròn trạng thái màu sắc nét (Dành cho độ khó: Dễ, Vừa, Khó).
     */
    public static Icon createDotIcon(final Color color, final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paintColor = (c != null && !c.isEnabled()) ? new Color(100, 116, 139) : color;
                g2.setColor(paintColor);
                int yOffset = (getIconHeight() - size) / 2;
                g2.fillOval(x, y + yOffset, size, size);
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size + 2; }

            @Override
            public int getIconHeight() { return size + 2; }
        };
    }

    /**
     * Icon Robot đại diện cho Chế độ AI.
     */
    public static Icon createRobotIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int w = size;
                int h = size;
                int ox = x + 1;
                int oy = y + 1;

                // Ăng-ten
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(ox + w / 2, oy, ox + w / 2, oy + 2);
                g2.fillOval(ox + w / 2 - 1, oy, 3, 3);

                // Đầu Robot
                int headW = w - 4;
                int headH = h - 5;
                g2.drawRoundRect(ox + 2, oy + 3, headW, headH, 3, 3);

                // Tai trái, tai phải
                g2.fillRect(ox, oy + 5, 2, 4);
                g2.fillRect(ox + headW + 2, oy + 5, 2, 4);

                // Mắt
                g2.fillRect(ox + 4, oy + 6, 2, 2);
                g2.fillRect(ox + headW - 2, oy + 6, 2, 2);

                // Miệng
                g2.drawLine(ox + 5, oy + 9, ox + headW - 1, oy + 9);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size + 2; }

            @Override
            public int getIconHeight() { return size + 2; }
        };
    }

    /**
     * Icon Người chơi đơn.
     */
    public static Icon createUserIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int headSize = (int) (size * 0.45);
                int headX = ox + (size - headSize) / 2;

                // Đầu
                g2.fillOval(headX, oy, headSize, headSize);

                // Thân (vai)
                int bodyY = oy + headSize + 2;
                int bodyH = size - (headSize + 2);
                g2.fillRoundRect(ox + 1, bodyY, size - 2, bodyH, 5, 5);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size + 2; }

            @Override
            public int getIconHeight() { return size + 2; }
        };
    }

    /**
     * Icon 2 Người chơi (PvP).
     */
    public static Icon createUsersIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);

                int ox = x + 1;
                int oy = y + 1;

                // Người phụ (phía sau bên phải)
                Color backColor = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) (fg.getAlpha() * 0.55));
                g2.setColor(backColor);
                g2.fillOval(ox + 7, oy, 5, 5);
                g2.fillRoundRect(ox + 5, oy + 6, 8, 7, 4, 4);

                // Người chính (phía trước bên trái)
                g2.setColor(fg);
                g2.fillOval(ox + 1, oy + 2, 6, 6);
                g2.fillRoundRect(ox, oy + 9, 8, 6, 4, 4);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size + 3; }

            @Override
            public int getIconHeight() { return size + 2; }
        };
    }

    /**
     * Icon Kiếm giao đấu (Lượt bạn đi trước).
     */
    public static Icon createSwordsIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int s = size - 2;

                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Thanh kiếm 1
                g2.drawLine(ox + 2, oy + 2, ox + s, oy + s);
                // Cán kiếm 1
                g2.drawLine(ox + 2, oy + 5, ox + 5, oy + 2);

                // Thanh kiếm 2
                g2.drawLine(ox + s, oy + 2, ox + 2, oy + s);
                // Cán kiếm 2
                g2.drawLine(ox + s - 3, oy + 2, ox + s, oy + 5);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size + 2; }

            @Override
            public int getIconHeight() { return size + 2; }
        };
    }

    /**
     * Icon Mũi tên Play (Bắt đầu ván cờ).
     */
    public static Icon createPlayIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int[] xs = {x + 2, x + size - 1, x + 2};
                int[] ys = {y + 2, y + size / 2, y + size - 2};
                g2.fillPolygon(xs, ys, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Ngôi nhà (Về Menu).
     */
    public static Icon createHomeIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int s = size - 2;

                // Mái nhà
                int[] xs = {ox + s / 2, ox, ox + s};
                int[] ys = {oy, oy + s / 2, oy + s / 2};
                g2.fillPolygon(xs, ys, 3);

                // Thân nhà
                int bodyW = s - 4;
                int bodyH = s / 2;
                g2.fillRect(ox + 2, oy + s / 2, bodyW, bodyH);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Hoàn tác (Undo).
     */
    public static Icon createUndoIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int s = size - 2;

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Cung tròn quay lại
                g2.drawArc(ox + 3, oy + 2, s - 3, s - 2, 30, 200);

                // Mũi tên chỉ ngược sang trái
                int[] xs = {ox + 1, ox + 6, ox + 6};
                int[] ys = {oy + 5, oy + 2, oy + 8};
                g2.fillPolygon(xs, ys, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Tải lại / Đấu lại (Rematch / Refresh).
     */
    public static Icon createRefreshIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int s = size - 2;

                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Vòng tròn mở
                g2.drawArc(ox + 1, oy + 1, s - 2, s - 2, 45, 270);

                // Đầu mũi tên
                int[] xs = {ox + s - 3, ox + s + 1, ox + s - 1};
                int[] ys = {oy + 1, oy + 4, oy + 7};
                g2.fillPolygon(xs, ys, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Cài đặt / Thanh trượt cấu hình (Settings).
     */
    public static Icon createSettingsIcon(final int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fg = resolveColor(c, null);
                g2.setColor(fg);

                int ox = x + 1;
                int oy = y + 1;
                int s = size - 2;

                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // 3 thanh trượt hiện đại
                int y1 = oy + 2;
                int y2 = oy + s / 2;
                int y3 = oy + s - 2;

                g2.drawLine(ox, y1, ox + s, y1);
                g2.drawLine(ox, y2, ox + s, y2);
                g2.drawLine(ox, y3, ox + s, y3);

                // Các nút điều chỉnh trên thanh
                g2.fillRect(ox + 3, y1 - 2, 3, 5);
                g2.fillRect(ox + s - 5, y2 - 2, 3, 5);
                g2.fillRect(ox + 5, y3 - 2, 3, 5);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
}
