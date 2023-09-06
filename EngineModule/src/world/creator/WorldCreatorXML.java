package world.creator;

import com.sun.org.apache.xpath.internal.operations.Bool;
import world.Grid;
import world.entity.Entity;
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
    // TODO LIST:
    // todo: 1. check if the type can also be decimal, or only float (i assumed it's only float)
    // todo: 2. finish converting 'property' in action to expression -> make sure it's ok (ask about the fuctions,
    //          are some of them invalid? like random)
    // todo: 3. fucking evaluate function איףףףףףףףףףףףףףףףף
    // todo: 4. האם צריך לבדוק שאנטיטי שקיבלנו באחד מהתנאים תואם לאנטיטי הראשי????
    Map<String, Property> environmentVarMap;
    ArrayList<EntityDefinition> entityDefList;
    ArrayList<Rule> rulesList;
    Map<String, Integer> endConditionsMap;
    Grid grid;
    ////////////////////////////////////////////// GENERAL METHODS /////////////////////////////////////////////////////
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
    public void setGrid(Grid grid) {
        this.grid = grid;
    }
    ////////////////////////////////////////////////////////////////////////////////////
    public void printDELETELATER(){
        System.out.println("Grid: num of rows: " + grid.getNumOfRows() + " , num of cols: " + grid.getNumOfCols() + "\n");

        System.out.println("Environment Variables Map:");
        int i=1;
        for(Map.Entry<String, Property> envMap : environmentVarMap.entrySet()){
            String name = envMap.getKey();
            Property property = envMap.getValue();
            System.out.println("Environment Variable #" + i++);
            System.out.println("Name: " + name);
            System.out.println("Type: " + property.getType());
            if(property.getType().equals("float")){
                System.out.println("Range: " + property.getDefinition().getBottomLimit() + " - " + property.getDefinition().getTopLimit());
            }
            System.out.println("Value: " + property.getVal() + "\n");
        }
        System.out.println("\n------------------------------------------------------");

        int j=1;
        System.out.println("Entity Definition List:");
        for(EntityDefinition entityDefinition : entityDefList){
            System.out.println("Entity #" + j++ + " Name: " + entityDefinition.getName());
            System.out.println("Population: " + entityDefinition.getNumOfInstances());
            Map<String, PropertyDefinition> propsDef = entityDefinition.getPropsDef();
            System.out.println("    Properties Definition:");
            for(Map.Entry<String, PropertyDefinition> props : propsDef.entrySet()){
                PropertyDefinition propertyDefinition = props.getValue();
                System.out.println("    Name: " + propertyDefinition.getName());
                System.out.println("    Type: " + propertyDefinition.getType());
                System.out.println("    Random?: " + propertyDefinition.getRandomlyInitialized());
                if(propertyDefinition instanceof FloatPropertyDefinition){
                    System.out.println("    Range: " + propertyDefinition.getBottomLimit() + " - " + propertyDefinition.getTopLimit());
                }
                System.out.println("    Init Value: " + propertyDefinition.getInitValue() + "\n");
            }
        }

        System.out.println("\n------------------------------------------------------");


    }

    //public World createWorldFromXMLFile(PRDWorld prdWorld) {
    public void createWorldFromXMLFile(PRDWorld prdWorld) throws XMLFileException {
        int threads = prdWorld.getPRDThreadCount();
        System.out.println("num of threads: " + threads);

        setGrid(validateAndCreateGrid(prdWorld.getPRDGrid()));
        setEnvironmentVarMap(validateAndCreateEnvironment(prdWorld.getPRDEnvironment().getPRDEnvProperty()));
        setEntityDefList(validateAndCreateEntities(prdWorld.getPRDEntities().getPRDEntity()));
        setRulesList(validateAndCreateRules(prdWorld.getPRDRules().getPRDRule()));
        printDELETELATER();
        /*
        setEndConditionsMap(validateAndCreateTermination(prdWorld.getPRDTermination()));
        return (new World(entityDefList, environmentVarMap, rulesList, endConditionsMap, grid, threads));*/
    }

    public Grid validateAndCreateGrid(PRDWorld.PRDGrid prdGrid) throws XMLFileException {
        int rows = prdGrid.getRows();
        if(rows < 10 || rows > 100){
            throw new XMLFileException("XML File Error: Number of rows in grid must be between 10-100!\n");
        }

        int cols = prdGrid.getColumns();
        if(cols < 10 || cols > 100){
            throw new XMLFileException("XML File Error: Number of columns in grid must be between 10-100!\n");
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
            throw new XMLFileException("XML File Error: File contains duplicated environment variable names!\n");
        } else if(envName.trim().contains(" ")){
            throw new XMLFileException("XML File Error: Environment variable names cannot contain spaces!\n");
        }
        return envName.trim();
    }
    public FloatProperty createFloatEnvironmentProperty(PRDEnvProperty prdEnvProperty, String propName){
        Double from=null, to=null;

        if(prdEnvProperty.getPRDRange() != null){
            from = prdEnvProperty.getPRDRange().getFrom();
            to = prdEnvProperty.getPRDRange().getTo();
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
            throw new XMLFileException("XML File Error: File contains duplicated entity names!\n");
        }
        if(newEntName.contains(" ")){ // todo -> is this relevant?
            throw new XMLFileException("XML File Error: File contains entity name with spaces!\n");
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
            throw new XMLFileException("XML File Error: Entity contains duplicated property names!\n");
        }
        if(newPropName.contains(" ")){ // todo: check if this is still relevant?
            throw new XMLFileException("XML File Error: Entity's property names cannot contain spaces!\n");
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
    ArrayList<Action> validateAndCreateActionsList(List<PRDAction> prdActions) throws XMLFileException {
        ArrayList<Action> actionsList = new ArrayList<>();
        for(PRDAction a : prdActions){
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

        SecondaryEntity secondaryEntity = null;
        if(prdAction.getPRDSecondaryEntity() != null){
            PRDAction.PRDSecondaryEntity prdSecondaryEntity = prdAction.getPRDSecondaryEntity();
            secondaryEntity = validateAndCreateSecondaryEntity(prdSecondaryEntity);
        }

        if(prdAction.getType().equals("increase")){
            return new Increase(mainEntityName, secondaryEntity, propertyName, expression);
        } else {
            return new Decrease(mainEntityName, secondaryEntity, propertyName, expression);
        }
    }
    public Kill validateAndCreateKillAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entDef = findEntityDefinitionInWorld(prdAction.getEntity());
        SecondaryEntity secondaryEntity = null;
        if(prdAction.getPRDSecondaryEntity() != null){
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        }
        return new Kill(entDef.getName(), null, null);
    }
    public Calculation validateAndCreateCalculationAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String resultProp = validatePropertyBelongsToEntity(prdAction.getResultProp(), entityDefinition);
        validatePropertyIsANumber(prdAction.getProperty(), entityDefinition, "Calculation");

        SecondaryEntity secondaryEntity=null;
        if(prdAction.getPRDSecondaryEntity() != null){
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        }

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
        // validate entity
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String propertyName = validatePropertyBelongsToEntity(prdAction.getProperty(), entityDefinition);

        // validate property
        PropertyDefinition propertyDefinition = entityDefinition.getPropsDef().get(propertyName);

        // validate value
        Expression expression = validateExpressionValueInSetAction(prdAction, propertyDefinition.getType(), entityDefinition);

        // validate secondary entity
        SecondaryEntity secondaryEntity=null;
        if(prdAction.getPRDSecondaryEntity() != null){
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());
        }

        return new Set(entityDefinition.getName(), null, propertyName, expression);
    }
    public Expression validateExpressionValueInSetAction(PRDAction prdAction, String propertyType, EntityDefinition entityDefinition) throws XMLFileException {
        switch (propertyType) { // validate value matches by type
            case "float": {
                return validateExpressionIsANumber(prdAction.getValue(), entityDefinition, "Set");
            } case "boolean": {
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

        SecondaryEntity secondaryEntity=null;
        if(prdAction.getPRDSecondaryEntity() != null)
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        return new Replace(definitionToKill.getName(), secondaryEntity, definitionToCreate.getName(), mode, definitionToCreate);
    }
    public Proximity validateAndCreateProximityAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition sourceDefinition = findEntityDefinitionInWorld(prdAction.getPRDBetween().getSourceEntity());
        EntityDefinition targetDefinition = findEntityDefinitionInWorld(prdAction.getPRDBetween().getTargetEntity());
        Expression depth = validateExpressionIsANumber(prdAction.getPRDEnvDepth().getOf(), sourceDefinition, "Proximity");
        ArrayList<Action> thenActions = validateAndCreateThenActions(prdAction.getPRDActions().getPRDAction());

        SecondaryEntity secondaryEntity=null;
        if(prdAction.getPRDSecondaryEntity() != null)
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        return new Proximity(sourceDefinition.getName(), secondaryEntity, null, depth, thenActions, targetDefinition.getName(), grid);
    }

    /** ///////////////////////////////////////// CONDITION VALIDATION ////////////////////////////////////////////// **/
    public Condition validateAndCreateConditionAction(PRDAction prdAction) throws XMLFileException {
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(prdAction.getEntity());
        String mainEntityName = entityDefinition.getName();

        SecondaryEntity secondaryEntity=null;
        if(prdAction.getPRDSecondaryEntity() != null)
            secondaryEntity = validateAndCreateSecondaryEntity(prdAction.getPRDSecondaryEntity());

        if (prdAction.getPRDCondition().getSingularity().equals("single"))
            return validateAndCreateSingleCondition(prdAction, secondaryEntity);
        else
            return validateAndCreateMultipleCondition(prdAction, secondaryEntity, mainEntityName);
    }
    /** ///////////////////////////////// SINGLE CONDITION VALIDATION ////////////////////////////////////////////// **/
    public SingleCondition validateAndCreateSingleCondition(PRDAction prdAction, SecondaryEntity secondaryEntity) throws XMLFileException {
        // NOTE: this is when the single condition is the main action, not part of a multiple condition
        PRDCondition prdCondition = prdAction.getPRDCondition();
        String mainEntityName = prdCondition.getEntity();
        EntityDefinition entityDefinition = findEntityDefinitionInWorld(mainEntityName);

        String propertyType = null;// also checking of what type the property is
        Expression propertyName = validatePropertyExpressionInSingleCondition(prdCondition.getProperty(), entityDefinition, propertyType);

        SingleCondition.Operator operator = validateOperatorInSingleCondition(prdCondition.getOperator());

        Expression value; //    protected String value;
        switch (propertyType){
            case "float":
                value = validateExpressionIsANumber(prdCondition.getValue(), entityDefinition, "Single Condition");
            case "boolean":
                value = validateExpressionIsABoolean(prdCondition.getValue(), entityDefinition, "Single Condition");
            default: // case "string":
                value = validateExpressionIsAString(prdCondition.getValue(), entityDefinition, "Single Condition");
        }

        ArrayList<Action> thenActions=null;
        if(prdAction.getPRDThen() != null)
            thenActions = validateAndCreateThenActions(prdAction.getPRDThen().getPRDAction());
        else
            throw new XMLFileException("XML File Error: Single Condition must contain 'then actions'!\n");

        ArrayList<Action> elseActions=null;
        if(prdAction.getPRDElse() != null)
            elseActions = validateAndCreateThenActions(prdAction.getPRDElse().getPRDAction());

        // todo: property is now an expression, needs to change in the action itself
        // כרגע זה פלסטר - נשאר סטרינג
        return new SingleCondition(mainEntityName, secondaryEntity, propertyName.getName(), operator, value, thenActions, elseActions);
    }
    public Expression validatePropertyExpressionInSingleCondition(String prdProperty, EntityDefinition entityDefinition, String type) throws XMLFileException {
        Expression property = new Expression(prdProperty);

        if(property.isNameOfFunction()){ // todo: what to do when it's a name of a function??
            validateFunctionPropertyExpressionInSingleCondition(property, type);
        } else if(property.isNameOfProperty(entityDefinition)){
            if(!(entityDefinition.getPropsDef().containsKey(property.getName())))
                throw new XMLFileException("XML File Error: Entity " + entityDefinition.getName() + " doesn't have a property named " + property + "!\n");
            if (entityDefinition.getPropsDef().get(property.getName()) instanceof FloatPropertyDefinition)
                type = "float";
            else if (entityDefinition.getPropsDef().get(property.getName()) instanceof BooleanPropertyDefinition)
                type = "boolean";
            else
                type = "string";
        } /*else { /// todo (wtf) ??????????????
            if(property.getName().matches("\\d+"))
                type = "float";
            else if (property.getName().equals("true") || property.getName().equals("false"))
                type = "boolean";
            else
                type = "string";
        }*/
        return property;
    }
    public void validateFunctionPropertyExpressionInSingleCondition(Expression propertyExpression, String type) throws XMLFileException {
        if(propertyExpression.getName().startsWith("environment")){
            String envName = propertyExpression.getStringInParenthesis();
            if(!(environmentVarMap.containsKey(envName)))
                throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
            if(environmentVarMap.get(envName) instanceof FloatProperty)
                type = "float";
            else if (environmentVarMap.get(envName) instanceof BooleanProperty)
                type = "boolean";
            else
                type = "string";
        } else if (propertyExpression.getName().startsWith("random")) {
            throw new XMLFileException("XML File Error: Property in Single Condition is not valid!\n");
            /*if(!(propertyExpression.getStringInParenthesis().matches("\\d+")))
                throw new XMLFileException("XML File Error: the value returning from function 'random' is not a number!\n");
            type = "float";*/
        } else if(propertyExpression.getName().startsWith("evaluate")){
            throw new XMLFileException("XML File Error: Property in Single Condition is not valid!\n");
            /*String[] valueInParenthesis = propertyExpression.getStringInParenthesis().split("\\.");
            String entityName = valueInParenthesis[0];
            String propertyName = valueInParenthesis[1];
            EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
            if(!(entDef.getPropsDef().containsKey(propertyName)))
                throw new XMLFileException("XML File Error: Entity " + entDef.getName() + " doesn't have a property named " + propertyName + "!\n");
            type = entDef.getPropsDef().get(propertyName).getType();*/
        } else if (propertyExpression.getName().startsWith("percent")) {
            throw new XMLFileException("XML File Error: Property in Single Condition is not valid!\n");
            // ???
        } else { // ticks function. just checking if it gets the right values
            throw new XMLFileException("XML File Error: Property in Single Condition is not valid!\n");
            /*String[] valueInParenthesis = propertyExpression.getStringInParenthesis().split("\\.");
            EntityDefinition entDef = findEntityDefinitionInWorld(valueInParenthesis[0]);
            validatePropertyBelongsToEntity(valueInParenthesis[1], entDef);
            type = "float";*/
        }
    }
    public ArrayList<Action> validateAndCreateThenActions(List<PRDAction> prdActionsList) throws XMLFileException {
        ArrayList<Action> thenActions = new ArrayList<>();
        for(PRDAction prdAction : prdActionsList){
            thenActions.add(validateAndCreateAction(prdAction));
        }
        return thenActions;
    }
    public SingleCondition.Operator validateOperatorInSingleCondition(String prdOperation) throws XMLFileException {
        if(prdOperation.equals("EQUAL"))
            return SingleCondition.Operator.EQUAL;
        else if(prdOperation.equals("NOTEQUAL"))
            return SingleCondition.Operator.NOTEQUAL;
        else if(prdOperation.equals("LESSTHAN"))
            return SingleCondition.Operator.LESSTHAN;
        else if(prdOperation.equals("BIGGERTHAN"))
            return SingleCondition.Operator.BIGGERTHAN;
        else
            throw new XMLFileException("XML File Error: Operation sign in condition isn't legal!\n");
    }
    /** ///////////////////////////////////////////////// END OF CONDITION /////////////////////////////////////////////////// **/
    /** ///////////////////////////////////////// MULTIPLE CONDITION VALIDATION ////////////////////////////////////////////// **/
    public MultipleCondition validateAndCreateMultipleCondition(PRDAction prdAction, SecondaryEntity secondaryEntity, String mainEntityName) throws XMLFileException {
        ArrayList<Condition> conditions = new ArrayList<>();
        for(PRDCondition prdCondition : prdAction.getPRDCondition().getPRDCondition()){
            conditions.add(validateAndCreateSubConditions(prdCondition));
        }

        ArrayList<Action> thenActions;
        if(prdAction.getPRDThen() != null)
            thenActions = validateAndCreateThenActions(prdAction.getPRDThen().getPRDAction());
        else
            throw new XMLFileException("XML File Error: Single Condition must contain 'then actions'!\n");

        ArrayList<Action> elseActions=null;
        if(prdAction.getPRDElse() != null)
            elseActions = validateAndCreateThenActions(prdAction.getPRDElse().getPRDAction());

        MultipleCondition.Logic logicSign = validateLogicSign(prdAction.getPRDCondition().getLogical());
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
    public Condition validateAndCreateSubConditions(PRDCondition prdCondition) throws XMLFileException {
        // this is when the conditions are part of a multiple condition!

        //protected List<PRDCondition> prdCondition;
        //    protected String value;
        //    protected String singularity;
        //    protected String operator;
        //    protected String property;
        //    protected String logical;
        //    protected String entity;

        // todo - > לא סיימתי עדיין !
        if(prdCondition.getSingularity().equals("single")){
            // todo: do i need to check if the entity matches the main entity?
            SingleCondition.Operator operator = validateOperatorInSingleCondition(prdCondition.getOperator());
            return new SingleCondition(null, null, null, operator, null, null, null);
        } else {
            MultipleCondition.Logic logicSign = validateLogicSign(prdCondition.getLogical());
            return new MultipleCondition(null, null, null, null, null, logicSign, null);
        }
    }


    /** ///////////////////////////////////////////// END OF MULTIPLE CONDITION  ///////////////////////////////////////////// **/
    /** //////////////////////////////////////////// SECONDARY ENTITY VALIDATION //////////////////////////////////////////// **/
    public SecondaryEntity validateAndCreateSecondaryEntity(PRDAction.PRDSecondaryEntity prdSecondaryEntity) throws XMLFileException {
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
    public Integer validateCountNumberInSecondaryEntity(String prdCount) throws XMLFileException {
        if(prdCount.equals("ALL"))
            return null;
        else if (prdCount.matches("\\d+")){
            if(Integer.parseInt(prdCount) > 0)
                return Integer.parseInt(prdCount);
            else
                throw new XMLFileException("XML File Error: Number of secondary entities must be over 0!\n");
        } else
            throw new XMLFileException("XML File Error: Number of secondary entities must be a number!\n");
    }
    public Condition validateSelectionConditionInSecondaryEntity(PRDCondition prdCondition) throws XMLFileException {
        if(prdCondition.getSingularity().equals("single")){
            EntityDefinition entDef = findEntityDefinitionInWorld(prdCondition.getEntity());
            String propName = validatePropertyBelongsToEntity(prdCondition.getProperty(), entDef); // todo -> also an expression
            SingleCondition.Operator operator = validateOperatorInSingleCondition(prdCondition.getOperator());
            Expression expression = validateExpressionIsANumber(prdCondition.getValue(), entDef, "Single Condition");
            return new SingleCondition(entDef.getName(), null, propName, operator, expression, null, null);
        }
        else{
            System.out.println("HASHEM ISHMOR"); // todo
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
    public Expression validateExpressionIsANumber(String prdExpression, EntityDefinition entityDefinition, String actionName) throws XMLFileException {
        Expression expression = new Expression(prdExpression);

        if(expression.isNameOfFunction()){ // check function returns a number
            validateFunctionExpressionIsANumber(expression);
        } else if(expression.isNameOfProperty(entityDefinition)){ // check the property is a number
            if(!(entityDefinition.getPropsDef().get(expression.getName()).getType().equals("float")))
                throw new XMLFileException("XML File Error: Property " + expression.getName() + " used in action " + actionName + " is not of a number type!\n");
        } else { // check is the value of the string is a number
            if(!(expression.isANumber()))
                throw new XMLFileException("XML File Error: Expression " + expression.getName() + " used in action " + actionName + " is not of a number type!\n");
        }
        return expression;
    }
    public void validateFunctionExpressionIsANumber(Expression expression) throws XMLFileException{
        // todo: maybe move to Expression class
        if(expression.getName().startsWith("environment")){
            String envName = expression.getStringInParenthesis();
            if(!(environmentVarMap.containsKey(envName)))
                throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
            if(!(environmentVarMap.get(envName) instanceof FloatProperty))
                throw new XMLFileException("XML File Error: the value returning from function 'environment' is not a number!\n");
        } else if (expression.getName().startsWith("random")) {
            if(!(expression.getStringInParenthesis().matches("\\d+")))
                throw new XMLFileException("XML File Error: the value returning from function 'random' is not a number!\n");
        } else if(expression.getName().startsWith("evaluate")){
            String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
            String entityName = valueInParenthesis[0];
            String propertyName = valueInParenthesis[1];

            EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
            if(!(entDef.getPropsDef().get(propertyName).getType().equals("float"))){
                throw new XMLFileException("XML File Error: the value returning from function 'evaluate' is not a number!\n");
            }
        } else if (expression.getName().startsWith("percent")) {
            System.out.println("~~~@@@ need to do @@@~~~");
            // todo :(((
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

        if(expression.isNameOfFunction()){ // check function returns a boolean
            if(expression.getName().startsWith("environment")){
                String envName = expression.getStringInParenthesis();
                if(!(environmentVarMap.containsKey(envName)))
                    throw new XMLFileException("XML File Error: Function 'environment' didn't receive an environment variable name!!\n");
                if(!(environmentVarMap.get(envName) instanceof StringProperty))
                    throw new XMLFileException("XML File Error: the value returning from function 'environment' is not a string!\n");
            } else if(expression.getName().startsWith("evaluate")){
                String[] valueInParenthesis = expression.getStringInParenthesis().split("\\.");
                String entityName = valueInParenthesis[0];
                String propertyName = valueInParenthesis[1];

                EntityDefinition entDef = findEntityDefinitionInWorld(entityName);
                if(!(entDef.getPropsDef().get(propertyName).getType().equals("string"))){
                    throw new XMLFileException("XML File Error: the value returning from function 'evaluate' is not a string!\n");
                }
            } else // any other function returns a number
                throw new XMLFileException("XML File Error: the value returning from the function is not a string!\n");
        } else if(expression.isNameOfProperty(entityDefinition)){ // check the property is a number
            if(!(entityDefinition.getPropsDef().get(expression.getName()).getType().equals("string")))
                throw new XMLFileException("XML File Error: Property " + expression.getName() + " used in action " + actionName + " is not of a string type!\n");
        }
        // else -> it's a string

        return expression;
    }
    /** END OF RULES **/
}
