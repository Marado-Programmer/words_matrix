package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.ipbeja.app.model.WSModel;
import pt.ipbeja.app.model.Word;

public class Game extends HBox {
    private static final double SAVE_LOG_BTN_WIDTH = 200.0;
    private final App app;
    private final WSBoard board;
    private final TextArea log;
    private final TextArea points;

    private final Button end;
    private final ConfirmationAlert endGameConfirmation;

    private StringBuilder gameLog;

    public Game(App app, WSModel model) {
        super();
        this.app = app;
        this.end = new Button("End Game Now");
        this.endGameConfirmation = new ConfirmationAlert(
                "End Game",
                "Are you sure you want to end game?",
                model::endGame
        );
        this.end.setOnAction(event -> this.endGameConfirmation.showAlert());

        this.board = new WSBoard(model, app);
        this.board.requestFocus();

        this.log = new TextArea("STATUS:\n");
        this.log.setEditable(false);

        VBox board = new VBox(this.board, this.end);
        board.setAlignment(Pos.CENTER);
        Button saveLog = new Button("Save current game log");
        saveLog.setOnAction(event -> this.app.saveGameLog());
        saveLog.setMinWidth(SAVE_LOG_BTN_WIDTH);
        // https://stackoverflow.com/questions/40883858/how-to-evenly-distribute-elements-of-a-javafx-vbox
        this.points = new TextArea();
        this.points.setDisable(true);
        this.points.setPrefHeight(saveLog.getHeight());
        HBox top = new HBox(saveLog, this.points);
        VBox log = new VBox(top, this.log);
        this.getChildren().addAll(board, log);
        this.setAlignment(Pos.CENTER);

        this.gameLog = new StringBuilder();
    }

    public WSBoard getBoard() {
        return this.board;
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

    public void allowReplay(boolean allow) {
        this.end.setVisible(allow);
    }
}
