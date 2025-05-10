package logic;

import frame.*;
import record.*;
import view.*;

public class LogicController {

    private int[][] map;
    private Level level;
    private GameFrame frame;
    private GameRecorder gameRecorder;
    private final static int HEIGHT = 4;
    private final static int WIDTH = 5;

    public LogicController( Level level,User user, GameFrame frame) {

        this.map = LogicController.copyMap(level);
        this.frame = frame;
        this.gameRecorder = new GameRecorder(map,frame.getLevelSelectionFrame().getUser());

    }

    public int getId(int row, int col) {
        return map[row][col];
    }

    public int[][] getMap() {
        return map;
    }

    public boolean isGameOver() {
        if(map[1][3] == 4 && map[2][3] == 4 && map[1][4] == 4 && map[2][4] == 4) {
            /*if(this.gameRecorder.getUser().getBestRecord()[level])*/

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

}