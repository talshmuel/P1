package world.rule.action.calculation;

import exception.DivisionByZeroException;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;

public class Divide extends Calculation {
    public Divide(String mainEntity, String secondaryEntity, String propToChangeName, String expression1, String expression2){
        super(mainEntity, secondaryEntity, propToChangeName, expression1, expression2);
    }
    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange) throws DivisionByZeroException, IncompatibleType {
        if(expression2Val instanceof Integer && (Integer)expression2Val == 0)
            throw new DivisionByZeroException();
        else if(expression2Val instanceof Double && (Double)expression2Val == 0)
            throw new DivisionByZeroException();
        else if(expressionVal instanceof Integer && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Integer)expressionVal/(Integer)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Double)expressionVal/(Double)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Integer && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Integer)expressionVal/(Double)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Double)expressionVal/(Integer)expression2Val);
            propsToChange.getMainProp().setTickNumThatHasChanged(this.getCurrentTick()); // update in which tick it has been changed
            return false;
        }
        else
            throw new IncompatibleType();
    }
}
