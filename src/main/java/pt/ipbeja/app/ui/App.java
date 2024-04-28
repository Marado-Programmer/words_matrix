package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;

public class App extends VBox implements WSView {
    private final @NotNull WSModel model;

    private final @NotNull Game game;

    public App(Stage stage) {
        this.model = new WSModel();
        this.model.registerView(this);

        this.game = new Game(this.model);
        Menu menu = new Menu((lines, cols) -> {
            this.model.setLines(lines);
            this.model.setCols(cols);

            this.model.setWordsProvider(new DBWordsProvider(new FileChooser(stage).choose()));

            this.model.startGame();
        });

        HBox center = new HBox(
                this.game,
                menu
        );
        this.setAlignment(Pos.CENTER);
        center.setAlignment(Pos.CENTER);
        this.getChildren().add(center);

        System.out.println(Paths.get(".").resolve("scores.txt"));
        model.setSaver(res -> {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get("").resolve("scores.txt"),
                    StandardOpenOption.APPEND
            )) {
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

        System.out.println(res.words_found().size());
        System.out.println(res.words().size());
        System.out.printf("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size());
    }
}
