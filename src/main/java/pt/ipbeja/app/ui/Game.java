package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pt.ipbeja.app.model.WSModel;
import pt.ipbeja.app.model.Word;

public class Game extends HBox {
    private final App app;
    private final WSBoard board;
    private final TextArea log;
    private final TextArea points;

    private StringBuilder gameLog;

    public Game(App app, WSModel model) {
        this.app = app;
        Button end = new Button("End Game Now");
        end.setOnAction(event -> model.giveHint());

        this.board = new WSBoard(model, app);
        this.board.requestFocus();

        this.log = new TextArea("STATUS:\n");
        this.log.setDisable(true);

        VBox board = new VBox(this.board, end);
        board.setAlignment(Pos.CENTER);
        Button saveLog = new Button("Save current game log");
        saveLog.setOnAction(event -> this.app.saveGameLog());
        // https://stackoverflow.com/questions/40883858/how-to-evenly-distribute-elements-of-a-javafx-vbox
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        this.points = new TextArea();
        this.points.setDisable(true);
        this.points.setPrefHeight(saveLog.getHeight());
        HBox.setHgrow(this.points, Priority.SOMETIMES);
        HBox top = new HBox(saveLog, r, this.points);
        top.setAlignment(Pos.CENTER_RIGHT);
        VBox log = new VBox(top, this.log);
        this.getChildren().addAll(board, log);
        this.setAlignment(Pos.CENTER);

        this.gameLog = new StringBuilder();
    }

    public WSBoard getBoard() {
        return board;
    }

    public void log(String msg) {
        this.log.appendText(msg);
        this.gameLog.append(msg);
    }

    public String getLog() {
        return this.gameLog.toString();
    }

    public void points(Word word) {
        this.points.setText(String.format("\"%s\" = %d pontos.", word.word(), word.points()));
    }

    public void resetGameLog() {
        this.gameLog = new StringBuilder();
    }
}
