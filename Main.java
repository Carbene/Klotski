import frame.LoginFrame;

import javax.swing.*;

/**
 * 主方法，游戏的启动器
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
