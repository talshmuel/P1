package world.api;


public interface AssistFunctionsInterface {
    Object environment(String propName);
    Integer random(int max);
    Object evaluate(String expression);
    //Double percent(double whole, double part);
    Double percent(String expression);
    int ticks(String expression);


}
