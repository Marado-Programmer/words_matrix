package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ProvideWordDialog extends Dialog<String> {
    public ProvideWordDialog() {
        super();
        this.setTitle(StartWordSearch.TITLE);
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
                String w = word.getText();
                word.setText("");
                Platform.runLater(word::requestFocus);
                return w;
            }

            return null;
        });
    }
}
