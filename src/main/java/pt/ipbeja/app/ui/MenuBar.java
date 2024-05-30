package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pt.ipbeja.app.model.WSModel;

import java.nio.file.Path;

public class MenuBar extends javafx.scene.control.MenuBar {
    private final DirectorySaver scoreDir;
    private final DirectorySaver logDir;
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

        MenuItem hint = new MenuItem("Give word hint");
        hint.setOnAction(event -> model.giveHint());

        Menu quit = new Menu("Quit");
        quit.setOnAction(event -> Platform.exit());

        opts.getItems().addAll(scoreDir, logDir, hint);
        this.getMenus().addAll(opts, quit);
    }
    public Path getScoreDir() {
        return this.scoreDir.getDir();
    }
    public Path getLogDir() {
        return this.logDir.getDir();
    }
}
