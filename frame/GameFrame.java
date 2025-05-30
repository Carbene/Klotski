package frame;

import logic.AnswerSolver;
import logic.LogicController;
import record.Move;
import record.User;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

import static frame.Style.styleBtn;

/**
 * 游戏主界面类，负责展示游戏视图并处理游戏逻辑。
 * 包含华容道游戏的所有核心功能，包括棋盘显示、移动逻辑、计时计步、存档读档、
 * 联网对战功能以及胜利条件判断等。
 */
public class GameFrame extends JFrame {
    private LevelSelectionFrame selectionFrame;
    private boolean isTimed;
    private ServerSocket serverSocket;
    private Socket socket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    public boolean isSpectator;
    private Queue<String> messages;

    private JLabel stepsLabel;
    private JLabel timerLabel;
    private JLabel userLabel;
    private int stepCount = 0;
    private int timeElapsed = 0;

    private JPanel klotskiBoardPanel;
    private BackgroundPanel backgroundPanel;
    private Block selectedBlock;

    private Timer gameTimer;
    private ArrayList<Block> blocks;
    private LogicController logicController;
    private User user;
    private MusicPlayer musicPlayer;
    private boolean isSaved = false;
    private UserInterfaceFrame userInterfaceFrame;
    private volatile boolean isRunning = true;
    private volatile boolean isWinning = false;
    private boolean isMoved = true;
    private java.util.List<Direction> solution;
    private java.util.List<Integer> x_selected;
    private java.util.List<Integer> y_selected;
    private java.util.List<Integer> type_selected;
    ListIterator<Direction> iter;

