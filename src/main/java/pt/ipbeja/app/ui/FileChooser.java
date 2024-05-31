package pt.ipbeja.app.ui;

import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class FileChooser {
    final Stage stage;

    public FileChooser(Stage stage) {
        this.stage = stage;
    }

    public File choose() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Which DB file would you like to use?");
        // https://stackoverflow.com/questions/14256588/opening-a-javafx-filechooser-in-the-user-directory
        fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir")).resolve("src/main/resources").toFile());
        fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(this.stage);
    }
}
