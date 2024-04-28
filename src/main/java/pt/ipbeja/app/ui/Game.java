package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.WSModel;

public class Game extends VBox {
    public @NotNull WSBoard getBoard() {
        return board;
    }

    private final @NotNull WSBoard board;

    public Game(@NotNull WSModel model) {
        Button end = new Button("End Game Now");
        end.setOnAction(event -> {
            model.endGame();
        });

        this.board = new WSBoard(model);
        this.board.requestFocus();

        this.getChildren().addAll(this.board, end);
        this.setAlignment(Pos.CENTER);
    }


}