    /**
     * 这是第一类的有参构造器，作用是新建一个游戏界面，在选择新开始游戏时使用
     *@param selectionFrame 上一级的视图，便于退出时进行相关设置（可能可以通过公开化方法进行消除）
     * @param userInterfaceFrame 上一级的视图，便于退出时进行相关设置（可能可以通过公开化方法进行消除）
     * @param user 用户对象，全局应当唯一
     * @param level 地图传入
     * @param isTimed 模式注释
     * @param musicPlayer 音乐播放器对象，可能需要底部新增BGM Setting
     */
    public GameFrame(LevelSelectionFrame selectionFrame,UserInterfaceFrame userInterfaceFrame,User user,Level level, boolean isTimed, MusicPlayer musicPlayer) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.selectionFrame = selectionFrame;
        this.isTimed = isTimed;
        this.user = user;
        this.logicController = new LogicController(level,user,isTimed,false);
        this.musicPlayer = musicPlayer;
        this.userInterfaceFrame = userInterfaceFrame;
        this.isSpectator = false;
        this.messages = new LinkedList<>();

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel(false);
        this.setClientSocket();
        initializeKlotskiBoard(level);

    }

    /**
     * 这是一个有参构造器，在读取存档时使用
     * @param userInterfaceFrame 上一级视图，可能可以消除
     * @param logicController 游戏中控，实现游戏的核心逻辑，并导入残局
     * @param musicPlayer 音乐播放器对象，可能需要底部新增BGM Setting
     */
    public GameFrame(UserInterfaceFrame userInterfaceFrame,LogicController logicController,MusicPlayer musicPlayer) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.logicController = logicController;
        this.user = userInterfaceFrame.getUser();
        this.isTimed = logicController.getTime() != 0;
        this.musicPlayer = musicPlayer;
        this.stepCount = logicController.getStep();
        this.timeElapsed = logicController.getTime();
        this.isSpectator = false;
        this.userInterfaceFrame = userInterfaceFrame;
        this.messages = new LinkedList<>();

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel(false);
        setClientSocket();
        loadKlotskiBoard(LogicController.copyMap(this.logicController.getMap()));
    }

    /**
     * 这是一个有参构造器，在观战时使用
     * @param userInterfaceFrame 上级界面，方便返回
     * @param musicPlayer 音乐播放器对象，方便设置BGM
     * @param socket 连接的Socket对象，用于与服务器进行通信
     */

    public GameFrame(UserInterfaceFrame userInterfaceFrame,MusicPlayer musicPlayer, Socket socket) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.socket = socket;
        this.user = userInterfaceFrame.getUser();
        this.musicPlayer = musicPlayer;
        this.isSpectator = true;
        this.userInterfaceFrame = userInterfaceFrame;

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel(true);
        this.setSocket();
    }

    /**
     * 本方法为客户端监听设置了一个新的线程，方便即时将要求转化为移动等
     */
    private void setSocket() {
       new Thread(() -> {
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                this.out = new PrintWriter(socket.getOutputStream(),true);
                String line;
                while (isRunning && (line = in.readLine()) != null) {
                    System.out.println(line);
                    String[] input = line.split(" ");
                    if(input[0].equals("Map")){
                        this.logicController = new LogicController(Level.getLevel(Integer.parseInt(input[1])),this.user,false,true);
                        initializeKlotskiBoard(Level.getLevel(Integer.parseInt(input[1])));
                    } else if (input[0].equals("Move")) {
                        try{
                            this.doMove(input);
                        }catch(NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    if(line.equals("Requesting help")){
                        int choice = JOptionPane.showConfirmDialog(this,"Confirm to help","Confirmation",JOptionPane.YES_NO_OPTION);
                        if(choice == JOptionPane.YES_OPTION){
                            this.sendMessage("Confirm");
                            this.isSpectator = false;
                            JOptionPane.showMessageDialog(this, "Now you have one move chance.");
                        }else{
                            this.sendMessage("Cancel");
                        }
                    }
                    if (line.equals("Confirm")) {
                        this.isSpectator = false;
                        JOptionPane.showMessageDialog(this, "Now you have one move chance.");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 本方法设置了一个客户端监听线程，保证即时链接可能存在的服务器端，并且实现与之通信
     */
    private void setClientSocket() {
        if(isTimed){
            return;
        }
        new Thread(() -> {
            try {
                this.serverSocket = new ServerSocket(8080);
                while(isRunning){
                    this.clientSocket = serverSocket.accept();
                    if(this.clientSocket != null){
                        this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(),StandardCharsets.UTF_8));
                        this.out = new PrintWriter(this.clientSocket.getOutputStream(),true);
                        break;
                    }
                }
                if(this.messages.size() > 0){
                    for(String message : this.messages){
                        out.println(message);
                    }
                    this.messages.clear();
                }
                String line;
                while(isRunning && (line = in.readLine()) != null){
                    System.out.println(line);
                    String[] input = line.split(" ");
                    if (input[0].equals("Move")) {
                        this.doMove(input);
                    }
                    if(line.equals("Available help")){
                        int choice = JOptionPane.showConfirmDialog(this,"Confirm to accept help","Confirmation",JOptionPane.YES_NO_OPTION);
                        if(choice == JOptionPane.YES_OPTION){
                            this.sendMessage("Confirm");
                            this.selectedBlock.setSelected(false);
                            this.selectedBlock = this.blocks.getLast();
                            this.isSpectator = true;
                        }else{
                            this.sendMessage("Cancel");
                        }
                    }else if(line.equals("Confirm")){
                        this.selectedBlock.setSelected(false);
                        this.selectedBlock = this.blocks.getLast();
                        this.isSpectator = true;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * 这是一个发送消息的方法，用于向客户端或服务器发送消息
     * @param str 需要发送的消息字符串
     */
    private void sendMessage(String str){
        if(this.out != null){
            out.println(str);
        }else{
            if(this.logicController.getIsSpectator()){
                return;
            }
            this.messages.add(str);
        }
    }

    /**
     * 这个方法会消耗一个二维数组类型的变量，生成所有的Block，并生成Blocks，并绘制
     * @param map 被消耗的地图
     */
    private void initializeKlotskiBoard(Level map) {
        int[][] mapInitializer = LogicController.copyMap(map);

        ArrayList<Block> blocks = new ArrayList<>();

        for (int i = 0; i < mapInitializer.length; i++) {
            for (int j = 0; j < mapInitializer[0].length; j++) {
                Block block = null;
                if (mapInitializer[i][j] == 1) {
                    block = new Block("/klotiskiXiaoBin.jpg", i, j,1, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                } else if (mapInitializer[i][j] == 2) {
                    block = new Block("/klotiskiGuanYu.jpg", i, j,2, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                } else if (mapInitializer[i][j] == 3) {
                    block = new Block("/klotiskiDaBin.jpg", i, j,3, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                } else if (mapInitializer[i][j] == 4) {
                    block = new Block("/klotiskiCaoCao.jpg", i, j,4, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                    mapInitializer[i + 1][j + 1] = 0;
                }else if (mapInitializer[i][j] == -1){
                    block = new Block("/mountain.jpg", i, j,-1, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                }
                if (block != null) {
                    klotskiBoardPanel.add(block);
                    block.setLocation(j * block.GRIDSIZE + 2, i * block.GRIDSIZE + 2);
                    blocks.add(block);
                }
            }
        }
        this.repaint();
        blocks.add(new Block(this));
        this.selectedBlock = blocks.getLast();
        klotskiBoardPanel.revalidate();
        klotskiBoardPanel.repaint();
        klotskiBoardPanel.setFocusable(true);
        this.blocks = blocks;
        if(!isSpectator){
            if(this.clientSocket != null){
                this.out.println("Map " + map.getCODE());
            }else{
                this.messages.add("Map " + map.getCODE());
            }
        }
    }

    /**
     * 这是一个重载方法，主要用于读取存档时使用，依然会消耗一个二维数组类型的变量，生成所有的Block，并生成Blocks，并绘制
     * @param map 被消耗的地图
     */
    private void loadKlotskiBoard(int[][] map) {
        this.blocks = new ArrayList<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                Block block = null;
                if (map[i][j] == 1) {
                    block = new Block("/klotiskiXiaoBin.jpg", i, j,1, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE);
                    map[i][j] = 0;
                } else if (map[i][j] == 2) {
                    block = new Block("/klotiskiGuanYu.jpg", i, j,2, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;
                } else if (map[i][j] == 3) {
                    block = new Block("/klotiskiDaBin.jpg", i, j,3, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                } else if (map[i][j] == 4) {
                    block = new Block("/klotiskiCaoCao.jpg", i, j,4, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                    map[i][j + 1] = 0;
                    map[i + 1][j + 1] = 0;
                }else if (map[i][j] == -1){
                    block = new Block("/mountain.jpg", i, j,-1, this);
                    block.setSize(block.GRIDSIZE, block.GRIDSIZE);
                    map[i][j] = 0;
                }
                if (block != null) {
                    klotskiBoardPanel.add(block);
                    block.setLocation(j * block.GRIDSIZE + 2, i * block.GRIDSIZE + 2);
                    blocks.add(block);
                }
            }
        }
        this.repaint();
        blocks.add(new Block(this));
        this.selectedBlock = blocks.getLast();
        klotskiBoardPanel.revalidate();
        klotskiBoardPanel.repaint();
        klotskiBoardPanel.setFocusable(true);
    }

    /**
     * 这是一个计时器的开始方法，主要用于游戏开始时使用
     */
    private void startTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        updateTimerLabel();
        gameTimer = new Timer(1000, e -> {
            timeElapsed++;
            updateTimerLabel();
        });
        gameTimer.start();
    }

    /**
     * 这是一个计时器的停止方法，主要用于游戏结束时使用
     */
    private void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    /**
     * 这是一个同步方法，每过1s进行更新
     */
    private void updateTimerLabel() {
        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        this.logicController.setTime(minutes * 60 + seconds);
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        if(timeElapsed >= 300 && !isWinning){
            int choice = JOptionPane.showConfirmDialog(this,"Time out! Game over.");
            if(choice == JOptionPane.YES_OPTION || choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION){
                stopTimer();
                this.isRunning = false;
                this.logicController.setTime(0);
                this.logicController.setStep(0);
                this.dispose();
                this.userInterfaceFrame.setVisible(true);
            }

        }
    }

    /**
     * 这是一个同步方法，每移动一步进行一次更新
     */
    private void updateStepLabel(){
        stepsLabel.setText("Steps: " + stepCount);
    }

    /**
     * 这是一个背景面板的获得方法，它会接受所有的组件，并最终整合成一体
     */
    private void getBackgroundPanel(boolean isSpectator) {
        this.backgroundPanel = new BackgroundPanel("/gameBackgroundPic.jpg");
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        this.getKlotskiBoardPanel();

        backgroundPanel.add(getInfoPanel(isTimed), BorderLayout.NORTH);
        backgroundPanel.add(getBoardContainer(), BorderLayout.CENTER);
        backgroundPanel.add(getControlPanel(isSpectator), BorderLayout.SOUTH);
        backgroundPanel.add(getDirectionPanel(), BorderLayout.EAST);
        backgroundPanel.add(getDirectionPanel(), BorderLayout.WEST);
    }

    /**
     * 这是一个信息面板的获取方法，获取的是上方的信息，也就是步数和事件
     * @param isTimed 是否及时判断是否显示事件
     * @return 信息面板，包含步数和（可能的时间）
     */
    private JPanel getInfoPanel(boolean isTimed) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        infoPanel.setOpaque(false);
        this.stepsLabel = new JLabel("Steps: " + stepCount);
        Style.styleLabel(stepsLabel);
        infoPanel.add(stepsLabel);

        this.timerLabel = new JLabel("Time: 00:00");
        if (isTimed) {
            Style.styleLabel(timerLabel);
            infoPanel.add(timerLabel);
            startTimer();
        }

        this.userLabel = new JLabel("User: " + user.getId());
        Style.styleLabel(userLabel);
        infoPanel.add(userLabel);

        return infoPanel;
    }

    /**
     * 这是一个方向面板的获取方法，获取的是左右两侧的方向按钮（基于现在这个布局下我觉得不太能美化）。
     * @return 方向控制器面板
     */
    private JPanel getDirectionPanel(){
        JPanel directionPanel = new JPanel(new GridBagLayout());
        directionPanel.setOpaque(false);
        directionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton upButton = new JButton("Up");
        JButton downButton = new JButton("Down");
        JButton leftButton = new JButton("Left");
        JButton rightButton = new JButton("Right");

        styleBtn(upButton);
        styleBtn(downButton);
        styleBtn(leftButton);
        styleBtn(rightButton);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.33; // 分配权重
        gbc.weighty = 0.33;
        directionPanel.add(upButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0.33;
        directionPanel.add(leftButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0.33;
        directionPanel.add(rightButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.33;
        gbc.weighty = 0.33;
        directionPanel.add(downButton, gbc);

        upButton.addActionListener(e -> {
            if(!this.isSpectator) {
                doMove(Direction.UP, false);
                this.musicPlayer.playSoundEffectPressingButton();
            }
        });
        downButton.addActionListener(e -> {
            if(!this.isSpectator) {
                doMove(Direction.DOWN, false);
                this.musicPlayer.playSoundEffectPressingButton();
            }
        });
        leftButton.addActionListener(e -> {
            if(!this.isSpectator) {
                doMove(Direction.LEFT, false);
                this.musicPlayer.playSoundEffectPressingButton();
            }
        });
        rightButton.addActionListener(e -> {
            if(!this.isSpectator) {
                doMove(Direction.RIGHT, false);
                this.musicPlayer.playSoundEffectPressingButton();
        }
        });

        return directionPanel;

    }

    /**
     * 这是下方的控制面板的获取方法，包括多个按钮，如读档功能的GUI实现基于这个，并赋予各个按钮监听器激活对应的功能
     * @return 控制面板
     */
    private JPanel getControlPanel(boolean spectatorMemory) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setOpaque(false);
        if(!spectatorMemory) {
            JButton saveButton = new JButton("Save Game");
            JButton withdrawButton = new JButton("Withdraw");
            JButton reloadButton = new JButton("Reload");
            JButton answerButton = new JButton("Show Answer");
            JButton requestHelpButton = new JButton("Request Help");
            JButton bgmSettingButton = new JButton("BGM Setting");
            JButton quitButton = new JButton("Quit");

            styleBtn(saveButton);
            styleBtn(withdrawButton);
            styleBtn(reloadButton);
            styleBtn(answerButton);
            styleBtn(requestHelpButton);
            styleBtn(bgmSettingButton);
            styleBtn(quitButton);

            controlPanel.add(saveButton);
            controlPanel.add(withdrawButton);
            controlPanel.add(reloadButton);
            controlPanel.add(answerButton);
            controlPanel.add(requestHelpButton);
            controlPanel.add(bgmSettingButton);
            controlPanel.add(quitButton);

            saveButton.addActionListener(e -> {
                LogicController.saveGame(this.logicController, this.user);
                this.musicPlayer.playSoundEffectPressingButton();
                this.isSaved = true;
            });
            withdrawButton.addActionListener(e -> withdrawMove(true));
            reloadButton.addActionListener(e -> reloadGame());
            answerButton.addActionListener(e -> showAnswer());
            requestHelpButton.addActionListener(e -> {
                if(this.clientSocket != null){
                    sendMessage("Requesting help");
                }else{
                    JOptionPane.showMessageDialog(this, "No spectator available.");
                }
            });
            bgmSettingButton.addActionListener(e -> {
                this.userInterfaceFrame.shiftPlayStatus();
            });
            quitButton.addActionListener(e -> quitGame());

            return controlPanel;
        }else{
            JButton offerHelpButton = new JButton("Offer help");
            JButton bgmSettingButton = new JButton("BGM Setting");
            JButton quitButton = new JButton("Quit");

            styleBtn(offerHelpButton);
            styleBtn(bgmSettingButton);
            styleBtn(quitButton);

            controlPanel.add(offerHelpButton);
            controlPanel.add(bgmSettingButton);
            controlPanel.add(quitButton);

            offerHelpButton.addActionListener(e -> {
                this.musicPlayer.playSoundEffectPressingButton();
                this.sendMessage("Available help");
            });
            bgmSettingButton.addActionListener(e -> {
                this.userInterfaceFrame.shiftPlayStatus();
            });
            quitButton.addActionListener(e -> quitGame());

            return controlPanel;
        }
    }

    /**
     * 获取中心游戏地图的面板
     */
    private void getKlotskiBoardPanel() {
        this.klotskiBoardPanel = new JPanel(null);
        this.klotskiBoardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        this.klotskiBoardPanel.setBackground(new Color(200, 200, 200, 200));
        this.klotskiBoardPanel.setPreferredSize(new Dimension(480, 600));
        this.klotskiBoardPanel.add(new KeyBindingExample());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                klotskiBoardPanel.requestFocusInWindow();
            }
        });
    }

    /**
     * 获取中心的游戏面板，进行叠层防止变形
     * @return 中心面板
     */
    private JPanel getBoardContainer() {
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setOpaque(false);
        boardContainer.add(klotskiBoardPanel);

        return boardContainer;
    }

    /**
     * 重新加载游戏的具体实现
     */
    private void reloadGame() {
        for(int i = this.logicController.getMoves().size(); i > 0; i--){
            withdrawMove(false);
        }
        this.musicPlayer.playSoundEffectPressingButton();
        this.timeElapsed = 0;
        this.stepCount = 0;
        this.logicController.setTime(0);
        this.logicController.setStep(0);
        updateTimerLabel();
        updateStepLabel();
    }

    /**
     * 撤回的具体实现，重载也基于这个部分实现
     * @param isWithdraw 是否是撤回,判断是否需要播放音效
     */
    private void withdrawMove(boolean isWithdraw) {
        if(this.logicController.getMoves().isEmpty()){
            JOptionPane.showMessageDialog(this, "No moves to withdraw.");
            return;
        }else {
            Move withdrawedMove = this.logicController.getMoves().pop();
            this.selectedBlock.setSelected(false);
            this.selectedBlock = this.getBlock(withdrawedMove.getCoordinate()[0],  withdrawedMove.getCoordinate()[1], withdrawedMove.getType());
            Direction direction = Direction.getOpposite(withdrawedMove.getDirection());
            doMove(direction, true);
            this.selectedBlock.setSelected(false);
            this.selectedBlock = blocks.getLast();
            if(isWithdraw){
                this.musicPlayer.playSoundEffectPressingButton();
                this.musicPlayer.playSoundEffectMovingBlock();
            }
            repaint();
        }
    }

    /**
     * 这是搜索算法的接口，后面具体的搜索代码可以在这里接入总体
     */
    private void showAnswer() {
        if(this.selectedBlock != null){
            this.selectedBlock.setSelected(false);
            this.selectedBlock = blocks.getLast();
            repaint();
        }
        if(solution == null || isMoved){
            AnswerSolver answerSolver = new AnswerSolver(logicController);
            java.util.List<Direction> solution = answerSolver.getFinalSolution();
            java.util.List<Integer> x_selected = answerSolver.getX_selected();
            java.util.List<Integer> y_selected = answerSolver.getY_selected();
            java.util.List<Integer> type_selected = answerSolver.getType_selected();
            if(solution == null){
                JOptionPane.showMessageDialog(this, "No solution found!");
                return;
            }else{
                this.solution = solution;
                this.x_selected = x_selected;
                this.y_selected = y_selected;
                this.type_selected = type_selected;
                isMoved = false;
                iter = solution.listIterator();
                Direction answerDirection = iter.next();
                int answerIndex = iter.previousIndex();
                doMove(x_selected.get(answerIndex), y_selected.get(answerIndex), type_selected.get(answerIndex), answerDirection, false);
            }
        }else if(!isMoved && solution != null){
            Direction answerDirection = iter.next();
            int answerIndex = iter.previousIndex();
            doMove(x_selected.get(answerIndex), y_selected.get(answerIndex), type_selected.get(answerIndex), answerDirection, false);
        }
    }


    /**
     * 这是退出游戏的具体实现
     * 根据当前的游戏状态，判断是否需要保存游戏，并且弹出确认对话框，还可以唤起正确的关闭方法
     */
    private void quitGame() {
        this.musicPlayer.playSoundEffectPressingButton();
        if(!isSaved && !this.logicController.getIsSpectator()){
            int result = JOptionPane.showConfirmDialog(this, "This game has not been saved. Do you really want to quit?", "Warning", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                stopTimer();
                this.dispose();
                this.userInterfaceFrame.setVisible(true);
                isRunning = false;
                try{
                    if(this.serverSocket != null){
                        this.serverSocket.close();
                    }
                    if(this.clientSocket != null){
                        this.clientSocket.close();
                    }
                    if(this.socket != null){
                        this.socket.close();
                    }
                }catch (Exception e){
                    e.getStackTrace();
                }
            } else {
                return;
            }
        }else{
            stopTimer();
            dispose();
            this.userInterfaceFrame.setVisible(true);
            isRunning = false;
            try{
                if(this.serverSocket != null){
                    this.serverSocket.close();
                }
                if(this.clientSocket != null){
                    this.clientSocket.close();
                }
                if(this.socket != null){
                    this.socket.close();
                }
            }catch (Exception e){
                e.getStackTrace();
            }
        }
    }

    /**
     * 获得当前面板选中的方块，保证视图与底层控制的统一
     * @return 方块
     */
    public Block getSelectedBlock() {
        return selectedBlock;
    }

    /**
     * 设置当前面板选中的方块，保证视图与底层控制的统一
     * @param selectedBlock 方块
     */
    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    /**
     * 执行方块移动操作
     * 根据选中方块的类型和移动方向，判断移动是否合法，并更新游戏状态
     *
     * @param direction 移动方向（上、下、左、右）
     * @param isWithdraw 是否为撤回操作，若为true则不计入步数统计
     */
    private void doMove(Direction direction,boolean isWithdraw) {
        int row = selectedBlock.getRow();
        int col = selectedBlock.getCol();
        int[][] map = logicController.getMap();
        if (this.logicController.getMap()[row][col] == 1 && Move.validateMove(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            this.isMoved = true;
            map[row][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 1;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 2 && Move.validateMove(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            this.isMoved = true;
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 2;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 3 && Move.validateMove(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            this.isMoved = true;
            map[row][col] = 0;
            map[row + 1][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 3;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 4 && Move.validateMove(4, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            this.isMoved = true;
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + 1][col] = 0;
            map[row + 1][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 4;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 4;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 4;
            map[row + direction.getRow() + 1][col + direction.getCol() + 1] = 4;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        }
        if(this.logicController.getIsSpectator()){
            this.selectedBlock.setSelected(false);
            this.selectedBlock = blocks.getLast();
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
        }
    }

    /**
     * 这是重载的doMove方法，主要用于处理来自服务器的移动指令
     * @param input 来自云端的移动指令
     */
    private void doMove(String[] input){
        Direction direction = Direction.getDirection(Integer.parseInt(input[4]));
        this.selectedBlock = getBlock(Integer.parseInt(input[1])-direction.getRow(),Integer.parseInt(input[2])-direction.getCol(),Integer.parseInt(input[3]));
        doMove(direction,false);
        this.selectedBlock = this.blocks.getLast();
        repaint();
    }

    /**
     * 这是一个移动方法，方便使用搜索结果进行移动
     * @param x 需要移动的方块的坐标x
     * @param y 需要移动的方块的坐标y
     * @param type 需要移动的方块的坐标种类
     * @param direction 需要移动的方块的方向
     * @param isWithdraw 是否撤回
     */
    private void doMove(int x, int y, int type, Direction direction, boolean isWithdraw) {
        try{this.selectedBlock = getBlock(x, y, type);
            int row = selectedBlock.getRow();
            int col = selectedBlock.getCol();
            int[][] map = logicController.getMap();
            if (this.logicController.getMap()[row][col] == 1 && Move.validateMove(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
                map[row][col] = 0;
                    map[row + direction.getRow()][col + direction.getCol()] = 1;
                    blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
                    if (!isWithdraw) {
                        afterMove(direction);
                    }
                } else if (map[row][col] == 2 && Move.validateMove(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
                    map[row][col] = 0;
                    map[row][col + 1] = 0;
                    map[row + direction.getRow()][col + direction.getCol()] = 2;
                    map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
                    blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
                    if (!isWithdraw) {
                        afterMove(direction);
                    }
                } else if (map[row][col] == 3 && Move.validateMove(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
                    map[row][col] = 0;
                    map[row + 1][col] = 0;
                    map[row + direction.getRow()][col + direction.getCol()] = 3;
                    map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
                    blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
                    if (!isWithdraw) {
                        afterMove(direction);
                    }
                } else if (map[row][col] == 4 && Move.validateMove(4, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
                    map[row][col] = 0;
                    map[row][col + 1] = 0;
                    map[row + 1][col] = 0;
                    map[row + 1][col + 1] = 0;
                    map[row + direction.getRow()][col + direction.getCol()] = 4;
                    map[row + direction.getRow()][col + direction.getCol() + 1] = 4;
                    map[row + direction.getRow() + 1][col + direction.getCol()] = 4;
                    map[row + direction.getRow() + 1][col + direction.getCol() + 1] = 4;
                    blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
                    if (!isWithdraw) {
                        afterMove(direction);
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
                JOptionPane.showMessageDialog(this, "Warning: Invalid move!");
            }
        }

    /**
     * 这个方法用于重绘方块，主要是为了实现移动的效果
     * @param row 可移除，当前的行数
     * @param col 可移除，当前的列数
     * @param nextRow 不可移除，为方块设置新的位置的行数
     * @param nextCol 不可移除，为方块设置新的位置的列数
     * @param selectedBlock 不可移除，当前选中的方块
     */
    private void blockRepaint(int row, int col, int nextRow, int nextCol, Block selectedBlock) {
        selectedBlock.setRow(nextRow);
        selectedBlock.setCol(nextCol);
        selectedBlock.setLocation(selectedBlock.getCol() * selectedBlock.GRIDSIZE + 2, selectedBlock.getRow() * selectedBlock.GRIDSIZE + 2);
        selectedBlock.repaint();
    }

    /**
     * 这个方法用于在每次移动后进行计步和判断游戏是否结束，并会记录移动，便于实现撤回和通信
     * @param direction 移动的方向
     */
    private void afterMove(Direction direction) {
        stepCount++;
        updateStepLabel();
        this.logicController.setStep(stepCount);
        this.logicController.record(this.selectedBlock,direction);
        this.musicPlayer.playSoundEffectMovingBlock();
        if(!isSpectator){
            sendMessage(this.logicController.getMoves().getLast().toString());
        }
        this.isSpectator = this.logicController.getIsSpectator();
        if(this.logicController.isGameOver(stepCount)){
            isWinning = true;
            showVictoryDialog();
        }
    }
    /**
     * 这是一个用以生成结束的对话框
     */
    private void showVictoryDialog() {
        Object[] options = {"Continue", "Exit"};
        String timeText = null;
        if(timeElapsed != 0){
            int minutes = timeElapsed / 60;
            int seconds = timeElapsed % 60;
            timeText = String.format("%02d:%02d", minutes, seconds);
        }
        int choice = JOptionPane.showOptionDialog(
                this,
                "Congratulations! You solved this problem within " + stepCount + " steps" + (timeText == null ? "" :  (" and "+ timeText)),
                "Victory",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == JOptionPane.YES_OPTION) {
        } else {
            stopTimer();
            dispose();
            userInterfaceFrame.setVisible(true);
        }
    }
    /**
     * 这是一个键盘绑定类的实现，主要是为了实现键盘的控制
     */
    private class KeyBindingExample extends JPanel {
        /**
         * 这是一个构造方法，主要用于设置键盘绑定
         */
        public KeyBindingExample() {
            setFocusable(true);

            InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
            inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
            inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
            inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");

            actionMap.put("moveUp", new AbstractAction() {
                /**
                 * 处理向上移动的事件
                 * @param e 待处理事件
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.UP,false);
                }
            });

            /**
             * 处理向下移动的事件
             * @param e 待处理事件
             */
            actionMap.put("moveDown", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.DOWN,false);
                }
            });

            /**
             * 处理向左移动的事件
             * @param e 待处理事件
             */
            actionMap.put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.LEFT,false);
                }
            });

            /**
             * 处理向右移动的事件
             * @param e 待处理事件
             */
            actionMap.put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.RIGHT,false);
                }
            });

            /**
             * 处理关闭窗口的事件
             * @param e 待处理事件
             */
            actionMap.put("escape", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    quitGame();
                }
            });
        }

        /**
         * 重绘组件的方法，用于绘制提示信息
         * @param g 需要重绘的组件
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    /**
     * 这是一个获取当前的blocks的方法
     * @return blocks
     */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    /**
     * 查询对应的block，撤回的重要工具函数
     * @param row 查找板块的行数
     * @param col 查找板块的列数
     * @param type 查找板块的属性
     * @return 对应的Block对象，如果没有找到则返回null
     */
    public Block getBlock(int row, int col, int type) {
        for (Block block : blocks) {
            if (block.getRow() == row && block.getCol() == col && block.getType() == type) {
                return block;
            }
        }
        return null;
    }
}
