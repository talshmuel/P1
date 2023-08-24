package data.transfer.object.definition;

public class PropertyValueInfo {
    String name;
    Object val;
    public PropertyValueInfo(String name, Object val){
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public Object getVal() {
        return val;
    }
}
