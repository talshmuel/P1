package world.rule.action.api;

import world.entity.Entity;
import world.entity.EntityDefinition;
import world.rule.action.condition.Condition;
import world.rule.action.condition.SingleCondition;

import java.util.ArrayList;
import java.util.List;

public class SecondaryEntity {
    EntityDefinition definition;
    Integer numOfSecondEntities; // if == null -> ALL -> translate it to population number
    Condition selection; // describes how they are selected
    public SecondaryEntity(Integer numOfSecondEntities, Condition selection) {
        // according to this info we'll create the secondary entities list in engine
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
}
