package data.transfer.object.definition;

public final class EnvironmentInfo {
    private String name;
    private String type;
    private Integer bottomLimit;
    //private Double bottomLimit;
    private Integer topLimit;
    //private Double topLimit;
    private Object value;

//    public EnvironmentInfo(String name, String type, Double bottom, Double top, Object value){
//        this.name = name;
//        this.type = type;
//        this.bottomLimit = bottom;
//        this.topLimit = top;
//        this.value = value;
//    }
    public EnvironmentInfo(String name, String type, Integer bottom, Integer top, Object value){
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

    public void setTopLimit(Integer topLimit) {
        this.topLimit = topLimit;
    }
    /*
    public void setTopLimit(Double topLimit) {
        this.topLimit = topLimit;
    }

     */

    public void setBottomLimit(Integer bottomLimit) {
        this.bottomLimit = bottomLimit;
    }
    /*
    public void setBottomLimit(Double bottomLimit) {
        this.bottomLimit = bottomLimit;
    }
     */
}
