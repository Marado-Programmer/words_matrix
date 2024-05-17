package pt.ipbeja.app.model.cell;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public class Cell extends BaseCell {

    public Cell(char actual) {
        super(actual);
    }
}
