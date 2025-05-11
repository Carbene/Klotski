package logic;

import frame.*;
import record.*;
import view.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.util.Stack;

public class LogicController implements Serializable {

    private int[][] map;
    private Level level;
    private GameFrame frame;
    private User user;
    private int step;
    private int time;
    private final Stack<Move> moves;
    private final static int HEIGHT = 4;
    private final static int WIDTH = 5;

    public LogicController( Level level,User user, GameFrame frame) {
        this.map = LogicController.copyMap(level);
        this.frame = frame;
        this.user = user;
        this.moves = new Stack<>();
    }

    public int getId(int row, int col) {
        return map[row][col];
    }

    public int[][] getMap() {
        return map;
    }

    public boolean isGameOver() {
        if(map[1][3] == 4 && map[2][3] == 4 && map[1][4] == 4 && map[2][4] == 4) {
            if(this.user.getBestRecord()[level.getCODE()][0] < step) {
                this.user.getBestRecord()[level.getCODE()][0] = this.step;
            }
            if(this.user.getBestRecord()[level.getCODE()][1] > time && time != 0) {
                this.user.getBestRecord()[level.getCODE()][1] = this.time;
            }

            return true;
        }
        return false;
    }

    public static int[][] copyMap(Level level) {
        int[][] map = new int[level.getHeight()][level.getWidth()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = level.getMAP()[i][j];
            }
        }
        return map;
    }

    public static int[][] copyMap(int[][] map) {
        int[][] mapCopy = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = map[i][j];
            }
        }
        return map;
    }

    public void stepAccumulate() {
        this.step++;
    }

    public int getStep(){
        return this.step;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    public void record(BoxComponent selectedBoxComponent, Direction direction) {
        moves.push(new Move(selectedBoxComponent, direction));
    }

    public Stack<Move> getMoves(){
        return moves;
    }

    public static void saveGame(LogicController controller) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Find your directory to save");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        String fileName = System.currentTimeMillis() + ".save";
        try{
            FileOutputStream fileOut = new FileOutputStream(filePath+ File.separator+fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(controller);
            objectOut.close();
            fileOut.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static LogicController loadGame(UserInterfaceFrame userInterfaceFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Find your save to load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Save files", "save"));
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        try{
            FileInputStream fileInput = new FileInputStream(filePath);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            if(objectInput.readObject() instanceof LogicController) {
                LogicController controller = (LogicController) objectInput.readObject();
                if(controller.user.getId().equals(userInterfaceFrame.getUser().getId())) {
                    return controller;
                } else {
                    JOptionPane.showMessageDialog(userInterfaceFrame, "This is not your save file", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } else {
                System.err.println("This is not a save file.");
                JOptionPane.showMessageDialog(userInterfaceFrame, "This is not a save file", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

        }catch(Exception e){
            System.err.println(e);
        }

        return null;
    }

    public Level getLevel() {
        return level;
    }
}