package frame;

import frame.theme.Style;
import logic.LogicController;
import record.Move;
import record.GameRecorder;
import record.User;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static frame.theme.Style.styleBtn;

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
    private GameRecorder gameRecorder;

    private Timer gameTimer;
    private ArrayList<BoxComponent> boxes;
    private LogicController logicController;
    private User user;


    public GameFrame(LevelSelectionFrame selectionFrame,User user,Level level, boolean isTimed) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.selectionFrame = selectionFrame;
        this.isTimed = isTimed;
        this.user = user;
        this.gameRecorder = new GameRecorder(LogicController.copyMap(level),this.user);
        this.logicController = new LogicController(level,user, this);


        setTitle("Klotski Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel();
        initializeKlotskiBoard(level);
        setListener();

    }

    private void initializeKlotskiBoard(Level map) {

        int[][] mapInitializer = LogicController.copyMap(map);

        ArrayList<BoxComponent> boxes = new ArrayList<>();

        for (int i = 0; i < mapInitializer.length; i++) {
            for (int j = 0; j < mapInitializer[0].length; j++) {
                BoxComponent box = null;
                if (mapInitializer[i][j] == 1) {
                    box = new BoxComponent(Color.ORANGE, i, j, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                } else if (mapInitializer[i][j] == 2) {
                    box = new BoxComponent(Color.GREEN, i, j, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                } else if (mapInitializer[i][j] == 3) {
                    box = new BoxComponent(Color.BLUE, i, j, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                } else if (mapInitializer[i][j] == 4) {
                    box = new BoxComponent(Color.RED, i, j, this);
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
        System.out.println("Timer started.");
    }

    private void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
            System.out.println("Timer stopped.");
        }
    }

    private void updateTimerLabel() {
        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void updateStepLabel(){
        stepsLabel.setText("Steps: " + stepCount);
    }

    private void getBackgroundPanel() {
        this.backgroundPanel = new BackgroundPanel("path/to/game_wallpaper.jpg");
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        this.getKlotskiBoardPanel();

        backgroundPanel.add(getInfoPanel(isTimed), BorderLayout.NORTH);
        backgroundPanel.add(getBoardContainer(), BorderLayout.CENTER);
        backgroundPanel.add(getControlPanel(), BorderLayout.SOUTH);
        backgroundPanel.add(getDirectionPanel(), BorderLayout.EAST);
        backgroundPanel.add(getDirectionPanel(), BorderLayout.WEST);
    }

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

        upButton.addActionListener(e -> doMove(Direction.UP,false));
        downButton.addActionListener(e -> doMove(Direction.DOWN,false));
        leftButton.addActionListener(e -> doMove(Direction.LEFT,false));
        rightButton.addActionListener(e -> doMove(Direction.RIGHT,false));

        return directionPanel;

    }

    private JPanel getControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setOpaque(false);
        JButton saveButton = new JButton("Save Game");
        JButton reloadButton = new JButton("Reload");
        JButton withdrawButton = new JButton("Withdraw");
        JButton answerButton = new JButton("Show Answer");
        JButton quitButton = new JButton("Quit");

        styleBtn(saveButton);
        styleBtn(reloadButton);
        styleBtn(withdrawButton);
        styleBtn(answerButton);
        styleBtn(quitButton);

        controlPanel.add(saveButton);
        controlPanel.add(reloadButton);
        controlPanel.add(withdrawButton);
        controlPanel.add(answerButton);
        controlPanel.add(quitButton);

        saveButton.addActionListener(e -> saveGame());
        reloadButton.addActionListener(e -> reloadGame());
        withdrawButton.addActionListener(e -> withdrawGame());
        answerButton.addActionListener(e -> showAnswer());
        quitButton.addActionListener(e -> quitGame());

        return controlPanel;
    }

    private void getKlotskiBoardPanel() {
        this.klotskiBoardPanel = new JPanel(null);
        this.klotskiBoardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        this.klotskiBoardPanel.setBackground(new Color(200, 200, 200, 200));
        this.klotskiBoardPanel.setPreferredSize(new Dimension(500, 400));
        this.klotskiBoardPanel.add(new KeyBindingExample());
    }

    private JPanel getBoardContainer() {


        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setOpaque(false);
        boardContainer.add(klotskiBoardPanel);

        return boardContainer;
    }

    private void saveGame() {
        stopTimer();
        System.out.println("Save Game button clicked.");
        JOptionPane.showMessageDialog(this, "Save functionality not implemented yet.");
    }

    private void reloadGame() {

        for(int i = gameRecorder.getMoves().size(); i > 0; i--){
            withdrawGame();
        }

    }

    private void withdrawGame() {

        if(this.gameRecorder.getMoves().isEmpty()){
            JOptionPane.showMessageDialog(this, "No moves to withdraw.");
            return;
        }else {
            Move withdrawedMove = this.gameRecorder.getMoves().pop();
            this.selectedBox = withdrawedMove.getBox();
            Direction direction = Direction.getOpposite(withdrawedMove.getDirection());
            doMove(direction, true);
            this.selectedBox = boxes.getLast();
            repaint();
        }
    }

    private void showAnswer() {
        System.out.println("Show Answer button clicked.");
    }

    private void quitGame() {
        stopTimer();
        dispose();
    }

    public BoxComponent getSelectedBox() {
        return selectedBox;
    }

    public void setSelectedBox(BoxComponent selectedBox) {
        this.selectedBox = selectedBox;
    }

    private void setListener() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopTimer();
                if (!selectionFrame.isVisible()) {
                    selectionFrame.setVisible(true);
                }
            }
        });


        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                klotskiBoardPanel.requestFocusInWindow();
            }
        });

    }



    private void doMove(Direction direction,boolean isWithdraw) {
        int row = selectedBox.getRow();
        int col = selectedBox.getCol();
        int[][] map = logicController.getMap();
        if (this.logicController.getMap()[row][col] == 1 && Move.checkMoveValidity(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 1;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 2 && Move.checkMoveValidity(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 2;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 3 && Move.checkMoveValidity(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + 1][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 3;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        } else if (map[row][col] == 4 && Move.checkMoveValidity(4, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col - 1] = 0;
            map[row + direction.getRow()][col - 1] = 4;
            map[row + direction.getRow()][col - 2] = 4;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            if(!isWithdraw){
                afterMove(direction);
            }
        }
    }

    private void boxRepaint(int row, int col, int nextRow, int nextCol, BoxComponent selectedBox) {

        // this method used to repaint the block to implement the move action

        selectedBox.setCol(nextCol);
        selectedBox.setRow(nextRow);
        selectedBox.setLocation(selectedBox.getCol() * selectedBox.GRIDSIZE + 2, selectedBox.getRow() * selectedBox.GRIDSIZE + 2);
        selectedBox.repaint();
    }

    private void afterMove(Direction direction) {
        stepCount++;
        updateStepLabel();
        this.gameRecorder.record(this.selectedBox,direction);
        if(this.logicController.isGameOver()){
            JOptionPane.showMessageDialog(this, "You win within" + stepCount + "steps!" +'\n' + "Your best record is ");
            this.dispose();
        }
    }

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
                    System.out.println("Escape pressed");
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

    public LevelSelectionFrame getLevelSelectionFrame() {
        return this.selectionFrame;
    }

    public ArrayList<BoxComponent> getBoxes() {
        return boxes;
    }
}
