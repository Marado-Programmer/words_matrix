package pt.ipbeja.app.model.cell;

import org.jetbrains.annotations.NotNull;

public class WildCell extends BaseCell {
    private final static char WILD = '*';
    public WildCell(char actual) {
        super(actual, WILD);
    }

    public static @NotNull WildCell fromCell(@NotNull BaseCell cell) {
        char[] actuals = cell.getActuals();
        assert actuals.length > 0;
        WildCell wild = new WildCell(actuals[0]);
        for (int i = 1; i < actuals.length; i++) {
            wild.addActual(actuals[i]);
        }
        return wild;
    }

    @Override
    public boolean hasSameDisplayAs(char actual) {
        return true;
    }
}
