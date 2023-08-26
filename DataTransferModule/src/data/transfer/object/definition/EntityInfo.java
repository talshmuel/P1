package data.transfer.object.definition;

import java.util.ArrayList;

public final class EntityInfo {
    String name;
    int population;
    ArrayList<PropertyInfo> properties;

    public EntityInfo(String name, int population, ArrayList<PropertyInfo> properties){
        this.name = name;
        this.population = population;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "EntityInfo{" +'\n'+"        "+
                "name='" + name + '\'' +
                ", population=" + population +
                ", properties=" + properties +
                '}'+'\n';
    }

    public String getName() {
        return name;
    }

    public ArrayList<PropertyInfo> getProperties() {
        return properties;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
