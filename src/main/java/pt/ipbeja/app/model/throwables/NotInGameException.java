package pt.ipbeja.app.model.throwables;

public class NotInGameException extends Exception {
    public NotInGameException() {
        super();
    }
    public NotInGameException(String message) {
        super(message);
    }
}
