package world;

import world.entity.Coordinate;
import world.entity.Entity;
import world.rule.action.condition.MultipleCondition;

import java.util.*;

public class Grid {
    private final Integer numOfRows;
    private final Integer numOfCols;
    Boolean[][] matrix; // true- a cell is taken by an entity. false- a cell is free
    Entity[][] entityMatrix; // null-> place is "free"
    // todo: אולי המטריצה תכיל הפניות לישויות
    public enum Direction {RIGHT, LEFT, DOWN, UP}

    public Grid(Integer numOfRows, Integer numOfCols) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        entityMatrix = new Entity[numOfRows][numOfCols];
        for(int i=0 ; i < numOfRows ; i++){
            for (int j = 0; j < numOfCols; j++) {
                entityMatrix[i][j] = null;
            }
        }


        matrix = new Boolean[numOfRows][numOfCols];

        // populate the matrix - at the beginning the matrix is "empty" so it's false
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                matrix[i][j] = false;
            }
        }
    }
    public Integer getNumOfRows() {
        return numOfRows;
    }
    public Integer getNumOfCols() {
        return numOfCols;
    }
    public Entity[][] getEntityMatrix() {
        return entityMatrix;
    }

    public boolean isPositionAvailableMATRIX(Integer row, Integer col){
        // returns true if place is free -> place is null
        // returns false if place "taken" -> there is an entity in there
        return (entityMatrix[row][col] == null);
    }
    public void updateGridCoordinateIsTakenMATRIX(Coordinate pos, Entity entity){
        entityMatrix[pos.getRow()][pos.getCol()] = entity;
    }
    public void updateGridCoordinateIsAvailableMATRIX(Coordinate pos){
        entityMatrix[pos.getRow()][pos.getCol()] = null;
    }
    public Coordinate moveEntityOnGridMATRIX(Entity entity){
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
        return entity.getPosition(); // the entity stays at the same place
    }
    public Coordinate canMoveRightMATRIX(Coordinate pos){
        if(pos.getCol().equals(numOfCols-1)){ // it's circular
            if(isPositionAvailableMATRIX(pos.getRow(), 0))
                return new Coordinate(pos.getRow(), 0);
        } else {
            if(isPositionAvailableMATRIX(pos.getRow(), pos.getCol()+1))
                return new Coordinate(pos.getRow(), pos.getCol()+1);
        }
        return null; // if the place isn't available, then return null.
    }
    public Coordinate canMoveLeftMATRIX(Coordinate pos){
        if(pos.getCol().equals(0)){ // it's circular
            if(isPositionAvailableMATRIX(pos.getRow(), numOfCols-1))
                return new Coordinate(pos.getRow(), numOfCols-1);
        } else {
            if(isPositionAvailableMATRIX(pos.getRow(), pos.getCol()-1))
                return new Coordinate(pos.getRow(), pos.getCol()-1);
        }
        return null; // if the place isn't available, then return null.
    }
    public Coordinate canMoveDownMATRIX(Coordinate currentPosition){
        if(currentPosition.getRow().equals(numOfRows-1)){ // it's circular
            if(isPositionAvailableMATRIX(0, currentPosition.getCol()))
                return new Coordinate(0, currentPosition.getCol());
        } else {
            if(isPositionAvailableMATRIX(currentPosition.getRow()+1, currentPosition.getCol()))
                return new Coordinate(currentPosition.getRow()+1, currentPosition.getCol());
        }
        return null;
    }

    public Coordinate canMoveUpMATRIX(Coordinate currentPosition){
        if(currentPosition.getRow().equals(0)){ // it's circular
            if(isPositionAvailableMATRIX(numOfRows-1, currentPosition.getCol()))
                return new Coordinate(numOfRows-1, currentPosition.getCol());
        } else {
            if(isPositionAvailableMATRIX(currentPosition.getRow()-1, currentPosition.getCol()))
                return new Coordinate(currentPosition.getRow()-1, currentPosition.getCol());
        }
        return null;
    }
    public List<Coordinate> findEnvironmentCells(Coordinate source, int rank) {
        // returns list of all the coordinates in the rank surrounding the source coordinate
        if(rank > numOfRows){ // todo: check this.....
            rank = rank%numOfRows;
        } else if (rank > numOfCols) {
            rank = rank%numOfCols;
        }

        List<Coordinate> result = new ArrayList<>();
        for (int r = 1; r <= rank; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) == r || Math.abs(dy) == r) {
                        int newX = (source.getRow() + dx + numOfRows) % numOfRows;
                        int newY = (source.getCol() + dy + numOfCols) % numOfCols;
                        result.add(new Coordinate(newX, newY));
                    }
                }
            }
        }
        return result;
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isPositionAvailable(Integer row, Integer col){ // returns true if the position is empty, and false otherwise
        return (!matrix[row][col]);
        // if the place is false -> then it's empty!  הפוך על הפוך
    }

    public void updateGridCoordinateIsTaken(Coordinate pos){
        matrix[pos.getRow()][pos.getCol()] = true;
    }

    public void updateGridCoordinateIsAvailable(Coordinate pos){
        matrix[pos.getRow()][pos.getCol()] = false;
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
        return entity.getPosition(); // the entity stays at the same place
    }

    public Coordinate canMoveRight(Coordinate currentPosition){
        if(currentPosition.getCol().equals(numOfCols-1)){ // it's circular
            if(isPositionAvailable(currentPosition.getRow(), 0))
                return new Coordinate(currentPosition.getRow(), 0);
        } else {
            if(isPositionAvailable(currentPosition.getRow(), currentPosition.getCol()+1))
                return new Coordinate(currentPosition.getRow(), currentPosition.getCol()+1);
        }

        return null; // if the place isn't available, then return null.
    }

    public Coordinate canMoveLeft(Coordinate currentPosition){
        if(currentPosition.getCol().equals(0)){ // it's circular
            if(isPositionAvailable(currentPosition.getRow(), numOfCols-1))
                return new Coordinate(currentPosition.getRow(), numOfCols-1);
        } else {
            if(isPositionAvailable(currentPosition.getRow(), currentPosition.getCol()-1))
                return new Coordinate(currentPosition.getRow(), currentPosition.getCol()-1);
        }

        return null; // the place isn't empty
    }

    public Coordinate canMoveDown(Coordinate currentPosition){
        if(currentPosition.getRow().equals(numOfRows-1)){ // it's circular
            if(isPositionAvailable(0, currentPosition.getCol()))
                return new Coordinate(0, currentPosition.getCol());
        } else {
            if(isPositionAvailable(currentPosition.getRow()+1, currentPosition.getCol()))
                return new Coordinate(currentPosition.getRow()+1, currentPosition.getCol());
        }

        return null;
    }

    public Coordinate canMoveUp(Coordinate currentPosition){
        if(currentPosition.getRow().equals(0)){ // it's circular
            if(isPositionAvailable(numOfRows-1, currentPosition.getCol()))
                return new Coordinate(numOfRows-1, currentPosition.getCol());
        } else {
            if(isPositionAvailable(currentPosition.getRow()-1, currentPosition.getCol()))
                return new Coordinate(currentPosition.getRow()-1, currentPosition.getCol());
        }
        return null;
    }


    public Boolean[][] getMatrix() {
        return matrix;
    }



    public void printMatrix(){
        for (int i=0 ; i < numOfRows ; i++){
            for (int j=0 ; j < numOfCols ; j++)
                System.out.println(matrix[i][j]+ "\t");
        }
        System.out.println();
    }
}
