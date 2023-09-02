package world.rule.action.api;

import world.entity.Entity;
import world.property.impl.Property;

import java.util.ArrayList;

public class ParametersForCondition extends ParametersForAction {
    ArrayList<ParametersForAction> thenParams;
    ArrayList<ParametersForAction> elseParams;

    public ParametersForCondition(Property mainProp, Entity mainEntity, Entity secondaryEntity, int currentTicks,
                                  ArrayList<ParametersForAction> thenParams, ArrayList<ParametersForAction> elseParams) {
        super(mainProp, mainEntity, secondaryEntity, currentTicks);
        this.thenParams = thenParams;
        this.elseParams = elseParams;
    }

    public ArrayList<ParametersForAction> getThenParams() {
        return thenParams;
    }

    public void setThenParams(ArrayList<ParametersForAction> thenProps) {
        this.thenParams = thenProps;
    }

    public ArrayList<ParametersForAction> getElseParams() {
        return elseParams;
    }

    public void setElseParams(ArrayList<ParametersForAction> elseProps) {
        this.elseParams = elseProps;
    }
}
