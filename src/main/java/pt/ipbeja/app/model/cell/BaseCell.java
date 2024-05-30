package pt.ipbeja.app.model.cell;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public abstract class BaseCell {
    private final @NotNull Set<Character> reals;
    protected final char display;

    public BaseCell(char real) {
        this(real, BaseCell.getDisplay(real));
    }
    protected BaseCell(char real, char display) {
        this.reals = new TreeSet<>();
        this.reals.add(real);
        this.display = display;
    }

    private static char getDisplay(char real) {
        real = String.valueOf(real).toUpperCase().charAt(0);
        return switch (real) {
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
            default -> real;
        };
    }

    public boolean addReal(char real) {
        int sz = this.reals.size();
        if (this.hasSameDisplayAs(real)) {
            this.reals.add(real);
        }
        return this.reals.size() > sz;
    }

    public void removeReal(char real) {
        this.reals.remove(real);
    }

    public char[] getReals() {
        char[] reals = new char[this.reals.size()];
        int i = 0;
        for (Character real : this.reals) {
            reals[i++] = real;
        }
        return reals;
    }

    public boolean hasSameDisplayAs(char real) {
        return this.display == BaseCell.getDisplay(real);
    }

    public char getDisplay() {
        return display;
    }

    public abstract int getPoints();
}
