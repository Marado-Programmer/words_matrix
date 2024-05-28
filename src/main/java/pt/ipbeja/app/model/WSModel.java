/**
 * @author João Augusto Costa Branco Marado Torres
 * @version 0.6, 2024/04/21
 */
package pt.ipbeja.app.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.ipbeja.app.model.cell.BaseCell;
import pt.ipbeja.app.model.cell.Cell;
import pt.ipbeja.app.model.cell.WildCell;
import pt.ipbeja.app.model.message_to_ui.ClickMessage;
import pt.ipbeja.app.model.message_to_ui.WordFoundMessage;
import pt.ipbeja.app.model.message_to_ui.WordPointsMessage;
import pt.ipbeja.app.model.results_saver.ResultsSaver;
import pt.ipbeja.app.model.throwables.*;
import pt.ipbeja.app.model.words_provider.DBWordsProvider;
import pt.ipbeja.app.model.words_provider.WordsProvider;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Game model.
 */
public class WSModel {
    /**
     * A natural number representing the minimum acceptable length for a matrix side.
     */
    public static final int MIN_SIDE_LEN = 5;
    /**
     * A natural number representing the maximum acceptable length for a matrix side.
     */
    public static final int MAX_SIDE_LEN = 12;

    public static final @NotNull String INVALID_IN_GAME_CHANGE_MSG_ERR = "a game it's currently happening. Cannot perform this action";
    public static final @NotNull String NO_WORDS_MSG_ERR = "no words were given for the game to be able to start";
    public static final @NotNull String NOT_IN_GAME_ERR = "can't perform this action if a game hasn't started";
    private static final @NotNull String INVALID_SIDE_LEN_MSG_FORMAT = String.format("the %s provided is invalid! it needs to be a number between %d and %d", "%s", MIN_SIDE_LEN, MAX_SIDE_LEN);

    private final @NotNull Random random;
    /**
     * The allowed orientations a word can be found in game.
     */
    private final @NotNull Set<@NotNull WordOrientations> orientationsAllowed;
    /**
     * The number of lines in the matrix.
     */
    private int lines;
    /**
     * The number of columns in the matrix.
     */
    private int cols;
    /**
     * The matrix of {@link BaseCell}s.
     * <p>Can be a simple <code>BaseCell[lines*cols]</code> and to access a <code>BaseCell</code> in line <code>a</code>
     * and column <code>b</code> you just <code>this.matrix[a * this.cols + b]</code>.</p>
     *
     * @see BaseCell
     */
    private @Nullable BaseCell @NotNull [] @NotNull [] matrix;
    /**
     * The user interface. To use when we want to communicate with the player.
     */
    private @Nullable WSView view;
    /**
     * Set of valid words that came from the last database provided using {@link #setWords(WordsProvider)}.
     *
     * @see #setWords(WordsProvider)
     */
    private @Nullable Set<@NotNull String> words;
    /**
     * Subset of {@link #words} of the words that are currently on the matrix to be found.
     *
     * @see #wordsFound
     */
    private @Nullable Set<@NotNull String> wordsToFind;
    /**
     * Subset of {@link #words} of the words that are currently on the matrix, that were already found.
     *
     * @see #wordsToFind
     */
    private @Nullable Set<@NotNull String> wordsFound;
    /**
     * Represents if a game it's currently happening.
     */
    private boolean inGame;
    /**
     * Saves the position of the first click on a word selection.
     */
    private @Nullable Position startSelected;
    /**
     * A {@link ResultsSaver} that saves the game results when it ends the way it wants.
     *
     * @see ResultsSaver
     */
    private @Nullable ResultsSaver saver;
    /**
     * Maximum of words that can appear in a game.
     */
    private int maxWords;
    /**
     * Minimum length in characters that a word needs to be in the game.
     */
    private int minWordSize;

    /**
     * Creates the model for a words matrix game.
     *
     * <p>You will need to configure the model for a game to be able to start by using
     * {@link #setDimensions(int, int)} and {@link #setWords(WordsProvider)} for example.</p>
     *
     * @see #setDimensions(int, int)
     * @see #setLines(int)
     * @see #setCols(int)
     * @see #setWords(WordsProvider)
     * @see #setWords(WordsProvider, boolean)
     */
    public WSModel() {
        this.random = new Random();
        this.maxWords = 0;
        this.minWordSize = 1;
        this.orientationsAllowed = new TreeSet<>();
        this.orientationsAllowed.addAll(List.of(WordOrientations.VERTICAL, WordOrientations.HORIZONTAL));
        this.matrix = new BaseCell[0][];
    }

