package data.transfer.object.definition;

public final class EnvironmentInfo {
    private String name;
    private String type;
    private Object value;

    private Object bottomLimit;
    private Object topLimit;
    public EnvironmentInfo(String name, String type, Object bottomLimit, Object topLimit){
        this.name = name;
        this.type = type;
        this.value = null;
        this.bottomLimit = bottomLimit;
        this.topLimit = topLimit;
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
    public void setTopLimit(Object topLimit) {
        this.topLimit = topLimit;
    }
    public void setBottomLimit(Object bottomLimit) {
        this.bottomLimit = bottomLimit;
    }
}