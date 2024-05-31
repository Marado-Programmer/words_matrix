package pt.ipbeja.app.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

class GameEndedAlert extends Alert {
    public GameEndedAlert(App app, String content) {
        super(AlertType.INFORMATION);
        this.setTitle(StartWordSearch.TITLE);
        this.setHeaderText("Game Ended");
        this.setContentText(content);

        ButtonType replayGame = new ButtonType("Replay game", ButtonBar.ButtonData.BACK_PREVIOUS);
        ButtonType playAgain = new ButtonType("Play again with same game options", ButtonBar.ButtonData.NEXT_FORWARD);

        this.getDialogPane().getButtonTypes().addAll(replayGame, playAgain);


        VBox box = new VBox(new Label(content));

        Button saveLog = new Button("Save game log");

        saveLog.setOnAction(event -> {
            app.saveGameLog();
            saveLog.setDisable(true);
            saveLog.setVisible(false);
        });

        box.getChildren().add(saveLog);


        this.getDialogPane().setContent(box);
    }
}
