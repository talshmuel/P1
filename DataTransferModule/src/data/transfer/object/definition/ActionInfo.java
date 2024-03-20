package data.transfer.object.definition;



import java.util.Map;

public final class ActionInfo {
    String type;
    String mainEntity;
    boolean thereIsSecondEntity;
    Map<String, Object> moreProperties;

    public ActionInfo(String type, String mainEntity, boolean thereIsSecondEntity, Map<String, Object> moreProperties){
        this.type = type;
        this.mainEntity = mainEntity;
        this.thereIsSecondEntity = thereIsSecondEntity;
        this.moreProperties = moreProperties;
    }

    public String getType() {
        return type;
    }

    public String getMainEntity() {
        return mainEntity;
    }

    public boolean thereIsSecondEntity() {
        return thereIsSecondEntity;
    }

    public Map<String, Object> getMoreProperties() {
        return moreProperties;
    }

    @Override
    public String toString() {
        String morePropString = "";

        for (Map.Entry<String, Object> prop : moreProperties.entrySet()) {
            morePropString+= prop.getKey()+": "+prop.getValue()+"\n";

        }


        return "Action type: "+type+"\n"+
                "Main entity: "+mainEntity+"\n"+
                "There is second entity: "+thereIsSecondEntity+"\n"+morePropString;

    }
}
