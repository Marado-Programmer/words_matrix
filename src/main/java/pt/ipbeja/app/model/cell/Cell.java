package pt.ipbeja.app.model.cell;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public class Cell extends BaseCell {

    public Cell(char actual) {
        super(actual);
    }

    @Override
    public int getPoints() {
        return 1;
    }
}
