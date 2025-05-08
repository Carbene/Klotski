package logic;

import frame.*;
import record.*;

public class LogicController {

    private int[][] map;
    private GameFrame frame;
    private GameRecorder gameRecorder;
    private final static int HEIGHT = 4;
    private final static int WIDTH = 5;

    public LogicController( int[][] map, GameFrame frame) {

        this.map = map;
        this.frame = frame;
        this.gameRecorder = new GameRecorder(map,frame);

    }

    public int getId(int row, int col) {
        return map[row][col];
    }

    public int[][] getMap() {
        return map;
    }

    public boolean isGameOver() {
        if(map[1][3] == 4 && map[2][3] == 4 && map[1][4] == 4 && map[2][4] == 4) {
            return true;
        }
        return false;
    }

}