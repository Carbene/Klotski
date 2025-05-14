package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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
    private transient MusicPlayer musicPlayer;

    /**
     * 有参构造器，设置用户界面
     * @param loginFrame 上级界面，方便回退
     * TODO: 也许可以公开loginFrame的方法进行不传入？
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
     * TODO: 也许应该公开，全局调用？
     */
    private void shiftPlayStatus() {
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
    //TODO:这里应当加入一个观战按钮
    private void setLogoutButton() {
        JButton logoutButton = new JButton("Log Out");
        styleBtn(logoutButton);
        buttonPanel.add(logoutButton);
        logoutButton.addActionListener(e -> {
            musicPlayer.playSoundEffectPressingButton();
            System.out.println("Logging out.");
            this.dispose();
            loginFrame.setVisible(true);
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
        setBGMButton();
        setLogoutButton();
        setSpectatorButton();
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
     *设置观战按钮
     * TODO: 这里应当实现一个观战按钮的功能
     */
    private void setSpectatorButton() {
        //在UserInterfaceFrame添加观战按钮：
        JButton spectateBtn = new JButton("观战");
        styleBtn(spectateBtn);
        buttonPanel.add(spectateBtn);
        spectateBtn.addActionListener(e -> {
            try {
                new SpectatorFrame().setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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
    // TODO:这里应当加入一个观战的具体实现

    /**
     * 获取当前用户，也许用户应该是全局唯一的
     * TODO: 优化用户类相关的代码
     * @return 当前用户
     */
    public User getUser() {
        return user;
    }

    /**
     * 传递音乐播放器对象
     * TODO: 似乎应该是唯一的
     * @return 获得音乐播放器对象
     */
    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}