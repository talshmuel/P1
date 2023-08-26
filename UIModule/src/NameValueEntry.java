import javax.naming.Name;

public class NameValueEntry {
    private String name;
    private String type;
    private Object bottomLimit;
    private Object topLimit;
    private Object value;

    public NameValueEntry(String name, String type, Object bottom, Object top, Object value){
        this.name = name;
        this.type = type;
        this.bottomLimit = bottom;
        this.topLimit = top;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public Object getBottomLimit() {
        return bottomLimit;
    }

    public Object getTopLimit() {
        return topLimit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setTopLimit(Double topLimit) {
        this.topLimit = topLimit;
    }

    public void setBottomLimit(Object bottomLimit) {
        this.bottomLimit = bottomLimit;
    }
}
