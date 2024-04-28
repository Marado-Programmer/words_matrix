/**
 * @author João Augusto Costa Branco Marado Torres
 * @version 0.6, 2024/04/21
 */
package pt.ipbeja.app.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Files;
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
    public static final int MIN_SIDE_LEN = 5;
    /**
     * A natural number representing the maximum acceptable length for a matrix side.
     */
    public static final int MAX_SIDE_LEN = 8;
    private static final String INVALID_SIDE_LEN_MSG_FORMAT = String.format(
            "the %s provided is invalid! it needs to be a number between %d and %d",
            "%s",
            MIN_SIDE_LEN,
            MAX_SIDE_LEN
    );

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
     * Set of valid words that came from the last database provided using {@link #parseWords(Path)}.
     *
     * @see #parseWords(Path)
     */
    private Set<String> words;
    /**
     * Subset of {@link #words} with words that can fit in the matrix.
     */
    private Set<String> words_in_use;
    /**
     * Subset of {@link #words_in_use} of the works that are currently on the matrix to be found.
     *
     * @see #words_found
     */
    private Set<String> words_to_find;
    /**
     * Subset of {@link #words_in_use} of the works that are currently on the matrix, that were already found.
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

    public WSModel() {
        this.random = new Random();
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols initial number of columns for the game to have.
     * @param file the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull String file) {
        this(lines, cols, Paths.get(file));
    }
    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols initial number of columns for the game to have.
     * @param file the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull URI file) {
        this(lines, cols, Paths.get(file));
    }
    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have.
     * @param cols initial number of columns for the game to have.
     * @param file the database with the words to put in the game.
     */
    public WSModel(int lines, int cols, @NotNull Path file) {
        this();
        this.setDimensions(lines, cols);
        this.parseWords(file);
        this.startGame();
    }

    public void setDimensions(int lines, int cols) {
        assert lines > 0 && cols > 0 : "`lines` and `cols` are natural numbers";

        if (this.in_game) {
            throw new RuntimeException();
        }

        boolean valid_lines = lines >= MIN_SIDE_LEN && lines <= MAX_SIDE_LEN;
        boolean valid_cols = cols >= MIN_SIDE_LEN && cols <= MAX_SIDE_LEN;
        if (!valid_lines || !valid_cols) {
            boolean both_invalid = !valid_lines && !valid_cols;
            String invalid = (valid_lines ? "" : "`lines``") +
                    (both_invalid ? " and " : "") +
                    (valid_cols ? "" : "`cols``");
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, invalid);
            throw new IllegalArgumentException(msg);
        }

        this.lines = lines;
        this.cols = cols;

        if (this.words != null && !this.words.isEmpty()) {
            this.calcUsableWords();
        }
    }

    private void calcUsableWords() {
        this.words_in_use = new TreeSet<>();
        for (String w :
                words) {
            if (this.wordFitsGrid(w)) {
                this.words_in_use.add(w);
            }
        }
    }

    private void parseWords(@NotNull Path words) {
        List<String> words_raw;

        try {
            words_raw = Files.readAllLines(words);
        } catch (Exception e) {
            throw new RuntimeException("TODO", e); // TODO: handle exception
        }

        this.words = new TreeSet<>();
        this.words_in_use = new TreeSet<>();
        for (String w :
                words_raw) {
            w = w.toUpperCase(); // needs to be uppercase
            // TODO: expect a format and rules and not just a valid word per line
            this.words.add(w);
            if (this.wordFitsGrid(w)) {
                this.words_in_use.add(w);
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
        for (String w :
                this.words_in_use) {
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
    }

    private void fillGrid() {
        for (int i = 0; i < this.lines; i++) {
            List<Cell> l = this.matrix.get(i);
            for (int j = 0; j < this.cols; j++) {
                if (l.get(j) == null) {
                    l.set(j, new Cell((char) this.random.nextInt('A', 'Z' + 1)));
                }
            }
            this.matrix.set(i, l);
        }
    }

    private void initGrid() {
        this.initClearGrid();
        this.populateGrid();
        this.printGrid();
        this.fillGrid();
    }

    private void printGrid() {
        System.out.println("GRID:");
        for (List<Cell> i : this.matrix) {
            System.out.print("\t");
            for (Cell j : i) {
                if (j == null) {
                    System.out.print(".");
                } else {
                    System.out.print(j.letter());
                }
            }
            System.out.println();
        }
    }

    private void addWordHorizontally(@NotNull String word) {
        char[] chars = word.toCharArray();

        Set<String> invalids = new TreeSet<>();

        while (true) {
            if (invalids.size() >= (this.lines * (this.cols - word.length() + 1))) {
                throw new RuntimeException("no space to add the word");
            }

            int start = this.random.nextInt(0, this.cols - word.length() + 1);
            int pos = this.random.nextInt(0, this.lines);

            if (invalids.contains(start + ";" + pos)) {
                continue;
            }

            List<Cell> line = this.matrix.get(pos);
            Set<Integer> unchanged_pos = new TreeSet<>();
            boolean invalid_pos = false;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (line.get(start) != null && line.get(start).letter() != c) {
                    while (i-- > 0) {
                        start--;
                        if (!unchanged_pos.contains(start)) {
                            line.set(start, null);
                            this.matrix.set(pos, line);
                        }
                    }
                    invalid_pos = true;
                }

                if (invalid_pos) {
                    break;
                }

                if (line.get(start) == null || line.get(start).letter() != c) {
                    line.set(start, new Cell(c));
                } else {
                    unchanged_pos.add(start);
                }

                start++;
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

        while (true) {
            if (invalids.size() == (this.cols * (this.lines - word.length() + 1))) {
                throw new RuntimeException("no space to add the word");
            }

            int start = this.random.nextInt(0, this.lines - word.length() + 1);
            int pos = this.random.nextInt(0, this.cols);

            if (invalids.contains(start + ";" + pos)) {
                continue;
            }

            Set<Integer> unchanged_pos = new TreeSet<>();
            boolean invalid_pos = false;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                List<Cell> line = this.matrix.get(start);
                if (line.get(pos) != null && line.get(pos).letter() != c) {
                    while (i-- > 0) {
                        line = this.matrix.get(--start);
                        if (!unchanged_pos.contains(start)) {
                            line.set(pos, null);
                            this.matrix.set(start, line);
                        }
                    }
                    invalid_pos = true;
                }

                if (invalid_pos) {
                    break;
                }

                if (line.get(pos) == null || line.get(pos).letter() != c) {
                    line.set(pos, new Cell(c));
                } else {
                    unchanged_pos.add(start);
                }

                this.matrix.set(start, line);

                start++;
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

        if (this.wordFound(getPossibleWord(start_pos, pos))) {
            this.view.wordFound(start_pos, pos);
            if (this.allWordsWereFound()) {
                this.endGame();
            }
            return true;
        } else {
            return false;
        }
    }

    private @NotNull String getPossibleWord(@NotNull Position start_pos, @NotNull Position end_pos) {
        StringBuilder possible = new StringBuilder();
        if (start_pos.line() == end_pos.line()) {
            List<Cell> line = this.matrix.get(end_pos.line());
            int start = Math.min(start_pos.col(), end_pos.col());
            int end = Math.max(start_pos.col(), end_pos.col());
            for (int i = start; i <= end; i++) {
                possible.append(line.get(i).letter());
            }
        } else if (start_pos.col() == end_pos.col()) {
            int start = Math.min(start_pos.line(), end_pos.line());
            int end = Math.max(start_pos.line(), end_pos.line());
            for (int i = start; i <= end; i++) {
                List<Cell> line = this.matrix.get(i);
                possible.append(line.get(end_pos.col()).letter());
            }

        }
        return possible.toString();
    }

    public @Nullable Position getStartSelected() {
        return this.start_selected;
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
        return new GameResults(this.words_in_use, this.words_found);
    }

    public boolean gameEnded() {
        return this.words_in_use.size() == this.words_found.size();
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

    public int getLines() {
        return this.lines;
    }

    public void setLines(int lines) {
        assert lines > 0 : "`lines` are natural numbers";

        if (this.in_game) {
            throw new RuntimeException();
        }

        boolean valid_lines = lines >= MIN_SIDE_LEN && lines <= MAX_SIDE_LEN;
        if (!valid_lines) {
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, "`lines`");
            throw new IllegalArgumentException(msg);
        }

        this.lines = lines;

        if (this.words != null && !this.words.isEmpty()) {
            this.calcUsableWords();
        }
    }

    public int getCols() {
        return this.cols;
    }

    public void setCols(int cols) {
        assert cols > 0 : "`cols` are natural numbers";

        if (this.in_game) {
            throw new RuntimeException();
        }

        boolean valid_cols = cols >= MIN_SIDE_LEN && cols <= MAX_SIDE_LEN;
        if (!valid_cols) {
            String msg = String.format(INVALID_SIDE_LEN_MSG_FORMAT, "`cols`");
            throw new IllegalArgumentException(msg);
        }

        this.cols = cols;

        if (this.words != null && !this.words.isEmpty()) {
            this.calcUsableWords();
        }
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

        System.out.println(word);

        if (this.words_to_find.contains(word)) {
            this.words_to_find.remove(word);
            this.words_found.add(word);
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

    public void setWordsProvider(@NotNull DBWordsProvider provider) {
        List<String> words_raw;

        this.words = new TreeSet<>();
        this.words_in_use = new TreeSet<>();

        String w;
        while ((w = provider.getWord()) != null) {
            // TODO: expect a format and rules and not just a valid word per line
            w = w.toUpperCase(); // needs to be uppercase
            this.words.add(w);
            if (this.wordFitsGrid(w)) {
                this.words_in_use.add(w);
            }
        }
    }
}
