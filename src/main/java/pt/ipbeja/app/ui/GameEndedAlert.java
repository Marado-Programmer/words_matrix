package pt.ipbeja.app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static pt.ipbeja.app.ui.StartWordSearch.TITLE;

public class GameEndedAlert extends Alert {
    public GameEndedAlert(App app, String content) {
        super(AlertType.INFORMATION);
        this.setTitle(TITLE);
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
