package world.rule.action.api;

import exception.SimulationRunningException;
import world.creator.XMLFileException;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Expression implements Serializable {
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

    public boolean isNameOfProperty(Entity entity){
        return entity.getProperties().containsKey(name);
    }

    public boolean isANumber(){
        return name.matches("[0-9.]+");
    }

    public boolean isBoolean(){
        return (name.equalsIgnoreCase("true") || name.equalsIgnoreCase("false"));
    }

    public boolean isEqual(Expression other){
        return (this.getValue().equals(other.getValue()));
    }

    public boolean isSmaller(Expression other) throws SimulationRunningException {
        if(value instanceof Double && other.getValue() instanceof Double){
            return ((Double)value < (Double)other.getValue());
        } else if(value instanceof Integer && other.getValue() instanceof Integer) {
            return ((Integer) value < (Integer) other.getValue());
        } else if(value instanceof Integer && other.getValue() instanceof Double) {
            return ((Integer) value < (Double) other.getValue());
        } else if(value instanceof Double && other.getValue() instanceof Integer) {
            return ((Double) value < (Integer) other.getValue());
        } else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    public boolean isBigger(Expression other) throws SimulationRunningException {
        if(value instanceof Double && other.getValue() instanceof Double){
            return ((Double)value > (Double)other.getValue());
        } else if(value instanceof Integer && other.getValue() instanceof Integer) {
            return ((Integer) value > (Integer) other.getValue());
        } else if(value instanceof Integer && other.getValue() instanceof Double) {
            return ((Integer) value > (Double) other.getValue());
        } else if(value instanceof Double && other.getValue() instanceof Integer) {
            return ((Double) value > (Integer) other.getValue());
        } else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    /** take an expression and returns its type **/
    public String decipherExpressionType(Map<String, Property> envMap, EntityDefinition entityDefinition, ArrayList<EntityDefinition> entityDefList) throws XMLFileException {
        if (this.isNameOfFunction()) {
            return decipherExpressionTypeFromFunction(envMap, entityDefList);
        } else if(this.isNameOfProperty(entityDefinition)){
            return decipherExpressionTypeFromProperty(entityDefList, entityDefinition.getName(), this.name);
        } else {
            if(isANumber())
                return "float";
            else if(isBoolean())
                return "boolean";
            else
                return "string";
        }
    }
    public String decipherExpressionTypeFromFunction(Map<String, Property> envMap, ArrayList<EntityDefinition> entityDefList) throws XMLFileException {
        if(name.startsWith("environment")){
            String envName = getStringInParenthesis();
            if(envMap.containsKey(envName)){
                return envMap.get(envName).getType();
            } else {
                throw new XMLFileException("Environment variable " + envName + " was not found in world!\n");
            }
        } else if(name.startsWith("evaluate")){
            String[] valueInParenthesis = getStringInParenthesis().split("\\.");
            String entityName = valueInParenthesis[0];
            String propertyName = valueInParenthesis[1];
            return decipherExpressionTypeFromProperty(entityDefList, entityName, propertyName);
        } else
            return "float";
    }
    public String decipherExpressionTypeFromProperty(ArrayList<EntityDefinition> entityDefList, String entityName, String propertyName) throws XMLFileException {
        for(EntityDefinition entDef : entityDefList){
            if(entDef.getName().equals(entityName)){
                if(!(entDef.getPropsDef().containsKey(propertyName)))
                    throw new XMLFileException("The property " + propertyName + " doesn't belong to the entity " + entityName + "!\n");
                else
                    return entDef.getPropsDef().get(propertyName).getType();
            }
        }
        throw new XMLFileException("The entity " + entityName + " was not found in world!\n");
    }

    public boolean checkIfExpressionMatchesType(String type, Map<String, Property> envMap, EntityDefinition entDef, ArrayList<EntityDefinition> entityDefList) throws XMLFileException {
        String expressionType = decipherExpressionType(envMap, entDef, entityDefList);
        return (expressionType.equalsIgnoreCase(type));
    }
}