    /**
     * Creates the model for a words matrix game.
     *
     * <p>You will need to set the words for the game to use with {@link #setWords(WordsProvider)} to start a game.</p>
     *
     * @param lines initial number of lines for the game to have
     * @param cols  initial number of columns for the game to have
     * @see #setWords(WordsProvider)
     * @see #setWords(WordsProvider, boolean)
     */
    public WSModel(int lines, int cols) {
        this();
        try {
            this.setDimensions(lines, cols);
        } catch (InvalidInGameChangeException e) {
            // this will NEVER happen because a game can't be running right when you are creating the model.
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the model for a words matrix game.
     *
     * <p>You will need to set the dimensions of the matrix with {@link #setDimensions(int, int)} to start a game.</p>
     *
     * @param provider where the words used in game come from
     * @see #setDimensions(int, int)
     * @see #setLines(int)
     * @see #setCols(int)
     */
    public WSModel(@NotNull WordsProvider provider) {
        this();
        this.setWords(provider);
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines    initial number of lines for the game to have
     * @param cols     initial number of columns for the game to have
     * @param provider where the words used in game come from
     */
    public WSModel(int lines, int cols, @NotNull WordsProvider provider) {
        this(lines, cols);
        this.setWords(provider);
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have
     * @param cols  initial number of columns for the game to have
     * @param file  the database with the words to put in the game
     */
    public WSModel(int lines, int cols, @NotNull String file) {
        this(lines, cols, new DBWordsProvider(Paths.get(file).toFile()));
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have
     * @param cols  initial number of columns for the game to have
     * @param file  the database with the words to put in the game
     */
    public WSModel(int lines, int cols, @NotNull URI file) {
        this(lines, cols, new DBWordsProvider(Paths.get(file).toFile()));
    }

    /**
     * Creates the model for a words matrix game.
     *
     * @param lines initial number of lines for the game to have
     * @param cols  initial number of columns for the game to have
     * @param file  the database with the words to put in the game
     */
    public WSModel(int lines, int cols, @NotNull Path file) {
        this(lines, cols, new DBWordsProvider(file.toFile()));
    }

    /**
     * Gives a random latin alphabet character.
     *
     * @param random To be able to create pseudorandom numbers
     * @return The random character
     */
    public static char randomLatinCharacter(@NotNull Random random) {
        return (char) random.nextInt('A', 'Z' + 1);
    }

    private static void throwInvalidInGameChange() throws InvalidInGameChangeException {
        throw new InvalidInGameChangeException(INVALID_IN_GAME_CHANGE_MSG_ERR);
    }

    /**
     * Sets the dimensions of the matrix (both lines and columns). To set lines and/or columns separately use
     * {@link #setLines(int)} and {@link #setCols(int)}.
     *
     * @param lines The number of lines for the matrix to have
     * @param cols  The number of columns for the matrix to have
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game
     * @throws IllegalArgumentException     If dimensions provided aren't allowed
     * @see #setLines(int)
     * @see #setCols(int)
     */
    public void setDimensions(int lines, int cols) throws InvalidInGameChangeException {
        if (this.inGame) {
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
    }

    /**
     * @return The number of lines in the matrix.
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
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game
     * @throws IllegalArgumentException     If dimensions provided aren't allowed
     * @see #setCols(int)
     * @see #setDimensions(int, int)
     */
    public void setLines(int lines) throws InvalidInGameChangeException {
        if (this.inGame) {
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
    }

    /**
     * @return The number of columns in the matrix
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
     * @throws InvalidInGameChangeException In case of trying to change the dimensions mid-game
     * @throws IllegalArgumentException     If dimensions provided aren't allowed
     * @see #setLines(int)
     * @see #setDimensions(int, int)
     */
    public void setCols(int cols) throws InvalidInGameChangeException {
        if (this.inGame) {
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
    }

    /**
     * Method to define which words to use in the game via a {@link WordsProvider}.
     *
     * @param provider     Any {@link WordsProvider}
     * @param keepExistent Choose if you want to keep the existent words provided in the past
     * @see WordsProvider
     */
    public void setWords(@NotNull WordsProvider provider, boolean keepExistent) {
        if (!keepExistent || this.words == null) {
            this.words = new TreeSet<>();
        }

        String line;
        while ((line = provider.getLine()) != null) {
            String[] words = this.parseLine(line);
            Collections.addAll(this.words, words);
        }
    }

    /**
     * Parses a line with possible (supported) words.
     *
     * @param line Line to be parsed
     * @return An array of words found in the line
     */
    private @NotNull String @NotNull [] parseLine(String line) {
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

        return Arrays.stream(words).filter(s -> !s.isBlank()).toArray(String[]::new);
    }

    /**
     * Starts the game
     *
     * @throws NoWordsException If no words were given for the game to be able to start
     * @throws CouldNotPopulateMatrixException If for some reason it couldn't put any words into the matrix
     * @throws InvalidInGameChangeException If a game it's currently running
     * 
     * @see #initMatrix()
     * @see #setDimensions(int, int)
     * @see #setWords(WordsProvider, boolean) 
     */
    public void startGame() throws NoWordsException, CouldNotPopulateMatrixException, InvalidInGameChangeException {
        if (inGame) {
            throw new InvalidInGameChangeException(INVALID_IN_GAME_CHANGE_MSG_ERR);
        }

        this.initMatrix();
        this.inGame = true;
        this.wordsFound = new TreeSet<>();
        this.startSelected = null;

        if (this.view != null) {
            this.view.gameStarted();
        }
    }

    /**
     * Creates the matrix step by step:
     * <ol>
     *     <li>Creates an empty matrix;</li>
     *     <li>Puts a subset of words in {@link #words} into the matrix;</li>
     *     <li>Fills blank spaces with random characters;</li>
     *     <li>Adds some wild cards.</li>
     * </ol>
     * <p>Some of this steps can be changed in behaviour by configuration.</p>
     */
    private void initMatrix() throws NoWordsException, CouldNotPopulateMatrixException {
        this.initClearMatrix();
        this.populateMatrix();
        this.fillMatrix();
        this.createWildCards(this.random.nextInt(0, MIN_SIDE_LEN));
    }

    /**
     * Creates an empty matrix with {@link #lines} lines and {@link #cols} columns.
     *
     * @see #setDimensions(int, int)
     * @see #setLines(int)
     * @see #setCols(int)
     */
    private void initClearMatrix() {
        this.matrix = new BaseCell[this.lines][this.cols];
    }

    /**
     * Populates the matrix with the words provided by {@link #setWords(WordsProvider, boolean)}.
     *
     * @throws NoWordsException                In case no words were provided
     * @throws CouldNotPopulateMatrixException In case for some reason could not fit ANY of the words (that you can see
     *                                         using {@link CouldNotPopulateMatrixException#getWords()}) in the matrix.
     * @see #getGameWords()
     * @see #addWordVertically(String)
     * @see #addWordHorizontally(String)
     * @see #addWordDiagonally(String)
     * @see #setWords(WordsProvider)
     * @see #setWords(WordsProvider, boolean)
     */
    private void populateMatrix() throws NoWordsException, CouldNotPopulateMatrixException {
        this.wordsToFind = new TreeSet<>();

        if (this.words == null || this.words.isEmpty()) {
            throw new NoWordsException(NO_WORDS_MSG_ERR);
        }

        Set<String> words = this.getGameWords();
        for (String w : words) {
            List<WordOrientations> orientations = new ArrayList<>(this.orientationsAllowed);
            Collections.shuffle(orientations);

            boolean added = false;
            for (WordOrientations orientation : orientations) {
                try {
                    switch (orientation) {
                        case VERTICAL -> this.addWordVertically(w);
                        case HORIZONTAL -> this.addWordHorizontally(w);
                        case DIAGONAL -> this.addWordDiagonally(w);
                    }
                    added = true;
                    break;
                } catch (WordCanNotFitMatrixException ignored) {
                }
            }

            if (added) {
                this.wordsToFind.add(w);
            }
        }

        if (this.wordsToFind.isEmpty()) {
            throw new CouldNotPopulateMatrixException(words, this.lines, this.cols);
        }
    }

    /**
     * Selects a subset of {@link #words} to use in a game.
     *
     * @return That subset
     * @see #calculateUsableWords()
     * @see #setMaxWords(int)
     */
    private @NotNull Set<String> getGameWords() {
        Set<String> usableWords = this.calculateUsableWords();

        List<String> words = new ArrayList<>(usableWords);
        Collections.shuffle(words);

        int max = usableWords.size();
        if (this.maxWords != 0 && this.maxWords < max) {
            max = this.maxWords;
        }

        return new HashSet<>(words.subList(0, max));
    }

    /**
     * Calculates the usable words in {@link #words} based on model state.
     *
     * @return A set of usable words
     * @see #setWords(WordsProvider)
     * @see #setWords(WordsProvider, boolean)
     * @see #wordFitsGrid(String)
     * @see #setMinWordSize(int)
     */
    private @NotNull Set<String> calculateUsableWords() {
        if (this.words == null) {
            return new TreeSet<>();
        }

        Set<String> usableWords = new TreeSet<>();
        for (String w : words) {
            if (this.wordFitsGrid(w) && (this.minWordSize <= w.length())) {
                usableWords.add(w);
            }
        }
        return usableWords;
    }

    /**
     * Tests if a {@link String} word fits on the board based on the number of lines and columns.
     *
     * @param word The word to test
     * @return `true` if it fits
     */
    private boolean wordFitsGrid(@NotNull String word) {
        return this.wordFitsHorizontally(word) || this.wordFitsVertically(word);
    }

    /**
     * Tests if a {@link String} word fits on the board based on the number of columns.
     *
     * @param word The word to test
     * @return `true` if it fits
     */
    private boolean wordFitsHorizontally(@NotNull String word) {
        return word.length() <= this.cols;
    }

    /**
     * Tests if a {@link String} word fits on the board based on the number of lines.
     *
     * @param word The word to test
     * @return `true` if it fits
     */
    private boolean wordFitsVertically(@NotNull String word) {
        return word.length() <= this.lines;
    }

    /**
     * Adds a word vertically on the matrix.
     *
     * @param word The word that will try to add
     * @throws WordCanNotFitMatrixException In case the `word` couldn't be put in any way into the matrix like this.
     */
    private void addWordVertically(@NotNull String word) throws WordCanNotFitMatrixException {
        // Saves 'the x-y-direction' "tuple" of invalid combination for the word to be put on.
        Set<String> invalids = new TreeSet<>();

        // The number of places for the word to be able to be put on the matrix is:
        final int placesToTryToFit = this.cols * (this.lines - word.length() + 1) * 2;  // the last multiplication it's
        // because the word can be
        // written in both directions.
        while (invalids.size() < placesToTryToFit) {
            // FIXME: this combinations can be repeated
            final boolean direction = this.random.nextBoolean();
            final int walk = direction ? 1 : -1;
            int start = this.random.nextInt(0, this.lines - word.length() + 1);
            if (!direction) {
                start += word.length() - 1;
            }
            final int x = this.random.nextInt(0, this.cols);

            if (invalids.contains(x + ";" + start + ";" + direction)) {
                continue;
            }

            Set<Integer> sameDisplayPos = new TreeSet<>();
            Set<Integer> sameActualPos = new TreeSet<>();
            boolean invalid_pos = false;
            int overlapCounter = 0;
            for (int i = 0; i < word.length(); i++, start += walk) {
                char c = word.charAt(i);

                // The cell isn't empty and doesn't share the display with `c`. We need to abort the word insertion.
                if (this.matrix[start][x] != null && !this.matrix[start][x].hasSameDisplayAs(c)) {
                    while (i-- > 0) {
                        start -= walk;
                        if (!sameDisplayPos.contains(start)) {
                            this.matrix[start][x] = null;
                        } else if (!sameActualPos.contains(start)) {
                            this.matrix[start][x].removeActual(c);
                        }
                    }
                    invalid_pos = true;
                    break;
                }

                if (this.matrix[start][x] == null || !this.matrix[start][x].hasSameDisplayAs(c)) {
                    this.matrix[start][x] = new Cell(c);
                } else {
                    ++overlapCounter;
                    sameDisplayPos.add(start);
                    if (!this.matrix[start][x].addActual(c)) {
                        sameActualPos.add(start);
                    }
                }
            }

            // We do not want words on top of others.
            // TODO: This does not stop bigger words of beign inserted on top of smaller ones.
            if (!invalid_pos && (overlapCounter < word.length())) {
                return;
            }

            invalids.add(x + ";" + start + ";" + direction);
        }

        throw new WordCanNotFitMatrixException(word, this.lines, this.cols);
    }

    /**
     * Adds a word horizontally on the matrix.
     *
     * @param word The word that will try to add
     * @throws WordCanNotFitMatrixException In case the `word` couldn't be put in any way into the matrix like this.
     */
    private void addWordHorizontally(@NotNull String word) throws WordCanNotFitMatrixException {
        // Saves 'the x-y-direction' "tuple" of invalid combination for the word to be put on.
        Set<String> invalids = new TreeSet<>();

        // The number of places for the word to be able to be put on the matrix is:
        final int placesToTryToFit = this.lines * (this.cols - word.length() + 1) * 2;  // the last multiplication it's
        // because the word can be
        // written in both directions.
        while (invalids.size() < placesToTryToFit) {
            // FIXME: this combinations can be repeated
            final boolean direction = this.random.nextBoolean();
            final int walk = direction ? 1 : -1;
            int start = this.random.nextInt(0, this.cols - word.length() + 1);
            if (!direction) {
                start += word.length() - 1;
            }
            final int y = this.random.nextInt(0, this.lines);

            if (invalids.contains(start + ";" + y + ";" + direction)) {
                continue;
            }

            Set<Integer> same_display_pos = new TreeSet<>();
            Set<Integer> same_actual_pos = new TreeSet<>();
            boolean invalid_pos = false;
            int overlapCounter = 0;
            for (int i = 0; i < word.length(); i++, start += walk) {
                char c = word.charAt(i);

                // The cell isn't empty and doesn't share the display with `c`. We need to abort the word insertion.
                if (this.matrix[y][start] != null && !this.matrix[y][start].hasSameDisplayAs(c)) {
                    while (i-- > 0) {
                        start -= walk;
                        if (!same_display_pos.contains(start)) {
                            this.matrix[y][start] = null;
                        } else if (!same_actual_pos.contains(start)) {
                            this.matrix[y][start].removeActual(c);
                        }
                    }
                    invalid_pos = true;
                    break;
                }

                if (this.matrix[y][start] == null || !this.matrix[y][start].hasSameDisplayAs(c)) {
                    this.matrix[y][start] = new Cell(c);
                } else {
                    ++overlapCounter;
                    same_display_pos.add(start);
                    if (!this.matrix[y][start].addActual(c)) {
                        same_actual_pos.add(start);
                    }
                }
            }

            // We do not want words on top of others.
            // TODO: This does not stop bigger words of beign inserted on top of smaller ones.
            if (!invalid_pos && (overlapCounter < word.length())) {
                return;
            }

            invalids.add(start + ";" + y + ";" + direction);
        }

        throw new WordCanNotFitMatrixException(word, this.lines, this.cols);
    }

    /**
     * Adds a word diagonally on the matrix.
     *
     * @param word The word that will try to add
     * @throws WordCanNotFitMatrixException In case the `word` couldn't be put in any way into the matrix like this.
     */
    private void addWordDiagonally(@NotNull String word) throws WordCanNotFitMatrixException {
        // Saves 'the x-y-directionX-directionY' "tuple" of invalid combination for the word to be put on.
        Set<String> invalids = new TreeSet<>();

        // The number of places for the word to be able to be put on the matrix is:
        final int placesToTryToFit = (this.cols - word.length() + 1) * (this.lines - word.length() + 1) * 4;
        // the last multiplication it's because the word can be written in many directions.

        while (invalids.size() < placesToTryToFit) {
            // FIXME: this combinations can be repeated
            final boolean directionX = this.random.nextBoolean();
            int startX = this.random.nextInt(0, this.lines - word.length() + 1);
            if (!directionX) {
                startX += word.length() - 1;
            }
            boolean directionY = this.random.nextBoolean();
            int startY = this.random.nextInt(0, this.cols - word.length() + 1);
            if (!directionY) {
                startY += word.length() - 1;
            }

            if (invalids.contains(startX + ";" + startY + ";" + directionX + ";" + directionY)) {
                continue;
            }

            final int direction_walk = directionX ? 1 : -1;
            final int incline_walk = directionY ? 1 : -1;

            Set<Integer> same_display_pos = new TreeSet<>();
            Set<Integer> same_actual_pos = new TreeSet<>();
            boolean invalid_pos = false;
            int overlapCounter = 0;
            for (int i = 0; i < word.length(); i++, startX += direction_walk, startY += incline_walk) {
                char c = word.charAt(i);

                // The cell isn't empty and doesn't share the display with `c`. We need to abort the word insertion.
                if (this.matrix[startY][startX] != null && !this.matrix[startY][startX].hasSameDisplayAs(c)) {
                    while (i-- > 0) {
                        startX -= direction_walk;
                        startY -= incline_walk;
                        if (!same_display_pos.contains(startY)) {
                            this.matrix[startY][startX] = null;
                        } else if (!same_actual_pos.contains(startY)) {
                            this.matrix[startY][startX].removeActual(c);
                        }
                    }
                    invalid_pos = true;
                    break;
                }

                if (this.matrix[startY][startX] == null || !this.matrix[startY][startX].hasSameDisplayAs(c)) {
                    this.matrix[startY][startX] = new Cell(c);
                } else {
                    ++overlapCounter;
                    same_display_pos.add(startY);
                    if (!this.matrix[startY][startX].addActual(c)) {
                        same_actual_pos.add(startY);
                    }
                }
            }

            // We do not want words on top of others.
            // TODO: This does not stop bigger words of beign inserted on top of smaller ones.
            if (!invalid_pos && (overlapCounter < word.length())) {
                return;
            }

            invalids.add(startX + ";" + startY + ";" + directionX + ";" + directionY);
        }

        throw new WordCanNotFitMatrixException(word, this.lines, this.cols);
    }

    /**
     * Fills the empty spaces in the matrix with random latin alphabet characters.
     *
     * @see #randomLatinCharacter(Random)
     */
    private void fillMatrix() {
        for (int i = 0; i < this.lines; i++) {
            for (int j = 0; j < this.cols; j++) {
                if (this.matrix[i][j] == null) {
                    this.matrix[i][j] = new Cell(randomLatinCharacter(this.random));
                }
            }
        }
    }

    /**
     * Create wild cards in random cells of the matrix.
     *
     * @param n The number of wild cards to create
     */
    private void createWildCards(int n) {
        for (int i = 0; i < n; i++) {
            int x = this.random.nextInt(0, this.cols);
            int y = this.random.nextInt(0, this.lines);
            this.matrix[y][x] = WildCell.fromCell(this.matrix[y][x]);
        }
    }

    /**
     * Starts and ends a word find.
     * <p>The first use of this funtion will set the starting position of the word you want to find. The second use will
     * see if a word in start to end position it's a valid word to be found. Either way you can start finding another
     * word again.</p>
     * @param pos The position of the start or end of the word
     * @return `false` if a word wasn't found
     * @throws NotInGameException If trying to find a word while not in-game
     */
    public boolean findWord(@NotNull Position pos) throws NotInGameException {
        if (!this.inGame) {
            throw new NotInGameException(NOT_IN_GAME_ERR);
        }

        if (this.view != null) {
            this.view.update(new ClickMessage(pos, this.matrix[pos.line()][pos.col()].getDisplay()));
        }

        if (this.startSelected == null) {
            this.startSelected = pos;
            return true;
        }

        Position startPos = this.startSelected;
        this.startSelected = null;

        boolean found = false;
        for (String possibleWord : this.getPossibleWords(startPos, pos)) {
            String word = this.wordWithWildcardInBoard(possibleWord);
            if (word != null) {
                this.view.wordFound(startPos, pos);
                this.view.update(new WordFoundMessage(startPos, pos, word));
                this.view.update(new WordPointsMessage(word, 0 /*points*/));
                found = true;
            }
        }

        if (this.allWordsWereFound()) {
            this.endGame();
        }

        return found;
    }

    /**
     * Transforms the sequence of cells from start to end to a list of the possible words they can create.
     * @param start The start position
     * @param end The end position
     * @return The possible words in the board in those positions
     * 
     * @see #getPossibleWordsVertically(Position, Position) 
     * @see #getPossibleWordsHorizontally(Position, Position) 
     * @see #getPossibleWordsDiagonally(Position, Position) 
     */
    private @NotNull String @NotNull [] getPossibleWords(@NotNull Position start, @NotNull Position end) {
        if (start.col() == end.col()) {
            return this.getPossibleWordsVertically(start, end);
        } else if (start.line() == end.line()) {
            return this.getPossibleWordsHorizontally(start, end);
        } else {
            double slope = (1.0 * start.line() - end.line()) / (start.col() - end.col());
            if (Math.abs(slope) == 1) {
                return this.getPossibleWordsDiagonally(start, end);
            }
        }

        return new String[0];
    }

    private @NotNull String @NotNull [] getPossibleWordsVertically(@NotNull Position startPos, @NotNull Position endPos) {
        List<String> words = new ArrayList<>();
        words.add("");
        int start = Math.min(startPos.line(), endPos.line());
        int end = Math.max(startPos.line(), endPos.line());
        for (int i = start; i <= end; i++) {
            BaseCell[] line = this.matrix[i];
            String[] bases = words.toArray(String[]::new);
            words.clear();
            for (char actual : line[endPos.col()].getActuals()) {
                for (String base : bases) {
                    words.add(base + actual);
                }
            }
        }
        return words.toArray(String[]::new);
    }

    private @NotNull String @NotNull [] getPossibleWordsHorizontally(@NotNull Position startPos, @NotNull Position endPos) {
        List<String> words = new ArrayList<>();
        words.add("");
        BaseCell[] line = this.matrix[endPos.line()];
        int start = Math.min(startPos.col(), endPos.col());
        int end = Math.max(startPos.col(), endPos.col());
        for (int i = start; i <= end; i++) {
            String[] bases = words.toArray(String[]::new);
            words.clear();
            for (char actual : line[i].getActuals()) {
                for (String base : bases) {
                    words.add(base + actual);
                }
            }
        }
        return words.toArray(String[]::new);
    }

    private @NotNull String @NotNull [] getPossibleWordsDiagonally(@NotNull Position startPos, @NotNull Position endPos) {
        List<String> words = new ArrayList<>();
        words.add("");
        double slope = (1.0 * startPos.line() - endPos.line()) / (startPos.col() - endPos.col());
        int startX = Math.min(startPos.col(), endPos.col());
        int startY = Math.min(startPos.line(), endPos.line());
        int endY = Math.max(startPos.line(), endPos.line());
        if (slope == 1) {
            for (int i = startY; i <= endY; i++) {
                BaseCell[] line = this.matrix[i];
                String[] bases = words.toArray(String[]::new);
                words.clear();
                for (char actual : line[startX].getActuals()) {
                    for (String base : bases) {
                        words.add(base + actual);
                    }
                }
                startX++;
            }
        } else if (slope == -1) {
            for (int i = endY; i >= startY; i--) {
                BaseCell[] line = this.matrix[i];
                String[] bases = words.toArray(String[]::new);
                words.clear();
                for (char actual : line[startX].getActuals()) {
                    for (String base : bases) {
                        words.add(base + actual);
                    }
                }
                startX++;
            }
        }
        return words.toArray(String[]::new);
    }

    /**
     * Checks if the word is in the board.
     * @param word The word or itself in reverse
     * @return The word
     * @throws NotInGameException If a game isn't happening
     */
    public @Nullable String wordInBoard(@NotNull String word) throws NotInGameException {
        if (!this.inGame) {
            throw new NotInGameException(NOT_IN_GAME_ERR);
        }

        if (this.wordsToFind == null || this.wordsFound == null) {
            throw new NotInGameException(NOT_IN_GAME_ERR);
        }

        word = word.toUpperCase();

        // https://stackoverflow.com/questions/7569335/reverse-a-string-in-java
        String reversed = new StringBuilder(word).reverse().toString();
        if (this.wordsToFind.contains(word)) {
            this.wordsToFind.remove(word);
            this.wordsFound.add(word);
            return word;
        } else if (this.wordsToFind.contains(reversed)) {
            this.wordsToFind.remove(reversed);
            this.wordsFound.add(reversed);
            return reversed;
        } else {
            return null;
        }
    }

    /**
     * Checks if the word is in the board.
     * <p>Because of how {@link BaseCell works}, there's no need to have a separated method like this. But because it
     * was present in the projects base, I'll keep it.</p>
     * @param word The word or itself in reverse
     * @return The word
     * @throws NotInGameException If a game isn't happening
     *
     * @see #wordInBoard(String)
     */
    public @Nullable String wordWithWildcardInBoard(@NotNull String word) throws NotInGameException {
        return this.wordInBoard(word);
    }

    /**
     * Ends the game and tells the {@link #view} about it, and the {@link #saver} to save the game results.
     * @return The game results
     *
     * @see #curGameResults()
     */
    public @NotNull GameResults endGame() {
        this.inGame = false;
        GameResults res = this.curGameResults();
        if (this.view != null) {
            this.view.gameEnded(res);
        }
        if (saver != null) {
            this.saver.save(res);
        }
        return res;
    }

    /**
     * @return The current game results
     */
    public @NotNull GameResults curGameResults() {
        if (this.wordsToFind == null || this.wordsFound == null) {
            return new GameResults(new TreeSet<>(), new TreeSet<>());
        }

        Set<String> union = new TreeSet<>(this.wordsToFind);
        union.addAll(this.wordsFound);
        return new GameResults(union, this.wordsFound);
    }

    /**
     * Adds the {@link WSView} that the model will use.
     * @param wsView The view
     */
    public void registerView(WSView wsView) {
        this.view = wsView;
    }

    /**
     * Get the text in a position
     *
     * @param position position
     * @return the text in the position
     */
    public BaseCell textInPosition(@NotNull Position position) {
        return this.matrix[position.line()][position.col()];
    }

    /**
     * Check if all words were found
     *
     * @return true if all words were found
     */
    public boolean allWordsWereFound() throws NotInGameException {
        if (!this.inGame) {
            throw new NotInGameException();
        }
        return this.wordsToFind == null || this.wordsToFind.isEmpty();
    }

    public int wordsInUse() {
        if (this.wordsToFind != null && this.wordsFound != null) {
            return this.wordsToFind.size() + this.wordsFound.size();
        }
        return 0;
    }

    public void setSaver(@NotNull ResultsSaver saver) {
        this.saver = saver;
    }

    public void setMaxWords(int maxWords) {
        assert maxWords > 0;
        this.maxWords = maxWords;
    }

    public void setMinWordSize(int minWordSize) {
        assert minWordSize > 0;
        this.minWordSize = minWordSize;
    }

    public @NotNull String matrixToString() {
        StringBuilder matrix = new StringBuilder();
        int size = 0;

        for (BaseCell[] cells : this.matrix) {
            size = cells.length;
            matrix.append('+').append(Arrays.stream(new String[size]).map(s -> "---").collect(Collectors.joining("+"))).append("+\n");
            for (BaseCell cell : cells) {
                matrix.append("| ").append(cell != null ? cell.getDisplay() : ' ').append(' ');
            }
            matrix.append("|\n");
        }
        matrix.append('+').append(Arrays.stream(new String[size]).map(s -> "---").collect(Collectors.joining("+"))).append("+\n");

        return matrix.toString();
    }

    public boolean gameEnded() {
        return this.wordsToFind == null || this.wordsToFind.isEmpty();
    }

    public void allowWordOrientation(WordOrientations... orientations) {
        this.orientationsAllowed.addAll(List.of(orientations));
    }

    public void disallowWordOrientation(WordOrientations... orientations) {
        List.of(orientations).forEach(this.orientationsAllowed::remove);
    }

    public @Nullable Set<String> getWords() {
        return this.words;
    }

    /**
     * Method to define which words to use in the game via a {@link WordsProvider}.
     *
     * @param provider Any {@link WordsProvider}
     * @see WordsProvider
     */
    public void setWords(@NotNull WordsProvider provider) {
        this.setWords(provider, true);
    }
}
