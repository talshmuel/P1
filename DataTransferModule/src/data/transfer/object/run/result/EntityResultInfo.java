package data.transfer.object.run.result;


import java.util.Map;

public final class EntityResultInfo {
    String name;
    int ID;
    Map<String, PropertyResultInfo> properties;
    int rowOnGrid;
    int colOnGrid;

    public EntityResultInfo(String name, int ID, Map<String, PropertyResultInfo> properties, int rowOnGrid, int colOnGrid) {
        this.name = name;
        this.ID = ID;
        this.properties = properties;
        this.rowOnGrid = rowOnGrid;
        this.colOnGrid = colOnGrid;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public int getColOnGrid() {
        return colOnGrid;
    }

    public int getRowOnGrid() {
        return rowOnGrid;
    }

    public Map<String, PropertyResultInfo> getProperties() {
        return properties;
    }
}
