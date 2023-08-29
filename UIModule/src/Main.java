import data.transfer.object.EndSimulationData;
import data.transfer.object.DataFromUser;
import data.transfer.object.definition.*;
import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.PropertyResultInfo;
import data.transfer.object.run.result.RunResultInfo;
import engine.Engine;
import engine.EngineInterface;
import exception.IncompatibleType;
import exception.PathDoesntExistException;
import xml.reader.validator.*;

import java.util.*;

public class Main {
    static EngineInterface engine = new Engine();

    public static void main(String[] args) throws IncompatibleType {


        boolean shutdown = false;

        System.out.println("-------------------------");
        System.out.println("-------PREDICTIONS-------");
        System.out.println("-------------------------\n");

        int choice = printStartMenuAndPerformAction();
        while (!shutdown) {
            switch (choice) {
                case 1:
                    choice = printAfterLoadXMLMenuAndPerformAction();

                    if (choice == 1 || choice == 2)
                        choice = 1;
                    else if(choice == 3)
                        choice=2;
                    else
                        choice = 3;
                    break;
                case 2:
                    choice = printAfterRestoreMenuAndPerformAction();

                    if (choice==5)
                        choice=3;

                    else if (choice!=1)
                        choice=2;
                    break;
                case 3:
                    printShutdownMenuAndPerformAction();
                    shutdown = true;
                    break;
            }

        }



    }

    public static int printAfterRestoreMenuAndPerformAction() throws IncompatibleType {
        System.out.println("Please enter the number of the action you want to " +
                "perform from the following menu:\n");
        System.out.println("1. Upload a simulation setup file");
        System.out.println("2. Display simulation definitions");
        System.out.println("3. Run simulation");
        System.out.println("4. Display full details of a past simulation run");
        System.out.println("5. Shutdown\n");
        int choice = checkValidNumberInput(1, 5);
        switch (choice){
            case 1: {
                handleLoadXMLChoice();
                break;
            }
            case 2:
                handleDisplaySimulationDefinitionsChoice();
                break;
            case 3:
                handleRunSimulationChoice();
                break;
            case 4:
                handleDisplayDetailsPastRunChoice();
                break;
            case 5:
                break;
        }
        return choice;
    }

    private static int checkValidNumberInput(int minRange, int maxRange){
        Scanner scanner = new Scanner(System.in);
        boolean validInput=false;
        int choice=0;

        do{
            try {
                choice = scanner.nextInt();
                if (choice >= minRange && choice <= maxRange) {
                    validInput = true;
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + maxRange + ".");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
            }
        } while(!validInput);
        return choice;
    }

    public static void handleDisplayDetailsPastRunChoice(){
        int i=1;
        ArrayList<RunResultInfo> resultsInfo = engine.displayRunResultsInformation();

        System.out.println("Please enter the number of the run result you would like to display:");
        for(RunResultInfo resultInfo : resultsInfo){
            System.out.println(i++ +". ID: "+ resultInfo.getId()+", running date: "+ resultInfo.getRunDate());
        }
        int choice = checkValidNumberInput(1, resultsInfo.size());

        System.out.println("\nPlease choose how to display the results (enter the appropriate number):");
        System.out.println("1. According to entities amount at the beginning and end of the simulation");
        System.out.println("2. According to property histogram");

        int mannerChoice = checkValidNumberInput(1, 2);

        switch (mannerChoice){
            case 1:
                displayEntitiesAmountResults(resultsInfo.get(choice-1));
                break;
            case 2:
                displayPropertyHistogramResults(resultsInfo.get(choice-1));
                break;
            default:
                break;
        }

    }

