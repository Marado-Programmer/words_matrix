package pt.ipbeja.app.model.throwables;

import java.util.Set;

public class CouldNotPopulateMatrixException extends Exception {
    private final Set<String> words;
    private final int lines, cols;

    public CouldNotPopulateMatrixException(Set<String> words, int lines, int cols) {
        super();
        this.words = words;
        this.lines = lines;
        this.cols = cols;
    }

    public Set<String> getWords() {
        return words;
    }

    public int getLines() {
        return lines;
    }

    public int getCols() {
        return cols;
    }
}
