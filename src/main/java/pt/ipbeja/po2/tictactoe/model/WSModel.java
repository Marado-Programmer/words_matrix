package pt.ipbeja.po2.tictactoe.model;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Game model
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class WSModel {


    public static final int MIN_SIDE_LEN = 5;
    public static final int MAX_SIDE_LEN = 8;
    private static final String INVALID_SIDE_LEN_MSG_FORMAT = String.format(
            "the %s provided is invalid! it needs to be a number between %d and %d",
            "%s",
            MIN_SIDE_LEN,
            MAX_SIDE_LEN
    );
    private final @NotNull Random random;
    // The following matrix could also be List<List<Character>>
    // for a more complex game, it should be a List<List<Cell>>
    // where Letter is a class with the letter and other attributes
    private List<List<Cell>> lettersGrid;
    private WSView wsView;
    private int lines;
    private int cols;
    private Set<String> words;
    private Set<String> words_in_use;
    private Set<String> words_to_find;
    private Set<String> words_found;
    private boolean in_game;
    private @Nullable Position start_selected;

    public WSModel(int lines, int cols, @NotNull String file) {
        this(lines, cols, Paths.get(file));
    }

    public WSModel(int lines, int cols, @NotNull URI file) {
        this(lines, cols, Paths.get(file));
    }

    public WSModel(int lines, int cols, @NotNull Path words) {
        this.random = new Random();
        this.setDimensions(lines, cols);
        this.parseWords(words);
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
        this.lettersGrid = new ArrayList<>(this.lines);
        for (int i = 0; i < this.lines; i++) {
            List<Cell> line = new ArrayList<>(this.cols);
            for (int j = 0; j < this.cols; j++) {
                line.add(null);
            }
            this.lettersGrid.add(line);
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
            List<Cell> l = this.lettersGrid.get(i);
            for (int j = 0; j < this.cols; j++) {
                if (l.get(j) == null) {
                    l.set(j, new Cell((char) this.random.nextInt('A', 'Z' + 1)));
                }
            }
            this.lettersGrid.set(i, l);
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
        for (List<Cell> i : this.lettersGrid) {
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

            List<Cell> line = this.lettersGrid.get(pos);
            Set<Integer> unchanged_pos = new TreeSet<>();
            boolean invalid_pos = false;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (line.get(start) != null && line.get(start).letter() != c) {
                    while (i-- > 0) {
                        start--;
                        if (!unchanged_pos.contains(start)) {
                            line.set(start, null);
                            this.lettersGrid.set(pos, line);
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
                this.lettersGrid.set(pos, line);
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
                List<Cell> line = this.lettersGrid.get(start);
                if (line.get(pos) != null && line.get(pos).letter() != c) {
                    while (i-- > 0) {
                        line = this.lettersGrid.get(--start);
                        if (!unchanged_pos.contains(start)) {
                            line.set(pos, null);
                            this.lettersGrid.set(start, line);
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

                this.lettersGrid.set(start, line);

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
            this.wsView.wordFound(start_pos, pos);
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
            List<Cell> line = this.lettersGrid.get(end_pos.line());
            int start = Math.min(start_pos.col(), end_pos.col());
            int end = Math.max(start_pos.col(), end_pos.col());
            for (int i = start; i <= end; i++) {
                possible.append(line.get(i).letter());
            }
        } else if (start_pos.col() == end_pos.col()) {
            int start = Math.min(start_pos.line(), end_pos.line());
            int end = Math.max(start_pos.line(), end_pos.line());
            for (int i = start; i <= end; i++) {
                List<Cell> line = this.lettersGrid.get(i);
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
        this.wsView.gameEnded(res);
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
        this.wsView = wsView;
    }

    /**
     * Get the text in a position
     *
     * @param position position
     * @return the text in the position
     */
    public Cell textInPosition(@NotNull Position position) {
        return this.lettersGrid.get(position.line()).get(position.col());
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
}
