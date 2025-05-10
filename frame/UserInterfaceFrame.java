package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import record.*;

import static frame.theme.Style.styleBtn;

public class UserInterfaceFrame extends JFrame {
    private LoginFrame loginFrame;
    private String selectedLevel = null;
    private boolean bgmEnabled = true;
    private User owner;
    private BackgroundPanel background;
    private JPanel buttonPanel;


    public UserInterfaceFrame(LoginFrame loginFrame, User owner) {
        this.loginFrame = loginFrame;
        this.owner = owner;

        initializeUserInterface();
        getBackgroundPanel();

    }

    private void shiftPlayStatus() {
        if (this.bgmEnabled) {
            // TODO: Implement BGM disabling logic
        } else {
            // TODO: Implement BGM enabling logic
        }
        // Potentially update button text or appearance
    }

    private void initializeUserInterface() {
        setTitle("Klotski Board Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void getBackgroundPanel() {
        this.background = new BackgroundPanel("src/frame/theme/userInterfaceBackgroundPic.jpg");
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 0));
        setStartGameButton();
        setLoadGameButton();
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

    private void setStartGameButton() {
        JButton startGameButton = new JButton("Start a New Game");
        styleBtn(startGameButton);
        buttonPanel.add(startGameButton);
        startGameButton.addActionListener(e -> {
            LevelSelectionFrame levelDialog = new LevelSelectionFrame(this);
            levelDialog.setVisible(true);
        });
    }

    private void setLoadGameButton() {
        JButton loadGameButton = new JButton("Load an Old Game");
        styleBtn(loadGameButton);
        buttonPanel.add(loadGameButton);
        loadGameButton.addActionListener(e -> {
            System.out.println("Load Game button clicked.");
            JOptionPane.showMessageDialog(this, "Load Game functionality not yet implemented.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void setBGMButton() {
        JButton bgmButton = new JButton("BGM Setting");
        styleBtn(bgmButton);
        buttonPanel.add(bgmButton);
        bgmButton.addActionListener(e -> {
            shiftPlayStatus();
        });
    }

    private void setLogoutButton() {
        JButton logoutButton = new JButton("Log Out");
        styleBtn(logoutButton);
        buttonPanel.add(logoutButton);
        logoutButton.addActionListener(e -> {
            System.out.println("Logging out.");
            this.dispose();
            loginFrame.setVisible(true);
        });
    }

}