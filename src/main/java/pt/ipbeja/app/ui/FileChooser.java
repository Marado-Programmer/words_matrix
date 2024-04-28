package pt.ipbeja.app.ui;

import javafx.stage.Stage;

import java.io.File;

public class FileChooser {
    final Stage stage;
    public FileChooser(Stage stage) {
        this.stage = stage;
    }

    public File choose() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Which DB file would you like to use?");
        fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(this.stage);
    }
}
