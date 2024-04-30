package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.*;
import pt.ipbeja.app.model.words_provider.DBWordsProvider;
import pt.ipbeja.app.model.words_provider.ManualWordsProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static pt.ipbeja.app.ui.StartWordSearch.TITLE;

public class App extends VBox implements WSView {
    private final @NotNull WSModel model;

    private final @NotNull Game game;
    private final @NotNull Menu menu;

    public App(Stage stage) {
        this.model = new WSModel();
        this.model.registerView(this);

        this.game = new Game(this.model);
        // https://stackoverflow.com/questions/28558165/javafx-setvisible-hides-the-element-but-doesnt-rearrange-adjacent-nodes
        this.game.managedProperty().bind(this.game.visibleProperty());
        this.game.setVisible(false);
        this.menu = new Menu((lines, cols, mode) -> {
            this.model.setLines(lines);
            this.model.setCols(cols);

            switch (mode) {
                case DB -> this.model.setWordsProvider(new DBWordsProvider(new FileChooser(stage).choose()));
                case MANUAL -> {
                    ManualWordsProvider provider = new ManualWordsProvider();

                    while (!provider.isClosed()) {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle(TITLE);
                        dialog.setHeaderText("Manual Words Provider");
                        dialog.setContentText("Provide a new word for the game to use:");
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresentOrElse(provider::provide, provider::close);
                    }

                    this.model.setWordsProvider(provider);
                }
                default -> throw new RuntimeException();
            }

            this.model.startGame();
        });
        this.menu.managedProperty().bind(this.menu.visibleProperty());

        HBox center = new HBox(this.game, this.menu);
        this.setAlignment(Pos.CENTER);
        center.setAlignment(Pos.CENTER);
        this.getChildren().add(center);

        System.out.println(Paths.get(".").resolve("scores.txt"));
        model.setSaver(res -> {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("").resolve("scores.txt"), StandardOpenOption.APPEND)) {
                writer.write(String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()));
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
    public void update(@NotNull MessageToUI messageToUI) {
        for (Position p : messageToUI.positions()) {
            Cell s = this.model.textInPosition(p);
            this.game.getBoard().getButton(p.line(), p.col()).setText(String.valueOf(s.letter()));
        }
        if (this.model.allWordsWereFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.setContentText("Level completed!");
            alert.showAndWait();
            System.exit(0);
        }
    }

    @Override
    public void gameStarted() {
        this.game.getBoard().buildGUI();
        this.game.setVisible(true);
        this.menu.setVisible(false);
    }

    @Override
    public void wordFound(@NotNull Position start, @NotNull Position end) {
        if (start.line() == end.line()) {
            int start_pos = Math.min(start.col(), end.col());
            int end_pos = Math.max(start.col(), end.col());
            for (int i = start_pos; i <= end_pos; i++) {
                Button btn = this.game.getBoard().getButton(end.line(), i);
                btn.setBackground(Background.fill(Color.GREEN));
            }
        } else if (start.col() == end.col()) {
            int start_pos = Math.min(start.line(), end.line());
            int end_pos = Math.max(start.line(), end.line());
            for (int i = start_pos; i <= end_pos; i++) {
                Button btn = this.game.getBoard().getButton(i, end.col());
                btn.setBackground(Background.fill(Color.GREEN));
            }
        }

        this.model.gameEnded();
    }

    @Override
    public void gameEnded(@NotNull GameResults res) {
        assert res.words_found() != null && res.words() != null;

        this.game.log("\t" + res.words_found().size() + "\n\t" + res.words().size() + "\n\t" + String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()) + "\n");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TITLE);
        alert.setHeaderText("Game ended");
        alert.setContentText("Do you want to play again (same configurations)?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.model.startGame();
        } else {
            this.game.setVisible(false);
            this.menu.setVisible(true);
        }
    }
}
