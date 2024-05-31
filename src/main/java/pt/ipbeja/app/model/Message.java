package pt.ipbeja.app.model;

abstract class Message implements MessageToUI {
    @Override
    public String toString() {
        return this.getMessage();
    }
}
