package pt.ipbeja.app.model.throwables;

public class WordCanNotFitMatrixException extends Exception {
    private final String word;
    private final int lines, cols;

    public WordCanNotFitMatrixException(String word, int lines, int cols) {
        super();
        this.word = word;
        this.lines = lines;
        this.cols = cols;
    }

    public String getWord() {
        return word;
    }

    public int getLines() {
        return lines;
    }

    public int getCols() {
        return cols;
    }
}
