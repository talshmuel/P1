package world.rule.action.calculation;

import exception.DivisionByZeroException;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

public abstract class Calculation extends Action {
    Expression expression2;
    public Calculation(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression1, Expression expression2){
        super(mainEntity, secondEntityInfo, propToChangeName, expression1);
        this.expression2=expression2;
    }
    @Override
    public abstract Boolean activate (ParametersForAction parameters)throws DivisionByZeroException, IncompatibleType;
    public Expression getExpression2() {
        return expression2;
    }
    public void setExpression2(Expression expression2) {
        this.expression2 = expression2;
    }
}
