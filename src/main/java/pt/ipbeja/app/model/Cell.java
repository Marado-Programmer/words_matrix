package pt.ipbeja.app.model;

import java.util.*;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public class Cell {
    private final Set<Character> actuals;
    private final char display;
    private Cell(char actual, char display) {
        this.actuals = new TreeSet<>();
        this.actuals.add(actual);
        this.display = display;
    }

    public static Cell from(char actual) {
        return new Cell(actual, Cell.getDisplay(actual));
    }

    private static char getDisplay(char actual) {
        actual = String.valueOf(actual).toUpperCase().charAt(0);
        return switch (actual) {
            case 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å' -> 'A';
            case 'Ç' -> 'C';
            case 'È', 'É', 'Ê', 'Ë' -> 'E';
            case 'Ì', 'Í', 'Î', 'Ï' -> 'I';
            case 'Ð', 'Þ' -> 'D';
            case 'Ñ' -> 'N';
            case 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ø' -> 'O';
            case 'Ù', 'Ú', 'Û', 'Ü' -> 'U';
            case 'Ý' -> 'Y';
            case 'ß' -> 'S';
            default -> actual;
        };
    }

    public boolean addActual(char actual) {
        int sz = this.actuals.size();
        if (this.hasSameDisplayAs(actual)) {
            this.actuals.add(actual);
        }
        return this.actuals.size() > sz;
    }

    public void removeActual(char actual) {
        this.actuals.remove(actual);
    }

    public char[] getActuals() {
        char[] actuals = new char[this.actuals.size()];
        int i = 0;
        for (Character actual : this.actuals) {
            actuals[i++] = actual;
        }
        return actuals;
    }

    public boolean hasSameDisplayAs(char actual) {
        return this.display == Cell.getDisplay(actual);
    }

    public char getDisplay() {
        return display;
    }
}
