/**
 * @author João Augusto Costa Branco Marado Torres
 * @version 0.6, 2024/04/21
 */
package pt.ipbeja.app.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.ipbeja.app.model.throwables.InvalidInGameChangeException;
import pt.ipbeja.app.model.words_provider.DBWordsProvider;
import pt.ipbeja.app.model.words_provider.WordsProvider;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Game model
 */
public class WSModel {
    /**
     * A natural number representing the minimum acceptable length for a matrix side.
     */
    public static final int MIN_SIDE_LEN = 3;
    /**
     * A natural number representing the maximum acceptable length for a matrix side.
     */
    public static final int MAX_SIDE_LEN = 12;
    private static final String INVALID_SIDE_LEN_MSG_FORMAT = String.format("the %s provided is invalid! it needs to be a number between %d and %d", "%s", MIN_SIDE_LEN, MAX_SIDE_LEN);

    private final @NotNull Random random;

    /**
     * The number of lines in the matrix.
     */
    private int lines;
    /**
     * The number of columns in the matrix.
     */
    private int cols;
    /**
     * The matrix of {@link Cell}s.
     * <p>Can be a simple <code>Cell[lines*cols]</code> and to access a <code>Cell</code> in line <code>a</code> and
     * column <code>b</code> you just <code>this.matrix[a * this.cols + b]</code>.</p>
     *
     * @see Cell
     */
    private List<List<Cell>> matrix;

    private WSView view;

    /**
     * Set of valid words that came from the last database provided using {@link #setWords(WordsProvider)}.
     *
     * @see #setWords(WordsProvider)
     */
    private Set<String> words;
    /**
     * Subset of {@link #words} with words that can fit in the matrix.
     */
    private Set<String> usableWords;
    /**
     * Subset of {@link #words} with words that are in game.
     */
    private Set<String> words_in_game;
    /**
     * Subset of {@link #usableWords} of the works that are currently on the matrix to be found.
     *
     * @see #words_found
     */
    private Set<String> words_to_find;
    /**
     * Subset of {@link #usableWords} of the works that are currently on the matrix, that were already found.
     *
     * @see #words_to_find
     */
    private Set<String> words_found;

    /**
     * Represents if a game it's currently happening.
     */
    private boolean in_game;

    private @Nullable Position start_selected;

    private ResultsSaver saver;

    private int maxWords;

    public WSModel() {
        this.random = new Random();
        this.maxWords = 0;
    }

    public WSModel(int lines, int cols) {
        this();
        try {
            this.setDimensions(lines, cols);
        } catch (InvalidInGameChangeException e) {
            throw new RuntimeException(e);
        }
    }

    public WSModel(int lines, int cols, @NotNull WordsProvider provider) {
        this(lines, cols);
        this.setWords(provider);
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols  initial number of columns for the game to have.
     * @param file  the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull String file) {
        this(lines, cols, new DBWordsProvider(Paths.get(file).toFile()));
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols  initial number of columns for the game to have.
     * @param file  the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull URI file) {
        this(lines, cols, new DBWordsProvider(Paths.get(file).toFile()));
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols  initial number of columns for the game to have.
     * @param file  the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull Path file) {
        this(lines, cols, new DBWordsProvider(file.toFile()));
    }

    /**
     * Sets the dimensions of the matrix (both lines and columns). To set lines and/or columns separately use
     * {@link #setLines(int)} and {@link #setCols(int)}.
     *
     * @param lines The number of lines for the matrix to have
     * @param cols The number of columns for the matrix to have
     *
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game.
     * @throws IllegalArgumentException If dimensions provided aren't allowed.
     *
     * @see #setLines(int)
     * @see #setCols(int)
     */
    public void setDimensions(int lines, int cols) throws InvalidInGameChangeException {
        if (this.in_game) {
            throwInvalidInGameChange();
        }

        if (lines < 0 || cols < 0) {
            throw new IllegalArgumentException("`lines` and `cols` are natural numbers");
        }

        boolean valid_lines = lines >= MIN_SIDE_LEN && lines <= MAX_SIDE_LEN;
        boolean valid_cols = cols >= MIN_SIDE_LEN && cols <= MAX_SIDE_LEN;
        if (!valid_lines || !valid_cols) {
            boolean both_invalid = !valid_lines && !valid_cols;
            String invalid = (valid_lines ? "" : "`lines``") + (both_invalid ? " and " : "") + (valid_cols ? "" : "`cols``");
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, invalid);
            throw new IllegalArgumentException(msg);
        }

