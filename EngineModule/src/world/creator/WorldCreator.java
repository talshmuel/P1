package world.creator;

import world.Grid;
import world.World;
import world.entity.EntityDefinition;
import world.property.impl.Property;
import world.rule.Rule;
import world.property.api.*;
import world.property.impl.*;
import world.rule.action.*;
import world.rule.action.Set;
import world.rule.action.api.Expression;
import world.rule.action.calculation.*;
import world.rule.action.condition.*;
import xml.reader.schema.generated.v1.*;
import xml.reader.validator.*;

import java.util.*;


public class WorldCreator {
    Map<String, Property> environmentVarMap;
    ArrayList<EntityDefinition> entityDefList;
    ArrayList<Rule> rulesList;
    Map<String, Integer> endConditionsMap;
    Grid grid;
    public void setEnvironmentVarMap(Map<String, Property> environmentVarMap) {
        this.environmentVarMap = environmentVarMap;
    }
    public void setEntityDefList(ArrayList<EntityDefinition> entityDefList) {
        this.entityDefList = entityDefList;
    }
    public void setRulesList(ArrayList<Rule> rulesList) {
        this.rulesList = rulesList;
    }
    public void setEndConditionsMap(Map<String, Integer> endConditionsMap) {
        this.endConditionsMap = endConditionsMap;
    }
    public World createWorldFromXMLFile(PRDWorld prdWorld) throws EnvironmentException, EntityException, PropertyException, MustBeNumberException, RuleException, TerminationException {
        setEnvironmentVarMap(validateAndCreateEnvironment(prdWorld.getPRDEvironment().getPRDEnvProperty()));
        setEntityDefList(validateAndCreateEntities(prdWorld.getPRDEntities().getPRDEntity()));
        setRulesList(validateAndCreateRules(prdWorld.getPRDRules().getPRDRule(), entityDefList));
        setEndConditionsMap(validateAndCreateTermination(prdWorld.getPRDTermination()));
        Grid grid = new Grid(10, 10); // todo: change hard coded
        return (new World(entityDefList, environmentVarMap, rulesList, endConditionsMap, grid, 3));
    }
    /** CREATE ENVIRONMENT **/
    public Map<String, Property> validateAndCreateEnvironment(List<PRDEnvProperty> prdEnvironment) throws EnvironmentException{
        Map<String, Property> environmentMap = new HashMap<>();
        String envName;

        for(PRDEnvProperty p : prdEnvironment) {
            envName = validateEnvironmentName(environmentMap, p.getPRDName());

            switch (p.getType()){
                case "decimal": // works for both
                case "float":{
                    FloatProperty floatProp = createFloatEnvironmentProperty(p, envName);
                    environmentMap.put(floatProp.getName(), floatProp);
                    break;
                }
                case "boolean":{
                    BooleanPropertyDefinition boolPropDef = new BooleanPropertyDefinition(envName, true, null);
                    environmentMap.put(envName, new BooleanProperty(boolPropDef));
                    break;
                }
                case "string":{
                    StringPropertyDefinition envPropDef = new StringPropertyDefinition(envName, true, null);
                    environmentMap.put(envName, new StringProperty(envPropDef));
                    break;
                }
            }
        }
        return environmentMap;
    }
    public String validateEnvironmentName(Map<String, Property> envMap, String envName) throws EnvironmentException{
        if(envMap.containsKey(envName.trim())){
            throw new EnvironmentException("Error: File contains duplicated environment variable names!\n");
        }
        if(envName.contains(" ")){
            throw new EnvironmentException("Error: Environment variable cannot contain spaces!\n");
        }
        return envName.trim();
    }
    public FloatProperty createFloatEnvironmentProperty(PRDEnvProperty p, String propName) {
        Double from=null, to=null;

        if(p.getPRDRange() != null){
            from = p.getPRDRange().getFrom();
            to = p.getPRDRange().getTo();
        }

        return new FloatProperty(new FloatPropertyDefinition(propName, true, null, to, from));
    }
    /** END OF ENVIRONMENT **/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** CREATE ENTITY **/
    public ArrayList<EntityDefinition> validateAndCreateEntities(List<PRDEntity> prdEntities) throws EntityException, PropertyException {
        ArrayList<EntityDefinition> entityDefinitionList = new ArrayList<>();

        for(PRDEntity e : prdEntities) {
            String entityName = validateEntityName(entityDefinitionList, e.getName().trim());
            Map<String, PropertyDefinition> propDef = validateAndCreatePropertiesDefinition(e);
            entityDefinitionList.add(new EntityDefinition(entityName, e.getPRDPopulation(), propDef));
        }
        return entityDefinitionList;
    }
    /** explanation: checks if the entity name already exists, and if it has spaces in the name. **/
    public String validateEntityName(ArrayList<EntityDefinition> entityDefs, String newEntName) throws EntityException {
        if(entityDefs.stream().anyMatch(entity -> entity.getName().equals(newEntName))){
            throw new EntityException("Error: File contains duplicated entity names!\n");
        }
        if(newEntName.contains(" ")){
            throw new EntityException("Error: File contains entity name with spaces!\n");
        }
        return newEntName;
    }
    /** CREATE PROPPERTIES **/
    public Map<String, PropertyDefinition> validateAndCreatePropertiesDefinition(PRDEntity e) throws PropertyException{
        List<PRDProperty> prdProperties = e.getPRDProperties().getPRDProperty();
        Map<String, PropertyDefinition> propDefsMap = new HashMap<>();

        for(PRDProperty p : prdProperties) {
            String propName = validatePropertyName(propDefsMap, p.getPRDName());

            switch (p.getType()) {
                case "decimal":{
                    //propDefsMap.put(propName, createIntegerPropertyDefinition(p, propName));
                    propDefsMap.put(propName, createFloatPropertyDefinition(p, propName));
                    break;
                }
                case "float":{
                    propDefsMap.put(propName, createFloatPropertyDefinition(p, propName));
                    break;
                }
                case "boolean":{
                    propDefsMap.put(propName, createBooleanPropertyDefinition(p, propName));
                    break;
                }
                case "string":{
                    propDefsMap.put(propName, createStringPropertyDefinition(p, propName));
                    break;
                }
            }
        }
        return propDefsMap;
    }
    public String validatePropertyName(Map<String, PropertyDefinition> map, String newPropName) throws PropertyException {
        if(map.containsKey(newPropName)){
            throw new PropertyException("Error: File cannot contain duplicated property names!\n");
        }
        if(newPropName.contains(" ")){
            throw new PropertyException("Error: Property names cannot contain spaces!\n");
        }
        return newPropName;
    }
    public FloatPropertyDefinition createFloatPropertyDefinition(PRDProperty p, String pName) {
        Double to=null, from=null;

        if(p.getPRDRange() != null){ // a range was given
            from = p.getPRDRange().getFrom();
            to = p.getPRDRange().getTo();
        }

        Double init = null;
        if(p.getPRDValue().getInit() != null){
            init = Double.parseDouble(p.getPRDValue().getInit());
        }

        return new FloatPropertyDefinition(pName, p.getPRDValue().isRandomInitialize(), init, to, from);
    }
    public BooleanPropertyDefinition createBooleanPropertyDefinition(PRDProperty p, String pName){
        Boolean isRandom = p.getPRDValue().isRandomInitialize();
        Boolean init = Boolean.parseBoolean(p.getPRDValue().getInit());
        return new BooleanPropertyDefinition(pName, isRandom, init);
    }
    public StringPropertyDefinition createStringPropertyDefinition(PRDProperty p, String pName){
        Boolean isRandom = p.getPRDValue().isRandomInitialize();
        String init = p.getPRDValue().getInit();
        return new StringPropertyDefinition(pName, isRandom, init);
    }
    /** END OF ENTITY **/
    /////////////////////////////////////////////////
    /** CREATE RULES **/
    public ArrayList<Rule> validateAndCreateRules(List<PRDRule> prdRules, ArrayList<EntityDefinition> entities)
            throws RuleException, MustBeNumberException, EntityException, PropertyException {

        ArrayList<Rule> rulesList = new ArrayList<>();

        for(PRDRule r : prdRules){
            String ruleName = validateRuleName(rulesList, r.getName().trim());
            double probability = validateProbability(r);
            int ticks = validateTicks(r);

            ArrayList<Action> actions = validateAndCreateActionsList(r.getPRDActions().getPRDAction(), entities);

            rulesList.add(new Rule(ruleName, actions, probability, ticks));
        }
        return rulesList;
    }
    public double validateProbability(PRDRule r){
        if(r.getPRDActivation() != null && r.getPRDActivation().getProbability() != null){
            return r.getPRDActivation().getProbability();
        }
        return 1.0; // if not provided -> default is 1
    }
    public int validateTicks(PRDRule r){
        if(r.getPRDActivation() != null && r.getPRDActivation().getTicks() != null){
            return r.getPRDActivation().getTicks();
        }
        return 1; // if not provided -> default is 1
    }
    public String validateRuleName(ArrayList<Rule> rules, String ruleName) throws RuleException{
        if(rules.stream().anyMatch(rule -> rule.getName().equals(ruleName))){
            throw new RuleException("Error: File cannot contain duplicated rules names.\n");
        }
        return ruleName;
    }
    /** EXPLANATION: checks if the by expression is a name of a function **/
    Boolean isNameOfFunction(String expression){ //
        Expression exp = new Expression(expression);
        return exp.isNameOfFunction();

//        Class<?> assist = AssistFunctions.class;
//        Method[] methods = assist.getDeclaredMethods();
//        return Arrays.stream(methods)
//                .anyMatch(m -> expression.startsWith(m.getName()));

    }

