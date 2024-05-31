package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pt.ipbeja.app.model.WSModel;

import java.nio.file.Path;

public class MenuBar extends javafx.scene.control.MenuBar {
    private final DirectorySaver scoreDir;
    private final DirectorySaver logDir;
    private final MenuItem hint;

    public MenuBar(Stage stage, WSModel model) {
        Menu opts = new Menu("Options");
        this.scoreDir = new DirectorySaver(
                stage,
                "Choose the score directory",
                "Which directory to use to save the scores?"
        );
        this.logDir = new DirectorySaver(
                stage,
                "Choose the log directory",
                "Which directory to use to save the logs?"
        );

        this.hint = new MenuItem("Give word hint");
        this.hint.setOnAction(event -> {
            if (!model.isOnReplay()) {
                model.giveHint();
            }
        });
        this.hint.setVisible(false);

        // https://gist.github.com/Warlander/815f5c435b2b11527ce65ff165dde023
        Menu quit = new Menu();
        MenuItem quitItem = new MenuItem();
        quitItem.setVisible(false);
        quitItem.setOnAction(actionEvent -> {
            ConfirmationAlert alert = new ConfirmationAlert(
                    "Quit program",
                    "Are you sure you want to quit?",
                    Platform::exit
            );
            alert.showAlert();
        });
        quit.getItems().add(quitItem);
        Label quitLabel = new Label();
        quitLabel.setText("Quit");
        quitLabel.setOnMouseClicked(evt -> quitItem.fire());
        quit.setGraphic(quitLabel);

        opts.getItems().addAll(this.scoreDir, this.logDir, this.hint);
        this.getMenus().addAll(opts, quit);
    }
    public Path getScoreDir() {
        return this.scoreDir.getDir();
    }
    public Path getLogDir() {
        return this.logDir.getDir();
    }

    public void permitHints(boolean permit) {
        this.hint.setVisible(permit);
    }
}
