package pt.ipbeja.po2.tictactoe.model;

import org.jetbrains.annotations.NotNull;

/**
 * Position in the board
 *
 * @author anonymized
 * @version 2024/04/14
 */
public record Position(int line, int col) {

    @Override
    public @NotNull String toString() {
        return line + ", " + col;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int col() {
        return col;
    }
}