    /** CREATE ACTION **/
    /** explanation: this function creates the actions list of a rule or a multiple condition action. **/
    public ArrayList<Action> validateAndCreateActionsList(List<PRDAction> prdActions, ArrayList<EntityDefinition> entities)
            throws MustBeNumberException, EntityException, PropertyException {
        ArrayList<Action> actionsList = new ArrayList<>();

        for(PRDAction a : prdActions){
            String entityNameInAction = a.getEntity();
            EntityDefinition entityDefinitionInWorld = validateEntityOfAction(a.getEntity(), entities);
            actionsList.add(validateAndCreateAction(a, entityNameInAction, entityDefinitionInWorld, entities));
        }
        return actionsList;
    }

    public Action validateAndCreateAction
            (PRDAction a, String entityNameInAction, EntityDefinition entityDefinitionInWorld, ArrayList<EntityDefinition> entities)
            throws MustBeNumberException, EntityException, PropertyException{

        switch (a.getType()) {
            case "increase": {
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(a.getProperty(), entityDefinitionInWorld);
                if(validateExpressionIsANumber(a, propertiesInWorld, "increase"))
                    return new Increase(entityNameInAction, null, a.getProperty(), new Expression(a.getBy()));
            }
            case "decrease":{
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(a.getProperty(), entityDefinitionInWorld);
                if(validateExpressionIsANumber(a, propertiesInWorld, "decrease"))
                    return new Decrease(entityNameInAction, null, a.getProperty(), new Expression(a.getBy()));
            }
            case "kill":{
                String propertyNameInAction = a.getProperty();
                return new Kill(entityNameInAction, null, propertyNameInAction);
            }
            case "set":{
                String propertyNameInAction = a.getProperty();
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(propertyNameInAction, entityDefinitionInWorld);
                String valToSet = validateExpressionAndPropertyTypes(a.getValue(), propertyNameInAction, propertiesInWorld);
                return new Set(entityNameInAction, null, propertyNameInAction, new Expression(valToSet));
            }
            case "calculation":{
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(a.getResultProp(), entityDefinitionInWorld);
                if(a.getPRDMultiply() != null){
                    return validateAndCreatesMultiplyAction(a, entityNameInAction, propertiesInWorld);
                } else {
                    return validateAndCreatesDivideAction(a, entityNameInAction, propertiesInWorld);
                }
            }
            case "condition":{
                // MAIN CONDITION IS MULTIPLE
                if(a.getPRDCondition().getSingularity().equals("multiple")){
                    return validateAndCreatMultipleCondition(a, entities);
                }
                // MAIN CONDITION IS SINGLE
                else {
                    return validateAndCreateSingleCondition(true, a, a.getPRDCondition(), entities ,entityDefinitionInWorld);
                }
            }
        }
        return null;
    }

