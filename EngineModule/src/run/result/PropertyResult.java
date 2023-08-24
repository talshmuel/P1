package run.result;

import java.io.Serializable;
import java.util.Map;

public class PropertyResult implements Serializable {
    Map<Object, Integer> histogram;//for each value of the property - how many entities have this value


    public PropertyResult(Map<Object, Integer> histogram){
        this.histogram = histogram;
    }

    public Map<Object, Integer> getHistogram() {
        return histogram;
    }
}
