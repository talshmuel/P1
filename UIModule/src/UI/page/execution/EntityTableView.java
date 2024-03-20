package UI.page.execution;

public class EntityTableView {
    private String name;
    private Integer population;
    private Integer selected;

    public EntityTableView(String name, Integer population, Integer selected) {
        this.name = name;
        this.population = population;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }
}