    public MultipleCondition.Logic validateLogicalSign(String prdLogical){
        switch (prdLogical){
            case "or": { return MultipleCondition.Logic.OR; }
            case "and": { return MultipleCondition.Logic.AND; }
        }
        return null;
    }

    public Condition validateAndCreatMultipleCondition(PRDAction a, ArrayList<EntityDefinition> entities) throws MustBeNumberException, EntityException, PropertyException {
        EntityDefinition entityDefinitionInWorld = validateEntityOfAction(a.getEntity(), entities);
        MultipleCondition.Logic logic = validateLogicalSign(a.getPRDCondition().getLogical());
        ArrayList<Condition> conditionsListInWorld = validateAndCreateConditionsList(a, a.getPRDCondition(), entities, entityDefinitionInWorld);
        ArrayList<Action> thenActions = validateAndCreateThenActions(a, entities);
        ArrayList<Action> elseActions = validateAndCreateElseActions(a, entities);
        return new MultipleCondition(entityDefinitionInWorld.getName(), null, null, thenActions, elseActions, logic, conditionsListInWorld);
    }

    public ArrayList<Action> validateAndCreateThenActions(PRDAction a, ArrayList<EntityDefinition> entities) throws MustBeNumberException, EntityException, PropertyException {
        if(a.getPRDThen() != null) { // there are Then Actions
            return validateAndCreateActionsList(a.getPRDThen().getPRDAction(), entities);
        }
        return null;
    }

