//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package view;

public enum Direction {
    LEFT(0, -1),
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0);

    private final int row;
    private final int col;

    private Direction(int var3, int var4) {
        this.row = var3;
        this.col = var4;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

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
