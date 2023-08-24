package world.rule.action.api;

import world.property.impl.Property;

import java.util.ArrayList;

public class PropertiesToCondition extends PropertiesToAction{
    ArrayList<PropertiesToAction> thenProps;
    ArrayList<PropertiesToAction> elseProps;

    public PropertiesToCondition(Property mainProp, ArrayList<PropertiesToAction> thenProps, ArrayList<PropertiesToAction> elseProps){
        super(mainProp);
        this.thenProps = thenProps;
        this.elseProps = elseProps;
    }

    public ArrayList<PropertiesToAction> getElseProps() {
        return elseProps;
    }

    public ArrayList<PropertiesToAction> getThenProps() {
        return thenProps;
    }
}
