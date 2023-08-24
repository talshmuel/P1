package world.property.api;


public class BooleanPropertyDefinition extends PropertyDefinition {
    Boolean initValue;

    public BooleanPropertyDefinition(String name, Boolean isRandomlyInitialized, Boolean initValue){
        super(name, isRandomlyInitialized);
        this.initValue = initValue;
    }

    @Override
    public String getType() {
        return "Boolean";
    }

    @Override
    public Object getTopLimit() {return null;
    }

    @Override
    public Object getBottomLimit() {return null;
    }

    @Override
    public Object getInitValue() {
        return initValue;
    }
}
