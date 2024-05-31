package pt.ipbeja.app.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmationAlert extends Alert {

    private final OnConfirmHandler handler;

    public ConfirmationAlert(String header, String content, OnConfirmHandler handler) {
        super(AlertType.CONFIRMATION);
        this.setTitle(StartWordSearch.TITLE);
        this.setHeaderText(header);
        this.setContentText(content);
        this.handler = handler;

    }

    public void showAlert() {
        Optional<ButtonType> result = this.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.handler.handle();
        }
    }

    public interface OnConfirmHandler {
        void handle();
    }
}