    public static void displayEntitiesAmountResults(RunResultInfo resultInfo){
        System.out.println("Entities amount");
        System.out.println("---------------");
        for(EntityResultInfo entityInfo : resultInfo.getEntitiesResults()){
            System.out.println(entityInfo.getName()+" - ");
            System.out.println("Amount at start: "+entityInfo.getNumOfInstanceAtStart());
            System.out.println("Amount at end: "+entityInfo.getNumOfInstanceAtEnd()+"\n");
        }

    }
    public static void displayPropertyHistogramResults(RunResultInfo resultInfo){
        int i=1;
        System.out.println("\nProperty histogram");
        System.out.println("------------------");
        System.out.println("Please enter the number of the entity to which the property belongs:");
        for(EntityResultInfo entityInfo : resultInfo.getEntitiesResults()){
            System.out.println(i++ +". "+entityInfo.getName());
        }
        int entChoice = checkValidNumberInput(1, resultInfo.getEntitiesResults().size());

        getPropChoiceAndDisplayHistogram(resultInfo.getEntitiesResults().get(entChoice-1));
    }
    public static void getPropChoiceAndDisplayHistogram(EntityResultInfo entityInfo){
        int i=1;

        System.out.println("Please enter the property number:");
        for(PropertyResultInfo propInfo : entityInfo.getPropertiesResults()) {
            System.out.println(i++ + ". " + propInfo.getName());
        }

        int propChoice = checkValidNumberInput(1, entityInfo.getPropertiesResults().size());

        PropertyResultInfo prop = entityInfo.getPropertiesResults().get(propChoice-1);
        System.out.println("The histogram of the property - "+prop.getName()+":");
        displayHistogram(prop.getHistogram(), prop.getName());
    }
    public static void displayHistogram(Map<Object, Integer> histogram, String propName){
        histogram.forEach((val, numOfEntities)->{
            System.out.println(numOfEntities+ " entities for which the value of the property "+ propName+" is: "+ val);
            System.out.println("\n");
        });
    }
    public static void printShutdownMenuAndPerformAction(){
        Scanner scanner = new Scanner(System.in);
        String choice=" ";
        boolean validInput=false;
        System.out.println("Please enter 'y' if you want to save the current state of the system ");
        System.out.println("(results of past simulations and current simulation settings), enter 'n' else:");

        do{
            try {
                choice = scanner.nextLine();
                if(!(choice.equals("y")) && !(choice.equals("n"))) {
                    scanner.nextLine();
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                }
                else
                    validInput=true;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter only 'y' or 'n'.\n");
            }
        } while(!validInput);

        if(Objects.equals(choice, "y")){
            saveCurrentSystemState();
        }
    }

