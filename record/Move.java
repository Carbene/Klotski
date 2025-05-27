package record;

import logic.LogicController;
import view.Direction;
import view.Block;

import java.io.Serializable;

/**
 * 一个记录工具类，帮助我们记录每一步的移动
 */
public class Move implements Serializable {

    private int[] coordinateInfo = new int[2];
    private int type;
    private Direction direction;
    private static final int HEIGHT = 5;
    private static final int WIDTH = 4;

    /**
     * 这是一个有参构造器，记录移动
     * @param selectedBlock 被移动的方块
     * @param direction 移动的方向
     */
    public Move(Block selectedBlock, Direction direction) {
        this.direction = direction;
        this.type = selectedBlock.getType();
        coordinateInfo[0] = selectedBlock.getRow();
        coordinateInfo[1] = selectedBlock.getCol();
    }

    /**
     * 获取这个移动的方向
     * @return 移动方向
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * 判断这一移动是否合法（不碰撞，不出边界）
     * @param type 板块的类型
     * @param nextRow 移动到的行数
     * @param nextCol 移动到的列数
     * @param direction 方向
     * @param controller 逻辑控制器
     * @return 移动的合法性的布尔值
     */
    public static boolean validateMove(int type, int nextRow, int nextCol, Direction direction, LogicController controller) {

        // this method used to ensure the move is valid so collision to the other block or the boundary will not happen.

        switch (type) {
            case 1 :
                return checkInHeightSize(nextRow) && checkInWidthSize(nextCol) && controller.getId(nextRow,nextCol) == 0;
            case 2 :
                if (checkInHeightSize(nextRow) && checkInWidthSize(nextCol) && checkInWidthSize(nextCol+1)) {
                    if ((direction == Direction.UP || direction == Direction.DOWN) && controller.getId(nextRow, nextCol) == 0 && controller.getId(nextRow, nextCol + 1) == 0) {
                        return true;
                    } else if (direction == Direction.LEFT && controller.getId(nextRow, nextCol) == 0) {
                        return true;
                    } else return direction == Direction.RIGHT && controller.getId(nextRow, nextCol + 1) == 0;
                }
                return false;
            case 3 :
                if(checkInHeightSize(nextRow) && checkInWidthSize(nextCol) && checkInHeightSize(nextRow+1)) {
                    if(direction == Direction.DOWN && controller.getId(nextRow + 1,nextCol) == 0){return true;}
                    else if(direction == Direction.UP && controller.getId(nextRow,nextCol) == 0){return true;}
                    else return (direction == Direction.LEFT || direction == Direction.RIGHT) && controller.getId(nextRow, nextCol) == 0 && controller.getId(nextRow + 1, nextCol) == 0;
                }
                return false;
            case 4 :
                if(checkInHeightSize(nextRow) && checkInWidthSize(nextCol) && checkInHeightSize(nextRow+1) && checkInWidthSize(nextCol+1)) {
                    return (controller.getId(nextRow, nextCol) == 0 || controller.getId(nextRow, nextCol) == 4) && (controller.getId(nextRow + 1, nextCol) == 0 || controller.getId(nextRow + 1, nextCol) == 4) && (controller.getId(nextRow, nextCol + 1) == 0 || controller.getId(nextRow, nextCol + 1) == 4) && (controller.getId(nextRow + 1, nextCol + 1) == 0 || controller.getId(nextRow + 1, nextCol + 1) == 4);
                }
                return false;
            default :
                return false;
        }
    }

    /**
     * 不出边界的判定
     * @param row 行数
     * @return 合法性的布尔值
     */
    private static boolean checkInHeightSize(int row) {
        return row >= 0 && row < HEIGHT;
    }

    /**
     * 不出边界的判定
     * @param col 列数
     * @return 合法性的布尔值
     */
    private static boolean checkInWidthSize(int col) {
        return col >= 0 && col < WIDTH;
    }

    /**
     * 获取这一移动记录的方块的类型，用于筛选
     * @return 类型
     */
    public int getType() {
        return type;
    }

    /**
     * 获取移动“到”的坐标
     * @return 坐标
     */
    public int[] getCoordinate() {
        return coordinateInfo;
    }

    /**
     * 重写的toString方法，用于输出这一移动的记录，便于网络传输
     * @return 移动记录的字符串表示
     */
    @Override
    public String toString() {
        return "Move " + coordinateInfo[0] + " " + coordinateInfo[1] + " " + type + " " + direction.getCode();
    }

}
