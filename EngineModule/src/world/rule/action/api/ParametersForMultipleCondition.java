package world.rule.action.api;

import world.entity.Entity;
import world.property.impl.Property;

import java.util.ArrayList;

public class ParametersForMultipleCondition extends ParametersForCondition{
    ArrayList<ParametersForCondition> conditionsParams;

    public ParametersForMultipleCondition(Property mainProp, Entity mainEntity, Entity secondaryEntity, int currentTicks,
                                          ArrayList<ParametersForAction> thenProps, ArrayList<ParametersForAction> elseProps,
                                          ArrayList<ParametersForCondition> conditionsParams) {
        super(mainProp, mainEntity, secondaryEntity, currentTicks, thenProps, elseProps);
        this.conditionsParams = conditionsParams;
    }

    public ArrayList<ParametersForCondition> getConditionsParams() {
        return conditionsParams;
    }

    public void setConditionsParams(ArrayList<ParametersForCondition> conditionsParams) {
        this.conditionsParams = conditionsParams;
    }
}
