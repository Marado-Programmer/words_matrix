package pt.ipbeja.app.throwables;

public class InvalidInGameChangeException extends Exception {
    public InvalidInGameChangeException() {
        super();
    }

    public InvalidInGameChangeException(String message) {
        super(message);
    }
}
