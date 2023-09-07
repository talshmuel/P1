package world.api;


import world.entity.Entity;

public interface AssistFunctionsInterface {
    Object environment(String propName);
    Integer random(int max);
    Object evaluate(String expression, Entity mainEntity, Entity secondEntity, Entity thirdEntity);
    Double percent(String expression, Entity mainEntity, Entity secondEntity, Entity thirdEntity);
    int ticks(String expression, Entity entity);


}
