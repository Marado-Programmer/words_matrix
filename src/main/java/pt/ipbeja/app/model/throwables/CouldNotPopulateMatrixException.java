package pt.ipbeja.app.model.throwables;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CouldNotPopulateMatrixException extends Exception {
    private final @NotNull Set<@NotNull String> words;
    private final int lines, cols;

    public CouldNotPopulateMatrixException(@NotNull Set<@NotNull String> words, int lines, int cols) {
        super();
        this.words = words;
        this.lines = lines;
        this.cols = cols;
    }

    public @NotNull Set<String> getWords() {
        return words;
    }

    public int getLines() {
        return lines;
    }

    public int getCols() {
        return cols;
    }
}