    public ArrayList<Action> validateAndCreateElseActions(PRDAction a, ArrayList<EntityDefinition> entities) throws MustBeNumberException, EntityException, PropertyException {
        if(a.getPRDElse() != null) { // there are Else Actions
            return validateAndCreateActionsList(a.getPRDElse().getPRDAction(), entities);
        }
        return null;
    }

    public SingleCondition.Operator validateOperator(String prdOperator){
        switch(prdOperator){
            case "lt":{ return SingleCondition.Operator.LESSTHAN; }
            case "bt": { return SingleCondition.Operator.BIGGERTHAN; }
            case "!=": { return SingleCondition.Operator.NOTEQUAL; }
            case "=": { return SingleCondition.Operator.EQUAL; }
        }
        return null;
    }

    public Condition validateAndCreateSingleCondition(Boolean mainCond, PRDAction a, PRDCondition c, ArrayList<EntityDefinition> entities, EntityDefinition entityDefinitionInWorld) throws MustBeNumberException, EntityException, PropertyException {
        String property = c.getProperty();
        Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(property, entityDefinitionInWorld);
        SingleCondition.Operator operator = validateOperator(c.getOperator());

        // validates if the value type matches the property type
        String expression = validateExpressionAndPropertyTypes(c.getValue(), property, propertiesInWorld);

        if(mainCond){ // if not a main condition -> doesn't have then/else actions.
            ArrayList<Action> thenActions = validateAndCreateThenActions(a, entities);
            ArrayList<Action> elseActions = validateAndCreateElseActions(a, entities);
            return new SingleCondition(c.getEntity(), null, property, operator, new Expression(expression), thenActions, elseActions);

        } else {
            return new SingleCondition(c.getEntity(), null, property, operator, new Expression(expression), null, null); // thenActions, elseActions);
        }

    }

    public ArrayList<Condition> validateAndCreateConditionsList(PRDAction a, PRDCondition prdCondition, ArrayList<EntityDefinition> entities, EntityDefinition entityDefinitionInWorld) throws MustBeNumberException, EntityException, PropertyException {
        List<PRDCondition> prdConditionList = prdCondition.getPRDCondition();
        ArrayList<Condition> conditionsListInWorld = new ArrayList<>();

        for(PRDCondition c : prdConditionList){
            if(c.getSingularity().equals("single")){
                conditionsListInWorld.add(validateAndCreateSingleCondition(false, a, c, entities, entityDefinitionInWorld));
            } else { // multiple condition
                EntityDefinition entityDefinition = validateEntityOfAction(a.getEntity(), entities);

                MultipleCondition.Logic logic = validateLogicalSign(c.getLogical());
                ArrayList<Condition> conditions = validateAndCreateConditionsList(a, c, entities, entityDefinition);
                conditionsListInWorld.add(new MultipleCondition(c.getEntity(), null, null, null, null, logic, conditions));
            }
        }
        return conditionsListInWorld;
    }

    public boolean validateExpressionIsANumber(PRDAction a, Map<String, PropertyDefinition> propertiesInWorld, String actionName) throws MustBeNumberException {
        if(isNameOfFunction(a.getBy())){ // checks if the function will return a number
            return validateExpressionFunctionIsANumber(a.getBy(), actionName, propertiesInWorld);
        } else if(propertiesInWorld.containsKey(a.getBy())){ // checks if the property is of numeric type
            validateExpressionPropertyIsANumber(a.getBy(), propertiesInWorld, actionName);
            return true; // todo
        } else {
            if(a.getBy().matches("\\d+"))
                return true;
            else
                throw new MustBeNumberException(actionName);
        }
    }

