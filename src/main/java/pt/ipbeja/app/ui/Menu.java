package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import static pt.ipbeja.app.model.WSModel.MAX_SIDE_LEN;
import static pt.ipbeja.app.model.WSModel.MIN_SIDE_LEN;

public class Menu extends VBox {
    public Menu(@NotNull OnStartHandler handler) {
        Button btn = new Button("Start");
        NumberInput lines = new NumberInput("lines:\t", MAX_SIDE_LEN);
        lines.setMin(MIN_SIDE_LEN);
        lines.setMax(MAX_SIDE_LEN);
        NumberInput columns = new NumberInput("columns:\t", MAX_SIDE_LEN);
        columns.setMin(MIN_SIDE_LEN);
        columns.setMax(MAX_SIDE_LEN);
        btn.setOnAction(event -> handler.onStart(lines.getVal(), columns.getVal()));

        this.getChildren().addAll(lines, columns, btn);
        this.setAlignment(Pos.CENTER);
    }

    public interface OnStartHandler {
        void onStart(int lines, int cols);
    }
}
