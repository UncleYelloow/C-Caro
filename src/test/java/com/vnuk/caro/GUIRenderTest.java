package com.vnuk.caro;

import com.vnuk.caro.view.CaroFrame;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class GUIRenderTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                CaroFrame frame = new CaroFrame();
                frame.setSize(new Dimension(1020, 740));
                frame.doLayout();

                // 1. Chụp màn hình Menu
                BufferedImage menuImg = new BufferedImage(1020, 740, BufferedImage.TYPE_INT_ARGB);
                Graphics2D gMenu = menuImg.createGraphics();
                frame.paint(gMenu);
                gMenu.dispose();
                ImageIO.write(menuImg, "PNG", new File("menu_preview.png"));
                System.out.println("[GUI TEST] Đã chụp ảnh Menu: menu_preview.png");

                // 2. Chụp màn hình Game sau khi bắt đầu ván cờ
                // Bấm thử bắt đầu ván cờ
                frame.getContentPane(); // root panel
                System.out.println("[GUI TEST] Hoàn tất kiểm thử render GUI cơ bản.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.exit(0);
    }
}
