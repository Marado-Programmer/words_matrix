package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class MenuBar extends javafx.scene.control.MenuBar {
    private @Nullable Path scoreDir;
    public MenuBar(Stage stage) {
        Menu opts = new Menu("Options");
        MenuItem setScoreDir = new MenuItem("Choose the score directory");
        this.scoreDir = null;
        setScoreDir.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Which directory to use to save the scores?");
            this.scoreDir = directoryChooser.showDialog(stage).toPath();
        });

        Menu quit = new Menu("Quit");
        quit.setOnAction(event -> Platform.exit());

        opts.getItems().add(setScoreDir);
        this.getMenus().addAll(opts, quit);
    }
    public @Nullable Path getScoreDir() {
        return scoreDir;
    }
}
