package data.transfer.object.run.result;

import java.util.Map;

public final class PropertyResultInfo {
    String name;
    Map<Object, Integer> histogram;//for each value of the property - how many entities have this value


    public PropertyResultInfo(String name, Map<Object, Integer> histogram){
        this.histogram = histogram;
        this.name = name;
    }

    public Map<Object, Integer> getHistogram() {
        return histogram;
    }

    public String getName() {
        return name;
    }
}
