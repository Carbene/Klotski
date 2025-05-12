package frame;

import logic.LogicController;
import record.Move;
import record.User;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static frame.Style.styleBtn;

/**
 * 这是游戏的主界面，包含了游戏的所有核心逻辑和视图
 */
public class GameFrame extends JFrame {
    private LevelSelectionFrame selectionFrame;
    private boolean isTimed;

    private JLabel stepsLabel;
    private JLabel timerLabel;
    private int stepCount = 0;
    private int timeElapsed = 0;

    private JPanel klotskiBoardPanel;
    private BackgroundPanel backgroundPanel;
    private BoxComponent selectedBox;

    private Timer gameTimer;
    private ArrayList<BoxComponent> boxes;
    private LogicController logicController;
    private User user;
    private MusicPlayer musicPlayer;
    private boolean isSaved = false;
    private UserInterfaceFrame userInterfaceFrame;

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
        this.logicController = new LogicController(level,user,isTimed);
        this.musicPlayer = musicPlayer;
        this.userInterfaceFrame = userInterfaceFrame;

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel();
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
        this.logicController = logicController;
        this.isTimed = logicController.getTime() != 0;
        this.musicPlayer = musicPlayer;
        this.stepCount = logicController.getStep();
        this.timeElapsed = logicController.getTime();

        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel();
        reloadKlotskiBoard(LogicController.copyMap(this.logicController.getMap()));
        setListener();
    }

    /**
     * 这个方法会消耗一个二维数组类型的变量，生成所有的Box，并生成Boxes，并绘制
     * @param map 被消耗的地图
     */
    private void initializeKlotskiBoard(Level map) {
        int[][] mapInitializer = LogicController.copyMap(map);

        ArrayList<BoxComponent> boxes = new ArrayList<>();

        for (int i = 0; i < mapInitializer.length; i++) {
            for (int j = 0; j < mapInitializer[0].length; j++) {
                BoxComponent box = null;
                if (mapInitializer[i][j] == 1) {
                    box = new BoxComponent(Color.ORANGE, i, j,1, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                } else if (mapInitializer[i][j] == 2) {
                    box = new BoxComponent(Color.GREEN, i, j,2, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                } else if (mapInitializer[i][j] == 3) {
                    box = new BoxComponent(Color.BLUE, i, j,3, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                } else if (mapInitializer[i][j] == 4) {
                    box = new BoxComponent(Color.RED, i, j,4, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                    mapInitializer[i + 1][j + 1] = 0;
                }
                if (box != null) {
                    klotskiBoardPanel.add(box);
                    box.setLocation(j * box.GRIDSIZE + 2, i * box.GRIDSIZE + 2);
                    boxes.add(box);
                }
            }
        }
        this.repaint();
        boxes.add(new BoxComponent(this));
        this.selectedBox = boxes.getLast();
        klotskiBoardPanel.revalidate();
        klotskiBoardPanel.repaint();
        klotskiBoardPanel.setFocusable(true);
        this.boxes = boxes;
    }

    /**
     * 这是一个重载方法，主要用于读取存档时使用，依然会消耗一个二维数组类型的变量，生成所有的Box，并生成Boxes，并绘制
     * @param map 被消耗的地图
     */
    private void reloadKlotskiBoard(int[][] map) {
        this.boxes = new ArrayList<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                BoxComponent box = null;
                if (map[i][j] == 1) {
                    box = new BoxComponent(Color.ORANGE, i, j,1, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE);
                    map[i][j] = 0;
                } else if (map[i][j] == 2) {
                    box = new BoxComponent(Color.GREEN, i, j,2, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;
                } else if (map[i][j] == 3) {
                    box = new BoxComponent(Color.BLUE, i, j,3, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                } else if (map[i][j] == 4) {
                    box = new BoxComponent(Color.RED, i, j,4, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                    map[i][j + 1] = 0;
                    map[i + 1][j + 1] = 0;
                }
                if (box != null) {
                    klotskiBoardPanel.add(box);
                    box.setLocation(j * box.GRIDSIZE + 2, i * box.GRIDSIZE + 2);
                    boxes.add(box);
                }
            }
        }
        this.repaint();
        boxes.add(new BoxComponent(this));
        this.selectedBox = boxes.getLast();
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
    private void getBackgroundPanel() {
        this.backgroundPanel = new BackgroundPanel("/gameBackgroundPic.jpg");
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        this.getKlotskiBoardPanel();

        backgroundPanel.add(getInfoPanel(isTimed), BorderLayout.NORTH);
        backgroundPanel.add(getBoardContainer(), BorderLayout.CENTER);
        backgroundPanel.add(getControlPanel(), BorderLayout.SOUTH);
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
            doMove(Direction.UP, false);
            this.musicPlayer.playSoundEffectPressingButton();
        });
        downButton.addActionListener(e -> {
            doMove(Direction.DOWN,false);
            this.musicPlayer.playSoundEffectPressingButton();
        });
        leftButton.addActionListener(e -> {
            doMove(Direction.LEFT,false);
            this.musicPlayer.playSoundEffectPressingButton();
        });
        rightButton.addActionListener(e -> {
            doMove(Direction.RIGHT,false);
            this.musicPlayer.playSoundEffectPressingButton();
        });

        return directionPanel;

    }

    /**
     * 这是下方的控制面板的获取方法，包括多个按钮，如读档功能的GUI实现基于这个，并赋予各个按钮监听器激活对应的功能
     * @return 控制面板
     */
    private JPanel getControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setOpaque(false);
        JButton saveButton = new JButton("Save Game");
        JButton withdrawButton = new JButton("Withdraw");
        JButton reloadButton = new JButton("Reload");
        JButton answerButton = new JButton("Show Answer");
        JButton quitButton = new JButton("Quit");

        styleBtn(saveButton);
        styleBtn(withdrawButton);
        styleBtn(reloadButton);
        styleBtn(answerButton);
        styleBtn(quitButton);

        controlPanel.add(saveButton);
        controlPanel.add(withdrawButton);
        controlPanel.add(reloadButton);
        controlPanel.add(answerButton);
        controlPanel.add(quitButton);

        saveButton.addActionListener(e -> {
            LogicController.saveGame(this.logicController,this.user);
            this.musicPlayer.playSoundEffectPressingButton();
            this.isSaved = true;
        });
        withdrawButton.addActionListener(e -> withdrawMove(true));
        reloadButton.addActionListener(e -> reloadGame());
        answerButton.addActionListener(e -> showAnswer());
        quitButton.addActionListener(e -> quitGame());

        return controlPanel;
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
            this.selectedBox.setSelected(false);
            this.selectedBox = this.getBox(withdrawedMove.getCoordinate()[0],  withdrawedMove.getCoordinate()[1], withdrawedMove.getType());
            Direction direction = Direction.getOpposite(withdrawedMove.getDirection());
            doMove(direction, true);
            this.selectedBox.setSelected(false);
            this.selectedBox = boxes.getLast();
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
    public BoxComponent getSelectedBox() {
        return selectedBox;
    }

    /**
     * 设置当前面板选中的方块，保证视图与底层控制的统一
     * @param selectedBox 方块
     */
    public void setSelectedBox(BoxComponent selectedBox) {
        this.selectedBox = selectedBox;
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
     * box移动的具体实现
     * @param direction 移动的方向
     * @param isWithdraw 是否是撤回，决定计步
     */
    private void doMove(Direction direction,boolean isWithdraw) {
        int row = selectedBox.getRow();
        int col = selectedBox.getCol();
        int[][] map = logicController.getMap();
        if (this.logicController.getMap()[row][col] == 1 && Move.validateMove(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 1;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 2 && Move.validateMove(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 2;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 3 && Move.validateMove(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + 1][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 3;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
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
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        }
    }

    /**
     * 这个方法用于重绘方块，主要是为了实现移动的效果
     * @param row 可移除，当前的行数
     * @param col 可移除，当前的列数
     * @param nextRow 不可移除，为方块设置新的位置的行数
     * @param nextCol 不可移除，为方块设置新的位置的列数
     * @param selectedBox 不可移除，当前选中的方块
     */
    private void boxRepaint(int row, int col, int nextRow, int nextCol, BoxComponent selectedBox) {
        selectedBox.setRow(nextRow);
        selectedBox.setCol(nextCol);
        selectedBox.setLocation(selectedBox.getCol() * selectedBox.GRIDSIZE + 2, selectedBox.getRow() * selectedBox.GRIDSIZE + 2);
        selectedBox.repaint();
    }

    /**
     * 这个方法用于在每次移动后进行计步和判断游戏是否结束，并会记录移动，便于实现撤回和通信
     * @param direction 移动的方向
     */
    private void afterMove(Direction direction) {
        stepCount++;
        updateStepLabel();

        this.logicController.setStep(stepCount);
        this.logicController.record(this.selectedBox,direction);
        this.musicPlayer.playSoundEffectMovingBlock();
        if(this.logicController.isGameOver()){
            JOptionPane.showMessageDialog(this, "You win within" + stepCount + "steps!" +'\n' + "Your best record is ");
            this.dispose();
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
     * 这是一个获取当前的boxes的方法
     * @return boxes
     */
    public ArrayList<BoxComponent> getBoxes() {
        return boxes;
    }

    /**
     * 查询对应的box，撤回的重要工具函数
     * @param row 查找板块的行数
     * @param col 查找板块的列数
     * @param type 查找板块的属性
     * @return
     */
    public BoxComponent getBox(int row, int col,int type) {
        for (BoxComponent box : boxes) {
            if (box.getRow() == row && box.getCol() == col && box.getType() == type) {
                return box;
            }
        }
        return null;
    }
}
