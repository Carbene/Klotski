package frame;

import frame.thme.Style;
import logic.LogicController;
import record.Move;
import record.GameRecorder;
import record.User;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static frame.thme.Style.styleBtn;

public class GameFrame extends JFrame {

    private LevelSelectionFrame selectionFrame;
    private String gameMode;
    private String gameLevel;
    private boolean bgmEnabled;

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


    public GameFrame(LevelSelectionFrame selectionFrame, String mode, Leveled map, boolean bgm) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.selectionFrame = selectionFrame;
        this.gameMode = mode;
        this.gameLevel = map.getCHINESENAME();
        this.bgmEnabled = bgm;
        this.gameRecorder = new GameRecorder(this.copyMap(map),new User());


        setTitle("Klotski Game - Level: " + gameLevel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getBackgroundPanel();
        initializeKlotskiBoard(map);
        setListener();
        this.logicController = new LogicController(copyMap(map), this);

    }

    private void initializeKlotskiBoard(Leveled map) {

        int[][] mapInitializer = copyMap(map);

        ArrayList<BoxComponent> boxes = new ArrayList<>();

        for (int i = 0; i < mapInitializer.length; i++) {
            for (int j = 0; j < mapInitializer[0].length; j++) {
                BoxComponent box = null;
                if (mapInitializer[i][j] == 1) {
                    box = new BoxComponent(Color.ORANGE, i, j, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                } else if (mapInitializer[i][j] == 2) {
                    box = new BoxComponent(Color.ORANGE, i, j, this);
                    box.setSize(box.GRIDSIZE * 2, box.GRIDSIZE);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i][j + 1] = 0;
                } else if (mapInitializer[i][j] == 3) {
                    box = new BoxComponent(Color.BLUE, i, j, this);
                    box.setSize(box.GRIDSIZE, box.GRIDSIZE * 2);
                    mapInitializer[i][j] = 0;
                    mapInitializer[i + 1][j] = 0;
                } else if (mapInitializer[i][j] == 4) {
                    box = new BoxComponent(Color.GREEN, i, j, this);
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

    private void getBackgroundPanel() {
        this.backgroundPanel = new BackgroundPanel("path/to/game_wallpaper.jpg");
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        this.getKlotskiBoardPanel();

        backgroundPanel.add(getInfoPanel(this.gameMode), BorderLayout.NORTH);
        backgroundPanel.add(getBoardContainer(), BorderLayout.CENTER);
        backgroundPanel.add(getControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel getInfoPanel(String gameMode) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        infoPanel.setOpaque(false);
        JLabel stepsLabel = new JLabel("Steps: 0");
        Style.styleLabel(stepsLabel);
        infoPanel.add(stepsLabel);

        JLabel timerLabel = new JLabel("Time: 00:00");
        if ("Time-limited".equals(gameMode)) {
            Style.styleLabel(timerLabel);
            infoPanel.add(timerLabel);
            startTimer();
        }

        return infoPanel;
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
        System.out.println("Reload Game button clicked.");
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

    private int[][] copyMap(Leveled leveled) {
        int[][] map = new int[leveled.getHeight()][leveled.getWidth()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = leveled.getMAP()[i][j];
            }
        }
        return map;
    }

    private void doMove(Direction direction) {
        int row = selectedBox.getRow();
        int col = selectedBox.getCol();
        int[][] map = logicController.getMap();
        if (this.logicController.getMap()[row][col] == 1 && Move.checkMoveValidity(1, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 1;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            afterMove(direction);
        } else if (map[row][col] == 2 && Move.checkMoveValidity(2, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col + 1] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 2;
            map[row + direction.getRow()][col + direction.getCol() + 1] = 2;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            afterMove(direction);
        } else if (map[row][col] == 3 && Move.checkMoveValidity(3, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row + 1][col] = 0;
            map[row + direction.getRow()][col + direction.getCol()] = 3;
            map[row + direction.getRow() + 1][col + direction.getCol()] = 3;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            afterMove(direction);
        } else if (map[row][col] == 4 && Move.checkMoveValidity(4, row + direction.getRow(), col + direction.getCol(), direction, this.logicController)) {
            map[row][col] = 0;
            map[row][col - 1] = 0;
            map[row + direction.getRow()][col - 1] = 4;
            map[row + direction.getRow()][col - 2] = 4;
            boxRepaint(row, col, row + direction.getRow(), col + direction.getCol(), selectedBox);
            afterMove(direction);
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
                    doMove(Direction.UP);
                }
            });

            actionMap.put("moveDown", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.DOWN);
                }
            });

            actionMap.put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.LEFT);
                }
            });

            actionMap.put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doMove(Direction.RIGHT);
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
}





