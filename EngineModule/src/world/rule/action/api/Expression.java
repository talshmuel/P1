package world.rule.action.api;

import world.api.AssistFunctions;
import world.rule.action.Proximity;

public class Expression {
    String name;
    Object value;
    public Expression(String name) {
        this.name = name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }

    public void extractValueFromExpression(){
        if(!isNameOfFunction()){

        } else if(!isNameOfProperty()){

        }
        //this.value = ...
    }

    public boolean isNameOfFunction(){
        if(name.startsWith("environment")){
            return true;
        } else if(name.startsWith("random")){
            return true;
        } else if(name.startsWith("evaluate")){
            return true;
        } else if(name.startsWith("percent")){
            return true;
        } else return name.startsWith("ticks");
    }

    public boolean isNameOfProperty(){
        return false;
    }


}
