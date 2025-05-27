package frame;

import record.User;
import view.Level;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class RankFrame extends JFrame {
    private JTextArea rankTextArea;
    private JScrollPane rankPane;
    private Level selectedLevel;
    private MusicPlayer musicPlayer;

    /**
     * 有参构造器，设置用户界面
     */
    public RankFrame(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        initializeRankFrame();
        this.add(getLevelListPanel(), BorderLayout.WEST);
        this.add(getRankPane(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    /**
     * 初始化排行榜界面
     */
    private void initializeRankFrame() {
        this.setTitle("Rank");
        this.setSize(300, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
    }

    private JPanel getLevelListPanel(){
        JPanel levelListPanel = new JPanel();
        levelListPanel.setLayout(new BoxLayout(levelListPanel, BoxLayout.Y_AXIS));
        levelListPanel.setOpaque(false);
        levelListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        for (Level level : Level.values()) {
            JButton levelButton = new JButton(String.valueOf(level.getCODE()));
            levelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            levelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, levelButton.getPreferredSize().height));
            levelButton.addActionListener(e -> {
                this.selectedLevel = level;
                updatePreview();
                musicPlayer.playSoundEffectPressingButton();
            });
            Style.styleBtn(levelButton);
            levelListPanel.add(levelButton);
            levelListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        levelListPanel.add(Box.createVerticalGlue());

        return levelListPanel;
    }

    private JScrollPane getRankPane() {
        rankTextArea = new JTextArea();
        rankTextArea.setEditable(false);
        rankPane = new JScrollPane(rankTextArea);
        return rankPane;
    }

    private void updatePreview() {
        if (selectedLevel == null) return;
        ArrayList<User> users = User.deserializeList();
        users.removeIf(user -> "Visitor".equals(user.getId()));
        users.removeIf(user1 ->  User.getBestRecord(user1,selectedLevel.getCODE(),0) == 0);
        users.sort((u1, u2) -> Integer.compare(
                User.getBestRecord(u1, selectedLevel.getCODE(), 0),
                User.getBestRecord(u2, selectedLevel.getCODE(), 0)
        ));
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (User user : users) {
            sb.append(rank++)
                    .append(". ")
                    .append(user.getId())
                    .append(" Steps: ")
                    .append(User.getBestRecord(user, selectedLevel.getCODE(), 0))
                    .append("\n");
        }
        rankTextArea.setText(sb.toString());
    }

}