/*
package frame;

import view.*;
import record.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

import static frame.thme.Style.styleBtn;
import static view.LeveledMap.LEVEL1;

public class GameFrame extends JFrame {

    private LevelSelectionFrame selectionFrame;
    private String gameMode;
    private String gameLevel;
    private boolean bgmEnabled;

    private JLabel stepsLabel;
    private JLabel timerLabel;
    private int stepCount = 0;
    private int timeElapsed = 0;

    private JPanel klotskiBoardPanel;
    private BackgroundPanel backgroundPanel;

    private BoxComponent selectedBox;

    private Timer gameTimer;

    public GameFrame(LevelSelectionFrame selectionFrame, String mode, String level, boolean bgm) {

        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.selectionFrame = selectionFrame;
        this.gameMode = mode;
        this.gameLevel = level;
        this.bgmEnabled = bgm;

        setTitle("Klotski Game - Level: " + level);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.backgroundPanel = new BackgroundPanel("path/to/game_wallpaper.jpg");
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        infoPanel.setOpaque(false);
        stepsLabel = new JLabel("Steps: 0");
        styleLabel(stepsLabel);
        infoPanel.add(stepsLabel);

        timerLabel = new JLabel("Time: 00:00");
        styleLabel(timerLabel);
        if ("Time-limited".equals(gameMode)) {
            infoPanel.add(timerLabel);
        }

        klotskiBoardPanel = new JPanel(null);
        klotskiBoardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        klotskiBoardPanel.setBackground(new Color(200, 200, 200, 200));
        klotskiBoardPanel.setPreferredSize(new Dimension(500, 400));
        initializeKlotskiBoard(LEVEL1.getMap().getMatrix());

        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setOpaque(false);
        boardContainer.add(klotskiBoardPanel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setOpaque(false);
        JButton saveButton = new JButton("Save Game");
        JButton reloadButton = new JButton("Reload");
        JButton answerButton = new JButton("Show Answer");
        JButton quitButton = new JButton("Quit");

        styleBtn(saveButton);
        styleBtn(reloadButton);
        styleBtn(answerButton);
        styleBtn(quitButton);

        controlPanel.add(saveButton);
        controlPanel.add(reloadButton);
        controlPanel.add(answerButton);
        controlPanel.add(quitButton);

        backgroundPanel.add(infoPanel, BorderLayout.NORTH);
        backgroundPanel.add(boardContainer, BorderLayout.CENTER);
        backgroundPanel.add(controlPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveGame());
        reloadButton.addActionListener(e -> reloadGame());
        answerButton.addActionListener(e -> showAnswer());
        quitButton.addActionListener(e -> quitGame());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopTimer();
                if (!selectionFrame.isVisible()) {
                    selectionFrame.setVisible(true);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    quitGame();
                }
            }
        });
        setFocusable(true);

        if ("Time-limited".equals(gameMode)) {
            startTimer();
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                klotskiBoardPanel.requestFocusInWindow();
            }
        });
    }

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(2, 3, 0, 0));
    }

    private void initializeKlotskiBoard(int[][] map) {
        int GRID_SIZE = 100;
        ArrayList<BoxComponent> boxes = new ArrayList<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                BoxComponent box = null;
                if (map[i][j] == 1) {
                    box = new BoxComponent(Color.ORANGE, i, j,this);
                    box.setSize(GRID_SIZE, GRID_SIZE);
                    map[i][j] = 0;
                } else if (map[i][j] == 2) {
                    box = new BoxComponent(Color.PINK, i, j,this);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;
                } else if (map[i][j] == 3) {
                    box = new BoxComponent(Color.BLUE, i, j,this);
                    box.setSize(GRID_SIZE, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                } else if (map[i][j] == 4) {
                    box = new BoxComponent(Color.GREEN, i, j,this);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                    map[i][j + 1] = 0;
                    map[i + 1][j + 1] = 0;
                }
                if (box != null) {

                    klotskiBoardPanel.add(box);
                    box.setLocation(j * GRID_SIZE + 2, i * GRID_SIZE + 2);
                    boxes.add(box);

                }
            }
        }
        this.repaint();

        klotskiBoardPanel.revalidate();
        klotskiBoardPanel.repaint();
        klotskiBoardPanel.setFocusable(true);
    }

    public void doMouseClick(Point point) {
        Component component = this.getComponentAt(point);
        if (component instanceof BoxComponent clickedComponent) {
            if (selectedBox == null) {
                selectedBox = clickedComponent;
                selectedBox.setSelected(true);
            } else if (selectedBox != clickedComponent) {
                selectedBox.setSelected(false);
                clickedComponent.setSelected(true);
                selectedBox = clickedComponent;
            } else {
                clickedComponent.setSelected(false);
                selectedBox = null;
            }
        }

    }

    */
/*protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.getID() == KeyEvent.KEY_PRESSED ) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> doMoveRight();
                case KeyEvent.VK_LEFT -> doMoveLeft();
                case KeyEvent.VK_UP -> doMoveUp();
                case KeyEvent.VK_DOWN -> doMoveDown();
            }
        }
    }

    private void doMoveRight() {

        if (selectedBox != null) {
            if (Move.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove(selectedBox, Direction.RIGHT);
            }
        }

    }

    private void doMoveLeft() {

        if (selectedBox != null) {
            if (Move.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove(selectedBox, Direction.RIGHT);
            }
        }

    }

    private void doMoveUp() {

        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove(selectedBox, Direction.RIGHT);
            }
        }

    }

    private void doMoveDown() {

        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove(selectedBox, Direction.RIGHT);
            }
        }

    }*//*







    private void startTimer() {
        if (gameTimer != null && gameTimer.isRunning()) gameTimer.stop();
        timeElapsed = 0;
        updateTimerLabel();
        gameTimer = new Timer(1000, e -> { timeElapsed++; updateTimerLabel(); });
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

    private void saveGame() {
        stopTimer();
        System.out.println("Save Game button clicked.");
        JOptionPane.showMessageDialog(this, "Save functionality not implemented yet.");
    }

    private void reloadGame() {
        System.out.println("Reload Game button clicked.");
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
}
*/
