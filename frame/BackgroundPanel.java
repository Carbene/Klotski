package frame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                this.backgroundImage = null;
                return;
            }
            File imageFile = new File(imagePath);
            this.backgroundImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            this.backgroundImage = null;
        }
    }

    public BackgroundPanel(Image image) {
        this.backgroundImage = image;
    }

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