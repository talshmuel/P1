package world.property.api;

public class FloatPropertyDefinition extends PropertyDefinition {
    Double topLimitVal;
    Double bottomLimitVal;
    Double initValue;

    public FloatPropertyDefinition(String name, Boolean isRandomlyInitialized,
                                   Double initValue, Double topLimitVal, Double bottomLimitVal){
        super(name, isRandomlyInitialized);
        this.bottomLimitVal = bottomLimitVal;
        this.topLimitVal = topLimitVal;
        this.initValue = initValue;
    }

    @Override
    public String getType() {
        return "Float";
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
