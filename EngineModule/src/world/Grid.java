package world;

import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.GridResultInfo;
import world.entity.Coordinate;
import world.entity.Entity;
import java.io.Serializable;
import java.util.*;

public class Grid implements Serializable {
    private Integer numOfRows;
    private Integer numOfCols;
    Entity[][] entityMatrix; // null-> place is "free"

    public Grid(Integer numOfRows, Integer numOfCols) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;

        entityMatrix = new Entity[numOfRows][numOfCols];
        for(int i=0 ; i < numOfRows ; i++){
            for (int j = 0; j < numOfCols; j++) {
                entityMatrix[i][j] = null;
            }
        }
    }
    public GridResultInfo getGridResultInfo(){
        EntityResultInfo[][] entityResultOnMatrix = new EntityResultInfo[numOfRows][numOfCols];
        for(int i=0 ; i < numOfRows ; i++){
            for (int j = 0; j < numOfCols; j++) {
                if (entityMatrix[i][j] != null)
                    entityResultOnMatrix[i][j] = entityMatrix[i][j].getEntityResultInfo();
                else
                    entityResultOnMatrix[i][j] =null;
            }
        }
        return new GridResultInfo(numOfRows, numOfCols,entityResultOnMatrix);
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

    public boolean isPositionAvailable(Integer row, Integer col){
        // returns true if place is free -> place is null
        // returns false if place "taken" -> there is an entity in there
        return (entityMatrix[row][col] == null);
    }
    public void updateGridCoordinateIsTaken(Coordinate pos, Entity entity){
        entityMatrix[pos.getRow()][pos.getCol()] = entity;
    }
    public void updateGridCoordinateIsAvailable(Coordinate pos){
        entityMatrix[pos.getRow()][pos.getCol()] = null;
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
    public Coordinate canMoveRight(Coordinate pos){
        if(pos.getCol().equals(numOfCols-1)){ // it's circular
            if(isPositionAvailable(pos.getRow(), 0))
                return new Coordinate(pos.getRow(), 0);
        } else {
            if(isPositionAvailable(pos.getRow(), pos.getCol()+1))
                return new Coordinate(pos.getRow(), pos.getCol()+1);
        }
        return null; // if the place isn't available, then return null.
    }
    public Coordinate canMoveLeft(Coordinate pos){
        if(pos.getCol().equals(0)){ // it's circular
            if(isPositionAvailable(pos.getRow(), numOfCols-1))
                return new Coordinate(pos.getRow(), numOfCols-1);
        } else {
            if(isPositionAvailable(pos.getRow(), pos.getCol()-1))
                return new Coordinate(pos.getRow(), pos.getCol()-1);
        }
        return null; // if the place isn't available, then return null.
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
    public List<Coordinate> findEnvironmentCells(Coordinate source, int rank) {
        // returns list of all the coordinates in the rank surrounding the source coordinate
        if(rank > numOfRows){
            rank = rank%numOfRows;
        } else if (rank > numOfCols) {
            rank = rank%numOfCols;
        }

        List<Coordinate> result = new ArrayList<>();
        for (int r = 1; r <= rank; r++) {
            for (int row = -r; row <= r; row++) {
                for (int col = -r; col <= r; col++) {
                    if (Math.abs(row) == r || Math.abs(col) == r) {
                        int newRow = (source.getRow() + row + numOfRows) % numOfRows;
                        int newCol = (source.getCol() + col + numOfCols) % numOfCols;
                        result.add(new Coordinate(newRow, newCol));
                    }
                }
            }
        }
        return result;
    }

    public Coordinate findNewAvailableCell(){ // without updating anything, only saying it's free
        Random random = new Random();
        boolean entityInPlace=false;

        while(!entityInPlace){
            int newRow = random.nextInt(numOfRows);
            int newCol = random.nextInt(numOfCols);
            if (isPositionAvailable(newRow, newCol)) {
                Coordinate position = new Coordinate(newRow, newCol);
                return position;
            }
        }
        return null;
    }

    public void cleanup() {
        numOfRows=0;
        numOfCols=0;

    }
}