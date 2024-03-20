package world.entity;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private Integer row;
    private Integer col;
    public Coordinate(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }
    public Integer getRow() {
        return row;
    }
    public boolean isEqual(Coordinate other) {
        return other.getRow().equals(row) && other.getCol().equals(col);
    }
    public void setRow(Integer row) {
        this.row = row;
    }
    public Integer getCol() {
        return col;
    }
    public void setCol(Integer col) {
        this.col = col;
    }
}
