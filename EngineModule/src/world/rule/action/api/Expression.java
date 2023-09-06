package world.rule.action.api;

import world.api.AssistFunctions;
import world.entity.EntityDefinition;
import world.property.api.PropertyDefinition;
import world.rule.action.Proximity;

import java.util.Map;

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

    public String getStringInParenthesis(){
        if(name.startsWith("environment")){
            return name.substring(12, name.length()-1);
        } else if(name.startsWith("random")){
            return name.substring(7, name.length()-1);
        } else if(name.startsWith("evaluate")){
            return name.substring(9, name.length()-1);
        } else if(name.startsWith("percent")){
            return name.substring(8, name.length()-1);
        } else { //if (name.startsWith("ticks")){
            return name.substring(6, name.length()-1);
        }
    }

    public boolean isNameOfProperty(EntityDefinition entityDefinition){
        return entityDefinition.getPropsDef().containsKey(name);
    }

    public boolean isANumber(){
        return name.matches("\\d+");
    }


}
