package frame;

import view.*;
import record.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LevelSelectionFrame extends JDialog {
    private String selectedLevel = null;
    private String[] levels = {"Level1", "Medium", "Hard", "Expert"};
    private JLabel previewLabel;
    private JLabel achivementStepsLabel;
    private JLabel achivementTimeLabel;
    private UserInterfaceFrame userInterfaceFrame;
    private User owner;

    public LevelSelectionFrame(Frame owner) {
        super(owner, "Select Level", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.userInterfaceFrame = (UserInterfaceFrame) owner;
        getBackgroundPanel();


    }

    private void updatePreview(String level) {
        previewLabel.setText(level);
        achivementStepsLabel.setText("Best: [Score for " + level + "]");
        achivementTimeLabel.setText("Best: [Time for " + level + "]");
    }

    private void startGame(UserInterfaceFrame userInterfaceFrame, String level) {
        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first using the 'Select Level' button.", "Level Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.userInterfaceFrame.setVisible(false);
        GameFrame gameFrame = new GameFrame(this, selectedLevel, setLevel(selectedLevel), true);

        gameFrame.setVisible(true);
        this.setVisible(false);
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }

    public Leveled setLevel(String selectedLevel) {
        for(Leveled map : Leveled.values()){
            if(map.getCHINESENAME().equals(selectedLevel)){
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
                updatePreview(level);
                this.selectedLevel = level;
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

        generalGameButton.addActionListener(e -> startGame(userInterfaceFrame, selectedLevel));

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
        previewContent.add(new JLabel(" [Level Image Placeholder] "));

        previewPanel.add(previewLabel);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewPanel.add(previewContent);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewContent.add(Box.createVerticalGlue());

        return previewPanel;
    }

    private JPanel getAchievementPanel(){
        JPanel achivementPanel = new JPanel();
        achivementPanel.setLayout(new BoxLayout(achivementPanel, BoxLayout.X_AXIS));
        achivementPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        achivementPanel.setOpaque(false);
        achivementPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        achivementStepsLabel = new JLabel("Best: ---");
        achivementStepsLabel.setForeground(Color.WHITE);
        achivementStepsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achivementStepsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        achivementTimeLabel = new JLabel("Best: ---");
        achivementTimeLabel.setForeground(Color.WHITE);
        achivementTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achivementTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        achivementPanel.add(Box.createHorizontalBox());
        achivementPanel.add(achivementStepsLabel);
        achivementPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        achivementPanel.add(achivementTimeLabel);
        achivementPanel.add(Box.createHorizontalGlue());

        return achivementPanel;
    }

    private JPanel getWrapperPanel(){
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(getAchievementPanel());

        return wrapperPanel;
    }

    private BackgroundPanel getBackgroundPanel(){

        BackgroundPanel background = new BackgroundPanel("path/to/level_select_wallpaper.jpg");
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        background.add(getLevelListPanel(), BorderLayout.WEST);
        background.add(getGameStartPanel(), BorderLayout.SOUTH);
        background.add(getPreviewPanel(), BorderLayout.CENTER);
        background.add(getWrapperPanel(), BorderLayout.NORTH);

        return background;
    }

}

/*
package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.OutputStreamWriter;

public class LevelSelectionFrame extends JDialog {
    private String selectedLevel = null; // Store the chosen level
    private String[] levels = {"Easy", "Medium", "Hard", "Expert"};
    private JLabel previewLabel; // To show level preview (text/image)
    private JLabel achivementStepsLabel; // To show best achievement
    private JLabel achivementTimeLabel;
    private UserInterfaceFrame userInterfaceFrame;

    public LevelSelectionFrame(Frame owner) {
        super(owner, "Select Level", true); // Modal dialog
        setSize(500, 400);
        setLocationRelativeTo(owner); // Center relative to the owner frame
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Dispose on close

        this.userInterfaceFrame = (UserInterfaceFrame) owner;
        if(this.userInterfaceFrame == null){

            System.out.println("UserInterfaceFrame is null");

        }

        // Use BackgroundPanel for wallpaper
        // Replace "path/to/level_select_wallpaper.jpg" with your actual image path
        BackgroundPanel background = new BackgroundPanel("path/to/level_select_wallpaper.jpg");
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background); // Set as the content pane

        // Panel for level buttons (left side)
        JPanel levelListPanel = new JPanel();
        levelListPanel.setLayout(new BoxLayout(levelListPanel, BoxLayout.Y_AXIS));
        levelListPanel.setOpaque(false); // Make transparent to see background
        levelListPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding
        JPanel gameStartPanel = new JPanel();
        gameStartPanel.setLayout(new BoxLayout(gameStartPanel, BoxLayout.X_AXIS));
        gameStartPanel.setOpaque(false);
        gameStartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (String level : levels) {
            JButton levelButton = new JButton(level);
            levelButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center buttons horizontally
            levelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, levelButton.getPreferredSize().height)); // Make buttons wide
            levelButton.addActionListener(e -> {
                updatePreview(level); // Update preview when button is hovered/clicked
                // You might want selection on click instead of hover
                this.selectedLevel = level;
            });
            levelListPanel.add(levelButton);
            levelListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        }
        levelListPanel.add(Box.createVerticalGlue()); // Pushes buttons up


        JButton generalGameButton = new JButton("Start General Game");
        JButton timedGameButton = new JButton("Start Timed Game");
        generalGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        timedGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameStartPanel.add(Box.createHorizontalGlue());
        gameStartPanel.add(generalGameButton);
        gameStartPanel.add(Box.createRigidArea(new Dimension(20, 0))); // 20px space
        gameStartPanel.add(timedGameButton);
        gameStartPanel.add(Box.createHorizontalGlue());
        generalGameButton.addActionListener(e -> {
            // Find which level button might be "selected" (e.g., based on previewLabel state)
            // For now, let's assume the previewLabel's text holds the potential level
            String potentialLevel = previewLabel.getText(); // Simplistic way
            if (potentialLevel != null && !potentialLevel.startsWith("Preview:")) {
                this.selectedLevel = potentialLevel;
                System.out.println("Level selected: " + this.selectedLevel);
                this.dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, "Please click on a level name first.", "No Level Selected", JOptionPane.WARNING_MESSAGE);
            }

        });

        generalGameButton.addActionListener(e -> {

            startGame(userInterfaceFrame, selectedLevel );

        });



        // Panel for Preview and Achievement (right side)
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setOpaque(false); // Make transparent
        previewPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        previewLabel = new JLabel("Preview: Select a level");
        previewLabel.setForeground(Color.WHITE); // Text color for visibility
        previewLabel.setFont(new Font("Arial", Font.BOLD, 16));
        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel achivementPanel = new JPanel();
        achivementPanel.setLayout(new BoxLayout(achivementPanel, BoxLayout.X_AXIS));
        achivementPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        achivementPanel.setOpaque(false);
        achivementPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


        achivementStepsLabel = new JLabel("Best: ---");
        achivementStepsLabel.setForeground(Color.WHITE);
        achivementStepsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achivementStepsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        achivementTimeLabel = new JLabel("Best: ---");
        achivementTimeLabel.setForeground(Color.WHITE);
        achivementTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        achivementTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Placeholder for actual preview content (e.g., an image)
        JPanel previewContent = new JPanel(); // Could hold an image icon
        previewContent.setOpaque(false);
        previewContent.setPreferredSize(new Dimension(200, 150));
        previewContent.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        previewContent.add(new JLabel(" [Level Image Placeholder] ")); // Placeholder text


        previewPanel.add(previewLabel);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewPanel.add(previewContent);
        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        previewContent.add(Box.createVerticalGlue()); // Pushes content up

        achivementPanel.add(Box.createHorizontalBox());
        achivementPanel.add(achivementStepsLabel);
        achivementPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        achivementPanel.add(achivementTimeLabel);
        previewPanel.add(Box.createHorizontalGlue());

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(achivementPanel);

        // Add panels to the background panel
        background.add(levelListPanel, BorderLayout.WEST);
        background.add(gameStartPanel, BorderLayout.SOUTH);
        background.add(previewPanel, BorderLayout.CENTER);
        background.add(wrapperPanel, BorderLayout.NORTH);
    }

    */
/** Updates the preview and achievement info for the given level *//*

    private void updatePreview(String level) {
        // In a real app, load preview image and best score for 'level'
        previewLabel.setText(level); // Update label to show which level is focused
        achivementStepsLabel.setText("Best: [Score for " + level + "]"); // Placeholder
        achivementTimeLabel.setText("Best: [Time for " + level + "]");
        System.out.println("Previewing level: " + level);
        // Potentially load an image into 'previewContent' panel here
    }

    private void startGame(UserInterfaceFrame userInterfaceFrame, String level) {

        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a level first using the 'Select Level' button.",
                    "Level Not Selected",
                    JOptionPane.WARNING_MESSAGE);
            return; // Don't start game if level not chosen
        }


        // Open Game Frame
        this.userInterfaceFrame.setVisible(false);
        GameFrame gameFrame = new GameFrame( this,selectedLevel,selectedLevel,true);
        gameFrame.setVisible(true);
        this.setVisible(false);

    }

    */
/** Returns the selected level after the dialog is closed *//*

    public String getSelectedLevel() {
        return selectedLevel;
    }
}
*/
