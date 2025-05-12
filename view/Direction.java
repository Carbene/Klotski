//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package view;

/**
 * 枚举常量，定义移动的方向
 */
public enum Direction {
    LEFT(0, -1),
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0);

    private final int row;
    private final int col;

    /**
     * 一个私有构造器
     * @param row 行
     * @param col 列
     */
    private Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * 返回这个方向移动的数值意义
     * @return 行上的移动
     */
    public int getRow() {
        return this.row;
    }

    /**
     * 返回这个方向移动的数值意义
     * @return 列上的移动
     */
    public int getCol() {
        return this.col;
    }

    /**
     * 获得相反的方向
     * @param direction 需要取反的方向
     * @return 反方向
     */
    public static Direction getOpposite(Direction direction) {

        switch (direction) {

            case LEFT:
                return RIGHT;
                case RIGHT:
                    return LEFT;
                    case UP:
                        return DOWN;
                        case DOWN:
                            return UP;
                            default:
                                return null;

        }


    }

}
