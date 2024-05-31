package pt.ipbeja.app.ui;

import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class FileChooser {
    private static final File RESOURCES_DIR = Paths.get(System.getProperty("user.dir"))
            .resolve("src")
            .resolve("main")
            .resolve("resources")
            .toFile();
    private final Stage stage;

    public FileChooser(Stage stage) {
        super();
        this.stage = stage;
    }

    public File choose() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Which DB file would you like to use?");
        // https://stackoverflow.com/questions/14256588/opening-a-javafx-filechooser-in-the-user-directory
        fileChooser.setInitialDirectory(RESOURCES_DIR);
        fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(this.stage);
    }
}
