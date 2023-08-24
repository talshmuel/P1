package run.result;


import java.io.Serializable;
import java.util.Map;

public class EntityResult implements Serializable {
    int numOfInstanceAtStart;
    int numOfInstanceAtEnd;

    Map<String, PropertyResult> propertiesResults;

    public EntityResult(Map<String, PropertyResult> propertiesResults, int numOfInstanceAtStart, int numOfInstanceAtEnd){
        this.propertiesResults = propertiesResults;
        this.numOfInstanceAtStart = numOfInstanceAtStart;
        this.numOfInstanceAtEnd = numOfInstanceAtEnd;
    }

    public int getNumOfInstanceAtEnd() {
        return numOfInstanceAtEnd;
    }

    public int getNumOfInstanceAtStart() {
        return numOfInstanceAtStart;
    }

    public Map<String, PropertyResult> getPropertiesResults() {
        return propertiesResults;
    }
}
