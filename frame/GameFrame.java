package frame;

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
import java.util.concurrent.CountDownLatch;
import java.util.Queue;

import static frame.Style.styleBtn;

/**
 * 这是游戏的主界面，包含了游戏的所有核心逻辑和视图
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
    private int stepCount = 0;
    private int timeElapsed = 0;
    private int imagePathFollowing = 0;

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
    private final CountDownLatch latch = new CountDownLatch(1);

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
        setListener();

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

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel(false);
        setClientSocket();
        loadKlotskiBoard(LogicController.copyMap(this.logicController.getMap()));
        setListener();
    }

    public GameFrame(UserInterfaceFrame userInterfaceFrame,MusicPlayer musicPlayer, Socket socket) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.socket = socket;
        this.user = userInterfaceFrame.getUser();
        this.musicPlayer = musicPlayer;
        this.isSpectator = true;

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel(true);
        this.setSocket();
        setListener();
    }

    private void setSocket() {
       new Thread(() -> {
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                this.out = new PrintWriter(socket.getOutputStream(),true);
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    String[] input = line.split(" ");
                    if(input[0].equals("Map")){
                        this.logicController = new LogicController(Level.getLevel(Integer.parseInt(input[1])),this.user,false,true);
                        initializeKlotskiBoard(Level.getLevel(Integer.parseInt(input[1])));
                    } else if (input[0].equals("Move")) {
                        this.doMove(input);
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

    private void setClientSocket() {
        if(isTimed){
            return;
        }
        new Thread(() -> {
            try {
                this.serverSocket = new ServerSocket(8080);
                while(true){
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
                while((line = in.readLine()) != null){
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
                            repaint();
                            this.isSpectator = true;
                        }else{
                            this.sendMessage("Cancel");
                        }
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }

        }).start();
    }

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
                    block = new Block("/klotiskiDaBin.jpg", i, j,2, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                } else if (mapInitializer[i][j] == 3) {
                    block = new Block("/klotiskiGuanYu.jpg", i, j,3, this);
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
                    block = new Block("/klotiskiDaBin.jpg", i, j,2, this);
                    block.setSize(block.GRIDSIZE * 2, block.GRIDSIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;
                } else if (map[i][j] == 3) {
                    block = new Block("/klotiskiGuanYu.jpg", i, j,3, this);
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
        timeElapsed = 0;
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
            JButton quitButton = new JButton("Quit");

            styleBtn(saveButton);
            styleBtn(withdrawButton);
            styleBtn(reloadButton);
            styleBtn(answerButton);
            styleBtn(requestHelpButton);
            styleBtn(quitButton);

            controlPanel.add(saveButton);
            controlPanel.add(withdrawButton);
            controlPanel.add(reloadButton);
            controlPanel.add(answerButton);
            controlPanel.add(requestHelpButton);
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
            quitButton.addActionListener(e -> quitGame());

            return controlPanel;
        }else{
            JButton offerHelpButton = new JButton("Offer help");
            JButton quitButton = new JButton("Quit");

            styleBtn(offerHelpButton);
            styleBtn(quitButton);

            controlPanel.add(offerHelpButton);
            controlPanel.add(quitButton);

            offerHelpButton.addActionListener(e -> {
                this.musicPlayer.playSoundEffectPressingButton();
                this.sendMessage("Available help");
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
        this.klotskiBoardPanel.setPreferredSize(new Dimension(500, 400));
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
        System.out.println("Show Answer button clicked.");
    }

    /**
     * 这是退出游戏的具体实现
     * TODO: 切换的界面设置有问题，不能正确唤起用户界面
     */
    private void quitGame() {
        this.musicPlayer.playSoundEffectPressingButton();
        if(!isSaved) {
            int result = JOptionPane.showConfirmDialog(this, "This game has not been saved. Do you really want to quit?", "Warning", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                stopTimer();
                this.dispose();
            } else {
                return;
            }
        }else{
            stopTimer();
            dispose();
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
     * 为游戏面板设置监听器
     */
    private void setListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopTimer();
                if(selectionFrame != null){
                    if(!selectionFrame.isVisible() && !userInterfaceFrame.isVisible()){
                        selectionFrame.setVisible(true);
                        userInterfaceFrame.setVisible(true);
                    }
                }
                else{
                    if(!userInterfaceFrame.isVisible()){
                        userInterfaceFrame.setVisible(true);
                    }
                }
            }
        });

    }

    /**
     * block移动的具体实现
     * @param direction 移动的方向
     * @param isWithdraw 是否是撤回，决定计步
     */
    private void doMove(Direction direction,boolean isWithdraw) {
        int row = selectedBlock.getRow();
        int col = selectedBlock.getCol();
        int[][] map = logicController.getMap();
        if (this.logicController.getMap()[row][col] == 1 && Move.validateMove(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 1;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 2 && Move.validateMove(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 2;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 3 && Move.validateMove(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + 1][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 3;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
            blockRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBlock);
            if(!isWithdraw){
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

    private void doMove(String[] input){
        Direction direction = Direction.getDirection(Integer.parseInt(input[4]));
        this.selectedBlock = getBlock(Integer.parseInt(input[1])-direction.getRow(),Integer.parseInt(input[2])-direction.getCol(),Integer.parseInt(input[3]));
        doMove(direction,false);
        this.selectedBlock = this.blocks.getLast();
        repaint();
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
     *TODO: Some weird problem occurs here?
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
            showVictoryDialog();
        }
    }
    /**
     * 这是一个用以生成结束的对话框
     */
    private void showVictoryDialog() {
        Object[] options = {"继续游戏", "退出游戏"};

        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);

        int choice = JOptionPane.showOptionDialog(
                this,
                "恭喜！您以 " + stepCount + " 步、" + timeText + " 的成绩获胜！",
                "游戏胜利！",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // 处理用户选择
        if (choice == JOptionPane.YES_OPTION) {
        } else {
            stopTimer();
            dispose();
        }
    }
    /**
     * 这是一个键盘绑定的实现，主要是为了实现键盘的控制
     */
    private class KeyBindingExample extends JPanel {
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
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.UP,false);
                }
            });

            actionMap.put("moveDown", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.DOWN,false);
                }
            });

            actionMap.put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.LEFT,false);
                }
            });

            actionMap.put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.RIGHT,false);
                }
            });

            actionMap.put("escape", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    quitGame();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Use arrow keys to move", 50, 50);
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
     * @return
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
