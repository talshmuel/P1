package world.rule.action.calculation;

import exception.DivisionByZeroException;
import exception.IncompatibleType;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

public class Divide extends Calculation {
    public Divide(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression1, Expression expression2){
        super(mainEntity, secondEntityInfo, propToChangeName, expression1, expression2);
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleType {
        if(expression2.getValue() instanceof Integer && (Integer)expression2.getValue() == 0)
            throw new DivisionByZeroException();
        else if(expression2.getValue() instanceof Double && (Double)expression2.getValue() == 0)
            throw new DivisionByZeroException();
        else if(expression.getValue() instanceof Integer && expression2.getValue() instanceof Integer){
            parameters.getMainProp().set((Integer)expression.getValue()/(Integer)expression2.getValue());
            parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
            return false;
        }
        else if(expression.getValue() instanceof Double && expression2.getValue() instanceof Double){
            parameters.getMainProp().set((Double)expression.getValue()/(Double)expression2.getValue());
            parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
            return false;
        }
        else if(expression.getValue() instanceof Integer && expression2.getValue() instanceof Double){
            parameters.getMainProp().set((Integer)expression.getValue()/(Double)expression2.getValue());
            parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
            return false;
        }
        else if(expression.getValue() instanceof Double && expression2.getValue() instanceof Integer){
            parameters.getMainProp().set((Double)expression.getValue()/(Integer)expression2.getValue());
            parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
            return false;
        }
        else
            throw new IncompatibleType();
    }
    /*@Override // old version
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleType {
        if(expression2Val instanceof Integer && (Integer)expression2Val == 0)
            throw new DivisionByZeroException();
        else if(expression2Val instanceof Double && (Double)expression2Val == 0)
            throw new DivisionByZeroException();
        else if(expressionVal instanceof Integer && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Integer)expressionVal/(Integer)expression2Val); // old version
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Double)expressionVal/(Double)expression2Val); // old version
            return false;
        }
        else if(expressionVal instanceof Integer && expression2Val instanceof Double){
            propsToChange.getMainProp().set((Integer)expressionVal/(Double)expression2Val); // old version
            return false;
        }
        else if(expressionVal instanceof Double && expression2Val instanceof Integer){
            propsToChange.getMainProp().set((Double)expressionVal/(Integer)expression2Val); // old version
            return false;
        }
        else
            throw new IncompatibleType();
    }*/
}