    public boolean validateExpressionFunctionIsANumber(String byExpression, String actionName, Map<String, PropertyDefinition> propertiesInWorld) throws MustBeNumberException {
        String functionName = byExpression.substring(0, byExpression.indexOf("("));
        String expressionInParenthesis = byExpression.substring(byExpression.indexOf("(") + 1, byExpression.indexOf(")"));

        switch (functionName) {
            case "environment": {
                return validateEnvironmentFunction(expressionInParenthesis, actionName);
            }
            case "random": {
                if (expressionInParenthesis.matches("\\d+"))
                    return true;
                else
                    throw new MustBeNumberException("XML File Error: Random function must receive a number!\n");
            }
            case "evaluate": {
                return validateEvaluateFunction(expressionInParenthesis);
            }
            case "percent": {
                //return validatePercentFunction(expressionInParenthesis, actionName, propertiesInWorld);
                return true;
            }
            case "ticks": {
                return true;
                // todo
            }
            default: // case "ticks":{
                throw new MustBeNumberException("XML File Error: Function name doesn't exist!\n");
        }
    }
    public boolean validateEnvironmentFunction(String expressionInParenthesis, String actionName) throws MustBeNumberException{
        Property envProperty = environmentVarMap.get(expressionInParenthesis);
        if (envProperty.getType().equals("Integer") || envProperty.getType().equals("Float"))
            return true;
        else
            throw new MustBeNumberException("XML File Error: Property type in action " + actionName + " must be a number!\n");
    }
    public boolean validateEvaluateFunction(String expressionInParenthesis) throws MustBeNumberException {
        String[] splitExpression = expressionInParenthesis.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        for (EntityDefinition e : entityDefList) {
            if (e.getName().equals(entityName)) {
                if (e.getPropsDef().get(propertyName) != null) { // checks if the property type is a number
                    if (e.getPropsDef().get(propertyName).getType().equals("Integer") || e.getPropsDef().get(propertyName).getType().equals("Float"))
                        return true;
                    else
                        throw new MustBeNumberException("XML File Error: The property: " + propertyName + " does not belong to entity: " + entityName + "!\n");
                }
            }
        }

        throw new MustBeNumberException("XML File Error: The entity: " + entityName + " doesn't exist!\n");
    }
    public boolean validatePercentFunction(String expressionInParenthesis, String actionName, Map<String, PropertyDefinition> propertiesInWorld) throws MustBeNumberException {
        String expr1 = expressionInParenthesis.substring(0, expressionInParenthesis.indexOf(")"));
        String expr2 = expressionInParenthesis.substring(expressionInParenthesis.indexOf(",")+1);

        validateExpressionInPercentFunction(expr1, actionName, propertiesInWorld);
        validateExpressionInPercentFunction(expr2, actionName, propertiesInWorld);
        return true;

        //return validateExpressionFunctionIsANumber(expr1, actionName) && validateExpressionFunctionIsANumber(expr2, actionName);
    }

    void validateExpressionInPercentFunction(String expression, String actionName, Map<String, PropertyDefinition> propertiesInWorld) throws MustBeNumberException {
        if(isNameOfFunction(expression)){
            validateExpressionFunctionIsANumber(expression, actionName, propertiesInWorld);
        } else if (propertiesInWorld.containsKey(expression)){
            PropertyDefinition property = propertiesInWorld.get(expression);
            if(!property.getType().equals("Integer") && !property.getType().equals("Float"))
                throw new MustBeNumberException("XML File Error: Property " + property.getName() + " in percent function must be a number!\n");
        } else {
            if(!expression.matches("\\d+"))
                throw new MustBeNumberException("XML File Error: Expression in percent function must be a number!\n");
        }
    }

    public boolean validateTicksFunction(String byExpression, String expressionInParenthesis){

        return true;
    }
    void validateExpressionPropertyIsANumber(String byExpression, Map<String, PropertyDefinition> propertiesInWorld, String actionName) throws MustBeNumberException{
        PropertyDefinition property = propertiesInWorld.get(byExpression);
        if(!property.getType().equals("Integer") && !property.getType().equals("Float"))
            throw new MustBeNumberException("XML File Exception: property given in action: " + actionName + " must be a number!\n");
    }

    /** EXPLANATION: check if a property type and an expression type matches. returns the name of the expression. **/
    public String validateExpressionAndPropertyTypes(String expressionToCheck, String property, Map<String, PropertyDefinition> propertiesInWorld) throws PropertyException {
        if(!isNameOfFunction(expressionToCheck) && !propertiesInWorld.containsKey(expressionToCheck)) {
            // checks if the type of the property to set matches
            String typeOfProperty = propertiesInWorld.get(property).getType();
            switch (typeOfProperty){
                case "decimal":{} // this check is for both of them
                case "float":{
                    if(!expressionToCheck.matches("\\d+")) {
                        throw new PropertyException("Value in Set action does not match the type of this property!\n");
                    }
                    break;
                }
                case "boolean":{
                    if(!expressionToCheck.matches("true") && !expressionToCheck.matches("false")){
                        throw new PropertyException("Value in Set action does not match the type of this property!\n");
                    }
                    break;
                } // only case left is "string" which matches
            }
        }
        return expressionToCheck;
    }



