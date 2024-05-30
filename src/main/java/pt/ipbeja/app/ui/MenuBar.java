package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import java.nio.file.Path;

public class MenuBar extends javafx.scene.control.MenuBar {
    private final DirectorySaver scoreDir;
    private final DirectorySaver logDir;
    public MenuBar(Stage stage) {
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

        Menu quit = new Menu("Quit");
        quit.setOnAction(event -> Platform.exit());

        opts.getItems().addAll(scoreDir, logDir);
        this.getMenus().addAll(opts, quit);
    }
    public Path getScoreDir() {
        return this.scoreDir.getDir();
    }
    public Path getLogDir() {
        return this.logDir.getDir();
    }
}
