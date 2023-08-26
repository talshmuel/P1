public class EntityValueEntry {
    private String name;
    private String population;

    public EntityValueEntry(String name, String population){
        this.name=name;
        this.population=population;
    }

    public String getName() {
        return name;
    }

    public String getPopulation() {
        return population;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}