    /** EXPLANATION: checks if this property is of numeric type (decimal, float) **/
    public void checkIfPropertyIsNumeric(Map<String, PropertyDefinition> propertiesInWorld, String propertyToCheck) throws MustBeNumberException {
        // check if this property is numeric type:
        if(!propertiesInWorld.get(propertyToCheck).getType().equals("Integer") && !propertiesInWorld.get(propertyToCheck).getType().equals("Float")){
            throw new MustBeNumberException("Calculation");
        }
    }
    public String validateCalculationExpression(String arg, Map<String, PropertyDefinition> propertiesMap) throws MustBeNumberException {
        if(propertiesMap.containsKey(arg)){ // expression is a property
            checkIfPropertyIsNumeric(propertiesMap, arg);
        } else if(!isNameOfFunction(arg) && !arg.matches("\\d*\\.?\\d*")) { // expression is function or a number
            throw new MustBeNumberException("Calculation");
        }
        return arg;
    }
    public Multiply validateAndCreatesMultiplyAction(PRDAction a, String entityInAction, Map<String, PropertyDefinition> propertiesMap) throws MustBeNumberException{
        checkIfPropertyIsNumeric(propertiesMap, a.getResultProp()); // check if the result property is of numeric type
        String arg1 = validateCalculationExpression(a.getPRDMultiply().getArg1(), propertiesMap);
        String arg2 = validateCalculationExpression(a.getPRDMultiply().getArg2(), propertiesMap);
        return new Multiply(entityInAction, null, a.getResultProp(), new Expression(arg1), new Expression(arg2));
    }
    public Divide validateAndCreatesDivideAction(PRDAction a, String entityInAction, Map<String, PropertyDefinition> propertiesMap) throws MustBeNumberException{
        checkIfPropertyIsNumeric(propertiesMap, a.getResultProp()); // check if the result property is of numeric type
        String arg1 = validateCalculationExpression(a.getPRDDivide().getArg1(), propertiesMap);
        String arg2 = validateCalculationExpression(a.getPRDDivide().getArg2(), propertiesMap);
        return new Divide(entityInAction,null, a.getResultProp(), new Expression(arg1), new Expression(arg2));
    }
    /** EXPLANATION: checks if this entity exists in world, and if so -> returns the EntityDefinition **/
    public EntityDefinition validateEntityOfAction(String entityInAction, ArrayList<EntityDefinition> entities) throws EntityException {
        for(EntityDefinition e : entities){
            if(entityInAction.equals(e.getName())){
                return e;
            }
        }
        throw new EntityException("Error: Entity doesn't exist in world!\n");
    }

    /** EXPLANATION: checks if this property exists, and that it belongs to the entity. returns map of properties of this entity. **/
    public Map<String, PropertyDefinition> validatePropertyInAction(String propertyNameInAction, EntityDefinition entityInWorld) throws PropertyException{
        Map<String, PropertyDefinition> propertiesInWorld = entityInWorld.getPropsDef();

        if(!propertiesInWorld.containsKey(propertyNameInAction)){
            throw new PropertyException("Property doesn't exist in world!\n");
        }
        return propertiesInWorld;
    }
    /** CREATE TERMINATION **/
    public Map<String, Integer> validateAndCreateTermination(PRDTermination prdTermination) throws TerminationException{
        List<Object> prdByTicksOrPRDBySecond = prdTermination.getPRDByTicksOrPRDBySecond();
        Map<String, Integer> endConditions = new HashMap<>();

        if(prdByTicksOrPRDBySecond.size() == 0){
            throw new TerminationException("Error: File must contain at least one of the termination conditions! (by seconds or by ticks).\n");
        }

        for(Object o : prdByTicksOrPRDBySecond){
            if(o instanceof PRDByTicks){
                Integer ticks = ((PRDByTicks) o).getCount();
                endConditions.put("ticks", ticks);
            } else if(o instanceof PRDBySecond){
                Integer seconds = ((PRDBySecond) o).getCount();
                endConditions.put("seconds", seconds);
            }
        }
        return endConditions;
    }
}
