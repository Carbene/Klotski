package frame;

import record.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class RegisterFrame extends JFrame {

    private LoginFrame loginFrame;
    private JPanel fieldsPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JPanel buttonPanel;
    private JButton registerButton;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        initializeRegisterFrame();
        getFieldsPanel();
        getButtonPanel();
        this.add(fieldsPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void initializeRegisterFrame(){
        this.setTitle("Register");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
                loginFrame.setVisible(true);
            }
        };
        this.addWindowListener(windowAdapter);
    }

    private void getFieldsPanel() {
        this.fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        nameLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(nameLabel, gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(nameField, gbc);

        passwordLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(passwordLabel, gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(passwordField, gbc);
    }

    private void getButtonPanel() {
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            if(e.getSource() == registerButton) {
                String username = nameField.getText();
                String password = new String(passwordField.getPassword());
                if(!User.registerUser(username, password)) {

                }
                this.setVisible(false);
                this.loginFrame.setVisible(true);
            }
        });
        buttonPanel.add(registerButton, BorderLayout.SOUTH);
        buttonPanel.setVisible(true);
    }
}