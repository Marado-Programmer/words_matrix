package pt.ipbeja.app.throwables;

import java.util.Collections;
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
        return Collections.unmodifiableSet(this.words);
    }

    public int getLines() {
        return this.lines;
    }

    public int getCols() {
        return this.cols;
    }
}
