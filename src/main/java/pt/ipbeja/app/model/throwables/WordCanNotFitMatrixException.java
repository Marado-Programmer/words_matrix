package pt.ipbeja.app.model.throwables;

import org.jetbrains.annotations.NotNull;

public class WordCanNotFitMatrixException extends Exception {
    private final @NotNull String word;
    private final int lines, cols;

    public WordCanNotFitMatrixException(@NotNull String word, int lines, int cols) {
        super();
        this.word = word;
        this.lines = lines;
        this.cols = cols;
    }

    public @NotNull String getWord() {
        return word;
    }

    public int getLines() {
        return lines;
    }

    public int getCols() {
        return cols;
    }
}
