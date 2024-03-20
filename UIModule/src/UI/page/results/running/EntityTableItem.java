package UI.page.results.running;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EntityTableItem {

    private final StringProperty name;
    private final StringProperty amount;

    public EntityTableItem(String name, String amount) {
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleStringProperty(amount);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public StringProperty amountProperty() {
        return amount;
    }

}
