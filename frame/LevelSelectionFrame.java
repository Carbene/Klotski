package frame;

import view.*;
import record.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LevelSelectionFrame extends JDialog {
    private String selectedLevel;
    private String[] levels = {"1", "2", "3", "4","5"};
    private JLabel previewLabel;
    private JLabel achievementStepsLabel;
    private JLabel achivementTimeLabel;
    private UserInterfaceFrame userInterfaceFrame;
    private User user;

    public LevelSelectionFrame(Frame owner,User user) {
        super(owner, "Select Level", true);
        this.user = user;
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.userInterfaceFrame = (UserInterfaceFrame) owner;
        getBackgroundPanel();

    }

    private void updatePreview() {
        previewLabel.setText(this.selectedLevel);
        if(this.user.getId() != "Visitor") {
            achievementStepsLabel.setText(User.getBestRecord(user,Integer.parseInt(selectedLevel),0) == 0 ?"Please first play the game" : "Best: " + User.getBestRecord(user,Integer.parseInt(selectedLevel),0) + " Steps");
            achivementTimeLabel.setText(User.getBestRecord(user,Integer.parseInt(selectedLevel),1) == 0 ?"Please first play the game" : "Best: " + User.getBestRecord(user,Integer.parseInt(selectedLevel),1) + " Seconds");
        }else{
            achievementStepsLabel.setText("Please login to see your best record");
            achivementTimeLabel.setText("Please login to see your best record");
        }


    }

    private void startGame(boolean isTimed) {
        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first before using the 'Select Level' button.", "Level Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(isTimed){
            this.userInterfaceFrame.setVisible(false);
            GameFrame gameFrame = new GameFrame(this, user,setLevel(Integer.parseInt(selectedLevel)), true,userInterfaceFrame.getMusicPlayer());

            gameFrame.setVisible(true);
            this.setVisible(false);
        }else{
            this.userInterfaceFrame.setVisible(false);
            GameFrame gameFrame = new GameFrame(this, user,setLevel(Integer.parseInt(selectedLevel)), false,userInterfaceFrame.getMusicPlayer());

            gameFrame.setVisible(true);
            this.setVisible(false);
        }
    }

    public Level setLevel(int code) {
        for(Level map : Level.values()){
            if(map.getCODE() == code){
                return map;
            }
        }
        return null;
    }

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

    private JPanel getWrapperPanel(){
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(getAchievementPanel());

        return wrapperPanel;
    }

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