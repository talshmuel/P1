package data.transfer.object.run.result;

public class GridResultInfo {
    private final Integer numOfRows;
    private final Integer numOfCols;
    EntityResultInfo[][] entityMatrix; // null-> place is "free"

    public GridResultInfo(Integer numOfRows, Integer numOfCols, EntityResultInfo[][] entityMatrix) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        this.entityMatrix = entityMatrix;
    }

    public EntityResultInfo[][] getEntityMatrix() {
        return entityMatrix;
    }

    public Integer getNumOfCols() {
        return numOfCols;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }
}
