package world.api;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;

public class AssistFunctions implements AssistFunctionsInterface, Serializable {
    Map<String, Property> environmentVariables;

    public void setEnvironmentVariables(Map<String, Property> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public Object environment(String propName) {
        return environmentVariables.get(propName).getVal();
    }

    @Override
    public Object random(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }


}
