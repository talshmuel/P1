package world.rule.action.api;


import world.property.impl.Property;

import java.util.ArrayList;

public class PropertiesToMultipleCondition extends PropertiesToCondition {
    ArrayList<PropertiesToAction> conditionsProp;

    public PropertiesToMultipleCondition(Property mainProp, ArrayList<PropertiesToAction> thenProps, ArrayList<PropertiesToAction> elseProps,
                                         ArrayList<PropertiesToAction> conditionsProp){
        super(mainProp, thenProps, elseProps);
        this.conditionsProp = conditionsProp;
    }

    public ArrayList<PropertiesToAction> getConditionsProp() {
        return conditionsProp;
    }
}
