package world.rule.action.api;

import world.entity.Entity;
import world.property.impl.Property;

import java.util.ArrayList;

public class ParametersForMultipleCondition extends ParametersForCondition{
    ArrayList<ParametersForAction> conditionsParams;

    public ParametersForMultipleCondition(Property mainProp, Entity mainEntity, Entity secondaryEntity, int currentTicks,
                                          ArrayList<ParametersForAction> thenParams, ArrayList<ParametersForAction> elseParams,
                                          ArrayList<ParametersForAction> conditionsParams) {
        super(mainProp, mainEntity, secondaryEntity, currentTicks, thenParams, elseParams);
        this.conditionsParams = conditionsParams;
    }

    public ArrayList<ParametersForAction> getConditionsParams() {
        return conditionsParams;
    }

    public void setConditionsParams(ArrayList<ParametersForAction> conditionsParams) {
        this.conditionsParams = conditionsParams;
    }
}
