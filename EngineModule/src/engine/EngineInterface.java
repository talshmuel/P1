package engine;

import data.transfer.object.EndSimulationData;
import data.transfer.object.definition.PropertyInfo;
import data.transfer.object.definition.PropertyValueInfo;
import data.transfer.object.definition.SimulationInfo;
import data.transfer.object.run.result.RunResultInfo;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import exception.PathDoesntExistException;
import xml.reader.validator.*;

import java.util.ArrayList;


public interface EngineInterface {
    Boolean createSimulationByXMLFile(String fileName) throws FileDoesntExistException, InvalidXMLFileNameException, EnvironmentException, EntityException, PropertyException, MustBeNumberException, RuleException, TerminationException;
    EndSimulationData runSimulation()throws DivisionByZeroException, IncompatibleType, IncompatibleAction;
    void setEnvironmentVariable(String name, Object val)throws IncompatibleType;


    ArrayList<PropertyValueInfo> getEnvironmentValues();

    ArrayList<PropertyInfo> getEnvironmentDefinitions();
    ArrayList<RunResultInfo> displayRunResultsInformation();
    SimulationInfo displaySimulationDefinitionInformation();
    void cleanResults ();
    void saveState(String fileName) throws PathDoesntExistException;
    Engine restoreState(String fileName) throws FileDoesntExistException;

}
