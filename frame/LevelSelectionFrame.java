package frame;

import view.*;
import record.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 这是难度选择的用户界面，由用户选择界面唤起
 */
public class LevelSelectionFrame extends JDialog {
    private String selectedLevel;
    private String[] levels;
    private JLabel previewLabel;
    private JLabel achievementStepsLabel;
    private JLabel achivementTimeLabel;
    private UserInterfaceFrame userInterfaceFrame;
    private User user;

    /**
     * 一个有参构造器，初始化难度选择界面
     * @param owner 上级界面，这里应该具体化为用户界面
     * @param user 当前玩家
     *TODO: 用户类全局统一公开化？
     */
    public LevelSelectionFrame(Frame owner,User user) {
        super(owner, "Select Level", true);
        this.user = user;
        levels = new String[Level.values().length];
        for(Level level : Level.values()) {
            levels[level.getCODE() - 1] = String.valueOf(level.getCODE());
        }
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.userInterfaceFrame = (UserInterfaceFrame) owner;
        getBackgroundPanel();

    }

    /**
     * 更新视图方法，在难度选择后进行对应的渲染
     */
    private void updatePreview() {
        previewLabel.setText(this.selectedLevel);
        if(!this.user.getId().equals("Visitor")) {
            achievementStepsLabel.setText(User.getBestRecord(user,Integer.parseInt(selectedLevel),0) == 0 ?"Please first play the game" : "Best: " + User.getBestRecord(user,Integer.parseInt(selectedLevel),0) + " Steps");
            achivementTimeLabel.setText(User.getBestRecord(user,Integer.parseInt(selectedLevel),1) == 0 ?"Please first play the game" : "Best: " + User.getBestRecord(user,Integer.parseInt(selectedLevel),1) + " Seconds");
        }else{
            achievementStepsLabel.setText("Please login to see your best record");
            achivementTimeLabel.setText("Please login to see your best record");
        }
    }

    /**
     * 开始游戏的方法，传入一个布尔值，决定是否为计时模式
     * @param isTimed 是否为计时模式
     *TODO: 界面切换逻辑仍然有问题
     */
    private void startGame(boolean isTimed) {
        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first before using the 'Select Level' button.", "Level Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(isTimed){
            this.userInterfaceFrame.setVisible(false);
            GameFrame gameFrame = new GameFrame(this,this.userInterfaceFrame,user,setLevel(Integer.parseInt(selectedLevel)), true,userInterfaceFrame.getMusicPlayer());

            gameFrame.setVisible(true);
            this.setVisible(false);
        }else{
            this.userInterfaceFrame.setVisible(false);
            GameFrame gameFrame = new GameFrame(this,this.userInterfaceFrame, user,setLevel(Integer.parseInt(selectedLevel)), false,userInterfaceFrame.getMusicPlayer());

            gameFrame.setVisible(true);
            this.setVisible(false);
        }
    }

    /**
     * 获取对应代码的地图枚举常量
     * @param code 代码
     * @return 枚举常量
     * TODO: 其实这里本来的用途是为Level赋予了中文名称，但是似乎应为UTF的问题，和英文一同使用没有正常渲染，是否可能可以改进？
     */
    public Level setLevel(int code) {
        for(Level map : Level.values()){
            if(map.getCODE() == code){
                return map;
            }
        }
        return null;
    }

    /**
     * 获取对应的难度列表按钮，左侧的一列
     * @return 难度选择按钮
     */
    private JPanel getLevelListPanel(){
        JPanel levelListPanel = new JPanel();
        levelListPanel.setLayout(new BoxLayout(levelListPanel, BoxLayout.Y_AXIS));
        levelListPanel.setOpaque(false);
        levelListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        for (String level : levels) {
            JButton levelButton = new JButton(level);
            levelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            levelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, levelButton.getPreferredSize().height));
            levelButton.addActionListener(e -> {
                this.selectedLevel = level;
                updatePreview();
                this.userInterfaceFrame.getMusicPlayer().playSoundEffectPressingButton();
            });
            levelListPanel.add(levelButton);
            levelListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        levelListPanel.add(Box.createVerticalGlue());

        return levelListPanel;
    }

    /**
     * 底部的开始游戏按钮，有两种模式
     * @return 游戏开始按钮
     */
    private JPanel getGameStartPanel(){
        JPanel gameStartPanel = new JPanel();
        gameStartPanel.setLayout(new BoxLayout(gameStartPanel, BoxLayout.X_AXIS));
        gameStartPanel.setOpaque(false);
        gameStartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton generalGameButton = new JButton("Start General Game");
        JButton timedGameButton = new JButton("Start Timed Game");
        generalGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        timedGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameStartPanel.add(Box.createHorizontalGlue());
        gameStartPanel.add(generalGameButton);
        gameStartPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        gameStartPanel.add(timedGameButton);
        gameStartPanel.add(Box.createHorizontalGlue());

        generalGameButton.addActionListener(e -> {
            startGame(false);
            userInterfaceFrame.getMusicPlayer().playSoundEffectPressingButton();
        });
        timedGameButton.addActionListener(e -> {startGame(true);
            userInterfaceFrame.getMusicPlayer().playSoundEffectPressingButton();
        });

        return gameStartPanel;
    }

    /**
     * 预览按钮，中右侧，有地图的预览
     * TODO: 给Level截图并本地保存
     * @return 预览按钮
     */
    private JPanel getPreviewPanel(){
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setOpaque(false);
        previewPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        previewLabel = new JLabel("Preview: Select a level");
        previewLabel.setForeground(Color.WHITE);
        previewLabel.setFont(new Font("Arial", Font.BOLD, 16));
        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel previewContent = new JPanel();
        previewContent.setOpaque(false);
        previewContent.setPreferredSize(new Dimension(200, 150));
        previewContent.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        previewContent.add(new JLabel("Level Preview"));

        previewPanel.add(previewLabel);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewPanel.add(previewContent);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewContent.add(Box.createVerticalGlue());

        return previewPanel;
    }

    /**
     * 获取成就面板，显示最佳成绩
     * @return 成就面板
     */
    private JPanel getAchievementPanel(){
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
        achievementPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        achievementPanel.setOpaque(false);
        achievementPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        achievementStepsLabel = new JLabel("Best: ---");
        achievementStepsLabel.setForeground(Color.WHITE);
        achievementStepsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achievementStepsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        achivementTimeLabel = new JLabel("Best: ---");
        achivementTimeLabel.setForeground(Color.WHITE);
        achivementTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achivementTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        achievementPanel.add(Box.createHorizontalBox());
        achievementPanel.add(achievementStepsLabel);
        achievementPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        achievementPanel.add(achivementTimeLabel);
        achievementPanel.add(Box.createHorizontalGlue());

        return achievementPanel;
    }

    /**
     * 获取成就面板的包装器，设置对齐方式
     * @return 包装器
     */
    private JPanel getWrapperPanel(){
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(getAchievementPanel());

        return wrapperPanel;
    }

    /**
     * 获取背景板，进行美化
     * TODO: 找到背景资源图
     * @return 背景面板
     */
    private BackgroundPanel getBackgroundPanel(){

        BackgroundPanel background = new BackgroundPanel("src/frame/theme/levelSelectionBackgroundPic.jpg");
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        background.add(getLevelListPanel(), BorderLayout.WEST);
        background.add(getGameStartPanel(), BorderLayout.SOUTH);
        background.add(getPreviewPanel(), BorderLayout.CENTER);
        background.add(getWrapperPanel(), BorderLayout.NORTH);

        return background;
    }

}