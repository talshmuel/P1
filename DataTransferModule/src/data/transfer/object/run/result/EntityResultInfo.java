package data.transfer.object.run.result;

import java.util.ArrayList;

public final class EntityResultInfo {
    String name;
    int numOfInstanceAtStart;
    int numOfInstanceAtEnd;

    ArrayList<PropertyResultInfo> propertiesResults;

    public EntityResultInfo(String name, ArrayList<PropertyResultInfo> propertiesResults, int numOfInstanceAtStart, int numOfInstanceAtEnd){
        this.propertiesResults = propertiesResults;
        this.numOfInstanceAtStart = numOfInstanceAtStart;
        this.numOfInstanceAtEnd = numOfInstanceAtEnd;
        this.name = name;
    }

    public ArrayList<PropertyResultInfo> getPropertiesResults() {
        return propertiesResults;
    }

    public int getNumOfInstanceAtStart() {
        return numOfInstanceAtStart;
    }

    public int getNumOfInstanceAtEnd() {
        return numOfInstanceAtEnd;
    }

    public String getName() {
        return name;
    }
}
