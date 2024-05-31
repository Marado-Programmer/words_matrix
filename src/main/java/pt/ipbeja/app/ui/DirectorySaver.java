package pt.ipbeja.app.ui;

import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectorySaver extends MenuItem {
    private Path dir;
    private static final Path DEFAULT = Paths.get(System.getProperty("user.dir"));
    public DirectorySaver(Stage stage, String display, String title) {
        super(display);
        this.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(title);
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                this.dir = file.toPath();
            }
        });
    }

    public Path getDir() {
        if (this.dir == null) {
            return DirectorySaver.DEFAULT;
        }
        return this.dir;
    }
}
