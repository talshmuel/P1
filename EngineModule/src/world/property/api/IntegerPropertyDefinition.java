package world.property.api;

public class IntegerPropertyDefinition extends PropertyDefinition {
    Integer topLimitVal;
    Integer bottomLimitVal;
    Integer initValue;

    public IntegerPropertyDefinition(String name, Boolean isRandomlyInitialized,
                                     Integer initValue, Integer topLimitVal, Integer bottomLimitVal){
        super(name, isRandomlyInitialized);
        this.bottomLimitVal = bottomLimitVal;
        this.topLimitVal = topLimitVal;
        this.initValue = initValue;
    }

    @Override
    public String getType() {
        return "Integer";
    }

    @Override
    public Object getTopLimit() {
        return topLimitVal;
    }

    @Override
    public Object getBottomLimit() {
        return bottomLimitVal;
    }
    @Override
    public Object getInitValue() {
        return initValue;
    }
}
