package world;

import world.entity.Coordinate;
import world.entity.Entity;
import world.rule.action.condition.MultipleCondition;

import java.util.*;

public class Grid {
    private final Integer numOfRows;
    private final Integer numOfCols;
    Boolean[][] matrix; // if an entity is in a cell on the matrix it will be true, and false otherwise
    // todo: אולי המטריצה תכיל הפניות לישויות
    public enum Direction {RIGHT, LEFT, DOWN, UP}

    public Grid(Integer numOfRows, Integer numOfCols) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        matrix = new Boolean[numOfRows][numOfCols];

        // populate the matrix - at the beginning the matrix is "empty" so it's false
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                matrix[i][j] = false;
            }
        }
    }

    public boolean isPositionAvailable(Integer row, Integer col){ // returns true of the position is empty, and false otherwise
        return (!matrix[row][col]);
        // if the place is false -> then it's empty!
    }

    public void updateGrid(Coordinate pos){
        matrix[pos.getRow()][pos.getCol()] = true;
    }

    public Direction getRandomDirection() { // gets a random direction to go to on the grid
        Random random = new Random();
        int randomIndex = random.nextInt(Direction.values().length);
        System.out.println(Direction.values()[randomIndex]); //todo
        return Direction.values()[randomIndex];
    }

    public Coordinate moveEntityOnGrid(Entity entity){
        List<Coordinate> availableCoordinates = new ArrayList<>();
        Coordinate right = canMoveRight(entity.getPosition());
        Coordinate left = canMoveLeft(entity.getPosition());
        Coordinate down = canMoveDown(entity.getPosition());
        Coordinate up = canMoveUp(entity.getPosition());

        if(right != null)
            availableCoordinates.add(right);
        if(left != null)
            availableCoordinates.add(left);
        if(down != null)
            availableCoordinates.add(down);
        if(up != null)
            availableCoordinates.add(up);


        // if there are available positions, then shuffle the list and return the first item
        if(!availableCoordinates.isEmpty()) {
            Collections.shuffle(availableCoordinates);
            return availableCoordinates.get(0);
        }
        return null;
    }

    public Coordinate canMoveRight(Coordinate currentPosition){
        if(currentPosition.getCol().equals(numOfCols-1) && !matrix[currentPosition.getRow()][0]) // circular direction
             new Coordinate(currentPosition.getRow(), 0);
        else if(!matrix[currentPosition.getRow()][currentPosition.getCol() + 1]) // the place is empty
                return new Coordinate(currentPosition.getRow(), currentPosition.getCol() + 1);

        return null; // if the place isn't available, then return null.
    }

    public Coordinate canMoveLeft(Coordinate currentPosition){
        if(currentPosition.getCol().equals(0) && !matrix[currentPosition.getRow()][numOfCols-1])
            return new Coordinate(currentPosition.getRow(), numOfCols-1);
        else if(!matrix[currentPosition.getRow()][currentPosition.getCol() - 1])
            return new Coordinate(currentPosition.getRow(), currentPosition.getCol() - 1);

        return null;
    }

    public Coordinate canMoveDown(Coordinate currentPosition){
        if(currentPosition.getRow().equals(numOfRows-1) && !matrix[0][currentPosition.getCol()])
            return new Coordinate(0, currentPosition.getCol());
        else if(!matrix[currentPosition.getRow()+1][currentPosition.getCol()])
            return new Coordinate(currentPosition.getRow()+1, currentPosition.getCol());

        return null;
    }

    public Coordinate canMoveUp(Coordinate currentPosition){
        if(currentPosition.getRow().equals(0) && !matrix[numOfRows-1][currentPosition.getCol()])
            return new Coordinate(numOfRows-1, currentPosition.getCol());
        else if(!matrix[currentPosition.getRow()-1][currentPosition.getCol()])
            return new Coordinate(currentPosition.getRow()-1, currentPosition.getCol());

        return null;
    }


/*    public boolean canMoveRight(Coordinate position){ // returns true if the place is empty
        if(position.getCol().equals(numOfCols-1))
            return (!matrix[position.getRow()][0]); // go in a circular direction
        else
            return (!matrix[position.getRow()][position.getCol()+1]);
    }

    public boolean canMoveLeft(Coordinate position){
        if(position.getCol().equals(0))
            return (!matrix[position.getRow()][numOfCols-1]);
        else
            return (!matrix[position.getRow()][position.getCol()-1]);
    }

    public boolean canMoveDown(Coordinate position){
        if(position.getRow().equals(numOfRows-1))
            return (!matrix[0][position.getCol()]);
        else
            return (!matrix[position.getRow()+1][position.getCol()]);
    }

    public boolean canMoveUp(Coordinate position){
        if(position.getRow().equals(0))
            return (!matrix[numOfRows-1][position.getCol()]);
        else
            return (!matrix[position.getRow()-1][position.getCol()]);
    }*/





    public Integer getNumOfRows() {
        return numOfRows;
    }

    public Integer getNumOfCols() {
        return numOfCols;
    }

    public Boolean[][] getMatrix() {
        return matrix;
    }

    //    public Collection<Coordinate> findEnvironmentCells(Coordinate source, int rank) {
//        List<Coordinate> result = new ArrayList<>();
//        for (int r = 1; r <= rank; r++) {
//            for (int dx = -r; dx <= r; dx++) {
//                for (int dy = -r; dy <= r; dy++) {
//                    if (Math.abs(dx) == r || Math.abs(dy) == r) {
//                        int newX = (source.getRow() + dx + numOfRows) % numOfRows;
//                        int newY = (source.getCol() + dy + numOfCols) % numOfCols;
//                        result.add(new Coordinate(newX, newY));
//                    }
//                }
//            }
//        }
//        return result;
//    }

}
