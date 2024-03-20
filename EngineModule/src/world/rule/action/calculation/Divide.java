package world.rule.action.calculation;

import exception.SimulationRunningException;
import data.transfer.object.definition.ActionInfo;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;

import java.util.HashMap;
import java.util.Map;

public class Divide extends Calculation {
    public Divide(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression1, Expression expression2){
        super(mainEntity, secondEntityInfo, propToChangeName, expression1, expression2);
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        if(expression2.getValue() instanceof Integer && (Integer)expression2.getValue() == 0)
            throw new SimulationRunningException("Simulation Error:\nAttempt to divide by zero");
        else if(expression2.getValue() instanceof Double && (Double)expression2.getValue() == 0)
            throw new SimulationRunningException("Simulation Error:\nAttempt to divide by zero");
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
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(super.getSecondEntityInfo()!=null)
            haveSecondEntity=true;


        Map<String, Object> moreProp = new HashMap<>();
        moreProp.put("1st Argument", super.expression.getName());
        moreProp.put("2nd Argument", super.expression2.getName());

        return new ActionInfo("Calculation - Divide", super.getMainEntityName(), haveSecondEntity, moreProp);
    }


    /*@Override // old version
    public Boolean activate(ParametersForAction parameters)  {
        if(expression2Val instanceof Integer && (Integer)expression2Val == 0)
        else if(expression2Val instanceof Double && (Double)expression2Val == 0)
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
    }*/
}
