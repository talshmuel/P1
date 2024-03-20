package world.rule.action.api;


import world.entity.EntityDefinition;
import world.rule.action.condition.Condition;


import java.io.Serializable;


public class SecondaryEntity implements Serializable {
    String name;
    EntityDefinition definition;
    Integer numOfSecondEntities; // if == null -> ALL -> translate it to population number
    Condition selection; // describes how they are selected, if it's null then it's randomly
    public SecondaryEntity(String name, Integer numOfSecondEntities, Condition selection) {
        // according to this info we'll create the secondary entities list in engine
        this.name = name;
        this.numOfSecondEntities = numOfSecondEntities;
        this.selection = selection;
    }
    public EntityDefinition getDefinition() {
        return definition;
    }
    public void setDefinition(EntityDefinition definition) {
        this.definition = definition;
    }
    public Integer getNumOfSecondEntities() {
        return numOfSecondEntities;
    }
    public void setNumOfSecondEntities(Integer numOfSecondEntities) {
        this.numOfSecondEntities = numOfSecondEntities;
    }
    public Condition getSelection() {
        return selection;
    }
    public void setSelection(Condition selection) {
        this.selection = selection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
