package logic;

import view.Direction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.*;

import static view.Direction.UP;
import static view.Direction.DOWN;
import static view.Direction.RIGHT;
import static view.Direction.LEFT;

/**
 * 这是一个解答器类，使用DFS算法来寻找最优解
 * 通过深度优先搜索来找到最短路径
 * 该类会在初始化时自动开始寻找答案
 */
public class AnswerSolver {
    private static final int TARGET_ROW = 3;
    private static final int TARGET_COL = 1;
    private final LogicController controller;
    private int[][] newMap;

    List<Direction> finalSolution;
    List<Integer> x_selected;
    List<Integer> y_selected;
    List<Integer> type_selected;

    List<int[][]> pastMaps = new ArrayList<>();


    /**
     * 有参构造器，接收一个LogicController对象，并开始寻找答案
     * @param controller 逻辑控制器对象
     */
    public AnswerSolver(LogicController controller) {
        this.controller = deepCopyLogicController(controller);
        this.finalSolution = null;
        this.x_selected = new ArrayList<>();
        this.y_selected = new ArrayList<>();
        this.type_selected = new ArrayList<>();
        pastMaps.add(this.controller.deepCopyMap());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> solve(new ArrayList<>(), 1));
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }


    /**
     * 使用DFS解决问题
     * 创建一个queue队列储存剩余可能的情况
     * 每次取出一个，进行移动，移动后判断是否重复，重复则跳过，不重复则加入pastMaps，然后继续移动
     * 直到找到答案或者没有答案
     */
    public void solve(List<Direction> directions,int step) {
        Queue<Node> queue = tryMove(controller.deepCopyMap());
        while(!queue.isEmpty()){
            Node current = queue.poll();
            directions.add(current.path);
            x_selected.add(current.x);
            y_selected.add(current.y);
            type_selected.add(current.type);
            newMap = move(this.controller.deepCopyMap(), current.path, current.x, current.y, current.type);
            if(!isAdded(newMap,pastMaps)){
                directions.remove(directions.size()-1);
                x_selected.remove(x_selected.size()-1);
                y_selected.remove(y_selected.size()-1);
                type_selected.remove(type_selected.size()-1);
                continue;
            }
            else{
                pastMaps.add(deepCopyMap(newMap));
            }
            controller.changeMap(deepCopyMap(newMap));
            if(isSolved()){
                if(finalSolution==null||finalSolution.size()>directions.size()){
                    finalSolution = new ArrayList<>(directions);
                }
                return;
            }
            solve(directions,step+1);
            if(finalSolution!=null){
                return;
            }
            x_selected.remove(x_selected.size()-1);
            y_selected.remove(y_selected.size()-1);
            type_selected.remove(type_selected.size()-1);
            directions.remove(directions.size()-1);
            pastMaps.removeLast();
            controller.changeMap(deepCopyMap(pastMaps.get(pastMaps.size()-1)));
        }
        return;
    }

    /**
     * 判断地图是否重复
     */
    private boolean isAdded(int[][] map, List<int[][]> pastMaps) {
        for (int[][] pastMap : pastMaps) {
            if (Arrays.deepEquals(pastMap, map)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 得到最终答案
     */
    public List<Direction> getFinalSolution() {
        return finalSolution;
    }

    /**
     * 判断是否到达终点
     */
    private boolean isSolved() {
        int[][] map = this.controller.getMap();
        return map[TARGET_ROW][TARGET_COL] == 4 &&
                map[TARGET_ROW+1][TARGET_COL] == 4 &&
                map[TARGET_ROW][TARGET_COL+1] == 4 &&
                map[TARGET_ROW+1][TARGET_COL+1] == 4;
    }

    /**
     * 用于判断那些方块可以移动
     */
    private Queue<Node> tryMove(int[][] currentMap) {
        boolean[][] visited = new boolean[5][4];
        Queue<Node> queue = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (currentMap[i][j] != 0) {
                    visited[i][j] = true;
                }
            }
        }
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                if(visited[i][j]){
                    switch (currentMap[i][j]){
                        case 1:
                            visited[i][j]=false;
                            if(j>=1&&currentMap[i][j-1]==0){
                                queue.add(new Node(i,j,LEFT,1));
                            }
                            if(j<=2&&currentMap[i][j+1]==0){
                                queue.add(new Node(i,j,RIGHT,1));
                            }
                            if(i>=1&&currentMap[i-1][j]==0){
                                queue.add(new Node(i,j,UP,1));
                            }
                            if(i<=3&&currentMap[i+1][j]==0){
                                queue.add(new Node(i,j,DOWN,1));
                            }


                            continue;
                        case 2:
                            visited[i][j]=false;
                            visited[i][j+1]=false;
                            if(j>=1&&currentMap[i][j-1]==0){
                                queue.add(new Node(i,j,LEFT,2));
                            }
                            if(j<=1&&currentMap[i][j+2]==0){
                                queue.add(new Node(i,j,RIGHT,2));
                            }
                            if(i>=1&&currentMap[i-1][j]==0&&currentMap[i-1][j+1]==0){
                                queue.add(new Node(i,j,UP,2));
                            }
                            if(i<=3&&currentMap[i+1][j]==0&&currentMap[i+1][j+1]==0){
                                queue.add(new Node(i,j,DOWN,2));
                            }

                            continue;
                        case 3:
                            visited[i][j]=false;
                            visited[i+1][j]=false;
                            if(j>=1&&currentMap[i][j-1]==0&&currentMap[i+1][j-1]==0){
                                queue.add(new Node(i,j,LEFT,3));
                            }
                            if(j<=2&&currentMap[i][j+1]==0&&currentMap[i+1][j+1]==0){
                                queue.add(new Node(i,j,RIGHT,3));
                            }
                            if(i>=1&&currentMap[i-1][j]==0){
                                queue.add(new Node(i,j,UP,3));
                            }
                            if(i<=2&&currentMap[i+2][j]==0){
                                queue.add(new Node(i,j,DOWN,3));
                            }

                            continue;
                        case 4:
                            visited[i][j]=false;
                            visited[i+1][j]=false;
                            visited[i][j+1]=false;
                            visited[i+1][j+1]=false;
                            if(j>=1&&currentMap[i][j-1]==0&&currentMap[i+1][j-1]==0){
                                queue.add(new Node(i,j,LEFT,4));
                            }
                            if(j<=1&&currentMap[i][j+2]==0&&currentMap[i+1][j+2]==0){
                                queue.add(new Node(i,j,RIGHT,4));
                            }
                            if(i>=1&&currentMap[i-1][j]==0&&currentMap[i-1][j+1]==0){
                                queue.add(new Node(i,j,UP,4));
                            }
                            if(i<=2&&currentMap[i+2][j]==0&&currentMap[i+2][j+1]==0){
                                queue.add(new Node(i,j,DOWN,4));
                            }
                            continue;
                    }
                }
            }
        }
        return queue;
    }



    /**
     * 用于BFS
     */
    private static class Node {
        int x,y;
        Direction path;
        int type;
        Node(int x,int y, Direction p ,int t) {
            this.x = x;
            this.y = y;
            this.path = p;
            this.type = t;
        }
    }



    /**
     * 拷贝玩家现在游戏状态
     * @param orig 原始的LogicController对象
     * @return 深拷贝的LogicController对象
     */
    private static LogicController deepCopyLogicController(LogicController orig) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(orig);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (LogicController) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }



    /**
     * 实现controller的移动功能
     */
    private int[][] move(int[][] map,Direction dir,int x,int y,int type){
        switch (type){
            case 1:
                map[x][y] = 0;
                switch (dir){
                    case UP:
                        map[x-1][y] = 1;
                        break;
                    case DOWN:
                        map[x+1][y] = 1;
                        break;
                    case LEFT:
                        map[x][y-1] = 1;
                        break;
                    case RIGHT:
                        map[x][y+1] = 1;
                        break;
                }
                break;
            case 2:
                map[x][y] = 0;
                map[x][y+1] = 0;
                switch (dir){
                    case UP:
                        map[x-1][y] = 2;
                        map[x-1][y+1] = 2;
                        break;
                    case DOWN:
                        map[x+1][y] = 2;
                        map[x+1][y+1] = 2;
                        break;
                    case LEFT:
                        map[x][y-1] = 2;
                        map[x][y] = 2;
                        break;
                    case RIGHT:
                        map[x][y+1] = 2;
                        map[x][y+2] = 2;
                }
                break;
            case 3:
                map[x][y] = 0;
                map[x+1][y] = 0;
                switch (dir){
                    case UP:
                        map[x-1][y] = 3;
                        map[x][y] = 3;
                        break;
                    case DOWN:
                        map[x+1][y] = 3;
                        map[x+2][y] = 3;
                        break;
                    case LEFT:
                        map[x][y-1] = 3;
                        map[x+1][y-1] = 3;
                        break;
                    case RIGHT:
                        map[x][y+1] = 3;
                        map[x+1][y+1] = 3;
                        break;
                }
                break;
            case 4:
                map[x][y] = 0;
                map[x][y+1] = 0;
                map[x+1][y] = 0;
                map[x+1][y+1] = 0;
                switch (dir){
                    case UP:
                        map[x-1][y] = 4;
                        map[x-1][y+1] = 4;
                        map[x][y] = 4;
                        map[x][y+1] = 4;
                        break;
                    case DOWN:
                        map[x+1][y] = 4;
                        map[x+1][y+1] = 4;
                        map[x+2][y] = 4;
                        map[x+2][y+1] = 4;
                        break;
                    case LEFT:
                        map[x][y-1] = 4;
                        map[x][y] = 4;
                        map[x+1][y-1] = 4;
                        map[x+1][y] = 4;
                        break;
                    case RIGHT:
                        map[x][y+1] = 4;
                        map[x][y+2] = 4;
                        map[x+1][y+1] = 4;
                        map[x+1][y+2] = 4;
                        break;
                }
                break;
        }
        return map;
    }



    /**
     * 复制map
     */
    public int[][] deepCopyMap(int[][] map) {
        if (map == null) return null;

        int[][] copy = new int[map.length][];
        for (int i = 0; i < map.length; i++) {
            copy[i] = new int[map[i].length];
            System.arraycopy(map[i], 0, copy[i], 0, map[i].length);
        }
        return copy;
    }

    /**
     * 获取当前选中的坐标和类型
     * @return
     */
    public List<Integer> getX_selected() {
        return x_selected;
    }
    public List<Integer> getY_selected() {
        return y_selected;
    }
    public List<Integer> getType_selected() {
        return type_selected;
    }
}
