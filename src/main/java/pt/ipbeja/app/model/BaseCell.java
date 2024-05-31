package pt.ipbeja.app.model;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Cell in the board
 * Contains a letter and a boolean that indicates if the cell is part of a word
 */
public abstract class BaseCell {
    private final Set<Character> reals;
    private final char display;

    public BaseCell(char real) {
        this(real, getDisplay(real));
    }

    protected BaseCell(char real, char display) {
        super();
        this.reals = new TreeSet<>();
        this.reals.add(real);
        this.display = display;
    }

    private static char getDisplay(char real) {
        real = String.valueOf(real).toUpperCase(Locale.ROOT).charAt(0);
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
            reals[i] = real;
            i++;
        }
        return reals;
    }

    public boolean hasSameDisplayAs(char real) {
        return (int) this.display == (int) getDisplay(real);
    }

    public char getDisplay() {
        return this.display;
    }

    public abstract int getPoints();
}
