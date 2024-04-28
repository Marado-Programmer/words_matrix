package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.WSModel;

public class Game extends HBox {
    private final @NotNull WSBoard board;
    private final @NotNull TextArea log;

    public Game(@NotNull WSModel model) {
        Button end = new Button("End Game Now");
        end.setOnAction(event -> model.endGame());

        this.board = new WSBoard(model);
        this.board.requestFocus();

        this.log = new TextArea("STATUS:\n");

        VBox board = new VBox(this.board, end);
        board.setAlignment(Pos.CENTER);

        this.getChildren().addAll(board, this.log);
        this.setAlignment(Pos.CENTER);
    }

    public @NotNull WSBoard getBoard() {
        return board;
    }

    public void log(String msg) {
        this.log.appendText(msg);
    }
}
