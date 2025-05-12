package frame;

import record.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

/**
 * 这是登录面板，登陆相关的都在这里实现
 */
public class LoginFrame extends JFrame {
    private JRadioButton userRadioButton;
    private JRadioButton visitorRadioButton;
    private ButtonGroup loginTypeGroup;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel fieldsPanel;
    private JPanel buttonPanel;
    private JPanel radioPanel;
    private  final static User VISITOR_USER = new User();
    private RegisterFrame registerFrame;
    private User currentUser = VISITOR_USER;

    /**
     * 这是一个无参构造方法，构造了初始的用户登录界面
     */
    public LoginFrame() {

        initializeLoginFrame();

        getRadioPanel();
        getButtonPanel();
        getFieldsPanel();
        getActionListener();

        add(radioPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    /**
     * 初始化用户登录界面，设置事件监听
     */
    private void initializeLoginFrame(){
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(LoginFrame.this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };
        addWindowListener(windowAdapter);
    }

    /**
     * 召唤用户界面
     */
    private void startUserInterface() {
        UserInterfaceFrame userInterfaceFrame = new UserInterfaceFrame(this,currentUser);
        userInterfaceFrame.setVisible(true);
        this.setVisible(false);
    }

    /**
     * 获取上方的选择按钮，选择以何种身份登入
     */
    private void getRadioPanel() {
        this.radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userRadioButton = new JRadioButton("User");
        visitorRadioButton = new JRadioButton("Visitor");
        loginTypeGroup = new ButtonGroup();
        loginTypeGroup.add(userRadioButton);
        loginTypeGroup.add(visitorRadioButton);
        userRadioButton.setSelected(true);
        radioPanel.add(userRadioButton);
        radioPanel.add(visitorRadioButton);
    }

    /**
     * 获取身份信息的登入区
     * TODO: 在关闭后密码和用户信息未清除，需要在成功登入后先清除，避免危险
     */
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

    /**
     * 底部的按钮面板，登入预注册
     */
    private void getButtonPanel() {
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
    }

    /**
     * 设置事件监听器
     */
    private void getActionListener(){
        ActionListener radioListener = e -> {
            boolean isUser = userRadioButton.isSelected();
            fieldsPanel.setVisible(isUser);
            if (!isUser) {
                nameField.setText("");
                passwordField.setText("");
            }
            pack();
            setLocationRelativeTo(null);
        };
        userRadioButton.addActionListener(radioListener);
        visitorRadioButton.addActionListener(radioListener);
        loginButton.addActionListener(e -> {
            if(e.getSource() == loginButton) {
                login();
            }
        });

        this.registerButton.addActionListener(e -> {
            if (e.getSource() == registerButton) {
                register();
            };
        });
    }

    /**
     * 检查登录信息的合法性，并唤醒User类的方法查询本地用户信息
     */
    private void login(){
        if (visitorRadioButton.isSelected()) {
            startUserInterface();
        } else {
            String username = nameField.getText();
            String password = new String(passwordField.getPassword());
            ArrayList<User> users = User.deserializeList();
            if(users == null || users.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Error");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(this);
                JLabel errorLabel = new JLabel("No user data found. Please register first.");
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dialog.add(errorLabel);
                dialog.setVisible(true);
            }else{
                for (User user : users) {
                    if (user.getId().equals(username) && user.getPassword().equals(password)) {
                        currentUser = user;
                        startUserInterface();
                        return;
                    }
                }
            }
        }
    }

    /**
     * 唤醒用户注册界面
     * TODO: 对于那些未注册的却直接输入相关信息，是不是可以直接创建，给出一个新的用户？
     */
    private void register(){
        this.registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }
}