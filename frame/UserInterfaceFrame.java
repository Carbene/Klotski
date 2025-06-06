package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import logic.LogicController;
import record.*;

import static frame.Style.styleBtn;

/**
 * 这是用户界面的类
 */
public class UserInterfaceFrame extends JFrame {
    private LoginFrame loginFrame;
    private String selectedLevel = null;
    private boolean bgmEnabled = false;
    private User user;
    private BackgroundPanel background;
    private JPanel buttonPanel;
    public transient MusicPlayer musicPlayer;
    private String host = "";
    private Socket socket;

    /**
     * 有参构造器，设置用户界面
     * @param loginFrame 上级界面，方便回退
     * @param user 全局唯一的用户
     */
    public UserInterfaceFrame(LoginFrame loginFrame, User user) {
        this.loginFrame = loginFrame;
        this.user = user;
        this.musicPlayer = new MusicPlayer();

        initializeUserInterface();
        getBackgroundPanel();
        shiftPlayStatus();
    }

    /**
     * 切换背景音乐的播放状态，似乎应该是公开的
     */
    public void shiftPlayStatus() {
        bgmEnabled = !bgmEnabled;
        if (bgmEnabled) {
            musicPlayer.playBGM();
        } else {
            musicPlayer.stopBGM();
        }
    }

    /**
     * 获取退出游戏的按钮
     */
    private void setLogoutButton() {
        JButton logoutButton = new JButton("Log Out");
        styleBtn(logoutButton);
        buttonPanel.add(logoutButton);
        logoutButton.addActionListener(e -> {
            musicPlayer.playSoundEffectPressingButton();
            System.out.println("Logging out.");
            loginFrame.setVisible(true);
            this.dispose();
            musicPlayer.stopBGM();
            musicPlayer = null;
        });
    }

    /**
     * 初始化用户界面
     */
    private void initializeUserInterface() {
        setTitle("Klotski Board Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * 获取背景面板，这是背景图的实现，还有开关的实现
     */
    private void getBackgroundPanel() {
        this.background = new BackgroundPanel("/userInterfaceBackgroundPic.jpg");
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 0));
        setStartGameButton();
        setLoadGameButton();
        setSpectatorButton();
        setViewRankButton();
        setBGMButton();
        setLogoutButton();
        background.add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginFrame.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (!loginFrame.isVisible()) {
                    loginFrame.setVisible(true);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("ESC pressed, logging out.");
                    dispose();
                    loginFrame.setVisible(true);
                }
            }
        });
        setFocusable(true);
    }

    /**
     * 获得开始游戏的按钮
     */
    private void setStartGameButton() {
        JButton startGameButton = new JButton("Start a New Game");
        styleBtn(startGameButton);
        buttonPanel.add(startGameButton);
        startGameButton.addActionListener(e -> {
            musicPlayer.playSoundEffectPressingButton();
            LevelSelectionFrame levelDialog = new LevelSelectionFrame(this,this.user);
            levelDialog.setVisible(true);
        });
    }

    /**
     *设置观战按钮
     */
    private void setSpectatorButton() {
        JButton spectateBtn = new JButton("Spectate a game");
        styleBtn(spectateBtn);
        buttonPanel.add(spectateBtn);
        spectateBtn.addActionListener(e -> {
            getHost();
            try{
                this.socket = new Socket(host,8080);
            }catch (IOException exception){
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null,"Could not connect to server");
                return;
            }
            GameFrame gameFrame = new GameFrame(this,this.musicPlayer,this.socket);
            gameFrame.setVisible(true);
            this.setVisible(false);
        });
    }

    /**
     * 设置查看排行榜的按钮
     */
    private void setViewRankButton() {
        JButton viewRankButton = new JButton("View the Rank");
        styleBtn(viewRankButton);
        buttonPanel.add(viewRankButton);
        viewRankButton.addActionListener(e -> {
            RankFrame rankFrame = new RankFrame(this.musicPlayer);
            rankFrame.setVisible(true);
        });
    }

    /**
     * 获取服务器地址的对话框
     * 这个方法会弹出一个对话框，要求用户输入服务器地址
     */
    private void getHost() {

        JDialog hostDialog = new JDialog(this, "Server host setting", true);
        hostDialog.setLayout(new FlowLayout());
        hostDialog.add(new JLabel("Please input the host:"));
        JTextField textField = new JTextField(15);
        hostDialog.add(textField);
        JButton submmitButton = new JButton("Submit");
        hostDialog.add(submmitButton);
        submmitButton.addActionListener(e -> {
            String input = textField.getText();
            if(input.isEmpty()){
                JOptionPane.showMessageDialog(null,"Please input a valid host");
                textField.setText("");
                return;
            }
            this.host = input;
            hostDialog.dispose();
        });
        hostDialog.setSize(300, 100);
        hostDialog.setLocationRelativeTo(this);
        hostDialog.setVisible(true);
        hostDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    /**
     * 设置加载旧游戏的按钮
     */
    private void setLoadGameButton() {
        JButton loadGameButton = new JButton("Load an Old Game");
        styleBtn(loadGameButton);
        buttonPanel.add(loadGameButton);
        loadGameButton.addActionListener(e -> {
            musicPlayer.playSoundEffectPressingButton();
            this.loadGame();
        });
    }

    /**
     * 获得背景音乐调控的按钮
     */
    private void setBGMButton() {
        JButton bgmButton = new JButton("BGM Setting");
        styleBtn(bgmButton);
        buttonPanel.add(bgmButton);
        bgmButton.addActionListener(e -> {
            musicPlayer.playSoundEffectPressingButton();
            shiftPlayStatus();
        });
    }

    /**
     * 加载游戏的唤起方法，会转入LogicController的具体实现
     */
    private void loadGame() {
        LogicController logicController = LogicController.loadGame(this);
        if(logicController != null) {
            musicPlayer.playSoundEffectPressingButton();
            this.setVisible(false);
            GameFrame gameFrame = new GameFrame(this, logicController,musicPlayer);
            gameFrame.setVisible(true);
            this.setVisible(false);
        }
    }

    private void viewRank(){

    }

    /**
     * 获取当前用户，也许用户应该是全局唯一的
     * @return 当前用户
     */
    public User getUser() {
        return user;
    }

    /**
     * 传递音乐播放器对象
     * @return 获得音乐播放器对象
     */
    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}