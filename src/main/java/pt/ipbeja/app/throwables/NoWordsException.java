package pt.ipbeja.app.throwables;

public class NoWordsException extends Exception {
    public NoWordsException() {
        super();
    }

    public NoWordsException(String message) {
        super(message);
    }
}
