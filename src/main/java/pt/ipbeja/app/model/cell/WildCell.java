package pt.ipbeja.app.model.cell;

import org.jetbrains.annotations.NotNull;

public class WildCell extends BaseCell {
    private final static char WILD = '*';
    public WildCell(char real) {
        super(real, WILD);
    }

    public static @NotNull WildCell fromCell(@NotNull BaseCell cell) {
        char[] reals = cell.getReals();
        assert reals.length > 0;
        WildCell wild = new WildCell(reals[0]);
        for (int i = 1; i < reals.length; i++) {
            wild.addReal(reals[i]);
        }
        return wild;
    }

    @Override
    public boolean hasSameDisplayAs(char real) {
        return true;
    }

    @Override
    public int getPoints() {
        return 2;
    }
}
