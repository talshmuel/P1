package world.creator;

import world.Grid;
import world.World;
import world.entity.EntityDefinition;
import world.property.api.*;
import world.property.impl.*;
import world.rule.Rule;
import world.rule.action.*;
import world.rule.action.api.Expression;
import world.rule.action.api.SecondaryEntity;
import world.rule.action.calculation.Calculation;
import world.rule.action.calculation.Divide;
import world.rule.action.calculation.Multiply;
import world.rule.action.condition.Condition;
import world.rule.action.condition.MultipleCondition;
import world.rule.action.condition.SingleCondition;
import xml.reader.schema.generated.v2.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldCreatorXML {
    Map<String, Property> environmentVarMap;
    ArrayList<EntityDefinition> entityDefList;
    ArrayList<Rule> rulesList;
    Map<String, Integer> endConditionsMap;
    Grid grid;

    public World createWorldFromXMLFile(PRDWorld prdWorld) throws XMLFileException {
        int threads = prdWorld.getPRDThreadCount();
        grid = validateAndCreateGrid(prdWorld.getPRDGrid());
        environmentVarMap = validateAndCreateEnvironment(prdWorld.getPRDEnvironment().getPRDEnvProperty());
        entityDefList = validateAndCreateEntities(prdWorld.getPRDEntities().getPRDEntity());
        rulesList = validateAndCreateRules(prdWorld.getPRDRules().getPRDRule());
        endConditionsMap = new HashMap<>();
        endConditionsMap = validateAndCreateTermination(prdWorld.getPRDTermination());
        return (new World(entityDefList, environmentVarMap, rulesList, endConditionsMap, grid, threads));
    }

    public Map<String, Integer> validateAndCreateTermination(PRDTermination prdTermination) throws XMLFileException {
        if(prdTermination.getPRDByUser() != null && (!(prdTermination.getPRDBySecondOrPRDByTicks().isEmpty())))
            // both conditions appear
            throw new XMLFileException("XML File Error:\nEnd condition must be one of two: by user choice or by number of ticks/seconds, but not them both!\n");
        if(prdTermination.getPRDByUser() == null && (prdTermination.getPRDBySecondOrPRDByTicks().isEmpty()))
            // no end condition appears
            throw new XMLFileException("XML File Error:\nFile must contain at least one of the termination conditions! by user's choice or by ticks/seconds.\n");

        Map<String, Integer> endConditions = new HashMap<>();

        // if we got here, one of them isn't null for sure
        if(prdTermination.getPRDByUser() != null){
            endConditions.put("user", null);
        } else {
            List<Object> prdByTicksOrPRDBySecond = prdTermination.getPRDBySecondOrPRDByTicks();
            /*if(prdByTicksOrPRDBySecond.isEmpty())
                throw new XMLFileException("XML File Error: Termination condition by seconds/ticks is empty! There must be at least one of them.\n");*/

            for(Object o : prdByTicksOrPRDBySecond){
                if(o instanceof PRDByTicks){
                    Integer ticks = ((PRDByTicks) o).getCount();
                    endConditions.put("ticks", ticks);
                } else if(o instanceof PRDBySecond){
                    Integer seconds = ((PRDBySecond) o).getCount();
                    endConditions.put("seconds", seconds);
                }
            }
        }

        return endConditions;
    }

    public Grid validateAndCreateGrid(PRDWorld.PRDGrid prdGrid) throws XMLFileException {
        int rows = prdGrid.getRows();
        if(rows < 10 || rows > 100){
            throw new XMLFileException("XML File Error:\nNumber of rows in grid must be between 10-100!\n");
        }

        int cols = prdGrid.getColumns();
        if(cols < 10 || cols > 100){
            throw new XMLFileException("XML File Error:\nNumber of columns in grid must be between 10-100!\n");
        }

        return new Grid(rows, cols);
    }
    /** CREATE ENVIRONMENT **/
    public Map<String, Property> validateAndCreateEnvironment(List<PRDEnvProperty> prdEnvironment) throws XMLFileException {
        Map<String, Property> environmentMap = new HashMap<>();

        for(PRDEnvProperty prdEnvProperty : prdEnvironment) {
            String envName = validateEnvironmentName(environmentMap, prdEnvProperty.getPRDName());

            switch (prdEnvProperty.getType().toLowerCase()){
                case "decimal": // works for both
                case "float":{
                    FloatProperty floatProp = createFloatEnvironmentProperty(prdEnvProperty, envName);
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
    public String validateEnvironmentName(Map<String, Property> environmentMap, String envName) throws XMLFileException {
        if(environmentMap.containsKey(envName.trim())){
            throw new XMLFileException("XML File Error:\nFile contains duplicated environment variable names!\n");
        } else if(envName.trim().contains(" ")){
            throw new XMLFileException("XML File Error:\nEnvironment variable names cannot contain spaces!\n");
        }
        return envName.trim();
    }
    public FloatProperty createFloatEnvironmentProperty(PRDEnvProperty prdEnvProperty, String propName){
        Double from=null, to=null;
        if(prdEnvProperty.getPRDRange() != null){
            from = prdEnvProperty.getPRDRange().getFrom();
            to = prdEnvProperty.getPRDRange().getTo();
        } else {
            from = Double.valueOf(0);
            to = Double.valueOf(100);
        }

        return new FloatProperty(new FloatPropertyDefinition(propName, true, null, to, from));
    }
    /** END OF ENVIRONMENT **/
    /** CREATE ENTITY **/
    public ArrayList<EntityDefinition> validateAndCreateEntities(List<PRDEntity> prdEntities) throws XMLFileException{
        ArrayList<EntityDefinition> entityDefinitionList = new ArrayList<>();
        for(PRDEntity e : prdEntities) {
            String entityName = validateEntityName(entityDefinitionList, e.getName().trim());
            Map<String, PropertyDefinition> propDef = validateAndCreatePropertiesDefinition(e.getPRDProperties().getPRDProperty());
            // create entities defaulted with 0 instances
            entityDefinitionList.add(new EntityDefinition(entityName, 0, propDef));
        }
        return entityDefinitionList;
    }
    public String validateEntityName(ArrayList<EntityDefinition> entityDefs, String newEntName) throws XMLFileException {
        /** explanation: checks if the entity name already exists, and if it has spaces in the name. **/
        if(entityDefs.stream().anyMatch(entity -> entity.getName().equals(newEntName))){
            throw new XMLFileException("XML File Error:\nFile contains duplicated entity names!\n");
        }
        if(newEntName.contains(" ")){
            throw new XMLFileException("XML File Error:\nFile contains entity name with spaces!\n");
        }
        return newEntName;
    }
    /** CREATE PROPPERTIES **/
    public Map<String, PropertyDefinition> validateAndCreatePropertiesDefinition(List<PRDProperty> prdProperty) throws XMLFileException {
        Map<String, PropertyDefinition> propDefsMap = new HashMap<>();
        for(PRDProperty p : prdProperty){
            String propName = validatePropertyName(propDefsMap, p.getPRDName());

            switch(p.getType().toLowerCase()){
                case "decimal":{}
                case "float":{
                    propDefsMap.put(propName, createFloatPropertyDefinition(p, propName));
                    break;
                } case "boolean":{
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
    public String validatePropertyName(Map<String, PropertyDefinition> map, String newPropName) throws XMLFileException {
        if(map.containsKey(newPropName)){
            throw new XMLFileException("XML File Error:\nEntity contains duplicated property names!\n");
        }
        if(newPropName.contains(" ")){
            throw new XMLFileException("XML File Error:\nEntity's property names cannot contain spaces!\n");
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

        if(p.getPRDValue().isRandomInitialize() && p.getPRDRange() == null){
            from = Double.valueOf(0);
            to = Double.valueOf(100);
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
    /** END OF PROPPERTIES **/
    /** END OF ENTITY **/
    /** CREATE RULES **/
    public ArrayList<Rule> validateAndCreateRules(List<PRDRule> prdRules) throws XMLFileException {
        ArrayList<Rule> rulesList = new ArrayList<>();
        for(PRDRule r : prdRules){
            String ruleName = validateRuleName(rulesList, r.getName().trim());
            double probability = validateProbability(r.getPRDActivation());
            int ticks = validateTicks(r.getPRDActivation());
            ArrayList<Action> actionsList = validateAndCreateActionsList(r.getPRDActions().getPRDAction());
            rulesList.add(new Rule(ruleName, actionsList, probability, ticks));
        }
        return rulesList;
    }
    public String validateRuleName(ArrayList<Rule> rules, String ruleName) throws XMLFileException {
        if(rules.stream().anyMatch(rule -> rule.getName().equals(ruleName))){
            throw new XMLFileException("XML File Error: File cannot contain duplicated rules names.\n");
        }
        return ruleName;
    }
    public double validateProbability(PRDActivation prdActivation){
        if(prdActivation != null && prdActivation.getProbability() != null){
            return prdActivation.getProbability();
        }
        return 1.0; // if not provided -> default is 1
    }
    public int validateTicks(PRDActivation prdActivation){
        if(prdActivation != null && prdActivation.getTicks() != null){
            return prdActivation.getTicks();
        }
        return 1; // if not provided -> default is 1
    }
    /** CREATE ACTIONS LIST OF A SINGLE RULE **/
    ArrayList<Action> validateAndCreateActionsList(List<PRDAction> prdActionsList) throws XMLFileException {
        ArrayList<Action> actionsList = new ArrayList<>();
        for(PRDAction a : prdActionsList){
            Action action = validateAndCreateAction(a);
            actionsList.add(action);
        }
        return actionsList;
    }
    Action validateAndCreateAction(PRDAction prdAction) throws XMLFileException {
        switch (prdAction.getType()) {
            case "increase":
            case "decrease": // works for both
                return validateIncreaseDecreaseActions(prdAction);
            case "kill":
                return validateAndCreateKillAction(prdAction);
            case "set":
                return validateAndCreateSetAction(prdAction);
            case "calculation":
                return validateAndCreateCalculationAction(prdAction);
            case "replace":
                return validateAndCreateReplaceAction(prdAction);
            case "proximity":
                return validateAndCreateProximityAction(prdAction);
            default: // case "condition":
                return validateAndCreateConditionAction(prdAction);
        }
    }
    public Action validateIncreaseDecreaseActions(PRDAction prdAction) throws XMLFileException {
        // validate entity name exists
        String mainEntityName = prdAction.getEntity();
        EntityDefinition mainEntityDefinition = findEntityDefinitionInWorld(mainEntityName);

        // validate property belongs to entity, and that is of number type
        String propertyName = validatePropertyBelongsToEntity(prdAction.getProperty(), mainEntityDefinition);
        validatePropertyIsANumber(propertyName, mainEntityDefinition, prdAction.getType());

        // validate expression is valid, meaning, returning a number
        Expression expression = validateExpressionIsANumber(prdAction.getBy(), mainEntityDefinition, prdAction.getType());

        SecondaryEntity secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        if(prdAction.getType().equals("increase")){
            return new Increase(mainEntityName, secondaryEntity, propertyName, expression);
        } else {
            return new Decrease(mainEntityName, secondaryEntity, propertyName, expression);
        }
    }
    public Kill validateAndCreateKillAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entDef = findEntityDefinitionInWorld(prdAction.getEntity());
        SecondaryEntity secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        return new Kill(entDef.getName(), secondaryEntity, null);
    }
    public Calculation validateAndCreateCalculationAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String resultProp = validatePropertyBelongsToEntity(prdAction.getResultProp(), entityDefinition);
        validatePropertyIsANumber(prdAction.getProperty(), entityDefinition, "Calculation");
        SecondaryEntity secondaryEntity=validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        if(prdAction.getPRDMultiply() != null){
            Expression expression1 = validateExpressionIsANumber(prdAction.getPRDMultiply().getArg1(), entityDefinition, "Multiply");
            Expression expression2 = validateExpressionIsANumber(prdAction.getPRDMultiply().getArg2(), entityDefinition, "Multiply");
            return new Multiply(entityDefinition.getName(), secondaryEntity, resultProp, expression1, expression2);
        } else { // Divide
            Expression expression1 = validateExpressionIsANumber(prdAction.getPRDDivide().getArg1(), entityDefinition, "Divide");
            Expression expression2 = validateExpressionIsANumber(prdAction.getPRDDivide().getArg2(), entityDefinition, "Divide");
            return new Divide(entityDefinition.getName(), secondaryEntity, resultProp, expression1, expression2);
        }
    }
    public Set validateAndCreateSetAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String propertyName = validatePropertyBelongsToEntity(prdAction.getProperty(), entityDefinition);
        PropertyDefinition propertyDefinition = entityDefinition.getPropsDef().get(propertyName);
        Expression expression = validateExpressionValueInSetAction(prdAction, propertyDefinition.getType(), entityDefinition);
        SecondaryEntity secondaryEntity=validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        return new Set(entityDefinition.getName(), secondaryEntity, propertyName, expression);
    }
    public Expression validateExpressionValueInSetAction(PRDAction prdAction, String propertyType, EntityDefinition entityDefinition) throws XMLFileException {
        switch (propertyType) { // validate value matches by type
            case "Float": {
                return validateExpressionIsANumber(prdAction.getValue(), entityDefinition, "Set");
            } case "Boolean": {
                return validateExpressionIsABoolean(prdAction.getValue(), entityDefinition, "Set");
            } default: { // case "string"
                return validateExpressionIsAString(prdAction.getValue(), entityDefinition, "Set");
            }
        }
    }
    public Replace validateAndCreateReplaceAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition definitionToKill = findEntityDefinitionInWorld(prdAction.getKill());
        EntityDefinition definitionToCreate = findEntityDefinitionInWorld(prdAction.getCreate());
        String mode = prdAction.getMode();
        if(!(mode.equals("scratch")) && !(mode.equals("derived"))){
            throw new XMLFileException("XML File Error: 'mode' in action Replace is invalid! could be only 'scratch' or 'derived'\n");
        }
        SecondaryEntity secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        return new Replace(definitionToKill.getName(), secondaryEntity, definitionToCreate.getName(), mode, definitionToCreate);
    }
    public Proximity validateAndCreateProximityAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition sourceDefinition = findEntityDefinitionInWorld(prdAction.getPRDBetween().getSourceEntity());
        EntityDefinition targetDefinition = findEntityDefinitionInWorld(prdAction.getPRDBetween().getTargetEntity());
        Expression depth = validateExpressionIsANumber(prdAction.getPRDEnvDepth().getOf(), sourceDefinition, "Proximity");
        ArrayList<Action> thenActions = validateAndCreateActionsList(prdAction.getPRDActions().getPRDAction());
        SecondaryEntity secondaryEntity=validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        return new Proximity(sourceDefinition.getName(), secondaryEntity, null, depth, thenActions, targetDefinition.getName(), grid);
    }
    /** ///////////////////////////////////////// CONDITION VALIDATION ////////////////////////////////////////////// **/
    public Condition validateAndCreateConditionAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String mainEntityName = entityDefinition.getName();
        SecondaryEntity secondaryEntity=validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        if (prdAction.getPRDCondition().getSingularity().equals("single"))
            return validateAndCreateSingleCondition(true, prdAction, prdAction.getPRDCondition(), secondaryEntity);
        else
            return validateAndCreateMultipleCondition(true, prdAction, prdAction.getPRDCondition(), secondaryEntity, mainEntityName);
    }
    /** ///////////////////////////////// SINGLE CONDITION VALIDATION ////////////////////////////////////////////// **/
    public SingleCondition validateAndCreateSingleCondition(boolean mainCond, PRDAction prdAction, PRDCondition prdCondition, SecondaryEntity secondaryEntity) throws XMLFileException {
        String mainEntityName = prdCondition.getEntity();
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(mainEntityName);

        // first -> figure out what type the 'property' expression is:
        Expression propExpression = new Expression(prdCondition.getProperty());
        String type = propExpression.decipherExpressionType(environmentVarMap, entityDefinition, entityDefList);

        // then -> check if the value matches in its type
        Expression valueExpression = new Expression(prdCondition.getValue());
        if(!(valueExpression.checkIfExpressionMatchesType(type, environmentVarMap, entityDefinition, entityDefList)))
            throw new XMLFileException("XML File Exception: Property expression in Single Condition doesn't match the value expression type!\n");

        SingleCondition.Operator operator = validateOperatorInSingleCondition(prdCondition.getOperator());

        ArrayList<Action> thenActions=null, elseActions=null;
        if(mainCond) { // if not a main condition -> doesn't have then/else actions.
            if (prdAction.getPRDThen() != null)
                thenActions = validateAndCreateActionsList(prdAction.getPRDThen().getPRDAction());
            else
                throw new XMLFileException("XML File Error: Single Condition must contain 'then actions'!\n");

            if (prdAction.getPRDElse() != null)
                elseActions = validateAndCreateActionsList(prdAction.getPRDElse().getPRDAction());
        }

        return new SingleCondition(mainEntityName, secondaryEntity, propExpression, operator, valueExpression, thenActions, elseActions);
    }
    public SingleCondition.Operator validateOperatorInSingleCondition(String prdOperation) throws XMLFileException {
        switch (prdOperation) {
            case "=":
                return SingleCondition.Operator.EQUAL;
            case "!=":
                return SingleCondition.Operator.NOTEQUAL;
            case "lt":
                return SingleCondition.Operator.LESSTHAN;
            case "bt":
                return SingleCondition.Operator.BIGGERTHAN;
            default:
                throw new XMLFileException("XML File Error: Operation sign in condition isn't legal!\n");
        }
    }
    /** ///////////////////////////////////////////////// END OF CONDITION /////////////////////////////////////////////////// **/
    /** ///////////////////////////////////////// MULTIPLE CONDITION VALIDATION ////////////////////////////////////////////// **/
    public MultipleCondition validateAndCreateMultipleCondition(boolean mainCond, PRDAction prdAction, PRDCondition prdCondition, SecondaryEntity secondaryEntity, String mainEntityName) throws XMLFileException {
        ArrayList<Condition> conditions = new ArrayList<>();
        List<PRDCondition> prdConditionList = prdCondition.getPRDCondition();
        for(PRDCondition p : prdConditionList){
            if(p.getSingularity().equals("single")){
                Condition condition = validateAndCreateSingleCondition(false, prdAction, p, null);
                conditions.add(condition);
            } else{
                conditions.add(validateAndCreateMultipleCondition(false, prdAction, p, null, mainEntityName));
            }
        }

        ArrayList<Action> thenActions=null, elseActions=null;
        if(mainCond){
            if(prdAction.getPRDThen() != null)
                thenActions = validateAndCreateActionsList(prdAction.getPRDThen().getPRDAction());
            else
                throw new XMLFileException("XML File Error: Multiple Condition must contain 'then actions'!\n");

            if(prdAction.getPRDElse() != null)
                elseActions = validateAndCreateActionsList(prdAction.getPRDElse().getPRDAction());
        }

        MultipleCondition.Logic logicSign = validateLogicSign(prdCondition.getLogical());
        return new MultipleCondition(mainEntityName, secondaryEntity, null, thenActions, elseActions, logicSign, conditions);
    }

    MultipleCondition.Logic validateLogicSign(String prdLogical) throws XMLFileException {
        if(prdLogical.equals("or"))
            return MultipleCondition.Logic.OR;
        else if(prdLogical.equals("and"))
            return MultipleCondition.Logic.AND;
        else
            throw new XMLFileException("XML File Error: Logical sign in Multiple Condition isn't legal!\n");
    }

    /** ///////////////////////////////////////////// END OF MULTIPLE CONDITION  ///////////////////////////////////////////// **/
    /** //////////////////////////////////////////// SECONDARY ENTITY VALIDATION //////////////////////////////////////////// **/
    public SecondaryEntity validateAndCreateSecondaryEntity(PRDAction.PRDSecondaryEntity prdSecondaryEntity) throws XMLFileException {
        if(prdSecondaryEntity != null){
            EntityDefinition secondEntityDefinition = findEntityDefinitionInWorld(prdSecondaryEntity.getEntity());
            PRDAction.PRDSecondaryEntity.PRDSelection prdSelection = prdSecondaryEntity.getPRDSelection();

            // validating count:
            String prdCount = prdSelection.getCount();
            Integer numOfSecondEntities = validateCountNumberInSecondaryEntity(prdCount);

            // validating condition:
            PRDCondition prdCondition = prdSelection.getPRDCondition(); // validate
            Condition selection = validateSelectionConditionInSecondaryEntity(prdCondition);

            return new SecondaryEntity(secondEntityDefinition.getName(), numOfSecondEntities, selection);
        }
        else
            return null;
    }
    public Integer validateCountNumberInSecondaryEntity(String prdCount) throws XMLFileException {
        if(prdCount.equals("ALL"))
            return null;
        else if (prdCount.matches("[0-9.]+")){
            if(Integer.parseInt(prdCount) > 0)
                return Integer.parseInt(prdCount);
            else
                throw new XMLFileException("XML File Error: Number of secondary entities must be over 0!\n");
        } else
            throw new XMLFileException("XML File Error: Number of secondary entities must be a number!\n");
    }
    public Condition validateSelectionConditionInSecondaryEntity(PRDCondition prdCondition) throws XMLFileException {
        if(prdCondition.getSingularity().equals("single")){
            EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdCondition.getEntity());

            // first -> figure out what type the 'property' expression is:
            Expression propExpression = new Expression(prdCondition.getProperty());
            String type = propExpression.decipherExpressionType(environmentVarMap, entityDefinition, entityDefList);

            // then -> check if the value matches in its type
            Expression valueExpression = new Expression(prdCondition.getValue());
            if(!(valueExpression.checkIfExpressionMatchesType(type, environmentVarMap, entityDefinition, entityDefList)))
                throw new XMLFileException("XML File Exception: Selection condition in secondary entity is invalid: Property expression in Single Condition doesn't match the value expression type!\n");

            SingleCondition.Operator operator = validateOperatorInSingleCondition(prdCondition.getOperator());
            return new SingleCondition(entityDefinition.getName(), null, propExpression, operator, valueExpression, null, null);
        }
        else {
            return null;
        }
    }
    /** //////////////////////////////////////////// END OF SECONDARY ENTITY //////////////////////////////////////////// **/

    /** //////////////////////////////////////////// GENERAL ACTION HELPER METHODS ///////////////////////////////////////**/
    public EntityDefinition findEntityDefinitionInWorld(String entityName) throws XMLFileException{
        /** checks the entity exists **/
        for(EntityDefinition e : entityDefList){
            if(e.getName().equals(entityName)){
                return e;
            }
        }
        throw new XMLFileException("XML File Error: Entity " + entityName + " doesn't exist in world!\n");
    }
    public String validatePropertyBelongsToEntity(String propertyName, EntityDefinition entity) throws XMLFileException {
        if(entity.getPropsDef().containsKey(propertyName)){
            return propertyName;
        }
        throw new XMLFileException("XML File Error: Property " + propertyName + " doesn't belong to entity " + entity.getName() + "!\n");
    }
    public void validatePropertyIsANumber(String prdProperty, EntityDefinition mainEntity, String actionName) throws XMLFileException{
        if(mainEntity.getPropsDef().containsKey(prdProperty)){
            PropertyDefinition propDef = mainEntity.getPropsDef().get(prdProperty);
            if(!(propDef instanceof FloatPropertyDefinition))
                throw new XMLFileException("XML File Error: Property " + prdProperty + " used in action " + actionName + " is not of a number type!\n");
        }
    }
    /** END OF RULES **/
    public Expression validateExpressionIsANumber(String prdExpression, EntityDefinition entityDefinition, String actionName) throws XMLFileException {
        Expression expression = new Expression(prdExpression);
        if(expression.isNameOfFunction()){ // check function returns a number
            validateFunctionExpressionIsANumber(expression, entityDefinition);
        } else if(expression.isNameOfProperty(entityDefinition)){ // check the property is a number
            if(!(entityDefinition.getPropsDef().get(expression.getName()) instanceof FloatPropertyDefinition))
                throw new XMLFileException("XML File Error: Property " + expression.getName() + " used in action " + actionName + " is not of a number type!\n");
        } else { // check is the value of the string is a number
            if(!(expression.isANumber()))
                throw new XMLFileException("XML File Error: Expression " + expression.getName() + " used in action " + actionName + " is not of a number type!\n");
        }
        return expression;
    }
    public void validateFunctionExpressionIsANumber(Expression expression, EntityDefinition entityDefinition) throws XMLFileException{
        if(expression.getName().startsWith("environment")){
            String envName = expression.getStringInParenthesis();
            if(!(environmentVarMap.containsKey(envName)))
                throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
            if(!(environmentVarMap.get(envName) instanceof FloatProperty))
                throw new XMLFileException("XML File Error: the value returning from function 'environment' is not a number!\n");
        } else if (expression.getName().startsWith("random")) {
            if(!(expression.getStringInParenthesis().matches("[0-9.]+")))
                throw new XMLFileException("XML File Error: the value returning from function 'random' is not a number!\n");
        } else if(expression.getName().startsWith("evaluate")){
            String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
            String entityName = valueInParenthesis[0];
            String propertyName = valueInParenthesis[1];

            EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
            validatePropertyBelongsToEntity(propertyName, entDef);
            if(!(entDef.getPropsDef().get(propertyName) instanceof FloatPropertyDefinition))
                throw new XMLFileException("XML File Error: the value returning from function 'evaluate' is not a number!\n");
        } else if (expression.getName().startsWith("percent")) {
            String[] valueInParenthesis = expression.getStringInParenthesis().split(",");
            Expression val1 = new Expression(valueInParenthesis[0]);
            Expression val2 = new Expression(valueInParenthesis[0]);
            validateFunctionExpressionIsANumber(val1, entityDefinition);
            validateFunctionExpressionIsANumber(val2, entityDefinition);
        } else { // ticks function. just checking if it gets the right values
            String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
            EntityDefinition entDef = findEntityDefinitionInWorld(valueInParenthesis[0]);
            validatePropertyBelongsToEntity(valueInParenthesis[1], entDef);
        }
    }
    public Expression validateExpressionIsABoolean(String prdExpression, EntityDefinition entityDefinition, String actionName) throws XMLFileException {
        Expression expression = new Expression(prdExpression);

        if(expression.isNameOfFunction()){ // check function returns a boolean
            if(expression.getName().startsWith("environment")){
                String envName = expression.getStringInParenthesis();
                if(!(environmentVarMap.containsKey(envName)))
                    throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
                if(!(environmentVarMap.get(envName) instanceof BooleanProperty))
                    throw new XMLFileException("XML File Error: the value returning from function 'environment' is not a boolean!\n");
            } else if(expression.getName().startsWith("evaluate")){
                String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
                String entityName = valueInParenthesis[0];
                String propertyName = valueInParenthesis[1];

                EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
                if(!(entDef.getPropsDef().get(propertyName).getType().equals("boolean"))){
                    throw new XMLFileException("XML File Error: the value returning from function 'evaluate' is not a boolean!\n");
                }
            } else // any other function returns a number
                throw new XMLFileException("XML File Error: the value returning from the function is not a boolean!\n");
        } else if(expression.isNameOfProperty(entityDefinition)){ // check the property is a number
            if(!(entityDefinition.getPropsDef().get(expression.getName()).getType().equals("boolean")))
                throw new XMLFileException("XML File Error: Property " + expression.getName() + " used in action " + actionName + " is not of a boolean type!\n");
        } else { // check is the value of the string is a number
            if(!expression.getName().equals("true") && !expression.getName().equals("false"))
                throw new XMLFileException("XML File Error: Expression " + expression.getName() + " used in action " + actionName + " is not of a boolean type!\n");
        }
        return expression;
    }

    public Expression validateExpressionIsAString(String prdExpression, EntityDefinition entityDefinition, String actionName) throws XMLFileException {
        Expression expression = new Expression(prdExpression);

        if (expression.isNameOfFunction()) { // check function returns a boolean
            if (expression.getName().startsWith("environment")) {
                String envName = expression.getStringInParenthesis();
                if (!(environmentVarMap.containsKey(envName)))
                    throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
                if (!(environmentVarMap.get(envName) instanceof StringProperty))
                    throw new XMLFileException("XML File Error: the value returning from function 'environment' is not a string!\n");
            } else if (expression.getName().startsWith("evaluate")) {
                String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
                String entityName = valueInParenthesis[0];
                String propertyName = valueInParenthesis[1];

                EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
                if (!(entDef.getPropsDef().get(propertyName).getType().equals("string"))) {
                    throw new XMLFileException("XML File Error: the value returning from function 'evaluate' is not a string!\n");
                }
            } else // any other function returns a number
                throw new XMLFileException("XML File Error: the value returning from the function is not a string!\n");
        } else if (expression.isNameOfProperty(entityDefinition)) { // check the property is a number
            if (!(entityDefinition.getPropsDef().get(expression.getName()).getType().equals("string")))
                throw new XMLFileException("XML File Error: Property " + expression.getName() + " used in action " + actionName + " is not of a string type!\n");
        }
        // else -> it's a string

        return expression;
    }
}
