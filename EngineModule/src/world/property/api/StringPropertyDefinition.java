package world.property.api;


public class StringPropertyDefinition extends PropertyDefinition {
    String initValue;
    int maxRandomSize;
    public StringPropertyDefinition(String name, Boolean isRandomlyInitialized, String initValue){
        super(name, isRandomlyInitialized);
        this.initValue = initValue;
        maxRandomSize = 50;
    }

    @Override
    public String getType() {
        return "String";
    }

    @Override
    public Object getTopLimit(){
        return null;
    }

    @Override
    public Object getBottomLimit() {
        return null;
    }

    @Override
    public Object getInitValue() {
        return initValue;
    }
    public int getMaxRandomSize(){return maxRandomSize;}
}
