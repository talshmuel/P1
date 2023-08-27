package data.transfer.object.definition;

public final class PropertyInfo {
    String name;
    String type;
    Object topLimit;
    Object bottomLimit;
    boolean isRandomlyInitialized;

    public PropertyInfo(String name, String type, Object topLimit, Object bottomLimit, boolean isRandomlyInitialized){
        this.name = name;
        this.type = type;
        this.isRandomlyInitialized = isRandomlyInitialized;
        this.bottomLimit = bottomLimit;
        this.topLimit = topLimit;
    }

    @Override
    public String toString() {
        return "PropertyInfo{" +'\n'+"          "+
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", topLimit=" + topLimit +
                ", bottomLimit=" + bottomLimit +
                ", isRandomlyInitialized=" + isRandomlyInitialized +
                '}'+'\n';
    }

    public String getName() {
        return name;
    }

    public Object getBottomLimit() {
        return bottomLimit;
    }

    public Object getTopLimit() {
        return topLimit;
    }

    public String getType() {
        return type;
    }
    public Boolean getIsRandomInit() {return isRandomlyInitialized;}



}
