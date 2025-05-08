package record;

import logic.LogicController;
import view.Direction;
import view.BoxComponent;
import view.*;

public class Move {

    private BoxComponent selectedBoxComponent;
    private Direction direction;
    private static final int HEIGHT = 4;
    private static final int WIDTH = 5;

    public Move(BoxComponent selectedBoxComponent, Direction direction) {

        this.selectedBoxComponent = selectedBoxComponent;
        this.direction = direction;

    }

    public BoxComponent getSelectedBox() {
        return selectedBoxComponent;
    }

    public Direction getDirection() {
        return direction;
    }

    public static boolean checkMoveValidity(int id, int nextRow, int nextCol, Direction direction, LogicController controller) {

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


    /*public static boolean doMove(int row, int col, Direction direction, Map map) {

        int nextRow = row + direction.getRow();
        int nextCol = col + direction.getCol();

        if (map.getId(row, col) == 1 && Move.checkMoveValidity(1,nextRow,nextCol,direction)) {

            map.getMatrix()[row][col] = 0;
            map.getMatrix()[nextRow][nextCol] = 1;
            Move.boxMove(row, col, nextRow, nextCol);
            return true;

        } else if (map.getId(row, col) == 2 && checkMoveValidity(2,nextRow,nextCol, direction)) {

            map.getMatrix()[row][col] = 0;
            map.getMatrix()[row][col+1] = 0;
            map.getMatrix()[nextRow][nextCol] = 2;
            map.getMatrix()[nextRow][nextCol+1] = 2;
            Move.boxMove(row, col, nextRow, nextCol);
            return true;

        }else if(map.getId(row,col) == 3 && checkMoveValidity(3,nextRow,nextCol,direction)){

            map.getMatrix()[row][col] = 0;
            map.getMatrix()[row + 1][col] = 0;
            map.getMatrix()[nextRow][nextCol] = 3;
            map.getMatrix()[nextRow+1][nextCol] = 3;
            Move.boxMove(row, col, nextRow, nextCol);
            return true;

        }else if(map.getId(row,col) == 4 && checkMoveValidity(4,nextRow,nextCol,direction)){

            map.getMatrix()[row][col] = 0;
            map.getMatrix()[row + 1][col] = 0;
            map.getMatrix()[row][col+1] = 0;
            map.getMatrix()[row+1][col+1] = 0;
            map.getMatrix()[nextRow][nextCol] = 4;
            map.getMatrix()[nextRow+1][nextCol] = 4;
            map.getMatrix()[nextRow][nextCol+1] = 4;
            map.getMatrix()[nextRow][nextCol+1] = 4;
            Move.boxMove(row, col, nextRow, nextCol);
            return true;
        }

        return false;
    }

    private static void boxMove(int row, int col, int nextRow, int nextCol, Map map) {

        // this method used to repaint the block to implement the move action

        BoxComponent box = map.getBox(row, col);
        box.setCol(nextCol);
        box.setRow(nextRow);
        box.setLocation(box.getRow() * view.getGRID_SIZE() + 2, box.getRow() * view.getGRID_SIZE() + 2);
        box.repaint();

    }

    private static boolean checkMoveValidity(int id, int nextRow, int nextCol, Direction direction) {

        // this method used to ensure the move is valid so collision to the other block or the boundary will not happen.

        switch (id) {

            case 1 :

                return model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol) && model.getId(nextRow,nextCol) == 0;

            case 2 :

                if (model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol) && model.checkInWidthSize(nextCol+1)) {

                    if ((direction == Direction.UP || direction == Direction.DOWN) && model.getId(nextRow, nextCol) == 0 && model.getId(nextRow, nextCol + 1) == 0) {
                        return true;
                    } else if (direction == Direction.LEFT && model.getId(nextRow, nextCol) == 0) {
                        return true;
                    } else return direction == Direction.RIGHT && model.getId(nextRow, nextCol + 1) == 0;

                }

                return false;

            case 3 :

                if(model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol) && model.checkInHeightSize(nextRow+1)) {

                    if(direction == Direction.DOWN && model.getId(nextRow + 1,nextCol) == 0){return true;}
                    else if(direction == Direction.UP && model.getId(nextRow,nextCol) == 0){return true;}
                    else return (direction == Direction.LEFT || direction == Direction.RIGHT) && model.getId(nextRow, nextCol) == 0 && model.getId(nextRow + 1, nextCol) == 0;

                }

                return false;

            case 4 :

                if(model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol) && model.checkInHeightSize(nextRow+1) && model.checkInWidthSize(nextCol+1)) {

                    return (model.getId(nextRow, nextCol) == 0 || model.getId(nextRow, nextCol) == 4) && (model.getId(nextRow + 1, nextCol) == 0 || model.getId(nextRow + 1, nextCol) == 4) && (model.getId(nextRow, nextCol + 1) == 0 || model.getId(nextRow, nextCol + 1) == 4) && (model.getId(nextRow + 1, nextCol + 1) == 0 || model.getId(nextRow + 1, nextCol + 1) == 4);

                }

                return false;

            default :

                return false;

        }

    }
*/

}
