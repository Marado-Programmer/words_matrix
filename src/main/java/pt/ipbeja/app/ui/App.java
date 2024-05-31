package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipbeja.app.model.*;
import pt.ipbeja.app.model.wordsprovider.DBWordsProvider;
import pt.ipbeja.app.model.wordsprovider.ManualWordsProvider;
import pt.ipbeja.app.model.wordsprovider.WordsProvider;
import pt.ipbeja.app.throwables.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;

/**
 * The UI for the game
 */
public class App extends VBox implements WSView {
    private static final double PERCENT = 100.0;
    private static final String SCORES_FILE = "scores.txt";
    private static final String FX_BACKGROUND_COLOR_GREEN = "-fx-background-color: green";
    private final WSModel model;

    private final MenuBar menuBar;
    private final Game game;
    private final Menu menu;

    /**
     * @param stage The JavaFX stage
     */
    public App(Stage stage) {
        super();
        this.model = new WSModel();
        this.model.registerView(this);

        this.game = new Game(this, this.model);
        // https://stackoverflow.com/questions/28558165/javafx-setvisible-hides-the-element-but-doesnt-rearrange-adjacent-nodes
        this.game.managedProperty().bind(this.game.visibleProperty());
        this.game.setVisible(false);

        this.menu = new Menu((opts, mode) -> this.getOnStartHandler(stage, opts, mode),
                this.model.getOptions());

        this.menu.managedProperty().bind(this.menu.visibleProperty());

        this.menuBar = new MenuBar(stage, this.model);

        HBox centerH = new HBox(this.game, this.menu);
        VBox centerV = new VBox(centerH);
        centerH.setAlignment(Pos.CENTER);
        centerV.setAlignment(Pos.CENTER);
        this.getChildren().addAll(this.menuBar, centerV);
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/VBox.html
        VBox.setVgrow(centerV, Priority.ALWAYS);

        this.model.setSaver(this::save);
    }

    private void getOnStartHandler(Stage stage, GameOptions opts, Menu.ProviderMode mode)
            throws InvalidInGameChangeException {
        WordsProvider provider = getProviderFromMode(mode, stage);
        opts.setProvider(provider);
        opts.setKeepExistent(false);

        this.model.setOptions(opts);
        this.tryToStartGame();
    }

    private static void writeScore(GameResults res, BufferedWriter writer) throws IOException {
        writer.write(String.format(
                "%.2f%%\n",
                PERCENT * (double) res.words_found().size() / (double) res.words().size()
        ));
    }

    private static WordsProvider getProviderFromMode(Menu.ProviderMode mode, Stage stage) {
        FileChooser fileChooser = new FileChooser(stage);
        ProvideWordDialog dialog = new ProvideWordDialog();

        return switch (mode) {
            case DB -> getWordsProviderFromChoosingDB(fileChooser);
            case MANUAL -> getWordsProviderFromProvidingWords(dialog);
        };
    }

    private static ManualWordsProvider getWordsProviderFromProvidingWords(ProvideWordDialog dialog) {
        ManualWordsProvider provider = new ManualWordsProvider();

        while (provider.isOpen()) {
            Optional<String> result = dialog.showAndWait();
            result.ifPresentOrElse(provider::provide, provider::close);
        }
        return provider;
    }

    private static WordsProvider getWordsProviderFromChoosingDB(FileChooser fileChooser) {
        while (true) {
            try {
                File choose = fileChooser.choose();
                return new DBWordsProvider(choose);
            } catch (IOException ignored) {
            } catch (RuntimeException e) {
                ManualWordsProvider provider = new ManualWordsProvider();
                provider.close();
                return provider;
            }
        }
    }

    private void tryToStartGame() {
        int tries = WSModel.MIN_SIDE_LEN;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(StartWordSearch.TITLE);
        alert.setHeaderText("ERROR");

        while (0 < tries) {
            try {
                this.game.allowReplay(true);
                this.model.startGame();
                return;
            } catch (RuntimeException e) {
                alert.setContentText(e.toString());
                alert.showAndWait();
                System.err.println(Arrays.toString(e.getStackTrace()));
                return;
            } catch (InvalidInGameChangeException e) {
                throw new RuntimeException(e);
            } catch (CouldNotPopulateMatrixException e) {
                tries--;
            } catch (NoDimensionsDefinedException e) {
                alert.setContentText("No dimensions were given, can't start the game");
                alert.showAndWait();
                return;
            } catch (NoWordsException e) {
                alert.setContentText("No valid words were given so the game could not start.\n" +
                        "Valid words are words with latin characters with the minimum length defined in the menu " +
                        "and need to fit in a matrix with the given dimensions");
                alert.showAndWait();
                return;
            }

            alert.setContentText("Tried to start the game but could no make it in " + WSModel.MIN_SIDE_LEN + " tries." +
                    "\nTry to change the game options to some more realistic ones");
            alert.showAndWait();
        }
    }

//help

    /**
     * Simply updates the text for the buttons in the received positions
     *
     * @param messageToUI the WS model
     */
    @Override
    public void update(MessageToUI messageToUI) {
        this.game.log(messageToUI.getMessage() + "\n");
    }

    @Override
    public void updatePoints(Word word) {
        this.game.points(word);
    }

    @Override
    public void gameStarted() {
        this.game.getBoard().buildGUI();
        this.game.setVisible(true);
        this.menu.setVisible(false);
        this.game.resetGameLog();
        this.menuBar.permitHints(true);
    }

