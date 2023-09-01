package world.rule.action.api;

import world.property.impl.Property;


public class PropertiesToAction {
    Property mainProp;

    // todo: maybe add here a field of an entity, and send this entity to Replace and Proximity actions.

    public PropertiesToAction(Property mainProp) {
        this.mainProp = mainProp;

    }
    public Property getMainProp() {
        return mainProp;
    }
}


