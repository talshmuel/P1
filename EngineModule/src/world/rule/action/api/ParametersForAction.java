package world.rule.action.api;

import world.entity.Entity;
import world.property.impl.Property;

public class ParametersForAction {
    Property mainProp;
    Entity mainEntity; // instance
    Entity secondaryEntity; // instance, if there isn't -> it's null
    int currentTicks;

    public ParametersForAction(Property mainProp, Entity mainEntity, Entity secondaryEntity, int currentTicks) {
        this.mainProp = mainProp;
        this.mainEntity = mainEntity;
        this.secondaryEntity = secondaryEntity;
        this.currentTicks = currentTicks;
    }
    public Property getMainProp() {
        return mainProp;
    }

    public void setMainProp(Property mainProp) {
        this.mainProp = mainProp;
    }

    public Entity getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(Entity mainEntity) {
        this.mainEntity = mainEntity;
    }

    public Entity getSecondaryEntity() {
        return secondaryEntity;
    }

    public void setSecondaryEntity(Entity secondaryEntity) {
        this.secondaryEntity = secondaryEntity;
    }

    public int getCurrentTicks() {
        return currentTicks;
    }

    public void setCurrentTicks(int currentTicks) {
        this.currentTicks = currentTicks;
    }
}