    @Override
    public void wordFound(Position start, Position end) {
        double slope = ((double) start.line() - (double) end.line()) / (double) (start.col() - end.col());
        if (start.line() == end.line()) {
            this.wordFoundHorizontally(start, end);
        } else if (start.col() == end.col()) {
            this.wordFoundVertically(start, end);
        } else if (1.0 == Math.abs(slope)) {
            this.wordFoundDiagonally(start, end, slope);
        }

        this.game.getBoard().unselectAll();
    }

    private void wordFoundDiagonally(Position start, Position end, double slope) {
        int startX = Math.min(start.col(), end.col());
        int startY = Math.min(start.line(), end.line());
        int endY = Math.max(start.line(), end.line());
        if (1.0 == slope) {
            for (int i = startY; i <= endY; i++) {
                CellButton btn = this.game.getBoard().getButton(i, startX);
                btn.setStyle(FX_BACKGROUND_COLOR_GREEN);
                btn.setPartOfWord(true);
                startX++;
            }
        } else {
            for (int i = endY; i >= startY; i--) {
                CellButton btn = this.game.getBoard().getButton(i, startX);
                btn.setStyle(FX_BACKGROUND_COLOR_GREEN);
                btn.setPartOfWord(true);
                startX++;
            }
        }
    }

    private void wordFoundVertically(Position start, Position end) {
        int startPos = Math.min(start.line(), end.line());
        int endPos = Math.max(start.line(), end.line());
        for (int i = startPos; i <= endPos; i++) {
            CellButton btn = this.game.getBoard().getButton(i, end.col());
            btn.setStyle(FX_BACKGROUND_COLOR_GREEN);
            btn.setPartOfWord(true);
        }
    }

    private void wordFoundHorizontally(Position start, Position end) {
        int startPos = Math.min(start.col(), end.col());
        int endPos = Math.max(start.col(), end.col());
        for (int i = startPos; i <= endPos; i++) {
            CellButton btn = this.game.getBoard().getButton(end.line(), i);
            btn.setStyle(FX_BACKGROUND_COLOR_GREEN);
            btn.setPartOfWord(true);
        }
    }

    @Override
    public void gameEnded(GameResults res) {
        assert null != res.words_found() && null != res.words();
        this.menuBar.permitHints(false);
        Platform.runLater(() -> this.decideWhatIsNext(res));
    }

    private void decideWhatIsNext(GameResults res) {
        String resultsString = resultsString(res);

        this.game.log(resultsString);
        GameEndedAlert dialog = new GameEndedAlert(this, resultsString);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            switch (result.get().getButtonData()) {
                case BACK_PREVIOUS -> {
                    this.game.allowReplay(false);
                    this.model.replay();
                }
                case NEXT_FORWARD -> {
                    try {
                        moreWordsQuestion();
                        this.model.startGame();
                    } catch (NoWordsException | CouldNotPopulateMatrixException | InvalidInGameChangeException |
                             NoDimensionsDefinedException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> {
                    this.game.setVisible(false);
                    this.menu.setVisible(true);
                }
            }
        } else {
            this.game.setVisible(false);
            this.menu.setVisible(true);
        }
    }

    private static String resultsString(GameResults res) {
        StringBuilder builder = new StringBuilder();
        builder.append("\twords found:\t")
                .append(res.words_found().size())
                .append("\n\ttotal of words:\t")
                .append(res.words().size())
                .append("\n\tpercentage of words found:\t")
                .append(String.format(
                        "%.2f%%\n", PERCENT * (double) res.words_found().size() / (double) res.words().size()
                ))
                .append("\n")
                .append("words in game:\n");

        for (String word : res.words()) {
            builder.append("\t")
                    .append(res.words_found().contains(word) ? "+" : "-")
                    .append(" ")
                    .append(word)
                    .append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    private static void moreWordsQuestion() {
        ProvideWordDialog dialog = new ProvideWordDialog();
        ConfirmationAlert alert = new ConfirmationAlert(
                "Add words",
                "Would you like to add more words to the game?",
                () -> getWordsProviderFromProvidingWords(dialog)
        );
        alert.showAlert();
    }

    /**
     * Creates the current game log
     */
    void saveGameLog() {
        // https://stackoverflow.com/questions/732034/getting-unixtime-in-java
        long now = System.currentTimeMillis();
        try (BufferedWriter writer = Files.newBufferedWriter(
                this.getLogDir().resolve("log_" + now + ".md")
        )) {
            writer.write(String.format(
                    "# Date\n%d\n\n# Matrix\n%s\n# Logs\n```\n%s```\n",
                    now,
                    this.matrixToString(),
                    this.game.getLog()
            ));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void click(Position pos) {
        Platform.runLater(() -> this.clickRunnable(pos));
    }

    private void clickRunnable(Position pos) {
        try {
            String word = this.model.findWord(pos);
            if (null != word) {
                if (word.isEmpty()) {
                    this.game.getBoard().unselectAll();
                    this.game.getBoard().getButton(pos).setStyle("-fx-background-color: yellow;");
                }
            } else {
                this.game.getBoard().unselectAll();
            }
        } catch (NotInGameException ignored) {
        }
    }

    private Path getLogDir() {
        return this.menuBar.getLogDir();
    }

    private String matrixToString() {
        return this.model.matrixToString();
    }

    private void save(GameResults res) {
        Path dir = this.menuBar.getScoreDir();
        try (BufferedWriter writer = Files.newBufferedWriter(
                dir.resolve(SCORES_FILE),
                StandardOpenOption.APPEND
        )) {
            writeScore(res, writer);
        } catch (NoSuchFileException e) {
            try (BufferedWriter writer = Files.newBufferedWriter(dir.resolve(SCORES_FILE))) {
                writeScore(res, writer);
            } catch (IOException ignored) {
            }
        } catch (IOException ignored) {
        }
    }
}
