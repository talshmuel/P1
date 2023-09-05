package world.creator;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import world.Grid;
import world.entity.EntityDefinition;
import world.property.api.BooleanPropertyDefinition;
import world.property.api.FloatPropertyDefinition;
import world.property.api.PropertyDefinition;
import world.property.api.StringPropertyDefinition;
import world.property.impl.BooleanProperty;
import world.property.impl.FloatProperty;
import world.property.impl.Property;
import world.property.impl.StringProperty;
import world.rule.Rule;
import xml.reader.schema.generated.v2.*;
import xml.reader.validator.EntityException;
import xml.reader.validator.PropertyException;

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
    }

    //public World createWorldFromXMLFile(PRDWorld prdWorld) {
    public void createWorldFromXMLFile(PRDWorld prdWorld) throws XMLFileException {
        int threads = prdWorld.getPRDThreadCount();
        System.out.println("num of threads: " + threads);

        setGrid(validateAndCreateGrid(prdWorld.getPRDGrid()));
        setEnvironmentVarMap(validateAndCreateEnvironment(prdWorld.getPRDEnvironment().getPRDEnvProperty()));
        setEntityDefList(validateAndCreateEntities(prdWorld.getPRDEntities().getPRDEntity()));

        printDELETELATER();


        /*
        setRulesList(validateAndCreateRules(prdWorld.getPRDRules().getPRDRule(), entityDefList));
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
        if(newEntName.contains(" ")){
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
            throw new XMLFileException("XML File Error: File cannot contain duplicated property names!\n");
        }
        if(newPropName.contains(" ")){
            throw new XMLFileException("XML File Error: Property names cannot contain spaces!\n");
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
}
