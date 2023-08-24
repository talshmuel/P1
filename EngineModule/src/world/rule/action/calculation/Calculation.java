package world.rule.action.calculation;
import exception.DivisionByZeroException;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.PropertiesToAction;



public abstract class Calculation extends Action {
    String expression2;
    Object expression2Val;

    public Calculation(String mainEntity, String propToChangeName, String expression1, String expression2){
        super(mainEntity, propToChangeName, expression1);
        this.expression2 = expression2;

    }

    @Override
    public abstract Boolean activate (PropertiesToAction propsToChange)throws DivisionByZeroException, IncompatibleType;

    public void setExpression2Val(Object expression2Val) {
        this.expression2Val = expression2Val;
    }

    public String getExpression2() {
        return expression2;
    }
}