    public static int printAfterLoadXMLMenuAndPerformAction() throws IncompatibleType {
        System.out.println("Please enter the number of the action you want to " +
                "perform from the following menu:\n");
        System.out.println("1. Upload a simulation setup file");
        System.out.println("2. Display simulation definitions");
        System.out.println("3. Run simulation");
        System.out.println("4. Shutdown\n");

        int choice = checkValidNumberInput(1, 4);

        switch (choice){
            case 1:
                handleLoadXMLChoice();
                break;
            case 2:
                handleDisplaySimulationDefinitionsChoice();
                break;
            case 3:
                handleRunSimulationChoice();
                break;
            case 4:
                break;
            default:
                break;
        }
        return choice;
    }
    public static void handleRunSimulationChoice() {

        try {
            getEnvironmentVariablesValuesFromUser();

            EndSimulationData endSimulationData = engine.runSimulation(new DataFromUser());

            System.out.println("The simulation ended after " + endSimulationData.getEndConditionVal() + " " +
                    endSimulationData.getEndCondition());
            System.out.println("Run ID: " + endSimulationData.getRunId() + "\n");


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void getEnvironmentVariablesValuesFromUser()throws IncompatibleType{
        //Scanner scanner = new Scanner(System.in);
        int choice = 1;

        System.out.println("\nThe environment variables received random values within the range defined for " +
                "each, you can enter values for these variables.");

        while (choice!=0) {
            System.out.println("\nPlease enter the number of the environment variable you would like to change " +
                    "or the number 0 if you are done changing:");
            int i = 1;
            for (PropertyInfo propertyInfo : engine.getEnvironmentDefinitions()) {
                System.out.println(i++ +". "+ propertyInfo.getName());
            }
            choice = checkValidNumberInput(0, engine.getEnvironmentDefinitions().size());

            if(choice!=0)
                setEnvironmentVariableValue(engine.getEnvironmentDefinitions().get(choice-1));
        }

        System.out.println("\nThe values of the environment variables set for the simulation are:");
        int i = 1;
        for (PropertyValueInfo propertyValInfo : engine.getEnvironmentValues()) {
            System.out.println(i++ +". "+ propertyValInfo.getName()+": "+propertyValInfo.getVal());
        }
        System.out.println("\n");
    }

    private static void setEnvironmentVariableValue(PropertyInfo propertyInfo)throws IncompatibleType {
        Scanner scanner = new Scanner(System.in);

        switch (propertyInfo.getType()) {
            case "Integer": {
                Integer min = (Integer) propertyInfo.getBottomLimit();
                Integer max = (Integer) propertyInfo.getTopLimit();
                System.out.print("Please enter an integer number in the range " + min + "-" + max +
                        " for the environment variable - " + propertyInfo.getName() + ": ");
                Integer val = checkValidNumberInput(min, max);
                engine.setEnvironmentVariable(propertyInfo.getName(), val);
            }
            break;
            case "Float": {
                Double min = (Double) propertyInfo.getBottomLimit();
                Double max = (Double) propertyInfo.getTopLimit();
                System.out.print("Please enter a number in the range " + min + "-" + max +
                        " for the environment variable - " + propertyInfo.getName() + ": ");

                Double val = checkValidDoubleInput(min, max);
                engine.setEnvironmentVariable(propertyInfo.getName(), val);
            }
            break;
            case "String": {
                System.out.print("Please enter a free string for the environment variable - " + propertyInfo.getName() + ": ");
                String val = scanner.nextLine();
                engine.setEnvironmentVariable(propertyInfo.getName(), val);
            }
            break;
            case "Boolean": {
                System.out.print("Please enter 'true' or 'false' for the environment variable - " + propertyInfo.getName() + ": ");
                Boolean val = checkValidBooleanInput();
                engine.setEnvironmentVariable(propertyInfo.getName(), val);
            }
            break;
        }
    }

    public static void handleDisplaySimulationDefinitionsChoice(){
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        System.out.println("Simulation definitions:");
        System.out.println("-----------------------\n");
        System.out.println("Entities:");
        DisplayEntitiesDefinitions(simulationInfo.getEntities());
        System.out.println("\nRules:");
        DisplayRulesDefinitions(simulationInfo.getRules());
        System.out.println("End Conditions:");
        DisplayEndConditionsDefinitions(simulationInfo.getEndConditions());
    }

    public static void DisplayEndConditionsDefinitions(ArrayList<TerminationInfo> terminationInfos){
        int i=1;
        for(TerminationInfo terminationInfo : terminationInfos){
            System.out.println("#"+i++ +" end condition name: "+terminationInfo.getTerminationCondition());
            System.out.println("   value: "+terminationInfo.getVal());
        }
        System.out.print("\n");

    }
    public static void DisplayRulesDefinitions(ArrayList<RuleInfo> ruleInfos){
        int i=1;
        for(RuleInfo ruleInfo : ruleInfos){
            System.out.println("#"+i++ +" Rule name: "+ruleInfo.getName());
            System.out.println("   ticks: "+ruleInfo.getTicks());
            System.out.println("   probability: "+ruleInfo.getProbability());
            System.out.println("   number of actions: "+ruleInfo.getNumOfActions());
            System.out.print("   Actions: ");
            int j = 1;
            for(String actionName : ruleInfo.getActions()){
                if(j==1)
                    System.out.print(actionName);
                else
                    System.out.print(" ,"+actionName);
                j++;
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
    public static void DisplayEntitiesDefinitions(ArrayList<EntityInfo> entityInfos){
        int i=1;
        for(EntityInfo entityInfo : entityInfos){
            System.out.println("#"+i++ +" Entity name: "+entityInfo.getName());
            System.out.println("   Population: "+entityInfo.getPopulation());
            System.out.print("   Properties: ");
            int j=1;
            for(PropertyInfo propertyInfo : entityInfo.getProperties()){
                if(j==1) {
                    System.out.println("#" + j++ + " Property name: " + propertyInfo.getName());
                    System.out.println("                  type: " + propertyInfo.getType());
                    System.out.println("                  range: " + propertyInfo.getBottomLimit() + "-" + propertyInfo.getTopLimit());
                    System.out.println("                  is randomly initialized: " + propertyInfo.getIsRandomInit());
                }
                else{
                    System.out.println("               #" + j++ + " Property name: " + propertyInfo.getName());
                    System.out.println("                  type: " + propertyInfo.getType());
                    if(propertyInfo.getBottomLimit() != null) {
                        System.out.println("                  range: " + propertyInfo.getBottomLimit() + "-" + propertyInfo.getTopLimit());
                    }
                    System.out.println("                  is randomly initialized: " + propertyInfo.getIsRandomInit());

                }
            }
        }
    }

    private static int printStartMenuAndPerformAction() {
        System.out.println("Please enter the number of the action you want to " +
                "perform from the following menu:\n");
        System.out.println("1. Upload a simulation setup file");
        System.out.println("2. Restore a previous state of the system");
        System.out.println("3. Shutdown\n");

        int choice = checkValidNumberInput(1, 3);

        switch (choice){
            case 1:{
                handleLoadXMLChoice();
                break;
            }
            case 2:
                handleRestorePreviousStateChoice();
                break;
            case 3:
                break;
            default:
                break;
        }
        return choice;
    }

    private static void handleLoadXMLChoice() {
        Scanner scanner = new Scanner(System.in);
        String fileName;
        engine.cleanResults();
        while (true) {
            System.out.println("Please enter the full path of your simulation settings file " +
                    "(note that the file must be of XML type)");
            System.out.println("or enter 0 to return to main menu");
            fileName = scanner.nextLine();

            if(fileName.equals("0"))
            {
                scanner.nextLine(); // clean buffer
                printStartMenuAndPerformAction();
            }
            try {
                if (engine.createSimulationByXMLFile(fileName)) {
                    System.out.println("File was loaded successfully!\n");
                    return;
                }
            } catch (FileDoesntExistException | InvalidXMLFileNameException file) {
                System.out.println(file.getMessage());
            } catch (EnvironmentException environmentException){
                System.out.println(environmentException.getMessage());
            } catch (EntityException entityException){
                System.out.println(entityException.getMessage());
            } catch (PropertyException propertyException) {
                System.out.println(propertyException.getMessage());
            } catch (RuleException ruleException){
                System.out.println(ruleException.getMessage());
            } catch (MustBeNumberException m) {
                System.out.println(m.getMessage());
            } catch (TerminationException terminationException) {
                System.out.println(terminationException.getMessage());
            }
        }
    }
    public static void handleRestorePreviousStateChoice(){
        Scanner scanner = new Scanner(System.in);
        boolean validPath = false;
        while (!validPath){
            try {
                System.out.println("Please enter the full path including the name of the file where the system state is saved");
                System.out.println("or enter 0 to return to main menu");
                String fileName = scanner.nextLine();
                if(fileName.equals("0")){
                    printStartMenuAndPerformAction();
                }
                engine = engine.restoreState(fileName);
                validPath = true;
            } catch (FileDoesntExistException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void saveCurrentSystemState(){
        Scanner scanner = new Scanner(System.in);
        boolean validPath = false;
        while (!validPath) {
            try {
                System.out.println("Please enter the full path including the file name where you would like to save the system state");
                System.out.println("or enter 0 to return to main menu");

                String fileName = scanner.nextLine();
                if(fileName.equals("0")){
                    scanner.nextLine(); // clean buffer
                    printStartMenuAndPerformAction();
                }

                engine.saveState(fileName);
                validPath = true;
            } catch (PathDoesntExistException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static double checkValidDoubleInput(double minRange, double maxRange){
        Scanner scanner = new Scanner(System.in);
        boolean validInput=false;
        double choice=0.0;

        do{
            try {
                choice = scanner.nextDouble();
                if(minRange<=choice && choice<=maxRange){
                    validInput=true;
                } else{
                    System.out.println("Invalid input. Please a number between" + minRange + "and" + maxRange + ".");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter letters.");
            }
        } while(!validInput);

        return choice;
    }
    public static boolean checkValidBooleanInput(){
        Scanner scanner = new Scanner(System.in);
        boolean validInput=false, choice=false;

        do{
            try {
                choice = scanner.nextBoolean();
                if(choice == true || choice == false){
                    validInput=true;
                } else{
                    System.out.println("Invalid input. Please enter 'true' or 'false'.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        } while(!validInput);
        return choice;
    }
}