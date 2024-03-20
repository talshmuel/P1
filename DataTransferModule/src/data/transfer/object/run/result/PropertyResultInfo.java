package data.transfer.object.run.result;

import java.util.Map;

public final class PropertyResultInfo {
    String name;
    int tickNumThatHasChanged; // באיזה טיק הוא השתנה
    String type;
    Object value;
    public PropertyResultInfo(String name, String type, int tickNumThatHasChanged, Object value){
        this.name = name;
        this.tickNumThatHasChanged = tickNumThatHasChanged;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getTickNumThatHasChanged() {
        return tickNumThatHasChanged;
    }

    public Object getValue() {
        return value;
    }
}
