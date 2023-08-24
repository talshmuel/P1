package run.result;
import java.io.Serializable;
import java.util.Map;

public class Result implements Serializable {
    static int IDgenerator = 1;
    int ID;
    String date;

    Map<String, EntityResult> entitiesResults;

    public Result(String date, Map<String, EntityResult> entitiesResults){
        ID = IDgenerator++;
        this.date = date;
        this.entitiesResults = entitiesResults;
    }

    public int getID() {
        return ID;
    }

    public Map<String, EntityResult> getEntitiesResults() {
        return entitiesResults;
    }

    public String getDate() {
        return date;
    }
}
