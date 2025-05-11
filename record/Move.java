package record;

import logic.LogicController;
import view.Direction;
import view.BoxComponent;

public class Move {

    private BoxComponent selectedBox;
    private Direction direction;
    private static final int HEIGHT = 4;
    private static final int WIDTH = 5;

    public Move(BoxComponent selectedBoxComponent, Direction direction) {

        this.selectedBox = selectedBoxComponent;
        this.direction = direction;

    }

    public BoxComponent getBox() {
        return selectedBox;
    }

    public Direction getDirection() {
        return direction;
    }

    public static boolean validateMove(int id, int nextRow, int nextCol, Direction direction, LogicController controller) {

        // this method used to ensure the move is valid so collision to the other block or the boundary will not happen.

        switch (id) {
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

    private static boolean checkInHeightSize(int row) {
        return row >= 0 && row < HEIGHT;
    }

    private static boolean checkInWidthSize(int col) {
        return col >= 0 && col < WIDTH;
    }

}
