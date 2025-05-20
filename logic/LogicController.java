package logic;

import frame.UserInterfaceFrame;
import record.*;
import view.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Stack;

/**
 * 这是逻辑控制器类，也是由它实现对局的可存档
 */
public class LogicController implements Serializable {
    private int[][] map;
    private Level level;
    private User user;
    private int step;
    private int time;
    private boolean isTimed;
    private final Stack<Move> moves;
    private final static int HEIGHT = 4;
    private final static int WIDTH = 5;

    /**
     * 有参构造器，构造一个新的逻辑
     * @param level 当前游戏地图
     * @param user 当前玩家
     * @param isTimed 当前模式
     */
    public LogicController(Level level,User user,boolean isTimed) {
        this.map = LogicController.copyMap(level);
        this.user = user;
        this.moves = new Stack<>();
        this.isTimed = isTimed;
        this.level = level;
    }



    /**
     * 获取当前矩阵记录下的位置信息，占位信息
     * @param row 行
     * @param col 列
     * @return 数据
     */
    public int getId(int row, int col) {
        return map[row][col];
    }

    /**
     * 返回当前地图
     * @return 地图
     */
    public int[][] getMap() {
        return map;
    }

    /**
     * 获胜逻辑
     * TODO:获胜通知栏尚不完整
     * @return 是否胜利
     */
    public boolean isGameOver(int stepCount) {
        if(map[1][3] == 4 && map[2][3] == 4 && map[1][4] == 4 && map[2][4] == 4) {
            if(user.getUserSymbol()==1){
                if(level!=null&&user!=null){
                    if(User.getBestRecord(user,level.getCODE(),0) > this.step) {
                        User.setBestRecord(level,user,0,step);
                    }
                    if(User.getBestRecord(user,level.getCODE(),1) > this.time) {
                        User.setBestRecord(level,user,1,time);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 复制一份用于消耗的地图
     * @param level 底层地图
     * @return 地图
     */
    public static int[][] copyMap(Level level) {
        int[][] map = new int[level.getHeight()][level.getWidth()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = level.getMAP()[i][j];
            }
        }
        return map;
    }

    /**
     * 复制一份用于消耗的地图
     * @param map 地图
     * @return 地图
     */
    public static int[][] copyMap(int[][] map) {
        int[][] mapCopy = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                mapCopy[i][j] = map[i][j];
            }
        }
        return mapCopy;
    }

    /**
     * 设置保存的步数
     * @param step 当前步数
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * 获得当前存档步数
     * @return 步数
     */
    public int getStep(){
        return this.step;
    }

    /**
     * 设置当前已用时
     * @param time 已用时间
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * 获取已用时间
     * @return 已用时间
     */
    public int getTime() {
        return this.time;
    }

    /**
     * 记录移动信息
     * @param selectedBoxComponent 被移动的板块
     * @param direction 方向
     */
    public void record(BoxComponent selectedBoxComponent, Direction direction) {
        moves.push(new Move(selectedBoxComponent, direction));
    }

    /**
     * 获取当前记录信息的栈
     * @return 移动信息栈
     */
    public Stack<Move> getMoves(){
        return moves;
    }

    /**
     * 序列化保存游戏
     * @param controller 当前的记录器
     * @param user 当前用户
     * @return 是否成功
     */
    public static boolean saveGame(LogicController controller,User user) {
        if(!user.getId().equals("Visitor")) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Find your directory to save");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.CANCEL_OPTION) {
                File file = fileChooser.getSelectedFile();
            }
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String fileName = System.currentTimeMillis() + ".save";
            try{
                FileOutputStream fileOut = new FileOutputStream(filePath+ File.separator+fileName);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(controller);
                objectOut.close();
                fileOut.close();
                return true;
            } catch (Exception e) {
                e.getStackTrace();
            }
        }else{
            JOptionPane.showMessageDialog(null, "Please login to save your game", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * 读取存档
     * @param userInterfaceFrame 唤起该方法的母界面
     * @return 读取存档的控制器
     */
    public static LogicController loadGame(UserInterfaceFrame userInterfaceFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Find your save to load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Save files", "save"));
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            try{
                FileInputStream fileInput = new FileInputStream(filePath);
                ObjectInputStream objectInput = new ObjectInputStream(fileInput);
                Object o = objectInput.readObject();
                if(o instanceof LogicController) {
                    LogicController controller = (LogicController)o;
                    if(controller.user.getId().equals(userInterfaceFrame.getUser().getId())) {
                        return controller;
                    } else {
                        JOptionPane.showMessageDialog(null, "This is not your save file", "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                } else {
                    System.err.println("This is not a save file.");
                    JOptionPane.showMessageDialog(null, "This is not a save file", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 变更地图状态
     */
    public void changeMap(int[][] map) {
        this.map = map;
    }
    /**
     * 复制map
     */
    public int[][] deepCopyMap() {
        if (this.map == null) return null;

        int[][] copy = new int[this.map.length][];
        for (int i = 0; i < this.map.length; i++) {
            copy[i] = new int[this.map[i].length];
            System.arraycopy(this.map[i], 0, copy[i], 0, this.map[i].length);
        }
        return copy;
    }
}