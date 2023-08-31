package world.api;


public interface AssistFunctionsInterface {
    Object environment(String propName);
    Object random(int max);
    Object evaluate(String expression);
    double percent(double whole, double part);
    int ticks(String expression);


}
