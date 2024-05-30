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
import pt.ipbeja.app.model.message_to_ui.MessageToUI;
import pt.ipbeja.app.model.throwables.*;
import pt.ipbeja.app.model.words_provider.DBWordsProvider;
import pt.ipbeja.app.model.words_provider.ManualWordsProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;

import static pt.ipbeja.app.model.WSModel.MIN_SIDE_LEN;
import static pt.ipbeja.app.ui.StartWordSearch.TITLE;

public class App extends VBox implements WSView {
    private final WSModel model;

    private final MenuBar bar;
    private final Game game;
    private final Menu menu;

    public App(Stage stage) {
        this.model = new WSModel();
        this.model.registerView(this);

        this.game = new Game(this, this.model);
        // https://stackoverflow.com/questions/28558165/javafx-setvisible-hides-the-element-but-doesnt-rearrange-adjacent-nodes
        this.game.managedProperty().bind(this.game.visibleProperty());
        this.game.setVisible(false);
        this.menu = new Menu((GameOptions opts, Menu.ProviderMode mode) -> {
            opts.setProvider(switch (mode) {
                case DB -> new DBWordsProvider(new FileChooser(stage).choose());
                case MANUAL -> {
                    ManualWordsProvider provider = new ManualWordsProvider();

                    while (!provider.isClosed()) {
                        ProvideWordDialog dialog = new ProvideWordDialog();
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresentOrElse(provider::provide, provider::close);
                    }

                    yield provider;
                }
            });
            opts.setKeepExistent(false);

            try {
                this.model.setOptions(opts);
            } catch (InvalidInGameChangeException e) {
                throw new RuntimeException(e);
                // TODO: handle the error better.
            }

            int tries = MIN_SIDE_LEN;
            while (tries > 0) {
                try {
                    this.model.startGame();
                    break;
                } catch (RuntimeException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(TITLE);
                    alert.setHeaderText("ERROR");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                    System.err.println(Arrays.toString(e.getStackTrace()));
                    break;
                } catch (NoWordsException | NoDimensionsDefinedException e) {
                    break;
                } catch (InvalidInGameChangeException e) {
                    throw new RuntimeException(e);
                } catch (CouldNotPopulateMatrixException e) {
                    tries--;
                }
            }
        }, this.model.getOptions());
        this.menu.managedProperty().bind(this.menu.visibleProperty());

        this.bar = new MenuBar(stage, model);

        HBox centerH = new HBox(this.game, this.menu);
        VBox centerV = new VBox(centerH);
        centerH.setAlignment(Pos.CENTER);
        centerV.setAlignment(Pos.CENTER);
        this.getChildren().addAll(this.bar, centerV);
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/VBox.html
        VBox.setVgrow(centerV, Priority.ALWAYS);

        model.setSaver(res -> {
            Path dir = this.bar.getScoreDir();
            try (BufferedWriter writer = Files.newBufferedWriter(
                    dir.resolve("scores.txt"),
                    StandardOpenOption.APPEND
            )) {
                writer.write(String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()));
            } catch (NoSuchFileException e) {
                try (BufferedWriter writer = Files.newBufferedWriter(dir.resolve("scores.txt"))) {
                    writer.write(String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()));
                } catch (IOException ignored) {
                }
            } catch (IOException ignored) {
            }
        });
    }

    //play
    //options
    //quit
    //language
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
    }

    @Override
    public void wordFound(Position start, Position end) {
        double slope = (1.0 * start.line() - end.line()) / (start.col() - end.col());
        if (start.line() == end.line()) {
            int start_pos = Math.min(start.col(), end.col());
            int end_pos = Math.max(start.col(), end.col());
            for (int i = start_pos; i <= end_pos; i++) {
                CellButton btn = this.game.getBoard().getButton(end.line(), i);
                btn.setStyle("-fx-background-color: green");
                btn.setPartOfWord(true);
            }
        } else if (start.col() == end.col()) {
            int start_pos = Math.min(start.line(), end.line());
            int end_pos = Math.max(start.line(), end.line());
            for (int i = start_pos; i <= end_pos; i++) {
                CellButton btn = this.game.getBoard().getButton(i, end.col());
                btn.setStyle("-fx-background-color: green");
                btn.setPartOfWord(true);
            }
        } else if (Math.abs(slope) == 1) {
            int startX = Math.min(start.col(), end.col());
            int startY = Math.min(start.line(), end.line());
            int endY = Math.max(start.line(), end.line());
            if (slope == 1) {
                for (int i = startY; i <= endY; i++) {
                    CellButton btn = this.game.getBoard().getButton(i, startX);
                    btn.setStyle("-fx-background-color: green");
                    btn.setPartOfWord(true);
                    startX++;
                }
            } else {
                for (int i = endY; i >= startY; i--) {
                    CellButton btn = this.game.getBoard().getButton(i, startX);
                    btn.setStyle("-fx-background-color: green");
                    btn.setPartOfWord(true);
                    startX++;
                }
            }
        }

        this.game.getBoard().unselectAll();
    }

    @Override
    public void gameEnded(GameResults res) {
        assert res.words_found() != null && res.words() != null;
        Platform.runLater(() -> {
            String resultsString = this.resultsString(res);

            this.game.log(resultsString);
            GameEndedAlert dialog = new GameEndedAlert(this, resultsString);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent()) {
                switch (result.get().getButtonData()) {
                    case BACK_PREVIOUS -> this.model.replay();
                    case NEXT_FORWARD -> {
                        try {
                            this.moreWordsQuestion();
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
        });
    }

    private String resultsString(GameResults res) {
        StringBuilder s = new StringBuilder();
        s.append("\twords found:\t")
                .append(res.words_found().size())
                .append("\n\ttotal of words:\t")
                .append(res.words().size())
                .append("\n\tpercentage of words found:\t")
                .append(String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()))
                .append("\n")
                .append("words in game:\n");

        for (String word : res.words()) {
            s.append("\t")
                    .append(res.words_found().contains(word) ? "+" : "-")
                    .append(" ")
                    .append(word)
                    .append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    private void moreWordsQuestion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TITLE);
        alert.setHeaderText("Add words");
        alert.setContentText("Would you like to add more words to the game?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            ManualWordsProvider provider = new ManualWordsProvider();

            while (!provider.isClosed()) {
                ProvideWordDialog dialog = new ProvideWordDialog();
                dialog.showAndWait().ifPresentOrElse(provider::provide, provider::close);
            }

            this.model.setWords(provider, true);
        }
    }

    public void saveGameLog() {
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
        Platform.runLater(() -> {
            try {
                String word = model.findWord(pos);
                if (word != null) {
                    if (word.isEmpty()) {
                        this.game.getBoard().getButton(pos).setStyle("-fx-background-color: yellow;");
                    }
                } else {
                    this.game.getBoard().unselectAll();
                }
            } catch (NotInGameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Path getLogDir() {
        return this.bar.getLogDir();
    }

    public String matrixToString() {
        return this.model.matrixToString();
    }
}
