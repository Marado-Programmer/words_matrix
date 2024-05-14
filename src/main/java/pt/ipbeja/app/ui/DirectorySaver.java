package pt.ipbeja.app.ui;

import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectorySaver extends MenuItem {
    private @Nullable Path dir;
    private static final @NotNull Path DEFAULT = Paths.get(System.getProperty("user.dir"));
    public DirectorySaver(Stage stage, String display, String title) {
        super(display);
        this.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(title);
            this.dir = directoryChooser.showDialog(stage).toPath();
        });
    }

    public @NotNull Path getDir() {
        if (this.dir == null) {
            return DirectorySaver.DEFAULT;
        }
        return this.dir;
    }
}
