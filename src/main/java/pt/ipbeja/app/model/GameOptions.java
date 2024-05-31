package pt.ipbeja.app.model;

import pt.ipbeja.app.model.wordsprovider.WordsProvider;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class GameOptions {
    private int lines;
    private int columns;
    private WordsProvider provider;
    private boolean keepExistent;
    private int maxWords;
    private int minWordSize;

    private final Set<WordOrientations> orientationsAllowed;
    private int numberOfWilds;

    public GameOptions() {
        super();
        this.lines = WSModel.MIN_SIDE_LEN;
        this.columns = WSModel.MIN_SIDE_LEN;
        this.provider = null;
        this.keepExistent = false;
        this.maxWords = 5;
        this.minWordSize = 1;
        // https://docs.oracle.com/javase/8/docs/api/java/util/EnumSet.html
        this.orientationsAllowed = EnumSet.noneOf(WordOrientations.class);
        this.orientationsAllowed.add(WordOrientations.VERTICAL);
        this.orientationsAllowed.add(WordOrientations.HORIZONTAL);
        this.numberOfWilds = 1;
    }

    public void addOrientationAllowed(WordOrientations orientation) {
        this.orientationsAllowed.add(orientation);
    }

    public int getLines() {
        return this.lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public WordsProvider getProvider() {
        return this.provider;
    }

    public void setProvider(WordsProvider provider) {
        this.provider = provider;
    }

    public int getMaxWords() {
        return this.maxWords;
    }

    public void setMaxWords(int maxWords) {
        this.maxWords = maxWords;
    }

    public int getMinWordSize() {
        return this.minWordSize;
    }

    public void setMinWordSize(int minWordSize) {
        this.minWordSize = minWordSize;
    }

    public void removeOrientationAllowed(WordOrientations orientation) {
        this.orientationsAllowed.remove(orientation);
    }

    public void clearOrientationAllowed() {
        this.orientationsAllowed.clear();
    }

    public int getNumberOfWilds() {
        return this.numberOfWilds;
    }

    public void setNumberOfWilds(int numberOfWilds) {
        this.numberOfWilds = numberOfWilds;
    }

    public boolean isKeepExistent() {
        return this.keepExistent;
    }

    public void setKeepExistent(boolean keepExistent) {
        this.keepExistent = keepExistent;
    }

    public Set<WordOrientations> getOrientationsAllowed() {
        return Collections.unmodifiableSet(this.orientationsAllowed);
    }
}
