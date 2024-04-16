package pt.ipbeja.po2.tictactoe.model;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public class Cell {
    private final char letter;

    public Cell(char letter) {
        this.letter = letter;
    }
}
