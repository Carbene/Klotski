package logic;

import frame.*;
import record.*;
import view.*;

import java.util.Stack;

public class LogicController {

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

    public void stepAccumulate() {
        this.step++;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void record(BoxComponent selectedBoxComponent, Direction direction) {

        moves.push(new Move(selectedBoxComponent, direction));

    }

    public Stack<Move> getMoves(){
        return moves;
    }

    public static void saveGame(LogicController controller) {


    }
}