        this.lines = lines;
        this.cols = cols;

        if (this.words != null && !this.words.isEmpty()) {
            this.calculateUsableWords();
        }
    }

    /**
     * @return The number of lines in the matrix
     *
     * @see #setLines(int)
     * @see #setDimensions(int, int)
     */
    public int getLines() {
        return this.lines;
    }

    /**
     * Sets the lines of the matrix. To set columns use {@link #setCols(int)} and to set both at the same time
     * {@link #setDimensions(int, int)}.
     *
     * @param lines The number of lines for the matrix to have
     *
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game.
     * @throws IllegalArgumentException If dimensions provided aren't allowed.
     *
     * @see #setCols(int)
     * @see #setDimensions(int, int)
     */
    public void setLines(int lines) throws InvalidInGameChangeException {
        if (this.in_game) {
            throwInvalidInGameChange();
        }

        if (lines < 0) {
            throw new IllegalArgumentException("`lines` are natural numbers");
        }

        boolean valid_lines = lines >= MIN_SIDE_LEN && lines <= MAX_SIDE_LEN;
        if (!valid_lines) {
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, "`lines`");
            throw new IllegalArgumentException(msg);
        }

        this.lines = lines;

        if (this.words != null && !this.words.isEmpty()) {
            this.calculateUsableWords();
        }
    }

    /**
     * @return The number of columns in the matrix
     *
     * @see #setCols(int)
     * @see #setDimensions(int, int)
     */
    public int getCols() {
        return this.cols;
    }

    /**
     * Sets the columns of the matrix. To set lines use {@link #setLines(int)} and to set both at the same time
     * {@link #setDimensions(int, int)}.
     *
     * @param cols The number of columns for the matrix to have
     *
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game.
     * @throws IllegalArgumentException If dimensions provided aren't allowed.
     *
     * @see #setLines(int)
     * @see #setDimensions(int, int)
     */
    public void setCols(int cols) throws InvalidInGameChangeException {
        if (this.in_game) {
            throwInvalidInGameChange();
        }

        if (cols < 0) {
            throw new IllegalArgumentException("`cols` are natural numbers");
        }

        boolean valid_cols = cols >= MIN_SIDE_LEN && cols <= MAX_SIDE_LEN;
        if (!valid_cols) {
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, "`cols`");
            throw new IllegalArgumentException(msg);
        }

        this.cols = cols;

        if (this.words != null && !this.words.isEmpty()) {
            this.calculateUsableWords();
        }
    }

    /**
     * Method to define which words to use in the game via a {@link WordsProvider}.
     * @param provider Any {@link WordsProvider}.
     * @param keepExistent Choose if you want to keep the existent words provided in the past.
     * @see WordsProvider
     */
    public void setWords(@NotNull WordsProvider provider, boolean keepExistent) {
        if (!keepExistent || this.words == null) {
            this.words = new TreeSet<>();
            this.usableWords = new TreeSet<>();
        }

        String line;
        while ((line = provider.getLine()) != null) {
            String[] words = this.parseLine(line);

            for (String word : words) {
                this.words.add(word);
                if (this.wordFitsGrid(word)) {
                    this.usableWords.add(word);
                }
            }
        }
    }

    /**
     * Method to define which words to use in the game via a {@link WordsProvider}.
     * @param provider Any {@link WordsProvider}.
     * @see WordsProvider
     */
    public void setWords(@NotNull WordsProvider provider) {
        this.setWords(provider, true);
    }

    /**
     * Parses a line with possible (supported) words.
     * @param line Line to be parsed.
     * @return An array of words found in the line.
     */
    private @NotNull String[] parseLine(String line) {
        // trim whitespaces
        line = line.trim();

        // no words obviously
        if (line.isBlank()) {
            return new String[0];
        }

        // separate into latin alphabet sequences of words.
        // https://stackoverflow.com/questions/51732439/regex-accented-characters-for-special-field
        // https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
        // http://www.unicode.org/reports/tr24/
        String[] words = line.split("[^\\p{sc=LATN}]");

        // uppercase each word
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toUpperCase();
        }

        return words;
    }

    /**
     * Recalculates
     */
    private void calculateUsableWords() {
        this.usableWords = new TreeSet<>();
        for (String w : words) {
            if (this.wordFitsGrid(w)) {
                this.usableWords.add(w);
            }
        }
    }

    private boolean wordFitsHorizontally(@NotNull String word) {
        return word.length() <= this.cols;
    }

    private boolean wordFitsVertically(@NotNull String word) {
        return word.length() <= this.lines;
    }

    private boolean wordFitsGrid(@NotNull String word) {
        return this.wordFitsHorizontally(word) || this.wordFitsVertically(word);
    }

    private void initClearGrid() {
        this.matrix = new ArrayList<>(this.lines);
        for (int i = 0; i < this.lines; i++) {
            List<Cell> line = new ArrayList<>(this.cols);
            for (int j = 0; j < this.cols; j++) {
                line.add(null);
            }
            this.matrix.add(line);
        }
    }

    private void populateGrid() {
        this.words_to_find = new TreeSet<>();

        if (this.words != null && !this.words.isEmpty()) {
            this.calculateUsableWords();
        } else {
            throw new RuntimeException("No words");
        }

        List<String> words = new ArrayList<>(this.usableWords);
        Collections.shuffle(words);

        int max = this.usableWords.size();
        if (this.maxWords != 0 && this.maxWords < max) {
            max = this.maxWords;
        }

        this.words_in_game = new HashSet<>(words.subList(0, max));

        for (String w : this.words_in_game) {
            this.words_to_find.add(w);
            /* orientation: true for horizontal and false for vertical */
            boolean orientation = this.random.nextBoolean();

            // test if orientation is possible, if not, change it
            if (orientation) { // horizontal
                orientation = this.wordFitsHorizontally(w);
            } else { // vertical
                orientation = !this.wordFitsVertically(w);
            }

            try {
                if (orientation) { // horizontal
                    this.addWordHorizontally(w);
                } else { // vertical
                    this.addWordVertically(w);
                }
            } catch (Exception ignored) {
                try {
                    if (orientation && this.wordFitsVertically(w)) {
                        this.addWordVertically(w);
                    } else if (!orientation && this.wordFitsHorizontally(w)) {
                        this.addWordHorizontally(w);
                    } else {
                        this.words_to_find.remove(w);
                    }
                } catch (Exception e) {
                    this.words_to_find.remove(w);
                }
            }
        }

        if (this.words_to_find.isEmpty()) {
            throw new RuntimeException("No words could be used in the game");
        }
    }

    private void fillGrid() {
        for (int i = 0; i < this.lines; i++) {
            List<Cell> l = this.matrix.get(i);
            for (int j = 0; j < this.cols; j++) {
                if (l.get(j) == null) {
                    l.set(j, Cell.from((char) this.random.nextInt('A', 'Z' + 1)));
                }
            }
            this.matrix.set(i, l);
        }
    }

    private void initGrid() {
        this.initClearGrid();
        this.populateGrid();
        this.fillGrid();
    }

    private void addWordHorizontally(@NotNull String word) {
        char[] chars = word.toCharArray();

        Set<String> invalids = new TreeSet<>();

        boolean direction = this.random.nextBoolean();
        int walk = direction ? 1 : -1;

        while (true) {
            if (invalids.size() >= (this.lines * (this.cols - word.length() + 1))) {
                throw new RuntimeException("no space to add the word");
            }

            int start = this.random.nextInt(0, this.cols - word.length() + 1);
            if (!direction) {
                start += word.length();
            }
            final int pos = this.random.nextInt(0, this.lines);

            if (invalids.contains(start + ";" + pos)) {
                continue;
            }

            List<Cell> line = this.matrix.get(pos);
            Set<Integer> same_display_pos = new TreeSet<>();
            Set<Integer> same_actual_pos = new TreeSet<>();
            boolean invalid_pos = false;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (line.get(start) != null && !line.get(start).hasSameDisplayAs(c)) {
                    while (i-- > 0) {
                        start -= walk;
                        if (!same_display_pos.contains(start)) {
                            line.set(start, null);
                        } else if (!same_actual_pos.contains(start)) {
                            line.get(start).removeActual(c);
                        }
                    }
                    this.matrix.set(pos, line);
                    invalid_pos = true;
                }

                if (invalid_pos) {
                    break;
                }

                if (line.get(start) == null || !line.get(start).hasSameDisplayAs(c)) {
                    line.set(start, Cell.from(c));
                } else {
                    same_display_pos.add(start);
                    if (!line.get(start).addActual(c)) {
                        same_actual_pos.add(start);
                    }
                }

                start += walk;
            }

            if (!invalid_pos) {
                this.matrix.set(pos, line);
                break;
            } else {
                invalids.add(start + ";" + pos);
            }
        }
    }

    private void addWordVertically(@NotNull String word) {
        char[] chars = word.toCharArray();

        Set<String> invalids = new TreeSet<>();

        boolean direction = this.random.nextBoolean();
        int walk = direction ? 1 : -1;

        while (true) {
            if (invalids.size() == (this.cols * (this.lines - word.length() + 1))) {
                throw new RuntimeException("no space to add the word");
            }

            int start = this.random.nextInt(0, this.lines - word.length() + 1);
            if (!direction) {
                start += word.length();
            }
            final int pos = this.random.nextInt(0, this.cols);

            if (invalids.contains(start + ";" + pos)) {
                continue;
            }

            Set<Integer> same_display_pos = new TreeSet<>();
            Set<Integer> same_actual_pos = new TreeSet<>();
            boolean invalid_pos = false;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                List<Cell> line = this.matrix.get(start);
                if (line.get(pos) != null && !line.get(pos).hasSameDisplayAs(c)) {
                    while (i-- > 0) {
                        start -= walk;
                        line = this.matrix.get(start);
                        if (!same_display_pos.contains(start)) {
                            line.set(pos, null);
                        } else if (!same_actual_pos.contains(start)) {
                            line.get(pos).removeActual(c);
                        }
                        this.matrix.set(start, line);
                    }
                    invalid_pos = true;
                }

                if (invalid_pos) {
                    break;
                }

                if (line.get(pos) == null || !line.get(pos).hasSameDisplayAs(c)) {
                    line.set(pos, Cell.from(c));
                } else {
                    same_display_pos.add(start);
                    if (!line.get(pos).addActual(c)) {
                        same_actual_pos.add(start);
                    }
                }

                this.matrix.set(start, line);

                start += walk;
            }

            if (!invalid_pos) {
                break;
            } else {
                invalids.add(start + ";" + pos);
            }
        }
    }

    public boolean findWord(@NotNull Position pos) {
        if (!this.in_game) {
            throw new RuntimeException();
        }

        if (this.start_selected == null) {
            this.start_selected = pos;
            return true;
        }

        Position start_pos = this.start_selected;
        this.start_selected = null;

        boolean found = false;
        for (String possibleWord : getPossibleWords(start_pos, pos)) {
            if (this.wordFound(possibleWord)) {
                this.view.wordFound(start_pos, pos);
                this.view.update(new MessageToUI(List.of(), possibleWord));
                found = true;
            }
        }

        if (this.allWordsWereFound()) {
            this.endGame();
        }

        return found;
    }

    private @NotNull String[] getPossibleWords(@NotNull Position start_pos, @NotNull Position end_pos) {
        List<String> words = new ArrayList<>();
        words.add("");
        if (start_pos.line() == end_pos.line()) {
            List<Cell> line = this.matrix.get(end_pos.line());
            int start = Math.min(start_pos.col(), end_pos.col());
            int end = Math.max(start_pos.col(), end_pos.col());
            for (int i = start; i <= end; i++) {
                String[] bases = words.toArray(String[]::new);
                words.clear();
                for (char actual : line.get(i).getActuals()) {
                    for (String base : bases) {
                        words.add(base + actual);
                    }
                }
            }
        } else if (start_pos.col() == end_pos.col()) {
            int start = Math.min(start_pos.line(), end_pos.line());
            int end = Math.max(start_pos.line(), end_pos.line());
            for (int i = start; i <= end; i++) {
                List<Cell> line = this.matrix.get(i);
                String[] bases = words.toArray(String[]::new);
                words.clear();
                for (char actual : line.get(end_pos.col()).getActuals()) {
                    for (String base : bases) {
                        words.add(base + actual);
                    }
                }
            }
        }

        System.out.println(Arrays.toString(words.toArray(String[]::new)));
        return words.toArray(String[]::new);
    }

    public @NotNull GameResults endGame() {
        this.in_game = false;
        GameResults res = this.curGameResults();
        this.view.gameEnded(res);
        if (saver != null) {
            this.saver.save(res);
        }
        return res;
    }

    public @NotNull GameResults curGameResults() {
        return new GameResults(this.words_in_game, this.words_found);
    }

    public boolean gameEnded() {
        return this.words_in_game.size() == this.words_found.size();
    }

    public void startGame() {
        if (in_game) {
            throw new RuntimeException();
        }

        this.initGrid();
        this.in_game = true;
        this.words_found = new TreeSet<>();

        this.view.gameStarted();
    }

    public void registerView(WSView wsView) {
        this.view = wsView;
    }

    /**
     * Get the text in a position
     *
     * @param position position
     * @return the text in the position
     */
    public Cell textInPosition(@NotNull Position position) {
        return this.matrix.get(position.line()).get(position.col());
    }


    /**
     * Check if all words were found
     *
     * @return true if all words were found
     */
    public boolean allWordsWereFound() {
        if (!this.in_game) {
            throw new RuntimeException();
        }
        return this.words_to_find.isEmpty();
    }

    /**
     * Check if the word is in the board
     *
     * @param word word
     * @return true if the word is in the board
     */
    public boolean wordFound(String word) {
        if (!this.in_game) {
            throw new RuntimeException();
        }

        word = word.toUpperCase();

        // https://stackoverflow.com/questions/7569335/reverse-a-string-in-java
        String reversed = new StringBuilder(word).reverse().toString();
        if (this.words_to_find.contains(word)) {
            this.words_to_find.remove(word);
            this.words_found.add(word);
            return true;
        } else if (this.words_to_find.contains(reversed)) {
            this.words_to_find.remove(reversed);
            this.words_found.add(reversed);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the word with wildcard is in the board
     *
     * @param word word
     * @return true if the word with wildcard is in the board
     */
    public boolean wordWithWildcardFound(String word) {
        if (!this.in_game) {
            throw new RuntimeException();
        }

        word = word.toUpperCase();

        if (this.words_to_find.contains(word)) {
            this.words_to_find.remove(word);
            this.words_found.add(word);
            return true;
        } else {
            return false;
        }
    }

    public void setSaver(ResultsSaver saver) {
        this.saver = saver;
    }

    public int wordsInUse() {
        if (this.usableWords != null) {
            return this.usableWords.size();
        }
        return 0;
    }

    public static final String INVALID_IN_GAME_CHANGE_MSG_ERR = "A game it's currently happening. Cannot perform this action";
    private static void throwInvalidInGameChange() throws InvalidInGameChangeException {
        throw new InvalidInGameChangeException(INVALID_IN_GAME_CHANGE_MSG_ERR);
    }

    public void setMaxWords(int maxWords) {
        assert maxWords > 0;
        this.maxWords = maxWords;
    }
}
