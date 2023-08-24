package world.creator;

import world.World;
import world.entity.EntityDefinition;
import world.property.impl.Property;
import world.rule.Rule;
import world.api.AssistFunctions;
import world.property.api.*;
import world.property.impl.*;
import world.rule.action.*;
import world.rule.action.Set;
import world.rule.action.calculation.*;
import world.rule.action.condition.*;
import xml.reader.validator.*;
import xml.reader.schema.generated.*;
import java.lang.reflect.Method;
import java.util.*;


public class WorldCreator {

    public World createWorldFromXMLFile(PRDWorld prdWorld) throws EnvironmentException, EntityException, PropertyException, MustBeNumberException, RuleException, TerminationException {
        Map<String, Property> environmentVarMap = validateAndCreateEnvironment(prdWorld.getPRDEvironment().getPRDEnvProperty());
        ArrayList<EntityDefinition> entityDefList = validateAndCreateEntities(prdWorld.getPRDEntities().getPRDEntity());
        ArrayList<Rule> rulesList = validateAndCreateRules(prdWorld.getPRDRules().getPRDRule(), entityDefList);
        Map<String, Integer> endConditionsMap = validateAndCreateTermination(prdWorld.getPRDTermination());
        return (new World(entityDefList, environmentVarMap, rulesList, endConditionsMap));
    }
    /** CREATE ENVIRONMENT **/
    public Map<String, Property> validateAndCreateEnvironment(List<PRDEnvProperty> prdEnvironment) throws EnvironmentException{
        Map<String, Property> environmentMap = new HashMap<>();
        String envName;

        for(PRDEnvProperty p : prdEnvironment) {
            envName = validateEnvironmentName(environmentMap, p.getPRDName());

            switch (p.getType()){
                case "decimal":{
                    IntegerProperty intProp = createIntegerEnvironmentProperty(p, envName);
                    environmentMap.put(intProp.getName(), intProp);
                    break;
                }
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
    public IntegerProperty createIntegerEnvironmentProperty(PRDEnvProperty p, String propName) {
        Integer from=null, to=null;

        if(p.getPRDRange() != null){ // a range was given
            from = (int) p.getPRDRange().getFrom();
            to = (int) p.getPRDRange().getTo();
        }
        return new IntegerProperty(new IntegerPropertyDefinition(propName, true, null, to, from));
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
                    propDefsMap.put(propName, createIntegerPropertyDefinition(p, propName));
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
    public IntegerPropertyDefinition createIntegerPropertyDefinition(PRDProperty p, String pName) {
        Integer to=null, from=null;

        if(p.getPRDRange() != null){ // a range was given
            from = (int) p.getPRDRange().getFrom();
            to = (int) p.getPRDRange().getTo();
        }

        Integer init = null;
        if(p.getPRDValue().getInit() != null){
            init = Integer.parseInt(p.getPRDValue().getInit());
        }

        return new IntegerPropertyDefinition(pName, p.getPRDValue().isRandomInitialize(), init, to, from);
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
//        if(ruleName.contains(" ")){ // rule name can't have spaces
//            throw new RuleException("Error: Rule's name cannot contain spaces!\n");
//        }
        return ruleName;
    }
    /** EXPLANATION: checks if the by expression is a name of a function **/
    Boolean isNameOfFunction(String expression){ //
        Class<?> assist = AssistFunctions.class;
        Method[] methods = assist.getDeclaredMethods();
        return Arrays.stream(methods)
                .anyMatch(m -> expression.startsWith(m.getName()));

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
            throws MustBeNumberException, EntityException, PropertyException {

        switch (a.getType()) {
            case "increase": {
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(a.getProperty(), entityDefinitionInWorld);
                String by = validateIncreaseOrDecreaseAction(a, propertiesInWorld, "increase");
                return new Increase(entityNameInAction, a.getProperty(), by);
            }
            case "decrease":{
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(a.getProperty(), entityDefinitionInWorld);
                String by = validateIncreaseOrDecreaseAction(a, propertiesInWorld, "decrease");
                return new Decrease(entityNameInAction, a.getProperty(), by);
            }
            case "kill":{
                String propertyNameInAction = a.getProperty();
                return new Kill(entityNameInAction, propertyNameInAction);
            }
            case "set":{
                String propertyNameInAction = a.getProperty();
                Map<String, PropertyDefinition> propertiesInWorld = validatePropertyInAction(propertyNameInAction, entityDefinitionInWorld);
                String valToSet = validateExpressionAndPropertyTypes(a.getValue(), propertyNameInAction, propertiesInWorld);
                return new Set(entityNameInAction, propertyNameInAction, valToSet);
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
        return new MultipleCondition(entityDefinitionInWorld.getName(), null, thenActions, elseActions, logic, conditionsListInWorld);
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
            return new SingleCondition(c.getEntity(), property, operator, expression, thenActions, elseActions);

        } else {
            return new SingleCondition(c.getEntity(), property, operator, expression, null, null); // thenActions, elseActions);
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
                conditionsListInWorld.add(new MultipleCondition(c.getEntity(), null, null, null, logic, conditions));
            }
        }
        return conditionsListInWorld;
    }
    public String validateIncreaseOrDecreaseAction(PRDAction a, Map<String, PropertyDefinition> propertiesInWorld, String actionName) throws MustBeNumberException {
        if(!isNameOfFunction(a.getBy()) && !propertiesInWorld.containsKey(a.getBy())){ // checks if it's function or property
            if(!a.getBy().matches("\\d+")) { // checks if it's a number
                throw new MustBeNumberException(actionName);
            }
        }
        return a.getBy();
    }
    /** EXPLANATION: check if a property type and an expression type matches. returns the name of the expression. **/
    public String validateExpressionAndPropertyTypes(String expressionToCheck, String property, Map<String, PropertyDefinition> propertiesInWorld) throws PropertyException {

        if(!isNameOfFunction(expressionToCheck) && !propertiesInWorld.containsKey(expressionToCheck)) { // checks if it's function or property
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
        return new Multiply(entityInAction, a.getResultProp(), arg1, arg2);
    }
    public Divide validateAndCreatesDivideAction(PRDAction a, String entityInAction, Map<String, PropertyDefinition> propertiesMap) throws MustBeNumberException{
        checkIfPropertyIsNumeric(propertiesMap, a.getResultProp()); // check if the result property is of numeric type
        String arg1 = validateCalculationExpression(a.getPRDDivide().getArg1(), propertiesMap);
        String arg2 = validateCalculationExpression(a.getPRDDivide().getArg2(), propertiesMap);
        return new Divide(entityInAction, a.getResultProp(), arg1, arg2);
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
