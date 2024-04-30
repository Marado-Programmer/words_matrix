package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import static pt.ipbeja.app.ui.StartWordSearch.TITLE;

public class ProvideWordDialog extends Dialog<String> {
    public ProvideWordDialog() {
        this.setTitle(TITLE);
        this.setHeaderText("Manual Words Provider");
        this.setContentText("Provide a new word for the game to use:");

        ButtonType provide = new ButtonType("Provide word", ButtonBar.ButtonData.APPLY);

        this.getDialogPane().getButtonTypes().addAll(provide, ButtonType.CLOSE);

        TextField word = new TextField();
        word.setPromptText("Word");

        Node provideBtn = this.getDialogPane().lookupButton(provide);
        provideBtn.setDisable(true);

        word.textProperty().addListener((observable, oldValue, newValue) -> provideBtn.setDisable(newValue.isBlank()));

        HBox box = new HBox(new Label("Word"), word);
        box.setAlignment(Pos.CENTER);

        this.getDialogPane().setContent(box);

        Platform.runLater(word::requestFocus);

        this.setResultConverter(buttonType -> {
            if (buttonType == provide) {
                return word.getText();
            }

            return null;
        });
    }
}
