package engine;

import data.transfer.object.DataFromUser;
import data.transfer.object.definition.PropertyInfo;
import data.transfer.object.definition.PropertyValueInfo;
import data.transfer.object.definition.SimulationInfo;
import data.transfer.object.run.result.RunResultInfo;
import exception.*;
import world.creator.XMLFileException;

import java.io.IOException;
import java.util.ArrayList;


public interface EngineInterface {
    void createSimulationByXMLFile(String fileName) throws XMLFileException;
    void runSimulation(DataFromUser detailsToRun) ;
    ArrayList<PropertyValueInfo> getEnvironmentValues();
    ArrayList<PropertyInfo> getEnvironmentDefinitions();
    RunResultInfo displayRunResultsInformation(int runID);
    SimulationInfo displaySimulationDefinitionInformation();
    void setUserControlOnSpecificRun(int runID, String userControl);
    String getCurrentStateOfSpecificRun(int runID);
    void cleanup();
}
