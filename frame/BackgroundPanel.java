package frame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 这是工具类便于实现背景图片的绘制
 */
public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    /**
     * 这是一个有参构造器，生成最基础的JPanel
     * @param imagePath 所需要的背景图的地址
     */
    public BackgroundPanel(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                this.backgroundImage = null;
                System.out.println("Image path is null or empty");
                return;
            }
            URL imageURL = getClass().getResource(imagePath);
            if (imageURL == null) {
                this.backgroundImage = null;
                return;
            }
            this.backgroundImage = ImageIO.read(imageURL);
        } catch (IOException e) {
            this.backgroundImage = null;
        }
    }

    /**
     * 这是一个绘制方法的重写，绘制图片
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Background Image Not Found", 20, 30);
        }
    }
}