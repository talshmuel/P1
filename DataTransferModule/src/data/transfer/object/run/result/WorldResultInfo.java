package data.transfer.object.run.result;

import java.util.ArrayList;
import java.util.Map;

public final class WorldResultInfo {
    Map<String, ArrayList<EntityResultInfo>> allEntities; // new: map by string - name of entity TYPE, to an array of the entity INSTANCES
    GridResultInfo grid;

    public WorldResultInfo(Map<String, ArrayList<EntityResultInfo>> allEntities, GridResultInfo grid) {
        this.allEntities = allEntities;
        this.grid = grid;
    }

    public GridResultInfo getGrid() {
        return grid;
    }

    public Map<String, ArrayList<EntityResultInfo>> getAllEntities() {
        return allEntities;
    }
}
