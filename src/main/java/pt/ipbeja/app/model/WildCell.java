package pt.ipbeja.app.model;

public class WildCell extends BaseCell {
    private final static char WILD = '*';

    public WildCell(char real) {
        super(real, WILD);
    }

    public static WildCell fromCell(BaseCell cell) {
        char[] reals = cell.getReals();
        assert 0 < reals.length;
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
