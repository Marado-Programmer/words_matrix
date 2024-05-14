package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.WSModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Game extends HBox {
    private final @NotNull App app;
    private final @NotNull WSBoard board;
    private final @NotNull TextArea log;

    private StringBuilder gameLog;

    public Game(@NotNull App app, @NotNull WSModel model) {
        this.app = app;
        Button end = new Button("End Game Now");
        end.setOnAction(event -> model.endGame());

        this.board = new WSBoard(model);
        this.board.requestFocus();

        this.log = new TextArea("STATUS:\n");

        VBox board = new VBox(this.board, end);
        board.setAlignment(Pos.CENTER);
        Button saveLog = getSaveLog();
        VBox log = new VBox(saveLog, this.log);
        this.getChildren().addAll(board, log);
        this.setAlignment(Pos.CENTER);

        this.gameLog = new StringBuilder();
    }

    @NotNull
    private Button getSaveLog() {
        Button saveLog = new Button("Save current game log");
        saveLog.setOnAction(event -> {
            // https://stackoverflow.com/questions/732034/getting-unixtime-in-java
            long now = System.currentTimeMillis();
            try (BufferedWriter writer = Files.newBufferedWriter(
                    this.app.getLogDir().resolve("log_" + now + ".md")
            )) {
                writer.write(String.format(
                        "# Date\n%d\n\n# Matrix\n%s\n# Logs\n```\n%s```\n",
                        now,
                        this.app.matrixToString(),
                        this.gameLog.toString()
                ));
            } catch (IOException ignored) {
            }
        });
        return saveLog;
    }

    public @NotNull WSBoard getBoard() {
        return board;
    }

    public void log(String msg) {
        this.log.appendText(msg);
        this.gameLog.append(msg);
    }

    public void resetGameLog() {
        this.gameLog = new StringBuilder();
    }
}
