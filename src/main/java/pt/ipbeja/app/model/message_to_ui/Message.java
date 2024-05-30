package pt.ipbeja.app.model.message_to_ui;

public abstract class Message implements MessageToUI {
    @Override
    public String toString() {
        return this.getMessage();
    }
}
