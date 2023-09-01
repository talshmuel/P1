package world.rule.action.calculation;

import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;

public class Multiply extends Calculation {
    public Multiply(String mainEntity, String secondaryEntity, String propToChangeName, String expression1, String expression2){
        super(mainEntity, secondaryEntity, propToChangeName, expression1, expression2);
    }
    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange)throws IncompatibleType {
        if(expressionVal instanceof Integer && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Integer)expressionVal*(Integer)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Double)expressionVal*(Double)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Integer && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Integer)expressionVal*(Double)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Double)expressionVal*(Integer)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else
            throw new IncompatibleType();
    }
}

//return "Multiply action on the property:" + propToChange.getName() + "failed while attempting " +
//                        "to exceed the value range. The property value remains: " + propToChange.getVal();
//

// return "Cannot perform Multiply action on this kind of property.";
//        }