package frame;
import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
/**
 * 这是一个观战界面，用于展示游戏的状态
 * TODO: 实现观战功能
 * TODO: 实现观战界面的样式
 * TODO: 实现观战界面的功能
 * TODO: 实现观战界面的网络通信
 * TODO: 实现观战界面的聊天
 */
public class SpectatorFrame extends JFrame {
    private Socket socket;

    public SpectatorFrame() throws IOException {
        socket = new Socket("localhost", 8080);
        new Thread(this::receiveUpdates).start();
    }

    private void receiveUpdates() {
        while (true) {

        }
    }
}


