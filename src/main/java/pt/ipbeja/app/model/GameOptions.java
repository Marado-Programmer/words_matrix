package pt.ipbeja.app.model;

import pt.ipbeja.app.model.words_provider.WordsProvider;

import java.util.Set;
import java.util.TreeSet;

import static pt.ipbeja.app.model.WSModel.MIN_SIDE_LEN;

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
        this.lines = MIN_SIDE_LEN;
        this.columns = MIN_SIDE_LEN;
        this.provider = null;
        this.keepExistent = false;
        this.maxWords = 5;
        this.minWordSize = 1;
        this.orientationsAllowed = new TreeSet<>();
        this.orientationsAllowed.add(WordOrientations.VERTICAL);
        this.orientationsAllowed.add(WordOrientations.HORIZONTAL);
        this.numberOfWilds = 1;
    }

    public void addOrientationAllowed(WordOrientations orientation) {
        this.orientationsAllowed.add(orientation);
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public WordsProvider getProvider() {
        return provider;
    }

    public void setProvider(WordsProvider provider) {
        this.provider = provider;
    }

    public int getMaxWords() {
        return maxWords;
    }

    public void setMaxWords(int maxWords) {
        this.maxWords = maxWords;
    }

    public int getMinWordSize() {
        return minWordSize;
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
        return numberOfWilds;
    }

    public void setNumberOfWilds(int numberOfWilds) {
        this.numberOfWilds = numberOfWilds;
    }

    public boolean isKeepExistent() {
        return keepExistent;
    }

    public void setKeepExistent(boolean keepExistent) {
        this.keepExistent = keepExistent;
    }

    public Set<WordOrientations> getOrientationsAllowed() {
        return orientationsAllowed;
    }
}
